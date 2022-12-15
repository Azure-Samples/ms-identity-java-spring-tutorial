package com.microsoft.azuresamples.msal4j.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.*;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;

@Configuration
public class OAuthClientConfiguration {
	
	
	@Bean
	public ConfidentialClientApplication confidentialClientObject(
                @Value( "${app.auth.authority}") String token_uri,
                @Value("${app.auth.client-id}") String client_id,
                @Value("${app.auth.client-secret}") String client_secret				
			) throws Exception {
        
    	// Load properties file and set properties used throughout the sample
		ConfidentialClientApplication  app = ConfidentialClientApplication.builder(
				client_id,
                ClientCredentialFactory.createFromSecret(client_secret))
                .authority(token_uri)
                .build();
		return app;
    }
	
}
