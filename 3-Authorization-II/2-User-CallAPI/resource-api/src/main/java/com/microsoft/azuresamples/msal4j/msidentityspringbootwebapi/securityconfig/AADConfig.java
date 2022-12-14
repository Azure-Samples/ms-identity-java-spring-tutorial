package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.securityconfig;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.web.*;

/**
 * Configuration class for a custom security layer unique to AAD 
 */
@Configuration
public class AADConfig {

	//client id value obtained from application.yml file
    @Value("${azure.activedirectory.client-id}")
    String clientid;
    
    //tenant id value obtained from application.yml file
    @Value("${azure.activedirectory.tenant-id}")
    String tenantId;
    
    //base URL of JWK Set obtained from application.yml file
    @Value("${azure.activedirectory.instance-uri}")
    String instanceUri;   
    
    /**
     * JwtDecoder for signature and claim validation 
     */
    @Bean
    JwtDecoder jwtDecoder() {
    	
    	//creates the JWK Set URL
    	String JWKSet = instanceUri + tenantId + "/discovery/v2.0/keys";
    	
    	//users the JWK Set URL to create a NimbusJWTDecoder that (by default) performs signature validation
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(JWKSet).build();
        
        //provides the NimbusJWTDecoder with custom jwtValidation defined below
        nimbusJwtDecoder.setJwtValidator(jwtValidator());
        
        return nimbusJwtDecoder;
    }
    
    
	/**
	 * Returns a custom token validator to perform 
	 * standard and optional extended validation for an AAD token
	 */
    private OAuth2TokenValidator<Jwt> jwtValidator() {
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        
        // Add standard claim validation for valid issuer, audience, and timestamp claims
        String issuerUri =  instanceUri + tenantId + "/v2.0";
        validators = AADHelpers.AADStandardValidators(validators, issuerUri, clientid);
        
        /*Extended Validation 
         * Uncomment to allow user to limit access of API to specific client apps
         * Add client Id of your client apps to allowedClientApps
         * Extended Validation will add azp and appid validation for the id's provided
         */
        /*
        String[] allowedClientApps = new String[] {""};
        for (int i = 0; i < allowedClientApps.length; i++ ) {
			validators = AADHelpers.AADExtendedValidators(validators, allowedClientApps[i]);
        }
        */           
        return new AADDelegatingOAuth2TokenValidator<>(validators);
    }
          
    
}