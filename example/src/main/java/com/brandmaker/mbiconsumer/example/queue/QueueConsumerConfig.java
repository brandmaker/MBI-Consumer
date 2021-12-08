package com.brandmaker.mbiconsumer.example.queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

/**
 * 
 * Configure the ActiveMQ integration into Springboot
 * 
 * @author axel.amthor
 *
 */
@Configuration
@EnableJms
public class QueueConsumerConfig {

  @Value("${spring.active-mq.broker-url}")
  private String brokerUrl;

  @Bean
  public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
	  
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL(brokerUrl);

    return activeMQConnectionFactory;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    
    factory.setConnectionFactory(receiverActiveMQConnectionFactory());

    return factory;
  }

  @Bean
  public QueueConsumer receiver() {
    return new QueueConsumer();
  }
}
