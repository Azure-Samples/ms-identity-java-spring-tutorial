package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

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
		.antMatchers("/token_details").authenticated() 	//limit token_details page to authenticated users
        .antMatchers("/**").permitAll()					// allow all other endpoints
		.and().oauth2Login().defaultSuccessUrl("/")
		.and().logout().logoutUrl("/logout").clearAuthentication(true).logoutSuccessUrl("/");
  }
}
