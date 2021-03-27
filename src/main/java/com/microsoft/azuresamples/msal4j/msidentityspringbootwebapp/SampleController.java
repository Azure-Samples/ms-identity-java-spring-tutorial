// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController implements ErrorController {
    @Value( "${app.ui.base:base.jsp}" )
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
     *  Admin Only endpoint
     *  Demonstrates how to filter so only users with admin role can access.
     * 
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(path = "/admin_only")
    @PreAuthorize("hasAuthority('APPROLE_PrivilegedUser')")
    public String adminOnly(Model model) {
        model.addAttribute("roles", "PrivilegedUser");
        model.addAttribute(content, "content/role.jsp");
        return baseUI;
    }

    /**
     *  Regular User endpoint
     *  Demonstrates how to filter so only users with ONE OF or BOTH of PrivilegedUser OR RegularUser role can access.
     * 
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(path = "/regular_user")
    @PreAuthorize("hasAnyAuthority('APPROLE_RegularUser','APPROLE_RegularUser')")
    public String regularUser(Model model) {
        model.addAttribute("roles", "PrivilegedUser, RegularUser");
        model.addAttribute(content, "content/role.jsp");
        return baseUI;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleError(Model model, AccessDeniedException adex) {
        model.addAttribute(content, "content/403.jsp");
        return baseUI;
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String tokenDetails(Model model) {
        model.addAttribute(content, "content/survey.jsp");
        return baseUI;
    }
}
