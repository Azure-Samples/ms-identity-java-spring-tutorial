package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.experimental;

import java.util.ArrayList;
import java.util.Arrays;
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
	private final Collection<OAuth2TokenValidator<T>> tokenValidators;
	
	private final Collection<OAuth2TokenValidator<T>> issuerTokenValidators;
	
	private final Collection<OAuth2TokenValidator<T>> audienceTokenValidators;
	
	private final Collection<OAuth2TokenValidator<T>> extendedTokenValidators;
	
	private final Collection<OAuth2Error> errors = new ArrayList<>();
	/**
	 * Constructs a AADDelegatingOAuth2TokenValidator using the provided validators.
	 * @param tokenValidators 
	 */
	public AADDelegatingOAuth2TokenValidator(Collection<OAuth2TokenValidator<T>> tokenValidators) {
		Assert.notNull(tokenValidators, "tokenValidators cannot be null");
		
		//seperates specific token validators into individual groups for separate validation logic
		this.tokenValidators = new ArrayList<>();
		this.issuerTokenValidators = new ArrayList<OAuth2TokenValidator<T>>();
		this.audienceTokenValidators = new ArrayList<OAuth2TokenValidator<T>>();
		this.extendedTokenValidators = new ArrayList<OAuth2TokenValidator<T>>();
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
	 * Constructs a AADDelegatingOAuth2TokenValidator using the provided validators.
	 * @param tokenValidators 
	 */
	@SafeVarargs
	public AADDelegatingOAuth2TokenValidator(OAuth2TokenValidator<T>... tokenValidators) {
		this(Arrays.asList(tokenValidators));
	}

	@Override
	public OAuth2TokenValidatorResult validate(T token) {
		validateforOne(this.issuerTokenValidators, token);
		validateforOne(this.audienceTokenValidators, token);
		validateforOne(this.extendedTokenValidators, token);
		for (OAuth2TokenValidator<T> validator : this.tokenValidators) {	
			errors.addAll(validator.validate(token).getErrors());
		}
		return OAuth2TokenValidatorResult.failure(errors);
	}
	
	/**
	 * Validates for at least one valid claim among the validators provided
	 * @param validators - list of validators of which only one must be successful
	 * @param token - token to be validated from
	 */
	private void validateforOne(Collection<OAuth2TokenValidator<T>> validators, T token) {
		for (OAuth2TokenValidator<T> validator : validators) {
			OAuth2TokenValidatorResult result = validator.validate(token);
			errors.addAll(result.getErrors());
			if (!result.hasErrors()) {
				errors.clear();
				break;
			}
		}
	}

}
