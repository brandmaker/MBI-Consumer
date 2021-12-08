package com.brandmaker.mbiconsumer.example.webhook.consumer;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.brandmaker.mbiconsumer.example.webhook.rest.controller.HookController;


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
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })

// our controller is in a sibling package, give Spring some hints where to find it
@ComponentScan(basePackageClasses = HookController.class, basePackages = { 
		"com.brandmaker.mbiconsumer.example.webhook.rest" })
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

}
