package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class MsIdentitySpringBootWebappApplication extends WebSecurityConfigurerAdapter {
	public static void main(String[] args) {
		SpringApplication.run(MsIdentitySpringBootWebappApplication.class, args);
	}

	@Override
    public void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/token_details").authenticated()     // limit token_details page to authenticated users
			.antMatchers("/**").permitAll()                    // allow all other routes.
			.and().oauth2Login().defaultSuccessUrl("/")        // on successful auth, go to /, unless auth was auto-initiated from a protected endpoint.
			.and().logout().logoutUrl("/logout").clearAuthentication(true).logoutSuccessUrl("/"); // set logout url, clear authentication on logout, redirect to /
  }
}
