// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController {
    @Value( "${app.ui.base:base}" )
    private String baseUI;

    @Value( "${app.ui.content:bodyContent}" )
    private String content;

    /**
     *  Sign in status endpoint
     *  The page demonstrates sign-in status. For full details, see the src/main/webapp/content/status.jsp file.
     * 
     * @param model Model used for placing bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String status(Model model) {
        model.addAttribute(content, "content/status.html");
        return baseUI;
    }

    /**
     *  Token details endpoint
     *  Demonstrates how to extract and make use of token details
     *  For full details, see method: Utilities.filterclaims(OidcUser principal)
     * 
     * @param model Model used for placing claims param and bodyContent param in request before serving UI.
     * @param principal OidcUser this object contains all ID token claims about the user. See utilities file.
     * @return String the UI.
     */
    @GetMapping(path = "/token_details")
    public String tokenDetails(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("claims", Utilities.filterClaims(principal));
        model.addAttribute(content, "content/token.html");
        return baseUI;
    }

    /**
     *  Call Graph endpoint
     *  Demonstrates how to utilize OAuth2AuthorizedClient.
     *  Passes that client over to Utilities.graphUserProperties, which creates a GraphServiceClient (GraphSDK v3)
     *  For full details, see method: Utilities.graphUserProperties(OAuth2AuthorizedClient graphAuthorizedClient)
     * 
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param graphAuthorizedClient OAuth2AuthorizedClient this object contains Graph Access Token. See utilities file.
     * @return String the UI.
     */
    @GetMapping(path = "/call_graph")
    public String callGraph(Model model, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        model.addAttribute("user", Utilities.graphUserProperties(graphAuthorizedClient));
        model.addAttribute(content, "content/graph.html");
        return baseUI;
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String tokenDetails(Model model) {
        model.addAttribute(content, "content/survey.html");
        return baseUI;
    }
}
