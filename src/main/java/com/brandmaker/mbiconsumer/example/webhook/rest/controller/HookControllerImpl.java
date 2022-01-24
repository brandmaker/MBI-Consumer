package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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
		
		// spit out all headers, let's see whether we have a Authentication Header with an HTTP signature
		Enumeration<String> headerNames = httpRequest.getHeaderNames();
		while ( headerNames != null && headerNames.hasMoreElements() ) {
			String name = headerNames.nextElement();
			LOGGER.info( name + " = " + httpRequest.getHeader(name));
		}
			
		
		try {
			
			/** received event */
			JSONObject requestObject = webhookEventRequest.toJson();
			LOGGER.debug(requestObject.toString(4));
			
			/*
			 * Validate the data with the signature and the configured pub key
			 */
			boolean signatureValid = false;
			String authHeader = httpRequest.getHeader("Authorization");
			
			if ( authHeader != null && !authHeader.isBlank() )
				signatureValid = validateSignature(webhookEventRequest, httpRequest, authHeader);
			        
			if ( !signatureValid )
				return createResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized");

			
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
			Response r = createResponse(httpResponse, HttpServletResponse.SC_ACCEPTED, "accepted");
			
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

	private boolean validateSignature(WebhookTargetPayloadHttpEntity webhookEventRequest, HttpServletRequest httpRequest, String authHeader)
																												throws CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		boolean signatureValid = false;
		
		// get the header value w/o the scheme
		String rawHeader = authHeader.replaceFirst("[sS]ignature ", ""); // mind the blank at the end!
		
		// split the raw header value in a key-value map and eliminate the quotes
		Map<String, String> headerKeyMap = Arrays.asList(rawHeader.split(",")).stream().map(x -> x.split("=", 2)).collect(Collectors.toMap(x -> x[0].toLowerCase(), x -> x[1].trim().replaceAll("^'|'$", "")));
		
		for (Map.Entry<String, String> entry : headerKeyMap.entrySet()) {
		    LOGGER.debug(String.format("Header Key: %s = %s", entry.getKey(), entry.getValue()));
		}
		
		// these are the headers, from which the data is picked to be signed
		String[] validationHeaders = headerKeyMap.get("headers").split("[, ]");
		
		// get the signature data by retrieving the header values and concatenate to one string separated by a space/blank
		String data = "";
		for ( String validationHeader : validationHeaders ) {
			String h = validationHeader.toLowerCase();
			String s = null;
			if ( h.equals("host") ) {
				
				/*
				 * if this service is running behind a reverse proxy, it may contain name/ip of the reverse proxy 
				 * rather than the name of the requested server! So we need to look for this header first!
				 */
				s = httpRequest.getHeader("x-forwarded-server"); 
				
				if ( s == null || s.isBlank() )
					s = httpRequest.getHeader(h); 
			}
			else {
				s = httpRequest.getHeader(h); 
			}
			LOGGER.debug(validationHeader + " = " + s);
				
			if ( s != null && !s.isBlank() ) {
				if ( data.length() > 0 )
					data = data.concat(" ");
				data = data.concat(s);
			}
		}
		LOGGER.debug("data to validate: " + data);
		
		// get the effective base64 decoded signature
		byte[] sigDecoded = Base64.getDecoder().decode(headerKeyMap.get("signature").getBytes());
		String algo = headerKeyMap.get("algorithm");
		
		/*
		 * the value in the signature cannot be used as algorithm directly, it just indicates an algorithm
		 */
		if ( algo.equals("rsa-sha256") )
			algo = "SHA256withRSA";
		
		// get the public key. Please refer to the manual regarding this!
		PublicKey publicKey = getPublicKey(webhookEventRequest.getSystemBaseUri()); 
			
		try {
			
			Signature sig = Signature.getInstance(algo);
		    sig.initVerify(publicKey);
		    sig.update(data.getBytes());
		    
			boolean result = sig.verify(sigDecoded);
			LOGGER.info("Signature verification of '" + data + "' with " + algo + ": " + (result?"passed":"failed") );
			
			signatureValid = true;
		}
		catch (Exception e) {
			LOGGER.error("Key error", e);
		}
		return signatureValid;
	}

	/**
	 * This method should encapsulate the public key for the sending instance. For testing purposes, 
	 * this is grabbed from the sender dynamically <b>which is highly discouraged for production!</b>
	 * Instead, put the x509 string in here statically!
	 * 
	 * @return public key
	 * @throws CertificateException
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	private PublicKey getPublicKey(String hostUrl) throws CertificateException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		String cert = getCertFromBMUrl(hostUrl);
		
		byte[] byteKey = Base64.getDecoder().decode(cert.getBytes());
		LOGGER.info("Length: " + byteKey.length);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePublic(X509publicKey);
		
	}

	private String getCertFromBMUrl(String hostUrl) throws MalformedURLException, IOException {
		String cert;
		URL url = new URL(hostUrl.replaceAll("/$", "") + "/rest/sso/keys/public");
		URLConnection con = url.openConnection();
		con.connect();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;
		
		cert = IOUtils.toString(in, encoding);
		LOGGER.info("Cert " + cert);
		
		return cert;
	}

	private Response createResponse(HttpServletResponse httpResponse, int code, String message) {
		httpResponse.setStatus(code);
		httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpResponse.setCharacterEncoding("UTF-8");
		Response r = new Response(message, code);
		return r;
	}
	
	private void dumpAlgorithms() {
		ArrayList<String> algorithms = new ArrayList<>();
		for (Provider provider : Security.getProviders())
		    for (Service service : provider.getServices()) {
		        if (service.getType().equals("Signature"))
		            algorithms.add(service.getAlgorithm());
		    }
		LOGGER.info(algorithms.toString());
	}
	
}
