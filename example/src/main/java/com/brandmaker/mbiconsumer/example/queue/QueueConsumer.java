package com.brandmaker.mbiconsumer.example.queue;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import com.brandmaker.mediapool.rest.MediaPoolAssetManager;
import com.brandmaker.mediapool.webhook.MediaPoolEvent;

/**
 * <p>The consumer of the internal event queue.
 * <p>This consumer is doing the real work with Media Pool. It implements a JMS and ActiveMQ Listener, which will
 * <ul>
 * 		<li>Receive an event from the queue
 * 		<li>Analyze the event type, and if it is related to one of the channels we are managing, then
 * 		<li>Connect to Media Pool via REST API
 * 		<li>Retrieve meta data and store to JSON file
 * 		<li>Retrieve the binary in requested rendition and version and store to local file system
 * </ul>
 * <p>This is just an example on how to use the REST API of Media Pool to get access to any data stored there.
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
	MediaPoolAssetManager assetManager;
	
	/** Configured list of channels which we want to manage */
	@Value("#{'${spring.application.system.channels:}'.split(',')}")
	private List<String> mySyncChannels;

	/**
	 * <p>This method will be called as soon as something is enqueued and avaliable for the consumer(s)
	 * 
	 * @param message
	 */
	@JmsListener(destination = "${spring.active-mq.queue-name}")
	public void onMessage(Map<String, Object> message) {
		
		try {
			
			MediaPoolEvent event = new MediaPoolEvent(message);
			event.setMySyncChannels(mySyncChannels);
			
			LOGGER.info("dequeued event " + event.toJson().toString(4) );
			
			// process the event now. We have an "Asset Manager" and a REST Wrapper class which are handling all Media Pool API stuff
			assetManager.synchronize(event);
				
			
		} catch ( Exception e) {
			
			LOGGER.error("Problems on deserialization", e);
		}

	}
}
