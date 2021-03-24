// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class Utilities {
    private Utilities() {
        throw new IllegalStateException("Utility class. Don't instantiate");
    }

    public static Map<String,String> filterClaims(OidcUser principal) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        Map<String,String> filteredClaims = new HashMap<>();
        includeClaims.forEach(claim -> {
            if (principal.getIdToken().getClaims().containsKey(claim)) {
                filteredClaims.put(claim, principal.getIdToken().getClaims().get(claim).toString());
            }
        });
        return filteredClaims;
    }

    /**
     * Take a few of the User properties obtained from the graph /me endpoint and put them into KV pairs for UI to display.
     * @param graphClient OAuth2AuthorizedClient created by AAD Boot starter. See the SampleController class for details.
     * @return Map<String,String> select Key-Values from User object
     */
    public static Map<String,String> graphUserProperties(OAuth2AuthorizedClient graphClient) {
        User user = Utilities.getGraphSDKClient(graphClient).me().buildRequest().get();

        Map<String,String> userProperties = new HashMap<>();
        if (user == null) {
            userProperties.put("Graph Error", "GraphSDK returned null User object.");
        } else {
            userProperties.put("Display Name", user.displayName);
            userProperties.put("Phone Number", user.mobilePhone);
            userProperties.put("City", user.city);
            userProperties.put("Given Name", user.givenName);
        }
        return userProperties;
    }

    /**
     * getGraphSDKClient prepares and returns a graphServiceClient to make API calls to
     * Graph. See docs for GraphServiceClient (GraphSDK for Java)
     * 
     * Uses a Spring authorized graphClient provided by AAD Boot Starter. See the SampleController class for details.
     * 
     * @param graphClient OAuth2AuthorizedClient created by AAD Boot starter. See the SampleController class for details.
     * @return GraphServiceClient GraphServiceClient
     */
    
    public static GraphServiceClient getGraphSDKClient(@Nonnull OAuth2AuthorizedClient graphClient) {
        return GraphServiceClient.builder().authenticationProvider(new MsalGraphAuthenticationProvider(graphClient))
                .buildClient();
    }

    /**
     * Our Msal Graph Authentication Provider class. Required for setting up a
     * GraphServiceClient. It extends BaseAuthenticationProvider which in turn implements IAuthenticationProvider.
     */
    private static class MsalGraphAuthenticationProvider
            extends BaseAuthenticationProvider {

        private OAuth2AuthorizedClient graphClient;

        /**
         * Set up the MsalGraphAuthenticationProvider. Allows accessToken to be
         * used by GraphServiceClient through the interface IAuthenticationProvider
         * 
         * @param graphClient OAuth2AuthorizedClient created by AAD Boot starter. See the SampleController class for details.
         */
        public MsalGraphAuthenticationProvider(@Nonnull OAuth2AuthorizedClient graphClient) {
           this.graphClient = graphClient;
        }

        /**
         * This implementation of the IAuthenticationProvider helps injects the Graph access
         * token from Azure AD into the headers of the request used by GraphSDK.
         *
         * @param requestUrl the outgoing request URL
         * @return a future with the token
         */
        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl){
            return CompletableFuture.completedFuture(graphClient.getAccessToken().getTokenValue());
        }
    }
}
