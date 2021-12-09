package com.brandmaker.mbiconsumer.example.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

/**
 * 
 * Encapsulates the JMS message sending 
 * 
 * @author axel.amthor
 *
 */
public class Sender {

	private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

	@Value("${spring.active-mq.queue-name}")
	private String queueName;

	@Autowired
	private JmsTemplate jmsTemplate;

	public void send(Object message) {

		LOGGER.debug("sending a new message: '{}' to " + queueName, message.toString());
		
		jmsTemplate.convertAndSend(queueName, message);

	}
}
