package kueres.auth;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * 
 * Configuration for the connection between the Spring Boot server and the Keycloak service.
 *
 * @author Tim Engbrocks, tim.engbrocks@student.kit.edu
 * @version 1.0
 * @since Feb 22, 2021
 *
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class KeycloakConfiguration extends KeycloakWebSecurityConfigurerAdapter {
	
	/**
	 * All requests must be authenticated.
	 * Disables csrf protection.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		super.configure(http);
		http.authorizeRequests().anyRequest().authenticated();
		http.csrf().disable();
		
	}
	
	/**
	 * Register Keycloak as the global authentication provider.
	 * @param auth - the AuthenticationManagerBuilder
	 * @throws Exception when Keycloak could not be registered as the global authentication provider.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		
		auth.authenticationProvider(keycloakAuthenticationProvider);
		
	}
	
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
		
	}
	
	/**
	 * Load Keycloak config from application.properties instead of keycloak.json.
	 * @return The KeycloakConfigResolver.
	 */
	@Bean
	public KeycloakConfigResolver keycloakConfigResolver() {
		
	    return new KeycloakSpringBootConfigResolver();
	    
	}
	
}
