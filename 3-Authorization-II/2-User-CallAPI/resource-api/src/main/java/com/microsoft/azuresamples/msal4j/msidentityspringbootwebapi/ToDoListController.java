// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.HashMap;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model.ToDoList;
import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model.ToDoListItem;

@RestController
public class ToDoListController {

    private ToDoList TodoStore;
    
	/**
	 * API method to return the entire ToDoStore
	 * @param bearerTokenAuth that represents the access token of a calling user to be used in obtaining information about the user
	 */
    @GetMapping("/api/table")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.Read') || hasAuthority('SCOPE_ToDoList.ReadWrite')"
    + "|| hasAuthority('APPROLE_ToDoList.ReadWrite.All') || hasAuthority('APPROLE_ToDoList.Read.All')")
    public HashMap get(BearerTokenAuthentication bearerTokenAuth) {
        
    	// obtains the principal information from the access token 
    	// the  principal contains all the token's claims and, by extension, all of the identifying information of the API caller
    	OAuth2AuthenticatedPrincipal principal = getPrinciple(bearerTokenAuth);
        
    	// pre-populates the ToDoStore using the principal information of the user if the ToDoStore is empty
    	createIfNotCreated(principal);
        
    	// the principal can be used to determine if the API caller an app or a user
    	// performs different actions in accordance to what the API caller is
        if (isAppToken(principal)) {
        	
        	// if the API call is from an app, returns the entire ToDoStore
            return TodoStore.get();
        }
        else {
        	
        	//if the API call is from a user, return the entries in the ToDOStore of only that user's OID
            return TodoStore.getByUser(principal.getAttribute("oid"));
        }      
    } 
    
	/**
	 * API method to edit the information of a ToDoStore entry
	 * @param bearerTokenAuth that represents the access token of a calling user to be used in obtaining information about the user
	 * @param id that represents the id of an entry in the ToDoStore to be edited
	 */
    @GetMapping("/api/details/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.Read') || hasAuthority('SCOPE_ToDoList.ReadWrite')"
    + "|| hasAuthority('APPROLE_ToDoList.ReadWrite.All') || hasAuthority('APPROLE_ToDoList.Read.All')")    
    public HashMap details(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
        
    	// obtains the principal information from the access token 
    	// the  principal contains all the token's claims and, by extension, all of the identifying information of the API caller
    	OAuth2AuthenticatedPrincipal principal = getPrinciple(bearerTokenAuth);
    	
    	// pre-populates the ToDoStore using the principal information of the user if the ToDoStore is empty    	
        createIfNotCreated(principal);
              
        HashMap response = new HashMap();
        
        //obtains the ToDoListItem entry in the ToDoStore of the provided id
        ToDoListItem toBeDetailed = TodoStore.getById(id);
        
        //returns an error if the ToDoListItem does not exist
        if (toBeDetailed == null) {
            response.put("error", "To Do does not exist");
        }
        else {
        	
        	// the principal can be used to determine if the API caller an app or a user
        	// performs different actions in accordance to what the API caller is
            if (isAppToken(principal)) {
            	
            	// if the API call is from an app, performs the edit unconditionally
                response.put(id, toBeDetailed);
            }
            else {
            	
            	////if the API call is from a user, performs the edit only if the user OID matches with the entry (i.e. owns the entry)
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
    
	/**
	 * API method to return the entire ToDoStore
	 * @param bearerTokenAuth that represents the access token of a calling user to be used in obtaining information about the user
	 * @param id that represents the id of an entry in the ToDoStore to be deleted
	 */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ToDoList.ReadWrite.All')")
    public HashMap delete(BearerTokenAuthentication bearerTokenAuth, @PathVariable("id") Integer id) {
    	
    	// obtains the principal information from the access token 
    	// the  principal contains all the token's claims and, by extension, all of the identifying information of the API caller    	
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        
    	// pre-populates the ToDoStore using the principal information of the user if the ToDoStore is empty  
        HashMap response = new HashMap();
        
        //obtains the ToDoListItem entry in the ToDoStore of the provided id
        ToDoListItem toBeDeleted = TodoStore.getById(id);
        if (toBeDeleted == null) {
            response.put("error", "To Do does not exist");
            return response;
        }
        
        if (isAppToken(principal)) {
        	TodoStore.delete(id);
        }
        else {
            if (toBeDeleted.getOwner().equals(principal.getAttribute("oid"))) {
            	TodoStore.delete(id);
            }
            else {                    
                response.put("error", "You do not have permission to delete this");
                return response;
            } 
        }     
        return get(bearerTokenAuth);
    }
    
    @PostMapping("/api/add")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ToDoList.ReadWrite.All')")
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
        TodoStore.add(entryTBA);
        
        return get(bearerTokenAuth);
    }  
    
 
    @PostMapping("/api/edit")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_ToDoList.ReadWrite') || hasAuthority('APPROLE_ToDoList.ReadWrite.All')")
    public HashMap edit(BearerTokenAuthentication bearerTokenAuth, @RequestParam("todo") String todo, @RequestParam("id") Integer id) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        createIfNotCreated(principal);
        HashMap response = new HashMap();
        ToDoListItem toBeEdited = TodoStore.getById(id);
        if (toBeEdited == null) {
            response.put("error", "To Do does not exist");
            return response; 
        }
        if (!isAppToken(principal)) {
            if (!(toBeEdited.getOwner().equals(principal.getAttribute("oid")))) {                    
                response.put("error", "You do not have permission to edit this");
                return response;  
            }        
        }
        toBeEdited.setTodo(todo);
        TodoStore.edit(id, toBeEdited);
        return get(bearerTokenAuth);
    }
    
    /**
     * Checks the idtyp claim to determine if principal is an app or a user
     * @param principal
     * @return
     */
    public static boolean isAppToken(OAuth2AuthenticatedPrincipal principal) {
        String idtyp = principal.getAttribute("idtyp");
        if (idtyp != null && idtyp.equals( "app")) {
                return true;
        }      
        return false;
        
    }
    
    
	/**
	 * pre-populates the ToDoStore with sample data 
	 * @param principal of a calling user to be used to create some of the sample data
	 */
    private void createIfNotCreated(OAuth2AuthenticatedPrincipal principal) {
    	
    	//uses the principal to obtain a calling user oid value 
    	// The 'oid' (object id) is the only claim that should be used to uniquely identify a user in an Azure AD tenant.          
    	String userOID = principal.getAttribute("oid");
        
    	//populates the ToDoStore with two values under the users OID and two values under different OID's
    	//This is meant to demonstrate the differences in behavior between an app and a user calling the API
    	if (TodoStore == null) {
            this.TodoStore = new ToDoList();
            TodoStore.add(new ToDoListItem(userOID, "Pick up groceries"));
            TodoStore.add(new ToDoListItem(userOID, "Finish invoice report"));
            TodoStore.add(new ToDoListItem("Fake id of another User", "Rent a car"));
            TodoStore.add(new ToDoListItem("made up id of another", "Get vaccinated"));
        }
    }
    
    private OAuth2AuthenticatedPrincipal getPrinciple(BearerTokenAuthentication bearerTokenAuth) {
    	 OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
    	 return principal;
    }
    
}
