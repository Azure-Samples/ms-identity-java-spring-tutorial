package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.experimental;

import java.util.List;

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;

public class AADHelpers {
	/**
	 * Adds validators for issuer, audience, and timestamp to a list of validators
	 * @param validators - to be added validators to
	 * @param issuerUri - used to construct an issuer validator
	 * @param clientId - used to construct an audience validator
	 * @return
	 */
	public static List<OAuth2TokenValidator<Jwt>> AADStandardValidators(List<OAuth2TokenValidator<Jwt>> validators, String issuerUri, String clientId)
	{
        //add validator for ISS claim
        validators.add(new JwtIssuerValidator(issuerUri));
        
        //add validator for AUD claio
        validators.add(new JwtAudienceValidator(clientId));
        
        //add validator for timestamp claims
        validators.add(new JwtTimestampValidator());
        return validators;
	}
	
	/**
	 * Adds validators for azp and appid to a list of validators
	 * @param validators - to be added validators to
	 * @param clientApp - used to construct azp and appid validator
	 * @return
	 */
	public static List<OAuth2TokenValidator<Jwt>> AADExtendedValidators(List<OAuth2TokenValidator<Jwt>> validators, String clientApp)
	{
		//add validator for AZP claim
    	validators.add(new JwtAzpValidator(clientApp));
    	
    	//add valiator for Appid claim
    	validators.add(new JwtAppidValidator(clientApp));
        return validators;
	}
	

}
