package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity;
import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity.Event;
import com.brandmaker.mbiconsumer.example.queue.Sender;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@OpenAPIDefinition(
		info = @Info(
				title="BrandMaker MBI Consumer Example",
				version="1.0",
				description="Example implementation of a REST Endpoint, listening for events submitted from BrandMaker MBI.",
				contact=@Contact(name="BrandMaker Gmbh, Karlsruhe", url="https://www.brandmaker.com/products/digital-asset-manager/", email="info@brandmaker.com"),
				license=@License(name="Copyright © 2022, BrandMaker GmbH", url="https://www.brandmaker.com/imprint/")
		),
		tags={@Tag(name="BrandMaker MBI")}
	)
@Tag(name="BrandMaker MBI")
public class HookControllerImpl implements HookController {

	/** our logger is log4j */
	private static final Logger LOGGER = LoggerFactory.getLogger(HookController.class);
	
	/** pick value from application.yaml */
	@Value("${spring.application.system.publickey}")
	private String pubkey;
	
	@Value("${spring.application.system.customerId}")
	private String customerId;
	
	@Value("${spring.application.system.systemId}")
	private String systemId;
	
	@Autowired
	private Sender processingQueueSender;
	
	private String[] copyProps = { WebhookTargetPayloadHttpEntity.PROP_CUSTOMERID, 
			WebhookTargetPayloadHttpEntity.PROP_NAMESPACE, 
			WebhookTargetPayloadHttpEntity.PROP_SYSTEMBASEURI, 
			WebhookTargetPayloadHttpEntity.PROP_SYSTEMID };
	
	@Bean
    public HandlerExceptionResolver customHandlerExceptionResolver() {
        return new RestResponseEntityExceptionHandler();
    }
	 
	/* (non-Javadoc)
	 * @see com.brandmaker.mediapool.webhook.rest.controller.HookController#post(com.brandmaker.mediapool.webhook.rest.controller.HookRequestBody, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Response post( WebhookTargetPayloadHttpEntity webhookEventRequest, Boolean eventsInResponse, HttpServletResponse httpResponse, HttpServletRequest httpRequest) throws HookControllerException {
		
		long start = System.currentTimeMillis();
		
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		while ( headerNames != null && headerNames.hasMoreElements() ) {
			String name = headerNames.nextElement();
			LOGGER.debug( name + " = " + httpRequest.getHeader(name));
		}
			
		
		try {
			
			/** received event */
			JSONObject requestObject = webhookEventRequest.toJson();
			LOGGER.debug(requestObject.toString(4));
			
			/*
			 * TODO validate the data with the signature and the configured pub key
			 */

			
			/*
			 * pick the list of events and push them one by one to the processing queue
			 */
			List<Event> events = webhookEventRequest.getEvents();
			int n = 1;
			List<Event> processedEvents = new ArrayList<>();;
			
			/*
			 * process event array
			 */
			if ( events != null ) {
				for ( Event event : events )
				{
					
					/*
					 * TODO check if the event is targeted to this consumer ... ?
					 */
					
					
					/** data structure that will be put into the queue */
					JSONObject eventObject = event.toJson();
					
					
					/*
					 * these props need to go into each event element, as within the subsequent queue, 
					 * there is no "batch" but single, disjoint events
					 */
					try {
						for ( String prop : copyProps ) {
							if ( requestObject.has(prop) )
								eventObject.put(prop, requestObject.get(prop));
						}
					}
					catch ( JSONException j ) {
						throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "(4) cannot amend event object", j);
					}
					
					/*
					 * The JSON structure now should match the QueueEvent object structure
					 */
					
					/*
					 * Push event to the processing queue
					 * We will not process this event within this loop!
					 * 
					 * We are using spring JMS together with ActiveMQ as a broker. Configuration can be done via the application.yaml
					 * 
					 */
					processingQueueSender.send(eventObject.toString());
					
					processedEvents.add(event);
					
					LOGGER.debug( (n++) + ". Event queued" + eventObject.toString(4) );
					
				}
			}
			
			/*
			 * now we are done here and will send back the response to the requester
			 * 
			 * MBI is not interested on HOW we are processing the event itself nor whether this
			 * processing might fail. It wants us to tell whether we have successfully RECEIVED the event.
			 * 
			 * So the response over here is always "202 accepted" as we process the event later and asynchronously.
			 * 
			 * If too many consecutive errors are returned, the webhook will be disabled 
			 * and no further events will be received any more!
			 * 
			 * 
			 */
			httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
			httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			httpResponse.setCharacterEncoding("UTF-8");
			
			Response r = new Response("accepted", HttpServletResponse.SC_ACCEPTED);
			
			if ( eventsInResponse )
				r.setEvents(processedEvents);
			
			return r;
	        
			
		} 
		catch ( Exception e ) {
			LOGGER.error("Error:", e);
			throw new HookControllerException(e.getMessage()); 
		}
		finally
		{
			LOGGER.info("Finished processing webhook request  in " + (System.currentTimeMillis() - start) + " msec");
		}

	}
	
}
