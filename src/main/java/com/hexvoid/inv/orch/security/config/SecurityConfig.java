package com.hexvoid.inv.orch.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.hexvoid.inv.orch.security.UsernamePasswordAuthenticationProvider;
import com.hexvoid.inv.orch.security.filter.CorrelationIdFilter;
import com.hexvoid.inv.orch.security.filter.JWTTokenValidatorFilter;
import com.hexvoid.inv.orch.security.service.CustomUserDetailsService;
import com.hexvoid.inv.orch.security.util.SecurityPathRegistry;


@Configuration
public class SecurityConfig {


	private final JWTTokenValidatorFilter jwtTokenValidatorFilter;
	private final CorrelationIdFilter correlationIdFilter;

	SecurityConfig(JWTTokenValidatorFilter jwtTokenValidatorFilter,CorrelationIdFilter correlationIdFilter) {
		this.correlationIdFilter=correlationIdFilter;
		this.jwtTokenValidatorFilter=jwtTokenValidatorFilter;

	}
	/**
	 * Creates a {@link PasswordEncoder} bean using Spring Security's {@code DelegatingPasswordEncoder}.
	 * <p>
	 * This encoder supports multiple encoding formats and automatically chooses the appropriate one
	 * based on a prefix in the stored password (e.g., {bcrypt}).
	 *
	 * <p>It is a secure, flexible approach to password encoding and is the recommended strategy in Spring Security.
	 *
	 * @return a delegating password encoder supporting various encoding schemes
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/**
	 * Defines a custom {@link AuthenticationManager} bean responsible for initiating the
	 * authentication process.
	 *
	 * <p>This implementation wires a custom {@link UsernamePasswordAuthenticationProvider}
	 * using the injected {@link CustomUserDetailsService} (custom implementation of {@link org.springframework.security.core.userdetails.UserDetailsService})
	 * and the {@link PasswordEncoder} bean.
	 *
	 * <p>The {@code ProviderManager} is the default Spring implementation of {@code AuthenticationManager}.
	 * Credentials are preserved after authentication by disabling {@code eraseCredentialsAfterAuthentication}.
	 *
	 * @param customUserDetailsService the custom user details service for loading user data
	 * @param passwordEncoder the encoder used to verify passwords securely
	 * @return a fully configured {@code AuthenticationManager} bean
	 */
	//		@Bean
	//		AuthenticationManager authenticationManager(CustomUserDetailsService customUserDetailsService,
	//				PasswordEncoder passwordEncoder)
	//		{
	//			UsernamePasswordAuthenticationProvider authProvider =
	//					new UsernamePasswordAuthenticationProvider(customUserDetailsService, passwordEncoder);
	//	
	//			ProviderManager providerManager = new ProviderManager(authProvider);
	//			//default value is true in spring implementation
	//			//the provider will not earse password inside auth manager
	//			providerManager.setEraseCredentialsAfterAuthentication(false);
	//	
	//			return providerManager;
	//		}

	@Bean
	AuthenticationManager authenticationManager(UsernamePasswordAuthenticationProvider provider) {
		ProviderManager providerManager = new ProviderManager(provider);
		providerManager.setEraseCredentialsAfterAuthentication(false);
		return providerManager;
	}

	/**
	 * Security Configuration:
	 * -----------------------
	 * Defines the security filter chain for handling authentication and authorization in the application.
	 *
	 * Key Responsibilities:
	 * - Configure public and protected endpoints.
	 * - Enable role-based access control.
	 * - Register custom security filters.
	 * - Configure session management and disable CSRF for stateless APIs.
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(customizeEndpoints -> 

		SecurityPathRegistry.applyAccessRules(customizeEndpoints)

				);

		//		http.authorizeHttpRequests(customizer -> customizer
		//				.requestMatchers("/error", "/error/**").permitAll()
		//				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
		//				.requestMatchers("/api/auth/register","/api/auth/login").permitAll()
		//				.requestMatchers("/api/auth/logout").authenticated()
		//				.requestMatchers("/api/products").hasRole("ADMIN")
		//				.requestMatchers("/api/products/**").hasAnyRole("ADMIN","USER")
		//				);


		//is required for JWT if yes then why?
		http.addFilterBefore(jwtTokenValidatorFilter, BasicAuthenticationFilter.class );
		http.addFilterBefore(correlationIdFilter, JWTTokenValidatorFilter.class);
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.csrf(c->c.disable());

		return http.build();

	}

}
