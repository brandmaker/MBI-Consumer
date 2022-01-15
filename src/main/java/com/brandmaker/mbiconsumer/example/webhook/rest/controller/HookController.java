package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * <p>Hook controller
 * 
 * <p>This is supposed to pick the post message and basically validate the contents and the signature based on the given settings
 * 
 * @author axel.amthor
 *
 */
public interface HookController {

	/**
	 * <p>basic request validator method
	 * <p>the rest endpoint is simply "/hook"
	 * 
	 * @return Response message object with detailed status and error code
	 * 
	 * @param requestBody The auto-converted POST data. If this can not be converted to the HookRequestBody PoJo, an excpetion will be thrown
	 * @see {@link HookRequestBody}
	 * @param httpResponse The http reaponse object
	 * 
	 */
	@PostMapping(
			path="/hook", 
			consumes="application/json", 
			produces="application/json")
	@ResponseStatus(value=HttpStatus.ACCEPTED, reason="Request accepted")
	Response post(@RequestBody WebhookTargetPayloadHttpEntity requestBody, HttpServletResponse httpResponse, HttpServletRequest httpRequest);

}
