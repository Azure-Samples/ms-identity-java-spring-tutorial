// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.Read') || hasAuthority('SCOPE_ToDoList.ReadWrite')"
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
    }
    
    @GetMapping("/api/table")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.Read') || hasAuthority('SCOPE_ToDoList.ReadWrite')"
    + "|| hasAuthority('APPROLE_ReadWrite.All') || hasAuthority('APPROLE_Read.All')")
    public HashMap get(BearerTokenAuthentication bearerTokenAuth) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
        if (isAppToken(principal)) {
            return TDL.get();
        }
        else {
            return TDL.getByUser(principal.getAttribute("oid"));
        }      
    } 
    
    @GetMapping("/api/details/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.Read') || hasAuthority('APPROLE_Read.All')")
    public HashMap details(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
        HashMap response = new HashMap();
        ToDoListItem toBeDetailed = TDL.getById(id);
        if (toBeDetailed == null) {
            response.put("error", "To Do does not exist");
        }
        else {
            if (isAppToken(principal)) {
                response.put(id, toBeDetailed);
            }
            else {
                if (toBeDetailed.getOwner().equals(principal.getAttribute("oid"))) {
                    response.put(id, toBeDetailed);
                }
                else {
                    response.put("error", "You do not have permission to view this");
                }
                
            }
        }
        return response;
    }
    
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ReadWrite.All')")
    public HashMap delete(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
        if (isAppToken(principal)) {
            TDL.delete(id);
        }
        else {
            HashMap response = new HashMap();
            ToDoListItem toBeDelete = TDL.getById(id);
            if (toBeDelete == null) {
                response.put("error", "To Do does not exist");
            }
            else {
                if (toBeDelete.getOwner().equals(principal.getAttribute("oid"))) {
                    TDL.delete(id);
                }
                else {                    
                    response.put("error", "You do not have permission to delete this");
                    return response;
                } 
            }
        }     
        return get(bearerTokenAuth);
    }
    
    @PostMapping("/api/add")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ReadWrite.All')")
    public HashMap add(BearerTokenAuthentication bearerTokenAuth, @RequestBody String TBA) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
        if (isAppToken(principal)) {
            if (principal.getAttribute("oid") == null || principal.getAttribute("oid") == "") {
                HashMap response = new HashMap();
                response.put("error", "The owner's objectid was not provided in the ToDo list item payload");
                return response;
            }
        }
        ToDoListItem entryTBA = new ToDoListItem(principal.getAttribute("oid"), TBA);
        TDL.add(entryTBA);
        
        return get(bearerTokenAuth);
    }  
    
 
    @PostMapping("/api/edit")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ReadWrite.All')")
    public HashMap edit(BearerTokenAuthentication bearerTokenAuth, @RequestParam("todo") String todo, @RequestParam("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
        if (!isAppToken(principal)) {
            HashMap response = new HashMap();
            ToDoListItem toBeEdited = TDL.getById(id);
            if (toBeEdited == null) {
                response.put("error", "To Do does not exist");
                return response; 
            }
            else {
                if (toBeEdited.getOwner().equals(principal.getAttribute("oid"))) {                    
                    toBeEdited.setTodo(todo);
                    TDL.edit(id, toBeEdited);
                }
                else {
                    response.put("error", "You do not have permission to edit this");
                    return response;                    
                }
            }         
        }       
        return get(bearerTokenAuth);
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
            TDL.add(new ToDoListItem(principal.getAttribute("oid"), "finish todo"));
            TDL.add(new ToDoListItem(principal.getAttribute("oid"), "overworked, seek help"));
            TDL.add(new ToDoListItem("A man clearly burnt out", "overworked, seek help"));
            TDL.add(new ToDoListItem("To test some oid functionality", "overworked, seek help"));
        }
    }
    
}
