// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
