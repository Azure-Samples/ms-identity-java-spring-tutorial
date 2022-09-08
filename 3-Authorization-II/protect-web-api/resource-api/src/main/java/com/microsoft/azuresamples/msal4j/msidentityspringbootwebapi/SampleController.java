// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
// import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
// import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.experimental.*;

@RestController
public class SampleController {

    @GetMapping("/api/date")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_Read') || hasAuthority('SCOPE_ReadWrite')"
    + "|| hasAuthority('APPROLE_ReadWrite.All') || hasAuthority('APPROLE_Read.All')")
    public String date(BearerTokenAuthentication bearerTokenAuth) {
         
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        if (isAppToken(principal)) {
        	System.out.println("this principal is an App");
        }
        else {
        	System.out.println("this principal is a User");
        }
        
        return new DateResponse().toString();
    }

    private class DateResponse {
        private String humanReadable;
        private String timeStamp;

        public DateResponse() {
            Date now = new Date();
            this.humanReadable = now.toString();
            this.timeStamp = Long.toString(now.getTime());
        }

        public String toString() {
            return String.format("{\"humanReadable\": \"%s\", \"timeStamp\": \"%s\"}", humanReadable, timeStamp);
        }
    }
    
    /**
     * Checks the idtyp claim to determine if principal is an app or a user
     * @param principal
     * @return
     */
	public static boolean isAppToken(OAuth2AuthenticatedPrincipal principal) {
        String idtyp = principal.getAttribute("idtyp");
        if (idtyp != null & idtyp == "app") {
        		return true;
        }      
        return false;
        
	}
    
}
