---
page_type: sample
languages:
  - java
products:
  - azure
  - msal-java
  - azure-active-directory
  - microsoft-identity-platform  
name: Enable your Java Spring Boot web app to sign in users and call Microsoft Graph with the Microsoft identity platform
urlFragment: ms-identity-java-spring-tutorial
description: "This sample demonstrates a Java Spring MVC web app that signs in users and calls Microsoft Graph"
---
# Enable your Java Spring Boot web app to sign in users and call Microsoft Graph with the Microsoft identity platform

 1. [Overview](#overview)
 1. [Scenario](#scenario)
 1. [Contents](#contents)
 1. [Prerequisites](#prerequisites)
 1. [Setup](#setup)
 1. [Registration](#registration)
 1. [Running the sample](#running-the-sample)
 1. [Explore the sample](#explore-the-sample)
 1. [About the code](#about-the-code)
 1. [Deployment](#deployment)
 1. [More information](#more-information)
 1. [Community Help and Support](#community-help-and-support)
 1. [Contributing](#contributing)

![Build badge](https://identitydivision.visualstudio.com/_apis/public/build/definitions/a7934fdd-dcde-4492-a406-7fad6ac00e17/<BuildNumber>/badge)

## Overview

This sample demonstrates a Java Spring Boot web app that signs in users and obtains an access token for calling [Microsoft Graph](https://docs.microsoft.com/graph/overview). It uses the [Azure AD Spring Boot Starter client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-boot-starter-active-directory) for authentication, authorization, and token acquisition. It leverages [Microsoft Graph SDK for Java](https://github.com/microsoftgraph/msgraph-sdk-java) to obtain data from Graph.

![Overview](./ReadmeFiles/topology.png)

## Scenario

1. This Java Spring MVC web app leverages the **Azure AD Spring Boot Starter client library for Java** to obtain an [Access Token](https://docs.microsoft.com/azure/active-directory/develop/access-tokens) for [Microsoft Graph](https://docs.microsoft.com/graph/overview) from **Azure AD**.
1. The **Access Token** proves that the user is authorized to access the Microsoft Graph API endpoint as defined in the scope.

## Contents

> Give a high-level folder structure of the sample.

| File/folder       | Description                                |
|-------------------|--------------------------------------------|
| `CHANGELOG.md`    | List of changes to the sample.             |
| `CONTRIBUTING.md` | Guidelines for contributing to the sample. |
| `LICENSE`         | The license for the sample.                |

## Prerequisites

- [JDK Version 15](https://jdk.java.net/15/). This sample has been developed on a system with Java 15 but may be compatible with other versions.
- [Maven 3](https://maven.apache.org/download.cgi)
- [Java Extension Pack for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) for Visual Studio Code is recommended for running this sample VSCode.
- An **Azure AD** tenant. For more information see: [How to get an Azure AD tenant](https://docs.microsoft.com/azure/active-directory/develop/quickstart-create-new-tenant)
- A user account in your **Azure AD** tenant. This sample will not work with a **personal Microsoft account**. Therefore, if you signed in to the [Azure portal](https://portal.azure.com) with a personal account and have never created a user account in your directory before, you need to do that now.

## Setup

### Step 1: Clone or download this repository

From your shell or command line:

```console
    git clone https://github.com/Azure-Samples/ms-identity-java-spring-tutorial.git
    cd ms-identity-spring-tutorial
    cd 2-Authorization-I/call-graph
```

or download and extract the repository .zip file.

> :warning: To avoid path length limitations on Windows, we recommend cloning into a directory near the root of your drive.

### Register the sample application(s) with your Azure Active Directory tenant

There is one project in this sample. To register it, you can:

- follow the steps below for manually register your apps
- or use PowerShell scripts that:
  - **automatically** creates the Azure AD applications and related objects (passwords, permissions, dependencies) for you.
  - modify the projects' configuration files.

<details>
  <summary>Expand this section if you want to use this automation:</summary>

> :warning: If you have never used **Azure AD Powershell** before, we recommend you go through the [App Creation Scripts](./AppCreationScripts/AppCreationScripts.md) once to ensure that your environment is prepared correctly for this step.

1. On Windows, run PowerShell as **Administrator** and navigate to the root of the cloned directory
1. If you have never used Azure AD Powershell before, we recommend you go through the [App Creation Scripts](./AppCreationScripts/AppCreationScripts.md) once to ensure that your environment is prepared correctly for this step.
1. In PowerShell run:

   ```PowerShell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process -Force
   ```

1. Run the script to create your Azure AD application and configure the code of the sample application accordingly.
1. In PowerShell run:

   ```PowerShell
   cd .\AppCreationScripts\
   .\Configure.ps1
   ```

   > Other ways of running the scripts are described in [App Creation Scripts](./AppCreationScripts/AppCreationScripts.md)
   > The scripts also provide a guide to automated application registration, configuration and removal which can help in your CI/CD scenarios.

</details>

### Choose the Azure AD tenant where you want to create your applications

As a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com).
1. If your account is present in more than one Azure AD tenant, select your profile at the top right corner in the menu on top of the page, and then **switch directory** to change your portal session to the desired Azure AD tenant.

### Register the webApp app (java-spring-webapp-call-graph)

1. Navigate to the [Azure portal](https://portal.azure.com) and select the **Azure AD** service.
1. Select the **App Registrations** blade on the left, then select **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
   - In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-spring-webapp-call-graph`.
   - Under **Supported account types**, select **Accounts in this organizational directory only**.
   - In the **Redirect URI (optional)** section, select **Web** in the combo-box and enter the following redirect URI: `http://localhost:8080/login/oauth2/code/azure`.
1. Select **Register** to create the application.
1. In the app's registration screen, find and note the **Application (client) ID**. You use this value in your app's configuration file(s) later in your code.
1. Select **Save** to save your changes.
1. In the app's registration screen, select the **Certificates & secrets** blade in the left to open the page where we can generate secrets and upload certificates.
1. In the **Client secrets** section, select **New client secret**:
   - Type a key description (for instance `app secret`),
   - Select one of the available key durations (**In 1 year**, **In 2 years**, or **Never Expires**) as per your security posture.
   - The generated key value will be displayed when you select the **Add** button. Copy the generated value for use in the steps later.
   - You'll need this key later in your code's configuration files. This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Azure portal before navigating to any other screen or blade.
1. In the app's registration screen, click on the **API permissions** blade in the left to open the page where we add access to the Apis that your application needs.
   - Click the **Add permissions** button and then,
   - Ensure that the **Microsoft APIs** tab is selected.
   - In the *Commonly used Microsoft APIs* section, click on **Microsoft Graph**
   - In the **Delegated permissions** section, select the **User.Read** in the list. Use the search box if necessary.
   - Click on the **Add permissions** button in the bottom.

#### Configure the webApp app (java-spring-webapp-call-graph) to use your app registration

Open the project in your IDE (Visual Studio Code or IntelliJ IDEA) to configure the code.

> In the steps below, "ClientID" is the same as "Application ID" or "AppId".

1. Open the `src\main\resources\application.properties` file.
1. Find the key `Enter_Your_Tenant_ID_Here` and replace the existing value with your Azure AD tenant ID.
1. Find the key `Enter_Your_Client_ID_Here` and replace the existing value with the application ID (clientId) of `java-spring-webapp-call-graph` app copied from the Azure portal.
1. Find the key `Enter_Your_Client_Secret_Here` and replace the existing value with the key you saved during the creation of `java-spring-webapp-call-graph` copied from the Azure portal.

## Running the sample

To run the sample in Visual Studio Code, ensure that you have installed the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

1. Open the Explorer window in VS Code (shortcut: ctrl+shift+E).
1. Expand the `JAVA PROJECTS` blade.
1. Click the run button next to `ms-identity-spring-boot-webapp`.
1. Open your browser and navigate to `http://localhost:8080`.

## Explore the sample

- Note the signed-in or signed-out status displayed at the center of the screen.
- Click the context-sensitive button at the top right (it will read `Sign In` on first run)
  - Alternatively, click the link to `token details` or `call graph`. Since this is a protected page that requires authentication, you'll be automatically redirected to the sign-in page.
- Follow the instructions on the next page to sign in with an account in the Azure AD tenant.
- On the consent screen, note the scopes that are being requested.
- Upon successful completion of the sign-in flow, you should be redirected to the home page (`sign in status`), `token details` page, or `call graph` page, depending on which button you opted to use for signing in.
- Note the context-sensitive button now says `Sign out` and displays your username to its left.
- If you are on the home page, you'll see an option to click **ID Token Details**: click it to see some of the ID token's decoded claims.
- Click the **Call Graph** button to make a call to Microsoft Graph's [/me endpoint](https://docs.microsoft.com/graph/api/user-get?view=graph-rest-1.0&tabs=java#example-2-signed-in-user-request) endpoint and see a selection of user details obtained.
- You can also use the button on the top right to sign out. The status page will reflect this.

> :information_source: Did the sample not work for you as expected? Did you encounter issues trying this sample? Then please reach out to us using the [GitHub Issues](../../../../issues) page.

## We'd love your feedback!

Were we successful in addressing your learning objective? Consider taking a moment to [share your experience with us]([Enter_Survey_Form_Link](https://forms.office.com/r/iTZtCTrZrH).

## About the code

This sample demonstrates how to use **Azure AD Spring Boot Starter client library for Java** to sign in users into your Azure AD tenant. It also makes use of **Spring Oauth2 Client** and **Spring Web**. It uses claims from **ID Token** obtained from Azure Active Directory to display details of the signed-in user.

### Project Initialization

If you'd like to create a project like this from scratch, you may use [Spring Initializer](start.spring.io):

- For **Project**, select `Maven Project`
- For **Language**, select `Java`
- For **Spring Boot**, select version `2.3.9`
- For **Packaging**, select `Jar`
- For **Java** select version `11`
- For **Dependencies**, add the following:
  - Azure Active Directory
  - Spring Oauth2 Client
  - Spring Web

With this configuration, you should be able to download a functional application.

This app also serves `.jsp` pages, makes use of `JSTL` tags and `Spring Security` tags in the UI. Your design considerations may vary, but if you'd like to use this setup, you must also add the following dependencies to your `pom.xml` file. Make sure that the `spring-security-taglibs` and `tomcat-jasper` versions you add to your `pom.xml` file correspond to your exact `Spring Security` and `Tomcat` versions, respectively.

```xml
    <!-- Spring Security Taglibs. Match version to YOUR version of Spring Security -->
   <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-taglibs</artifactId>
      <version>5.3.8.RELEASE</version>
   </dependency>
   <!-- For JSTL on frontend pages -->
      <dependency>
         <groupId>jstl</groupId>
         <artifactId>jstl</artifactId>
         <version>1.2</version>
      </dependency>
   <!-- JSP. Match version to YOUR version of Tomcat -->
   <dependency>
      <groupId>org.apache.tomcat</groupId>
      <artifactId>tomcat-jasper</artifactId>
      <version>9.0.43</version>
   </dependency>
```

### ID Token Claims

To extract token details, make use of Spring Security's `AuthenticationPrincipal` and `OidcUser` object in a request mapping. See the [Sample Controller](./src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapp/SampleController.java) for an example of this app making use of ID Token claims.

```java
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//...
@GetMapping(path = "/some_path")
public String tokenDetails(@AuthenticationPrincipal OidcUser principal) {
    Map<String, Object> claims = principal.getIdToken().getClaims();
}
```

### Sign-in and sign-out links

To sign in, you must make a request to the Azure Active Directory sign-in endpoint that is automatically configured by **Azure AD Spring Boot Starter client library for Java**.

```html
<a class="btn btn-success" href="/oauth2/authorization/azure">Sign In</a>
```

To sign out, you must make POST request to the **logout** endpoint.

```HTML
<form:form action="/logout" method="POST">
    <input class="btn btn-warning" type="submit" value="Sign Out" />
</form:form>
```

### Authentication-dependent UI elements

This app has some simple logic in the .jsp pages for determining content to display based on whether the user is authenticated or not. For example, the following Spring Security Tags may be used:

```html
    <sec:authorize access="isAuthenticated()">
        <!-- this content only shows to authenticated users -->
    </sec:authorize>
    <sec:authorize access="isAnonymous()">
        <!-- this content only shows to not-authenticated users -->
    </sec:authorize>
```

### Protecting routes with AADWebSecurityConfigurerAdapter

By default, this app protects the **ID Token Details** page so that only logged-in users can access it. This app looks up this value from the `application.properties` file. To configure your app's specific requirements, extend `AADWebSecurityConfigurationAdapter` in one of your classes. For example see this app's [SecurityConfig](./src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapp/SecurityConfig.java) class.

```java
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends AADWebSecurityConfigurerAdapter{
  @Value( "${app.protect.authenticated}" )
  private String[] protectedRoutes;

    @Override
    public void configure(HttpSecurity http) throws Exception {
    // use required configuration form AADWebSecurityAdapter.configure:
    super.configure(http);
    // add custom configuration:
    http.authorizeRequests()
      .antMatchers(protectedRoutes).authenticated()     // limit these pages to authenticated users (default: /token_details)
      .antMatchers("/**").permitAll();                  // allow all other routes.
    }
}
```

### Call Graph

When the user navigates to `/call_graph`, the application creates an instance of the GraphServiceClient ([Microsoft Graph SDK for Java, v3](https://github.com/microsoftgraph/msgraph-sdk-java)), utilizing an Oauth2AuthorizedClient (`graphAuthorizedClient`) that AAD boot starter has prepared. The app asks the GraphServiceClient to call the  `/me` endpoint and displays details for the currently-signed-in user.

The Oauth2AuthorizedClient must be prepared with the correct scopes (see `application.properties` and the following **Scopes** section). It is used to surface the access token and place it in the Authorization header of GraphServiceClient requests.

```java
//see SampleController.java
@GetMapping(path = "/call_graph")
public String callGraph(@RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
  // See the Utilities.graphUserProperties() method for the full example of the following operation:
  GraphServiceClient graphServiceClient = Utilities.getGraphServiceClient(graphAuthorizedClient);
  User user = graphServiceClient.me().buildRequest().get();
  return user.displayName;
}
```

```ini
# see application.properties file
# Specifies the Microsoft Graph scopes that your app needs access to:
azure.activedirectory.authorization-clients.graph.scopes=https://graph.microsoft.com/User.Read
```

### Scopes

[Scopes](https://docs.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent) tell Azure AD the level of access that the application is requesting. Note the Microsoft Graph scopes requested by this application by referring to [application.properties](./src/main/resources/application.properties).

- By default, this application sets the scopes value to `https://graph.microsoft.com/User.Read`.
- The `User.Read` scope is for accessing the information of the currently-signed-in user from the [/me endpoint](https://graph.microsoft.com/v1.0/me).
- Valid requests to the [/me endpoint](https://graph.microsoft.com/v1.0/me) must contain the `User.Read` scope.

Upon signing in, Azure AD presents a consent dialogue to the user, based on the scopes requested by the application. If the user consents to one or more scopes and obtains a token, the scopes-consented-to are encoded into the resulting **Access Token**.

In this app, the `graphAuthorizedClient` (see previous section) surfaces the **Access Token** that proves which the scopes the user has consented to. The app leverages this to create an instance of `GraphServiceClient` (discussed above) that handles Graph requests.

Using `GraphServiceClient.me().buildRequest().get()`, a request built and made to `https://graph.microsoft.com/v1.0/me`. The **Access Token** is placed in the Authorization header of the request.

## Deployment
<!-- TODO: link to deployment -->
// TODO: link to deployment sample.

## More information

- [Microsoft identity platform (Azure Active Directory for developers)](https://docs.microsoft.com/azure/active-directory/develop/)
- [Overview of Microsoft Authentication Library (MSAL)](https://docs.microsoft.com/azure/active-directory/develop/msal-overview)
- [Quickstart: Register an application with the Microsoft identity platform (Preview)](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
- [Quickstart: Configure a client application to access web APIs (Preview)](https://docs.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
- [Understanding Azure AD application consent experiences](https://docs.microsoft.com/azure/active-directory/develop/application-consent-experience)
- [Understand user and admin consent](https://docs.microsoft.com/azure/active-directory/develop/howto-convert-app-to-be-multi-tenant#understand-user-and-admin-consent)
- [Application and service principal objects in Azure Active Directory](https://docs.microsoft.com/azure/active-directory/develop/app-objects-and-service-principals)
- [National Clouds](https://docs.microsoft.com/azure/active-directory/develop/authentication-national-cloud#app-registration-endpoints)
- [MSAL code samples](https://docs.microsoft.com/azure/active-directory/develop/sample-v2-code)
    // Add MSAL-java docs

For more information about how OAuth 2.0 protocols work in this scenario and other scenarios, see [Authentication Scenarios for Azure AD](https://docs.microsoft.com/azure/active-directory/develop/authentication-flows-app-scenarios).

## Community Help and Support

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community.
Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`azure-active-directory` `azure-ad-b2c` `ms-identity` `adal` `msal`].

If you find a bug in the sample, raise the issue on [GitHub Issues](../../../issues).

To provide feedback on or suggest features for Azure Active Directory, visit [User Voice page](https://feedback.azure.com/forums/169401-azure-active-directory).

## Contributing

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](/CONTRIBUTING.md).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.