package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class SampleController {
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String login(HttpServletRequest req) {
        req.setAttribute("bodyContent", "content/status.jsp");
        return "index.jsp";
    }

    @GetMapping(path = "/token_details")
    public String tokenDetails(HttpServletRequest req, @AuthenticationPrincipal OidcUser principal) {
        req.setAttribute("claims", filterClaims(principal));
        req.setAttribute("bodyContent", "content/token.jsp");
        return "index.jsp";
    }

    @GetMapping(path = "/survey")
    public String tokenDetails(HttpServletRequest req) {
        req.setAttribute("bodyContent", "content/survey.jsp");
        return "index.jsp";
    }

    private HashMap<String,String> filterClaims(OidcUser principal) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        HashMap<String,String> filteredClaims = new HashMap<>();
        includeClaims.forEach(claim -> {
            if (principal.getIdToken().getClaims().containsKey(claim)) {
                filteredClaims.put(claim, principal.getIdToken().getClaims().get(claim).toString());
            }
        });
        return filteredClaims;
    }
}
