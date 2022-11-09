
[CmdletBinding()]
param(    
    [Parameter(Mandatory=$False, HelpMessage='Tenant ID (This is a GUID which represents the "Directory ID" of the AzureAD tenant into which you want to create the apps')]
    [string] $tenantId,
    [Parameter(Mandatory=$False, HelpMessage='Azure environment to use while running the script. Default = Global')]
    [string] $azureEnvironmentName
)


Function Cleanup
{
    if (!$azureEnvironmentName)
    {
        $azureEnvironmentName = "Global"
    }

    <#
    .Description
    This function removes the Azure AD applications for the sample. These applications were created by the Configure.ps1 script
    #>

    # $tenantId is the Active Directory Tenant. This is a GUID which represents the "Directory ID" of the AzureAD tenant 
    # into which you want to create the apps. Look it up in the Azure portal in the "Properties" of the Azure AD. 

    # Connect to the Microsoft Graph API
    Write-Host "Connecting to Microsoft Graph"


    if ($tenantId -eq "") 
    {
        Connect-MgGraph -Scopes "Application.ReadWrite.All" -Environment $azureEnvironmentName
        $tenantId = (Get-MgContext).TenantId
    }
    else 
    {
        Connect-MgGraph -TenantId $tenantId -Scopes "Application.ReadWrite.All" -Environment $azureEnvironmentName
    }
    
    # Removes the applications
    Write-Host "Cleaning-up applications from tenant '$tenantId'"

    Write-Host "Removing 'service' (resource-api) if needed"
    try
    {
        Get-MgApplication -Filter "DisplayName eq 'resource-api'" | ForEach-Object {Remove-MgApplication -ApplicationId $_.Id }
    }
    catch
    {
        $message = $_
        Write-Warning $Error[0]
        Write-Host "Unable to remove the application 'resource-api'. Error is $message. Try deleting manually." -ForegroundColor White -BackgroundColor Red
    }

    Write-Host "Making sure there are no more (resource-api) applications found, will remove if needed..."
    $apps = Get-MgApplication -Filter "DisplayName eq 'resource-api'" | Format-List Id, DisplayName, AppId, SignInAudience, PublisherDomain
    
    if ($apps)
    {
        Remove-MgApplication -ApplicationId $apps.Id
    }

    foreach ($app in $apps) 
    {
        Remove-MgApplication -ApplicationId $app.Id
        Write-Host "Removed resource-api.."
    }

    # also remove service principals of this app
    try
    {
        Get-MgServicePrincipal -filter "DisplayName eq 'resource-api'" | ForEach-Object {Remove-MgServicePrincipal -ServicePrincipalId $_.Id -Confirm:$false}
    }
    catch
    {
        $message = $_
        Write-Warning $Error[0]
        Write-Host "Unable to remove ServicePrincipal 'resource-api'. Error is $message. Try deleting manually from Enterprise applications." -ForegroundColor White -BackgroundColor Red
    }
    Write-Host "Removing 'client' (webapp) if needed"
    try
    {
        Get-MgApplication -Filter "DisplayName eq 'webapp'" | ForEach-Object {Remove-MgApplication -ApplicationId $_.Id }
    }
    catch
    {
        $message = $_
        Write-Warning $Error[0]
        Write-Host "Unable to remove the application 'webapp'. Error is $message. Try deleting manually." -ForegroundColor White -BackgroundColor Red
    }

    Write-Host "Making sure there are no more (webapp) applications found, will remove if needed..."
    $apps = Get-MgApplication -Filter "DisplayName eq 'webapp'" | Format-List Id, DisplayName, AppId, SignInAudience, PublisherDomain
    
    if ($apps)
    {
        Remove-MgApplication -ApplicationId $apps.Id
    }

    foreach ($app in $apps) 
    {
        Remove-MgApplication -ApplicationId $app.Id
        Write-Host "Removed webapp.."
    }

    # also remove service principals of this app
    try
    {
        Get-MgServicePrincipal -filter "DisplayName eq 'webapp'" | ForEach-Object {Remove-MgServicePrincipal -ServicePrincipalId $_.Id -Confirm:$false}
    }
    catch
    {
        $message = $_
        Write-Warning $Error[0]
        Write-Host "Unable to remove ServicePrincipal 'webapp'. Error is $message. Try deleting manually from Enterprise applications." -ForegroundColor White -BackgroundColor Red
    }
}

# Pre-requisites
if ($null -eq (Get-Module -ListAvailable -Name "Microsoft.Graph.Applications")) {
    Install-Module "Microsoft.Graph.Applications" -Scope CurrentUser 
}

Import-Module Microsoft.Graph.Applications

if ($null -eq (Get-Module -ListAvailable -Name "Microsoft.Graph.Groups")) {
    Install-Module "Microsoft.Graph.Groups" -Scope CurrentUser 
}

Import-Module Microsoft.Graph.Groups

if ($null -eq (Get-Module -ListAvailable -Name "Microsoft.Graph.Users")) {
    Install-Module "Microsoft.Graph.Users" -Scope CurrentUser 
}

Import-Module Microsoft.Graph.Users

$ErrorActionPreference = "Stop"


try
{
    Cleanup -tenantId $tenantId -environment $azureEnvironmentName
}
catch
{
    $_.Exception.ToString() | out-host
    $message = $_
    Write-Warning $Error[0]    
    Write-Host "Unable to register apps. Error is $message." -ForegroundColor White -BackgroundColor Red
}

Write-Host "Disconnecting from tenant"
Disconnect-MgGraph
