// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.microsoft.graph.models.Group;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController {
    @Value( "${app.ui.base:base.jsp}" )
    private String baseUI;

    @Value( "${app.ui.content:bodyContent}" )
    private String content;

    @Value( "${app.manual-verification-groups}" )
    private ArrayList<String> manualVerificationGroups;

    private final String GROUPS_CLAIM_KEY = "groups";
    private final String OTHER_CLAIMS_KEY = "_claim_names";

    /**
     *  Sign in status endpoint
     *  The page demonstrates sign-in status. For full details, see the src/main/webapp/content/status.jsp file.
     * 
     * @param model Model used for placing bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String login(Model model) {
        model.addAttribute(content, "content/status.jsp");
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
        model.addAttribute(content, "content/token.jsp");
        return baseUI;
    }

    /**
     *  PreAuthorize Group
     *  Demonstrates how to filter access using @PreAuthorize annotation with a group membership.
     *  Any groups you use with PreAuhtorize annotations must be declared in the application.properties file.
     *  Default configuration: Logical AND strategy with TestGroup1 AND TestGroup2
     * @param req used to determine which endpoint triggered this, in order to display required groups.
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(path = "/preauthorize_groups")
    @PreAuthorize("hasRole('ROLE_TestGroup1') && hasRole('ROLE_TestGroup2')")
    public String preAuthorizeGroup(Model model, HttpServletRequest req) {
        addGroupsAttribute(model, req);
        model.addAttribute(content, "content/group.jsp");
        return baseUI;
    }

    /**
     *  Token Group
     *  Demonstrates how to filter access using group membership obtained from the token.
     *  Uses LOGICAL AND
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param principal OidcUser this object contains all ID token claims about the user. See utilities file.
     * @param graphAuthorizedClient OAuth2AuthorizedClient this object contains Graph Access Token. See utilities file.
     * @param req used to determine which endpoint triggered this, in order to display required groups.
     * @return String the UI.
     */
    @GetMapping(path = "/token_groups")
    public String tokenGroup(Model model, @AuthenticationPrincipal OidcUser principal,
            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient,
            HttpServletRequest req) {

        List<String> groups = new ArrayList<>();
        if (principal.containsClaim(GROUPS_CLAIM_KEY)) {
            // token contains groups claim!
            groups = principal.getClaimAsStringList(GROUPS_CLAIM_KEY);
        } else if (principal.containsClaim(OTHER_CLAIMS_KEY) && 
                    principal.getClaimAsMap(OTHER_CLAIMS_KEY).containsKey(GROUPS_CLAIM_KEY)) {
            // token was supposed to contain groups claim
            // but there was an overage (see README).
            // Must get groups from Graph.
            List<Group> graphGroups = Utilities.getMemberGroups(graphAuthorizedClient);
            groups = Utilities.mapGroupIDs(graphGroups);
        } else {
            // there wasn't supposed to be any groups claim in token
            // therefore, we shouldn't get them.
        }

        if (!groups.containsAll(manualVerificationGroups)) {
            // User doesn't meet requirements
            throw new AccessDeniedException("User does not have required groups");
        }

        addGroupsAttribute(model, req);
        model.addAttribute(content, "content/group.jsp");
        return baseUI;
    }

    // /**
    //  *  Graph Group
    //  *  Demonstrates how to filter access using group membership obtained from Graph.
    //  *  This may be required if there is a "Group Overage" or token does not contain groups claim for any reason.
    //  * @param model Model used for placing user param and bodyContent param in request before serving UI.
    //  * @param principal OidcUser this object contains all ID token claims about the user. See utilities file.
    //  * @param req used to determine which endpoint triggered this, in order to display required groups.
    //  * @return String the UI.
    //  */
    // @GetMapping(path = "/graph_group")
    // public String graphGroup(Model model, HttpServletRequest req, @AuthenticationPrincipal OidcUser principal) {
    //     addGroupsAttribute(model, req);
    //     model.addAttribute(content, "content/group.jsp");
    //     return baseUI;
    // }

    /**
     *  handleError - show custom 403 page on failing to meet roles requirements
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param req used to determine which endpoint triggered this, in order to display required roles.
     * @param adex the access-denied exception
     * @return String the UI.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleError(Model model, AccessDeniedException adex, HttpServletRequest req) {
        addGroupsAttribute(model, req);
        model.addAttribute(content, "content/403.jsp");
        return baseUI;
    }

    private void addGroupsAttribute(Model model, HttpServletRequest req) {
        String path = req.getServletPath();
        if (path.equals("/preauthorize_groups")) {
            model.addAttribute("groupsRequired", "TestGroup1, TestGroup2");
        } else if (path.equals("/token_groups") || path.equals("/graph_groups")) {
            model.addAttribute("groupsRequired", manualVerificationGroups.toString() );
        }
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String tokenDetails(Model model) {
        model.addAttribute(content, "content/survey.jsp");
        return baseUI;
    }
}
