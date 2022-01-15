package com.brandmaker.mbiconsumer.example.webhook.consumer;

import java.util.Arrays;

import javax.ws.rs.HttpMethod;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



/**
 * <p>We need some sophisticated control on security. In general, Spring REST endpoints are protected by various measures, which
 * we entirely turn <b>off</b> here!
 * 
 * @author axel.amthor
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter  {

	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable();
        httpSecurity.csrf().disable();
        httpSecurity.authorizeRequests()
	        .antMatchers("**").permitAll()
	        .antMatchers(HttpMethod.POST,"/hook").permitAll()
	        .anyRequest().authenticated();
	}
	
	 @Bean
	    CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//	        configuration.setAllowedOrigins(Arrays.asList("*"));
	        configuration.setAllowedMethods(Arrays.asList("*"));
	        configuration.setAllowedHeaders(Arrays.asList("*"));
	        configuration.setAllowCredentials(true);
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }
}
