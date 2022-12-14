package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.securityconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.util.StringUtils;

import com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.experimental.*;

@Configuration
public class AppSecurityConfig {

    @Value("${azure.activedirectory.client-id}")
    String clientid;
    
    @Value("${azure.activedirectory.tenant-id}")
    String tenantId;
    
    @Value("${azure.activedirectory.instance-uri}")
    String instanceUri;   
    
    /**
     * JwtDecoder for signature and claim validation 
     */
    @Bean
    JwtDecoder jwtDecoder() {
    	String JWKSet = instanceUri + tenantId + "/discovery/v2.0/keys";
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(JWKSet).build();
        nimbusJwtDecoder.setJwtValidator(jwtValidator());
        return nimbusJwtDecoder;
    }
    
    /* 
     * Adds standard and optional extended validation for AAD token
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