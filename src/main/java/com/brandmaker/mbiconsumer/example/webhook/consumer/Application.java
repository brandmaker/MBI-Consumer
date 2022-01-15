package com.brandmaker.mbiconsumer.example.webhook.consumer;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.brandmaker.mbiconsumer.example.webhook.rest.controller.HookController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(servers = { @Server(url = "https://www.amthor.de/spring"),
		@Server(url = "http://localhost:8080") } 
//		,info = @Info(title = "the title", 
//		version = "v1", 
//		description = "My API", 
//		license = @License(name = "Apache 2.0", 
//		url = "http://foo.bar"), 
//		contact = @Contact(url = "http://gigantic-server.com", name = "Fred", email = "Fred@gigagantic-server.com"))
)

/**
 * <p>
 * Spring Boot Application starter
 * <p>
 * Security auto config is <b>not loaded</b here
 * 
 * @see com.brandmaker.mediapool.webhook.consumer.SecurityConfiguration
 * 
 * @author axel.amthor
 *
 */
@EnableWebSecurity
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

// our controller is in a sibling package, give Spring some hints where to find it
@ComponentScan(basePackageClasses = HookController.class, basePackages = { 
		"com.brandmaker.mbiconsumer.example.queue",
		"com.brandmaker.mbiconsumer.example.webhook",
		"com.brandmaker.mbiconsumer.example.rest",
		"com.brandmaker.mbiconsumer.example.dtos",
		"com.brandmaker.mbiconsumer.example",
		})
public class Application extends SpringBootServletInitializer {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * SpringBoot Starter Application
	 * @param args
	 */
	public static void main(String[] args) {

		LOGGER.info("Start of MBI Consumer");
		
		SpringApplication.run(Application.class, args);
	}

	/* (non-Javadoc)
	 * @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer#configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}
	
	/**
	 * This bean will handle X-Forwarded headers if running behind a reverse proxy
	 * and map internal requests back to the external reverse proxy endpoint properly
	 * 
	 * @return
	 */
	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
	   return new ForwardedHeaderFilter();
	}

}
