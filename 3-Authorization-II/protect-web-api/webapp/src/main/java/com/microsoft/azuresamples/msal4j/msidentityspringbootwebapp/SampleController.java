// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Controller
public class SampleController {

    @Value( "${app.api.base-address}" )
    private String apiAddress;

    @Value( "${app.api.date-endpoint}" )
    private String apiEndpoint;

    /**
     * Add HTML partial fragment from /templates/content folder to request and serve base html
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param fragment used to determine which partial to put into UI
     */
    private String hydrateUI(Model model, String fragment) {
        model.addAttribute("bodyContent", String.format("content/%s.html", fragment));
        return "base"; //base.html in /templates folder
    }

    /**
     *  Sign in status endpoint
     *  The page demonstrates sign-in status. For full details, see the src/main/webapp/content/status.html file.
     * 
     * @param model Model used for placing bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String status(Model model) {
        return hydrateUI(model, "status");
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
        return hydrateUI(model, "token");
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String survey(Model model) {
        return hydrateUI(model, "survey");
    }
    
    /**
     *  Call API  endpoint
     *  Demonstrates how to call a protected API
     * 
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param apiAuthorizedClient OAuth2AuthorizedClient this object contains API Access Token.
     * @return String the UI.
     */
    @GetMapping(path = "/call_api")
    public String callAPI(Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {

        final WebClient apiClient = WebClient.builder()
            .baseUrl(apiAddress)
            .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
            .build();

        //Map<String,String> apiResp;
        //try {
            String response = apiClient.get().uri(apiEndpoint).retrieve().toEntity(String.class).block().getBody();
            //apiResp =  new ObjectMapper().readValue(response, HashMap.class);
        /*} catch (Exception ex) {
            apiResp = new HashMap<>();
            apiResp.put("Error", "Response was null or other error");
        }*/

        model.addAttribute("apiResp", response);
        return hydrateUI(model, "api");
    }
    
    @GetMapping(path = "/table")
    public String tableView(Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {
        final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
                .build();
        HashMap response = apiClient.get().uri("/api/table").retrieve().toEntity(HashMap.class).block().getBody();        
        model.addAttribute("apiResp", response);
        return hydrateUI(model, "table");
    }   
    
    @PostMapping(path = "/add")
    public String add (Model model, @RequestParam("todo") String todo, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {
        final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
                .build();
        
        HashMap response = apiClient.post().uri("/api/add").bodyValue(todo).retrieve().toEntity(HashMap.class).block().getBody();
        model.addAttribute("apiResp", response);
        return "redirect:table";
    } 
    
    @GetMapping(path = "/delete")
    public String delete (Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient, @RequestParam Integer id) {
        final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
                .build();
        HashMap response = apiClient.delete().uri("/api/delete/" + id).retrieve().toEntity(HashMap.class).block().getBody();
        model.addAttribute("apiResp", response);
        return "redirect:table";
    }
    
    @GetMapping(path = "/details")
    public String details (Model model, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient, @RequestParam Integer id) {
        final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
                .build();
        HashMap response = apiClient.get().uri("/api/details/" + id).retrieve().toEntity(HashMap.class).block().getBody();
        if (response.containsKey("error")) {
            model.addAttribute("apiResp", response.get("error"));
            return hydrateUI(model, "api");
        }        
        model.addAttribute("apiResp", response);
        return hydrateUI(model, "details");
    }
    
    @PostMapping(path = "/edit")
    public String edit (Model model, @RequestParam("todo") String todo, @RequestParam("id") Integer id, @RegisteredOAuth2AuthorizedClient("web-api") OAuth2AuthorizedClient apiAuthorizedClient) {
        final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", apiAuthorizedClient.getAccessToken().getTokenValue()))
                .build();
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("id", id);
        map.add("todo", todo);        
        HashMap response = apiClient.post().uri("/api/edit").body(BodyInserters.fromMultipartData(map)).retrieve().toEntity(HashMap.class).block().getBody();
        model.addAttribute("apiResp", response);
        return "redirect:table";
    }    
    
    @GetMapping(path = "/addPage")
    public String addPage(Model model, @AuthenticationPrincipal OidcUser principal) {
        return hydrateUI(model, "addPage");
    }
    
    @GetMapping(path = "/editPage")
    public String editPage(Model model, @AuthenticationPrincipal OidcUser principal, @RequestParam Integer id) {
        model.addAttribute("id", id);
        return hydrateUI(model, "editPage");
    }
}
