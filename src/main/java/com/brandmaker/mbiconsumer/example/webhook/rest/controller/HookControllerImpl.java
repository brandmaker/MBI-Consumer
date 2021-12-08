package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.brandmaker.mbiconsumer.example.queue.Sender;
import com.brandmaker.mediapool.webhook.MediaPoolEvent;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@OpenAPIDefinition(
		info = @Info(
				title="BrandMaker Media Pool Webhook Example",
				version="1.0",
				description="Example implementation of a web hook REST Endpoint, listening for events submitted from a Media Pool instance.",
				contact=@Contact(name="BrandMaker Gmbh, Karlsruhe", url="https://www.brandmaker.com/products/digital-asset-manager/", email="info@brandmaker.com"),
				license=@License(name="Copyright © 2020, BrandMaker GmbH", url="https://www.brandmaker.com/imprint/")
		),
		tags={@Tag(name="Media Pool Webhook")}
	)
@Tag(name="Media Pool Webhook")
public class HookControllerImpl implements HookController{

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
	
	private String[] copyProps = { MediaPoolEvent.PROP_CUSTOMERID, MediaPoolEvent.PROP_SYSTEMID, MediaPoolEvent.PROP_BASEURL };
	
	/* (non-Javadoc)
	 * @see com.brandmaker.mediapool.webhook.rest.controller.HookController#post(com.brandmaker.mediapool.webhook.rest.controller.HookRequestBody, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Response post(HookRequestBody requestBody, HttpServletResponse httpResponse) {
		
		long start = System.currentTimeMillis();
		
		try {
			JSONObject eventObject = new JSONObject();
			
			LOGGER.debug(requestBody.toString(4));
			
			String eventData = requestBody.getData();
			String signature = requestBody.getSignature();
			
			// ToDo: validate the data with the signature and the configured pub key
			
			
			
			/*
			 * parse data property and parse the inner structure as JSON
			 */
			JSONObject dataObject = null;
			try {
				dataObject = new JSONObject(eventData);
				LOGGER.info("decoded event data: " + dataObject.toString(4));
			}
			catch ( JSONException j ) {
				// the data isn't well formed, exit immediately
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(1) Data object not well formed", j);
			}
			
			/* this is the array of actual media pool events submitted in this request */
			JSONArray eventArray = null;
			try {
				eventArray = dataObject.getJSONArray("events");
			}
			catch ( JSONException j ) {
				
				// the array isn't well formed, exit immediately
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(2) Events array not well formed", j);
			}
			
			/*
			 * process event array
			 */
			for ( int n = 0; n < eventArray.length(); n++ )
			{
				// pick one event
				try {
					eventObject = eventArray.getJSONObject(n);
				}
				catch ( JSONException j ) {
					// the data isn't well formed, exit immediately
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(3) Event object not well formed", j);
				}
				
				/*
				 * these props need to go into each event element, as within the subsequent queue, 
				 * there is no "batch" but single, disjoint events
				 */
				try {
					for ( String prop : copyProps ) {
						if ( dataObject.has(prop) )
							eventObject.put(prop, dataObject.getString(prop));
					}
				}
				catch ( JSONException j ) {
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "(4) cannot amend event object", j);
				}
				
				/*
				 * validate event data
				 */
				MediaPoolEvent mediapoolEvent;
				try {
					mediapoolEvent = MediaPoolEvent.factory(eventObject);
				} 
				catch (Exception e) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "(5) cannot deserialze event", e);
				}
				
				// check source system IDs of this event
				// if you want to listen for a particular instance and custoomer ID, uncomment the following and the `else` branch below
//				if ( mediapoolEvent.getCustomerId().equals(customerId) && mediapoolEvent.getSystemId().equals(systemId) ) 
				{
					/*
					 * check for the proper channel. If this is not the case, we do not need to enqueue the event at all!
					 */
					if ( mediapoolEvent.isMyChannel() )
					{
						/*
						 * Push event to the processing queue
						 * We will not process this event within this loop!
						 * 
						 * We are using spring JMS together with ActiveMQ as a broker. Configuration can be done via the application.yaml
						 * 
						 */
						
						// serialize the event object to a map
						Map<String, Object> map = mediapoolEvent.toMap();
						
						// send this serialized event to media pool processing queue
						processingQueueSender.send(map);
						
						LOGGER.info( (n+1) + ". Event " + mediapoolEvent.getEvent().toString() + " for Asset " + mediapoolEvent.getAssetId() + " queued." );
					}
					else
						LOGGER.info("Not my business: " + mediapoolEvent.getChannelsFromPayload().toString() );
				
				}
//				else
//					LOGGER.error("Event " + mediapoolEvent.getEvent().toString() 
//							+ " ignored for customer " + mediapoolEvent.getCustomerId() + " on system " + mediapoolEvent.getSystemId() );
				
			}
			
			/*
			 * now we are done here and will send back the response to the requester
			 * 
			 * Media Pool is not interested on HOW we are processing he event itself nor whether this
			 * processing might fail. It wants us to tell whether we have successfully RECEIVED the event.
			 * 
			 * So the response over here is always "202 accepted" as we process the event later and asynchronously.
			 * 
			 * If too many consecutive errors are returned to Media Pool, the webhook will be disabled 
			 * and no further events will be recieved any more"
			 * 
			 * 
			 */
			httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
			return new Response("accepted", HttpServletResponse.SC_ACCEPTED);
			
		}
		finally
		{
			LOGGER.info("Finished processing webhook request  in " + (System.currentTimeMillis() - start) + " msec");
		}

	}
	
}
