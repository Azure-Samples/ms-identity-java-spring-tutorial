### Obtain an Access Token for your App using Client Credentials

Since daemon apps do not allow for user interface, there is no way for a user to sign in and authorize themselves as they would in a client app.

Fortunatly Spring OAuth supports an On-Behalf-Of flow that allows apps to be granted permission on behalf of the user to access protected resources. For this, see the class app's [OAuthClientConfiguration](.daemon/src/main/java/com/microsoft/azuresamples/msal4j/configuration/OAuthClientConfiguration.java) class.

This class configures an AuthorizedClientServiceOAuth2AuthorizedClientManager with the nessecary parameters to obtain an access token from authorized provider to then be used to call protect resources

```java
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