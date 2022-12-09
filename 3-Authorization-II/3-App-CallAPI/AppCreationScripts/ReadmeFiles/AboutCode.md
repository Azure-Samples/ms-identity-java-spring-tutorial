### Obtain an Access Token for your App using Client Credentials

As a daemon application, this sample creates a confidential client application to obtain the access token used to call your protected Web APIs. The confidential client application is configured in [OAuthClientConfiguration](".daemon\src\main\java\com\microsoft\azuresamples\msal4j\configuration\OAuthClientConfiguration.java") class.

In this class, we define a ClientRegistration object using information obtained defined in the application.yml file. The ClientRegistration represents a client registered with OAuth2 and holds all the information pertaining to the client
```java
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
```

We use this client registration to create an AuthorizedClientServiceOAuth2AuthorizedClientManager object, which is used by our daemon application to obtain an OAuth2AuthorizedClient.
 ```java
    	OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("azure")
				.principal("Test Tenant")
				.build();
		OAuth2AuthorizedClient authorizedClient = this.authorizedClientServiceAndManager.authorize(authorizeRequest);
```

The OAuth2AuthorizedClient represents a successfully authorized client that houses the access token and refresh token needed to call our protected Web APIs.
```java
		OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
		String accessTokenValue = accessToken.getTokenValue();
		
    	final WebClient apiClient = WebClient.builder()
                .baseUrl(apiAddress)
                .defaultHeader("Authorization", String.format("Bearer %s", accessTokenValue))
                .build();
```

### Validate your Azure access tokens using routes with AADDelegatingOAuth2TokenValidator

While `AADWebSecurityConfigurationAdapter` can be used to protect your routes so only valid users can access it, Azure protected API's require additional claims validation to ensure only valid users are calling you routes. For this, see this app's [AppSecurityConfig](.resource-api/src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapi/AppSecurityConfig.java) class.

This class configures your `AADWebSecurityConfigurationAdapter` with signature validation using `NimbusJwtDecoder` as well as custom validation for the `iss`, `aud`, `nbf`, and `exp` claims using `AADDelegatingOAuth2TokenValidator`

```java
    @Bean
    JwtDecoder jwtDecoder() {
    	String JWKSet = instanceUri + tenantId + "/discovery/v2.0/keys";
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(JWKSet).build();
        nimbusJwtDecoder.setJwtValidator(jwtValidator());
        return nimbusJwtDecoder;
    }
```
Additionally, you may also configure this class to perform custom extended claim validation of the `azp` and `appid` claim to restrict access of your API to only select web applications. 
```java
	/*Extended Validation 
	 * Uncomment to allow user to limit access of API to specific client apps
	 * Add client Id of your client apps to allowedClientApps
	 */
	String[] allowedClientApps = new String[] {""};
	for (int i = 0; i < allowedClientApps.length; i++ ) {
		validators = AADHelpers.AADExtendedValidators(validators, allowedClientApps[i]);
	}
```	