---
page_type: sample
name: Enable your Java Spring Boot daemon to call a protected Web API with its own identity
description: Enable your Java Spring Boot daemon to call a protected Web API with its own identity
languages:
 - java
products:
 - azure-active-directory
 - msal-java
urlFragment: ms-identity-java-spring-tutorial
extensions:
- services: ms-identity
- platform: Java
- endpoint: AAD v2.0
- level: 200
- client: Java Spring Daemon
- service: Java Spring Web API
---

# Enable your Java Spring Boot daemon to call a protected Web API with its own identity


* [Overview](#overview)
* [Scenario](#scenario)
* [Prerequisites](#prerequisites)
* [Setup the sample](#setup-the-sample)
* [Explore the sample](#explore-the-sample)
* [Troubleshooting](#troubleshooting)
* [How to deploy this sample to Azure](#how-to-deploy-this-sample-to-azure)
* [Next Steps](#next-steps)
* [Contributing](#contributing)
* [Learn More](#learn-more)

## Overview

This sample demonstrates a Java Spring Daemon calling a Java Spring Web API that is secured using Azure AD.

## Scenario

This sample demonstrates a Java Spring Daemon calling a Java Spring Web API that is secured using Azure AD.

 
1. The Daemon uses Spring OAuth 2.0 to aquire an [Access Token](https://aka.ms/access-tokens) from Azure AD using its own identity (without a user).
1. The Daemon then calls the Web API that is secured using by [Azure AD Spring Boot Starter client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-boot-starter-active-directory) to get the a list of ToDo's, and displays the result. 

![Scenario Image](./AppCreationScripts/ReadmeFiles/topology.png)

## Prerequisites

* [JDK Version 15](https://jdk.java.net/15/). This sample has been developed on a system with Java 15 but may be compatible with other versions.
* [Maven 3](https://maven.apache.org/download.cgi)
* [Java Extension Pack for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) is recommended for running this sample in VSCode.
* An **Azure AD** tenant. For more information, see: [How to get an Azure AD tenant](https://docs.microsoft.com/azure/active-directory/develop/test-setup-environment#get-a-test-tenant)
* A user account in your **Azure AD** tenant.
>This sample will not work with a **personal Microsoft account**. If you're signed in to the [Azure portal](https://portal.azure.com) with a personal Microsoft account and have not created a user account in your directory before, you will need to create one before proceeding.

## Setup the sample

### Step 1: Clone or download this repository

From your shell or command line:

```console
git clone https://github.com/Azure-Samples/ms-identity-java-spring-tutorial.git
```

or download and extract the repository *.zip* file.

> :warning: To avoid path length limitations on Windows, we recommend cloning into a directory near the root of your drive.

### Step 2: Install project dependencies
// Java installation steps
// Java installation steps

### Step 3: Register the sample application(s) in your tenant

There are two projects in this sample. Each needs to be separately registered in your Azure AD tenant. To register these projects, you can:

- follow the steps below for manually register your apps
- or use PowerShell scripts that:
  - **automatically** creates the Azure AD applications and related objects (passwords, permissions, dependencies) for you.
  - modify the projects' configuration files.

  <details>
   <summary>Expand this section if you want to use this automation:</summary>

    > :warning: If you have never used **Microsoft Graph PowerShell** before, we recommend you go through the [App Creation Scripts Guide](./AppCreationScripts/AppCreationScripts.md) once to ensure that your environment is prepared correctly for this step.
  
    1. On Windows, run PowerShell as **Administrator** and navigate to the root of the cloned directory
    1. In PowerShell run:

       ```PowerShell
       Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process -Force
       ```

    1. Run the script to create your Azure AD application and configure the code of the sample application accordingly.
    1. For interactive process -in PowerShell, run:

       ```PowerShell
       cd .\AppCreationScripts\
       .\Configure.ps1 -TenantId "[Optional] - your tenant id" -AzureEnvironmentName "[Optional] - Azure environment, defaults to 'Global'"
       ```

    > Other ways of running the scripts are described in [App Creation Scripts guide](./AppCreationScripts/AppCreationScripts.md). The scripts also provide a guide to automated application registration, configuration and removal which can help in your CI/CD scenarios.

  </details>

#### Choose the Azure AD tenant where you want to create your applications

To manually register the apps, as a first step you'll need to:

1. Sign in to the [Azure portal](https://portal.azure.com).
1. If your account is present in more than one Azure AD tenant, select your profile at the top right corner in the menu on top of the page, and then **switch directory** to change your portal session to the desired Azure AD tenant.

#### Register the service app (java-spring-webapi-auth)

1. Navigate to the [Azure portal](https://portal.azure.com) and select the **Azure Active Directory** service.
1. Select the **App Registrations** blade on the left, then select **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
    1. In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-spring-webapi-auth`.
    1. Under **Supported account types**, select **Accounts in this organizational directory only**
    1. Select **Register** to create the application.
1. In the **Overview** blade, find and note the **Application (client) ID**. You use this value in your app's configuration file(s) later in your code.
1. In the app's registration screen, select the **Expose an API** blade to the left to open the page where you can publish the permission as an API for which client applications can obtain [access tokens](https://aka.ms/access-tokens) for. The first thing that we need to do is to declare the unique [resource](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow) URI that the clients will be using to obtain access tokens for this API. To declare an resource URI(Application ID URI), follow the following steps:
    1. Select **Set** next to the **Application ID URI** to generate a URI that is unique for this app.
    1. For this sample, accept the proposed Application ID URI (`api://{clientId}`) by selecting **Save**.
        > :information_source: Read more about Application ID URI at [Validation differences by supported account types (signInAudience)](https://docs.microsoft.com/azure/active-directory/develop/supported-accounts-validation).
    
##### Publish Delegated Permissions

1. All APIs must publish a minimum of one [scope](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow#request-an-authorization-code), also called [Delegated Permission](https://docs.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent#permission-types), for the client apps to obtain an access token for a *user* successfully. To publish a scope, follow these steps:
1. Select **Add a scope** button open the **Add a scope** screen and Enter the values as indicated below:
    1. For **Scope name**, use `ToDoList.Read`.
    1. Select **Admins and users** options for **Who can consent?**.
    1. For **Admin consent display name** type in *Read users ToDo list using the 'java-spring-webapi-auth'*.
    1. For **Admin consent description** type in *Allow the app to read the user's ToDo list using the 'java-spring-webapi-auth'*.
    1. For **User consent display name** type in *Read your ToDo list items via the 'java-spring-webapi-auth'*.
    1. For **User consent description** type in *Allow the app to read your ToDo list items via the 'java-spring-webapi-auth'*.
    1. Keep **State** as **Enabled**.
    1. Select the **Add scope** button on the bottom to save this scope.
    > Repeat the steps above for another scope named **ToDoList.ReadWrite**
1. Select the **Manifest** blade on the left.
    1. Set `accessTokenAcceptedVersion` property to **2**.
    1. Select on **Save**.

> :information_source:  Follow [the principle of least privilege when publishing permissions](https://learn.microsoft.com/security/zero-trust/develop/protected-api-example) for a web API.

##### Publish Application Permissions

1. All APIs should publish a minimum of one [App role for applications](https://docs.microsoft.com/azure/active-directory/develop/howto-add-app-roles-in-azure-ad-apps#assign-app-roles-to-applications), also called [Application Permission](https://docs.microsoft.com/azure/active-directory/develop/v2-permissions-and-consent#permission-types), for the client apps to obtain an access token as *themselves*, i.e. when they are not signing-in a user. **Application permissions** are the type of permissions that APIs should publish when they want to enable client applications to successfully authenticate as themselves and not need to sign-in users. To publish an application permission, follow these steps:
1. Still on the same app registration, select the **App roles** blade to the left.
1. Select **Create app role**:
    1. For **Display name**, enter a suitable name for your application permission, for instance **ToDoList.Read.All**.
    1. For **Allowed member types**, choose **Application** to ensure other applications can be granted this permission.
    1. For **Value**, enter **ToDoList.Read.All**.
    1. For **Description**, enter *Allow the app to read every user's ToDo list using the 'java-spring-webapi-auth'*.
    1. Select **Apply** to save your changes.

    > Repeat the steps above for another app permission named **ToDoList.ReadWrite.All**

##### Configure Optional Claims

1. Still on the same app registration, select the **Token configuration** blade to the left.
1. Select **Add optional claim**:
    1. Select **optional claim type**, then choose **Access**.
    1. Select the optional claim **idtyp**.
    > Indicates token type. This claim is the most accurate way for an API to determine if a token is an app token or an app+user token. This is not issued in tokens issued to users.
    1. Select **Add** to save your changes.

##### Configure the service app (java-spring-webapi-auth) to use your app registration

Open the project in your IDE (like Visual Studio or Visual Studio Code) to configure the code.

> In the steps below, "ClientID" is the same as "Application ID" or "AppId".

1. Open the `resource-api\src\main\resources\application.yml` file.
1. Find the key `Enter_Your_Tenant_ID_Here` and replace the existing value with your Azure AD tenant/directory ID.
1. Find the key `Enter_Your_WebAPI_Client_ID_Here` and replace the existing value with the application ID (clientId) of `java-spring-webapi-auth` app copied from the Azure portal.

#### Register the client app (java-spring-daemon-auth)

1. Navigate to the [Azure portal](https://portal.azure.com) and select the **Azure Active Directory** service.
1. Select the **App Registrations** blade on the left, then select **New registration**.
1. In the **Register an application page** that appears, enter your application's registration information:
    1. In the **Name** section, enter a meaningful application name that will be displayed to users of the app, for example `java-spring-daemon-auth`.
    1. Under **Supported account types**, select **Accounts in this organizational directory only**
    1. Select **Register** to create the application.
1. In the **Overview** blade, find and note the **Application (client) ID**. You use this value in your app's configuration file(s) later in your code.
1. In the app's registration screen, select the **Certificates & secrets** blade in the left to open the page where you can generate secrets and upload certificates.
1. In the **Client secrets** section, select **New client secret**:
    1. Type a key description (for instance `app secret`).
    1. Select one of the available key durations (**6 months**, **12 months** or **Custom**) as per your security posture.
    1. The generated key value will be displayed when you select the **Add** button. Copy and save the generated value for use in later steps.
    1. You'll need this key later in your code's configuration files. This key value will not be displayed again, and is not retrievable by any other means, so make sure to note it from the Azure portal before navigating to any other screen or blade.
1. Since this app signs-in as itself using the [OAuth 2\.0 client credentials flow](https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-client-creds-grant-flow), we will now proceed to select **application permissions**, which is required by apps authenticating as themselves.
    1. In the app's registration screen, select the **API permissions** blade in the left to open the page where we add access to the APIs that your application needs:
    1. Select the **Add a permission** button and then:
    1. Ensure that the **My APIs** tab is selected.
    1. In the list of APIs, select the API `java-spring-webapi-auth`.
        1. We will select “Application permissions”, which should be the type of permissions that apps should use when they are authenticating just as themselves and not signing-in users. 
   1. In the **Application permissions** section, select the **ToDoList.Read.All**, **ToDoList.ReadWrite.All** in the list. Use the search box if necessary.
    1. Select the **Add permissions** button at the bottom.
1. At this stage, the permissions are assigned correctly but since the client app does not allow users to interact, the users' themselves cannot consent to these permissions. To get around this problem, we'd let the [tenant administrator consent on behalf of all users in the tenant](https://docs.microsoft.com/azure/active-directory/develop/v2-admin-consent). Select the **Grant admin consent for {tenant}** button, and then select **Yes** when you are asked if you want to grant consent for the requested permissions for all accounts in the tenant. You need to be a tenant admin to be able to carry out this operation.

##### Configure the client app (java-spring-daemon-auth) to use your app registration

Open the project in your IDE (like Visual Studio or Visual Studio Code) to configure the code.

> In the steps below, "ClientID" is the same as "Application ID" or "AppId".

1. Open the `daemon\src\main\resources\application.yml` file.
1. Find the key `Enter_Your_Tenant_ID_Here` and replace the existing value with your Azure AD tenant/directory ID.
1. Find the key `Enter_Your_Client_ID_Here` and replace the existing value with the application ID (clientId) of `java-spring-daemon-auth` app copied from the Azure portal.
1. Find the key `Enter_Your_Client_Secret_Here` and replace the existing value with the generated secret that you saved during the creation of `java-spring-daemon-auth` copied from the Azure portal.
1. Find the key `Enter_Your_WebAPI_Client_ID_Here` and replace the existing value with the application ID (clientId) of `java-spring-webapi-auth` app copied from the Azure portal.

### Step 4: Running the sample

1. Open a terminal or the integrated VSCode terminal.
1. In the root directory as this project, run `cd 3-Authorization-II\3-App-CallAPI\resource-api`.
1. run `mvn clean compile spring-boot:run`.
1. Open a terminal or the integrated VSCode terminal.
1. In the root directory as this project, run `cd 3-Authorization-II\3-App-CallAPI\daemon`.
1. run `mvn clean compile spring-boot:run`.

## Explore the sample
![Experience](./AppCreationScripts/ReadmeFiles/app.png)

- Note the output printed in the console log
- Each line of output is a different API call to your protected API
## Troubleshooting

<details>
	<summary>Expand for troubleshooting info</summary>

Use [Stack Overflow](http://stackoverflow.com/questions/tagged/msal) to get support from the community. Ask your questions on Stack Overflow first and browse existing issues to see if someone has asked your question before.
Make sure that your questions or comments are tagged with [`azure-active-directory` `msal-java` `ms-identity` `msal`].
If you find a bug in the sample, raise the issue on [GitHub Issues](../../../issues).

</details>

### Obtain an Access Token for your App using Client Credentials

As a daemon application, this sample creates a **confidential client application** to obtain an access token that is used to call your protected web API. The confidential client application is configured in the [OAuthClientConfiguration](".daemon\src\main\java\com\microsoft\azuresamples\msal4j\configuration\OAuthClientConfiguration.java") class.

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

Azure protected web API's must perform both signature and claim validation on the incoming access tokens to authorize a call. For extended claim validation, refer to this web API's [AppSecurityConfig](.resource-api/src/main/java/com/microsoft/azuresamples/msal4j/msidentityspringbootwebapi/AppSecurityConfig.java) class.

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
## How to deploy this sample to Azure

### Deploying web API to Azure App Services

There is one web API in this sample. To deploy it to **Azure App Services**, you'll need to:

* create an **Azure App Service**
* publish the projects to the **App Services**

> :warning: Please make sure that you have not switched on the *[Automatic authentication provided by App Service](https://docs.microsoft.com/en-us/azure/app-service/scenario-secure-app-authentication-app-service)*. It interferes the authentication code used in this code example.

#### Publishing using Visual Studio

##### Step 1: Create a new app on Azure App Service

1. Install the Visual Studio Code extension [Azure App Service](https://marketplace.visualstudio.com/items?itemName=ms-azuretools.vscode-azureappservice).
1. Open the Azure App Service Extension and navigate to the App Services tab located under Resources
1. Right-click on the App Services tab and select "Create New Web App..."
1. Enter a globally unique name for your web app (e.g. `java-spring-webapi-auth`) and press enter. Make a note of this name. If you chose `java-spring-webapi-auth` for your app name, your app's domain name will be `https://java-spring-webapi-auth.azurewebsites.net`
1. Select `Java 11` for your runtime stack.
1. Select `Java SE (Embedded Web Server)` for your Java web server stack.
1. If you are asked for an OS, choose `Linux`.
1. Select `Basic(B1)` or any other option for your pricing tier.

##### Step 2: Publish your files for (java-spring-webapi-auth)

1. Right-click on your newly created web app and select "Deploy to Web App"
1. Progress through the flow until deployment begins, a prompt should appear notifying you that deployment is in progress
1. A prompt should appear after a few minutes stating that deployment is complete.

#### Publishing using Maven

##### Step 1: Set up the configuration for the azure webapp maven plugin

1. Open a terminal window in the base directory of your Java Spring project and enter the following command:

```console
        mvn com.microsoft.azure:azure-webapp-maven-plugin:2.5.0:config
```

1. If you have not made a Java SE Web App in your subscription for this project, select the option, `create`, be pressing enter
1. When prompted for an OS option, select `Linux` by pressing enter.
1. When prompted for a javaVersion, select `Java 11` by entering the associated number for it
1. When prompted for a Pricing Tier, select `B1` or any other option for your pricing tier by entering the associated number for it
1. Finally, press enter on the last prompt to confirm your selections.
1. Open your pom.xml, it should now be updated with build information for deploying to Azure App Services

#### Step 2: Publish your files using Maven (java-spring-webapi-auth)

1. In the terminal window, enter the following command:

```console
        mvn clean package azure-webapp:deploy
```

1. The terminal log should display a successful build after a few minutes stating that the deployment was successful

> :note: The maven plugin uses information from your subscription to deploy to Azure App Services. Deployment is subject to the limitations of your subscription. Modify your pom.xml to meet your subscription limits.


## Next Steps

Learn how to:

* [Enable your Java Spring Boot web app to sign in users with Azure Active Directory and call a protected Web api](https://github.com/Azure-Samples/ms-identity-java-spring-tutorial/tree/spring-3-Auth/3-Authorization-II/2-User-CallAPI)


## Contributing

If you'd like to contribute to this sample, see [CONTRIBUTING.MD](/CONTRIBUTING.md).

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/). For more information, see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Learn More

* [Microsoft identity platform (Azure Active Directory for developers)](https://docs.microsoft.com/azure/active-directory/develop/)
* [Azure AD code samples](https://docs.microsoft.com/azure/active-directory/develop/sample-v2-code)
* [Overview of Microsoft Authentication Library (MSAL)](https://docs.microsoft.com/azure/active-directory/develop/msal-overview)
* [Register an application with the Microsoft identity platform](https://docs.microsoft.com/azure/active-directory/develop/quickstart-register-app)
* [Configure a client application to access web APIs](https://docs.microsoft.com/azure/active-directory/develop/quickstart-configure-app-access-web-apis)
* [Understanding Azure AD application consent experiences](https://docs.microsoft.com/azure/active-directory/develop/application-consent-experience)
* [Understand user and admin consent](https://docs.microsoft.com/azure/active-directory/develop/howto-convert-app-to-be-multi-tenant#understand-user-and-admin-consent)
* [Application and service principal objects in Azure Active Directory](https://docs.microsoft.com/azure/active-directory/develop/app-objects-and-service-principals)
* [Authentication Scenarios for Azure AD](https://docs.microsoft.com/azure/active-directory/develop/authentication-flows-app-scenarios)
* [Building Zero Trust ready apps](https://aka.ms/ztdevsession)
* [National Clouds](https://docs.microsoft.com/azure/active-directory/develop/authentication-national-cloud#app-registration-endpoints)
* [Microsoft Authentication Library for Java (MSAL4J)](https://github.com/AzureAD/microsoft-authentication-library-for-java)
* [MSAL4J Wiki](https://github.com/AzureAD/microsoft-authentication-library-for-java/wiki)
* [Azure Active Directory Spring Boot Starter client library for Java](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-boot-starter-active-directory)
* [Validating Access Tokens](https://docs.microsoft.com/azure/active-directory/develop/access-tokens#validating-tokens)
* [User and application tokens](https://docs.microsoft.com/azure/active-directory/develop/access-tokens#user-and-application-tokens)
* [Validation differences by supported account types](https://docs.microsoft.com/azure/active-directory/develop/supported-accounts-validation)
* [How to manually validate a JWT access token using the Microsoft identity platform](https://github.com/Azure-Samples/active-directory-dotnet-webapi-manual-jwt-validation/blob/master/README.md)
