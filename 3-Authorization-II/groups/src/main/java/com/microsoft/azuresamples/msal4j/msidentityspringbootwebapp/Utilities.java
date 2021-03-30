// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.graph.Graph;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.GroupCollectionRequest;
import com.microsoft.graph.requests.GroupCollectionRequestBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class Utilities {
    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    private Utilities() {
        throw new IllegalStateException("Utility class. Don't instantiate");
    }

    /**
     * Take a subset of ID Token claims and put them into KV pairs for UI to display.
     * @param principal OidcUser (see SampleController for details)
     * @return Map of filteredClaims
     */
    public static Map<String,String> filterClaims(OidcUser principal) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username", "roles"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        Map<String,String> filteredClaims = new HashMap<>();
        includeClaims.forEach(claim -> {
            if (principal.getIdToken().getClaims().containsKey(claim)) {
                filteredClaims.put(claim, principal.getIdToken().getClaims().get(claim).toString());
            } else {
                filteredClaims.put(claim, "This claim was not found in ID Token.");
            }
        });
        return filteredClaims;
    }
    
    /**
     * Get signed-in user's group memberships from Microsoft Graph
     * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. See the SampleController class for details.
     * @return Group memberships from Graph
     */
    public static List<Group> getMemberGroups(OAuth2AuthorizedClient graphAuthorizedClient) {
        GraphServiceClient graphServiceClient = GraphHelper.getGraphServiceClient(graphAuthorizedClient);

        // Set up the initial request builder and build request for the first page
        GroupCollectionRequestBuilder groupsRequestBuilder = graphServiceClient.groups();
        GroupCollectionRequest groupsRequest = groupsRequestBuilder.buildRequest().top(999);

        List<Group> allGroups = new ArrayList<>();

        do {
            try {
                // Execute the request
                GroupCollectionPage groupsCollection = groupsRequest.get();

                // Process each of the items in the response
                for (Group group : groupsCollection.getCurrentPage()) {
                    allGroups.add(group);
                }

                // Build the request for the next page, if there is one
                groupsRequestBuilder = groupsCollection.getNextPage();
                if (groupsRequestBuilder == null) {
                    groupsRequest = null;
                } else {
                    groupsRequest = groupsRequestBuilder.buildRequest();
                }

            } catch (ClientException ex) {
                // Handle failure
                logger.warn(ex.getMessage());
                logger.warn(Arrays.toString(ex.getStackTrace()));
                groupsRequest = null;
            }

        } while (groupsRequest != null);

        return allGroups;
    }

    public static List<String> mapGroupIDs(List<Group> groups) {
       return  groups.stream()
        .map(group -> group.id)
        .collect(Collectors.toList());
    }

}

