// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.Date;
import java.util.HashMap;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
// import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.experimental.*;
import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model.ToDoList;
import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model.ToDoListItem;

@RestController
public class SampleController {

    private ToDoList TDL;
    
    @GetMapping("/api/date")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_Read') || hasAuthority('SCOPE_ReadWrite')"
    + "|| hasAuthority('APPROLE_ReadWrite.All') || hasAuthority('APPROLE_Read.All')")
    public String callAPI(BearerTokenAuthentication bearerTokenAuth) {        
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        String response;
        if (isAppToken(principal)) {
            response = "this principal is an App";
        }
        else {
            response = "this principal is a User";
        }
        return response;
        //return new DateResponse().toString();
        //return String.format("{\"test\": \"%s\"}", test);
    }
    
    @GetMapping("/api/table")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_Read') || hasAuthority('SCOPE_ReadWrite')"
    + "|| hasAuthority('APPROLE_ReadWrite.All') || hasAuthority('APPROLE_Read.All')")
    public HashMap getTable(BearerTokenAuthentication bearerTokenAuth) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        return TDL.get();
    } 
    

    @PostMapping("/api/add")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ReadWrite') || hasAuthority('APPROLE_ReadWrite.All')")
    public HashMap add(BearerTokenAuthentication bearerTokenAuth, @RequestBody ToDoListItem tobeadded) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        TDL.add(tobeadded);
        return TDL.get();
    }  
    
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ReadWrite') || hasAuthority('APPROLE_ReadWrite.All')")
    public HashMap delete(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        TDL.delete(id);
        return TDL.get();
    }
    
    @GetMapping("/api/details/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_Read') || hasAuthority('APPROLE_Read.All')")
    public HashMap details(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        HashMap response = new HashMap();
        response.put(id, TDL.getOne(id));
        return response;
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
    
    private void createIfNotCreated(OAuth2AuthenticatedPrincipal principal) {
        if (TDL == null) {
            this.TDL = new ToDoList();
            TDL.add(new ToDoListItem(principal.getAttribute("preferred_username"), "finish todo"));
            TDL.add(new ToDoListItem(principal.getAttribute("preferred_username"), "overworked, seek help"));
        }
    }
    
}
