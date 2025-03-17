package config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity
@Configuration
public class RestSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// @formatter:off
        http.authorizeHttpRequests((authz) -> authz
                // - Allow DELETE on the /accounts resource (or any sub-resource)
                //   for "SUPERADMIN" role only
				.requestMatchers( HttpMethod.DELETE, "/accounts/**").hasRole("SUPERADMIN")

                // - Allow POST or PUT on the /accounts resource (or any sub-resource)
                //   for "ADMIN" or "SUPERADMIN" role only
				.requestMatchers( HttpMethod.PUT, "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")
				.requestMatchers( HttpMethod.POST, "/accounts/**").hasAnyRole("ADMIN", "SUPERADMIN")

				// - Allow GET on the /accounts resource (or any sub-resource)
                //   for all roles - "USER", "ADMIN", "SUPERADMIN"
				.requestMatchers( HttpMethod.GET, "/accounts/**").hasAnyRole("USER", "ADMIN", "SUPERADMIN")

				// - Allow GET on the /authorities resource
                //   for all roles - "USER", "ADMIN", "SUPERADMIN"
				.requestMatchers( HttpMethod.GET, "/authorities").hasAnyRole("USER", "ADMIN", "SUPERADMIN")

                // Deny any request that doesn't match any authorization rule
                .anyRequest().denyAll())
        .httpBasic(withDefaults())
        .csrf(CsrfConfigurer::disable);
        // @formatter:on

        return http.build();
	}
	
//	@Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
		UserDetails user = User.withUsername("user").password(passwordEncoder.encode("user")).roles("USER").build();
		UserDetails userAdmin = User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build();
		UserDetails userSuperAdmin = User.withUsername("superadmin").password(passwordEncoder.encode("superadmin")).roles("USER", "ADMIN", "SUPERADMIN").build();
		UserDetails mary = User.withUsername("mary").password(passwordEncoder.encode("mary")).roles("USER").build();
		UserDetails joe = User.withUsername("joe").password(passwordEncoder.encode("joe")).roles("USER", "ADMIN").build();

		return new InMemoryUserDetailsManager(user, userAdmin, userSuperAdmin, mary, joe);
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
