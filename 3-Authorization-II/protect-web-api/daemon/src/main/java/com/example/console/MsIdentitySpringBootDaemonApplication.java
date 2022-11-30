package com.example.console;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.Jwt.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.UserCollectionPage;

@Configuration
@SpringBootApplication
public class MsIdentitySpringBootDaemonApplication implements CommandLineRunner{
		    
	@Value( "${app.api.base-address}" )
    private String apiAddress;

    @Autowired
	private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;
    
	public static void main(String[] args) {
		SpringApplication.run(MsIdentitySpringBootDaemonApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception{
    	getTable();
    	add("Testing");
    	delete("5");
    	edit("3", "Changed for app");
    	details("2");
    }
    
    public void getTable() throws Exception{		
        final WebClient apiClient = getApiClient();
        try {
	        HashMap response = apiClient.get().uri("/api/table").retrieve().toEntity(HashMap.class).block().getBody();        
	        System.out.println(response.toString());
		} catch (Exception ex) {
			System.out.println("an error has occured");
		}		
    }
    
    public void add(String todo) throws Exception{		
    	final WebClient apiClient = getApiClient();
        try {
	        HashMap response = apiClient.post().uri("/api/add").bodyValue(todo).retrieve().toEntity(HashMap.class).block().getBody();  
	        if (response.containsKey("error")) {
	        	System.out.println(response);           
	        }
	        else {
		        getTable();
	        }	
		} catch (Exception ex) {
			System.out.println("an error has occured");
		}
    }
    
    public void delete(String id) throws Exception{		
	    final WebClient apiClient = getApiClient();
        try {
	        HashMap response = apiClient.delete().uri("/api/delete/" + id).retrieve().toEntity(HashMap.class).block().getBody();
	        if (response.containsKey("error")) {
	        	System.out.println(response);           
	        }
	        else {
		        getTable();
	        }	        		
		} catch (Exception ex) {
			System.out.println("an error has occured");
		}
        
    }
    
    public void edit(String id, String todo) throws Exception{           	
    	final WebClient apiClient = getApiClient();
        try {
	        LinkedMultiValueMap map = new LinkedMultiValueMap();
	        map.add("id", id);
	        map.add("todo", todo);
	        HashMap response = apiClient.post().uri("/api/edit").body(BodyInserters.fromMultipartData(map)).retrieve().toEntity(HashMap.class).block().getBody();
	        if (response.containsKey("error")) {
	        	System.out.println(response);           
	        }
	        else {
	        	getTable();
	        }	        
		} catch (Exception ex) {
			System.out.println("an error has occured");
		}
    }
    
    public void details(String id) throws Exception{           	
    	final WebClient apiClient = getApiClient();
    	   	
    	try {
            HashMap response = apiClient.get().uri("/api/details/" + id).retrieve().toEntity(HashMap.class).block().getBody();
            System.out.println(response);  	
		} catch (Exception ex) {
			System.out.println("an error has occured");
		}
    }
    
    private WebClient getApiClient() {
    	OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("azure")
				.principal("Test Tenant")
				.build();
		OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);
		OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
		String accessTokenValue = accessToken.getTokenValue();
		
    	final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", accessTokenValue))
                .build();
		
    	return apiClient;
    }

    
}
