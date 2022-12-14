package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.web;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.util.Assert;


public final class JwtAudienceValidator implements OAuth2TokenValidator<Jwt> {

	private final JwtClaimValidator<Object> validator;

	public JwtAudienceValidator(String audience) {
		Assert.notNull(audience, "audience cannot be null");
		this.validator = new JwtClaimValidator<>(JwtClaimNames.AUD, claimPredicate(audience));
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		Assert.notNull(token, "token cannot be null");
		return this.validator.validate(token);
	}
	
    Predicate<Object> claimPredicate(String claim) {
        return clm -> {
            if (clm == null) {
                return false;
            } else if (clm instanceof String) {
                return clm.equals(claim);
            } else if (clm instanceof List) {
                return ((List<?>) clm).contains(claim);
            } else {
                return false;
            }
        };
    }  

}