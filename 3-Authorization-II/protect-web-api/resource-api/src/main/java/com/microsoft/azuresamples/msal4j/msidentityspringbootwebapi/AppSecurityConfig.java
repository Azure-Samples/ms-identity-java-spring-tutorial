package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

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

@Configuration
public class AppSecurityConfig {

    @Value("${azure.activedirectory.client-id}")
    String audience;
    
    @Value("${azure.activedirectory.tenant-id}")
    String tenantId;
    
    /*
    private final OAuth2ResourceServerProperties.Jwt properties;
    
    public AppSecurityConfig(OAuth2ResourceServerProperties properties) {
        this.properties = properties.getJwt();
    }*/

    @Bean
    JwtDecoder jwtDecoder() {
    	String JWKSet = "https://login.microsoftonline.com/" + tenantId + "/discovery/v2.0/keys";
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(JWKSet).build();
        /*NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(properties.getJwkSetUri()).build();*/
        nimbusJwtDecoder.setJwtValidator(jwtValidator());
        return nimbusJwtDecoder;
    }

    private OAuth2TokenValidator<Jwt> jwtValidator() {
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        String issuerUri = "https://login.microsoftonline.com/" + tenantId + "/v2.0";      		
        //String issuerUri = properties.getIssuerUri();
        if (StringUtils.hasText(issuerUri)) {
            validators.add(new JwtIssuerValidator(issuerUri));
        }
        if (StringUtils.hasText(audience)) {
            validators.add(new JwtClaimValidator<>(JwtClaimNames.AUD, claimPredicate(audience)));
        }
        if (StringUtils.hasText(tenantId)) {
            validators.add(new JwtClaimValidator<>("tid", claimPredicate(tenantId)));
        } 
        validators.add(new JwtTimestampValidator());
        return new DelegatingOAuth2TokenValidator<>(validators);
    }
    
    
    Predicate<Object> claimPredicate(String claim) {
        return clm -> {
            if (clm == null) {
                return false;
            } else if (clm instanceof String) {
                return clm.equals(claim);
            } else if (clm instanceof List) {
                return ((List<?>) clm).contains(claim);
            } else {
                return false;
            }
        };
    }   
    
}