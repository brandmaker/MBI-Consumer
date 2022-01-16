package com.brandmaker.mbiconsumer.example.webhook.consumer;

import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.brandmaker.mbiconsumer.example.webhook.rest.controller.HookController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(servers = { @Server(url = "https://www.amthor.de/spring"),
		@Server(url = "http://localhost:8080") },
		info = @Info(title = "MBI WebHook Consumer Example", 
						version = "0.0.1. Snapshot", 
						description = "MBI WebHook Consumer Example Implementation", 
						license = @License(name = "Apache 2.0", 
							url = "https://github.com/brandmaker/MBI-Consumer"), 
						contact = @Contact(url = "https://www.brandmaker.com", name = "A. Amthor", email = "axel.amthor@brandmaker.com")
				)
)
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
/**
 * <p>
 * Spring Boot Application starter
 * <p>
 * Security auto config is <b>not loaded</b here
 * 
 * @see com.brandmaker.mbiconsumer.example.webhook.consumer.SecurityConfiguration
 * 
 * @author axel.amthor
 *
 */
public class Application extends SpringBootServletInitializer {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * SpringBoot Starter Application
	 * @param args
	 */
	public static void main(String[] args) {

		LOGGER.info("\n\n" +
				" ____                      _ __  __       _               __  __ ____ ___    ____                                          \n" + 
				"| __ ) _ __ __ _ _ __   __| |  \\/  | __ _| | _____ _ __  |  \\/  | __ )_ _|  / ___|___  _ __  ___ _   _ _ __ ___   ___ _ __ \n" + 
				"|  _ \\| '__/ _` | '_ \\ / _` | |\\/| |/ _` | |/ / _ \\ '__| | |\\/| |  _ \\| |  | |   / _ \\| '_ \\/ __| | | | '_ ` _ \\ / _ \\ '__|\n" + 
				"| |_) | | | (_| | | | | (_| | |  | | (_| |   <  __/ |    | |  | | |_) | |  | |__| (_) | | | \\__ \\ |_| | | | | | |  __/ |   \n" + 
				"|____/|_|  \\__,_|_| |_|\\__,_|_|  |_|\\__,_|_|\\_\\___|_|    |_|  |_|____/___|  \\____\\___/|_| |_|___/\\__,_|_| |_| |_|\\___|_|   \n\n");
		
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
