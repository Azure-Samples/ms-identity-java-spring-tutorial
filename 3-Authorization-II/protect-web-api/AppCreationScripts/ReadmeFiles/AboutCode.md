## About the code
This sample demonstrates how to use [Azure AD Spring Boot Starter client library for Java](https://docs.microsoft.com/java/api/overview/azure/active-directory-spring-boot-starter-readme?view=azure-java-stable) to sign in users into your Azure AD tenant. It also makes use of **Spring Oauth2 Client** and **Spring Web** boot starters. It uses claims from **ID Token** obtained from Azure Active Directory to display details of the signed-in user.

### Project Initialization

To make your own Spring boot resource API, create a new Java Maven project and copy the `pom.xml` file and the `src` folder within the `resource-api` directory of this repository.

### Access Token Claims

To extract token details, make use of Spring Security's `AuthenticationPrincipal` and `OidcUser` object in a request mapping. See the [Sample Controller](./resources/src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapi/SampleController.java) for an example of this app making use of ID Token claims.

```java
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
//...
@GetMapping("/api/date")
@ResponseBody
@PreAuthorize("hasAuthority('SCOPE_access_as_user')")
public String date(BearerTokenAuthentication bearerTokenAuth) {
    OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
    return principal.getAttribute("scp").toString();
}
```

### Protecting routes with AADWebSecurityConfigurerAdapter

By default, this app protects all routes so that only users with a valid access token can access it. To configure your app's specific requirements, extend `AADWebSecurityConfigurationAdapter` in one of your classes. For an example, see this app's [SecurityConfig](.resource-api/src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapi/SecurityConfig.java) class.

This app also configures the correct claims validation for the incoming bearer token from the `app-id-uri` and `client-id` property the `application.yml` file.

```java
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends AADResourceServerWebSecurityConfigurerAdapter {
    /**
     * Add configuration logic as needed.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests((requests) -> requests.anyRequest().authenticated());
    }
}
```