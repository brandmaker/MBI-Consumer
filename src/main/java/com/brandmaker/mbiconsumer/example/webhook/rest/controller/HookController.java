package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brandmaker.mbiconsumer.example.dtos.Response;
import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity;
import com.brandmaker.mbiconsumer.example.exceptions.HookControllerException;


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
	 * @param eventsInResponse Whether to copy processed events to the response
	 * @param httpResponse The http reaponse object
	 * @param httpRequest The http request object
	 *
	 * @throws HookControllerException
	 *
	 */
	@RequestMapping(
			method = RequestMethod.POST,
			path="/hook",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	Response  post(
			@RequestBody WebhookTargetPayloadHttpEntity requestBody,
			@RequestParam(name = "eventsInResponse", required = false, defaultValue = "false") Boolean eventsInResponse,
			HttpServletResponse httpResponse,
			HttpServletRequest httpRequest
	) throws HookControllerException;

}
