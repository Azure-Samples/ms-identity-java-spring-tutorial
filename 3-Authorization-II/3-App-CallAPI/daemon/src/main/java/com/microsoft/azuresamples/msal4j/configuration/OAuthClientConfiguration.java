package com.microsoft.azuresamples.msal4j.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.JwtBearerOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultJwtBearerTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;


@Configuration
public class OAuthClientConfiguration {
	
    @Bean
    ClientRegistration ADClientRegistration(
            @Value("${spring.security.oauth2.client.provider.azure.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.azure.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.azure.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.azure.scope}") String scope,
            @Value("${spring.security.oauth2.client.registration.azure.authorization-grant-type}") String authorizationGrantType,
            @Value("${spring.security.oauth2.client.registration.azure.client-authentication-method}") String authMethod
    ) {
        return ClientRegistration
                .withRegistrationId("azure")
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .scope(scope)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .clientAuthenticationMethod(new ClientAuthenticationMethod(authMethod))
                .build();
    }
    
    @Bean
    public ClientRegistrationRepository ADclientRegistrationRepository(ClientRegistration ADClientRegistration) {
        return new InMemoryClientRegistrationRepository(ADClientRegistration);
    }
   
    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
    
    
    
    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager (
            ClientRegistrationRepository ADclientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        

    	OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
    	
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                		ADclientRegistrationRepository, authorizedClientService);
        
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
	
}
