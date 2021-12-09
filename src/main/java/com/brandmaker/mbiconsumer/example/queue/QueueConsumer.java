package com.brandmaker.mbiconsumer.example.queue;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import com.brandmaker.mbiconsumer.example.EventProcessor;
import com.brandmaker.mbiconsumer.example.dtos.QueueEvent;
import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity;
import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity.Event;
import com.google.gson.Gson;

/**
 * <p>The consumer of the internal event queue.
 * <p>This consumer is doing the real work with MBI. It implements a JMS and ActiveMQ Listener, which will
 * <ul>
 * 		<li>Receive an event from the queue
 * 		<li>Analyze the event type, and if it is related to this demo consumer, then
 * 		<li>format the event payload
 * 		<li>dump the payload to a flat file as JSON
 * </ul>
 * <p>This is just an example on how to retrieve events from MBI and process them asynchronously.
 * 
 * <p><b>Hint:</b> Do not create worker threads here, leave the configuration of any parallelism up to the queue itself as this will give more control and even flexibility!
 * 
 * @author axel.amthor
 *
 */
public class QueueConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(QueueConsumer.class);

	/** The AssetManager is responsible for handling all necessary API operations */
	@Autowired
	EventProcessor eventProcessor;
	
	/** Configured list of channels which we want to manage */
	@Value("#{'${spring.application.system.channels:}'.split(',')}")
	private List<String> mySyncChannels;

	/**
	 * <p>This method will be called as soon as something is enqueued and avaliable for the consumer(s)
	 * 
	 * @param message
	 */
	@JmsListener(destination = "${spring.active-mq.queue-name}")
	public void onMessage(String message) {
		
		try {
			Gson gson = new Gson();
			
			QueueEvent queueEvent = gson.fromJson(message, QueueEvent.class);
			
			LOGGER.info("dequeued event " + queueEvent.toJson().toString(4) );
			
			// process the event now. 
			eventProcessor.process(queueEvent);
				
			
		} catch ( Exception e) {
			
			LOGGER.error("Problems on deserialization", e);
		}

	}
}
