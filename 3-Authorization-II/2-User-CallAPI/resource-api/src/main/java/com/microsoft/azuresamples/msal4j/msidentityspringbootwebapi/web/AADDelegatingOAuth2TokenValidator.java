package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.util.Assert;

/**
 * Custom DelegatingOAuth2TokenValidator for AAD tokens
 */
public final class AADDelegatingOAuth2TokenValidator<T extends AbstractOAuth2Token> implements OAuth2TokenValidator<T> {
	
	private final Collection<OAuth2TokenValidator<T>> tokenValidators; //for all general token validation
	
	private final Collection<OAuth2TokenValidator<T>> issuerTokenValidators; //for all ISS token validation
	
	private final Collection<OAuth2TokenValidator<T>> audienceTokenValidators; //for all AUD token validation
	
	private final Collection<OAuth2TokenValidator<T>> extendedTokenValidators; //for all AZP/appId extended token validation
	
	private final Collection<OAuth2Error> errors = new ArrayList<>(); //to store all failed validation results
	
	/**
	 * Constructs a AADDelegatingOAuth2TokenValidator using the provided validators.
	 * @param tokenValidators that will be used to validate individual token claims
	 */
	public AADDelegatingOAuth2TokenValidator(Collection<OAuth2TokenValidator<T>> tokenValidators) {
		
		//checks if tokenValidators is empty
		Assert.notNull(tokenValidators, "tokenValidators cannot be null");
		
		//Separates specific tokenValidators into individual groups for separate validation logic
		this.tokenValidators = new ArrayList<>(); //for non-custom claim validations
		this.issuerTokenValidators = new ArrayList<OAuth2TokenValidator<T>>(); //for custom ISS claim validations
		this.audienceTokenValidators = new ArrayList<OAuth2TokenValidator<T>>(); //for custom AUD claim validation
		this.extendedTokenValidators = new ArrayList<OAuth2TokenValidator<T>>(); //for custom AZP/appId extended claim validation 
		
		//loops through the tokenValidators and seperates them based off of their class
		for (OAuth2TokenValidator<T> validator : tokenValidators) {	
			if (validator instanceof JwtIssuerValidator) {
				this.issuerTokenValidators.add(validator);
			}
			else if (validator instanceof JwtAudienceValidator) {
				this.audienceTokenValidators.add(validator);
			}
			else if (validator instanceof JwtAzpValidator || validator instanceof JwtAppidValidator) {
				this.extendedTokenValidators.add(validator);
			}
			else {
				this.tokenValidators.add(validator);
			}
		}
	}

	/**
	 * Overrides existing validate logic with custom logic for validating claims
	 * @param token to be validated by your tokenValidators
	 */
	@Override
	public OAuth2TokenValidatorResult validate(T token) {
		
		//checks that the ISS claim matches at least 1 of the provided ISS validators instead of for all
		validateforOne(this.issuerTokenValidators, token);
		
		//checks that the AUD claim matches at least 1 of the provided ISS validators instead of for all
		validateforOne(this.audienceTokenValidators, token);
		
		//checks that the AZP/appId claim matches at least 1 of the provided ISS validators instead of for all
		validateforOne(this.extendedTokenValidators, token);
		
		//normal logic for the remaining token validation
		for (OAuth2TokenValidator<T> validator : this.tokenValidators) {	
			errors.addAll(validator.validate(token).getErrors());
		}
		
		//returns the result of token validation
		return OAuth2TokenValidatorResult.failure(errors);
	}
	
	/**
	 * Validates for at least one valid claim among the validators provided
	 * @param validators - list of validators of which only one must be successful
	 * @param token - token to be validated from
	 */
	private void validateforOne(Collection<OAuth2TokenValidator<T>> validators, T token) {
		
		//loops through provided token validators
		for (OAuth2TokenValidator<T> validator : validators) {
			
			//validates the token and adds all errors to the error total
			OAuth2TokenValidatorResult result = validator.validate(token);
			errors.addAll(result.getErrors());
			
			//if one validation is successful, clears all errors and immediately stops checking further
			if (!result.hasErrors()) {
				errors.clear();
				break;
			}
		}
	}

}
