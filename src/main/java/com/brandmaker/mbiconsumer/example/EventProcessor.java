package com.brandmaker.mbiconsumer.example;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.brandmaker.mbiconsumer.example.dtos.QueueEvent;

/**
 * @author axel.amthor
 * 
 */
@Component
public class EventProcessor {

	public static class ProcessingResult {
		public enum State {
			SUCCESS,
			FAILURE
		}

		private State state;
		private String message;
		
		public ProcessingResult(State state, String string) {
			this.setState(state, message);
		}

		public void setState(State state, String message) {
			this.state = state;
			this.message = message;
		}
	}
	
	@Bean
	public ProcessingResult process(QueueEvent queueEvent) {
		
		return new ProcessingResult(ProcessingResult.State.FAILURE, "not processed");
				
	}

}
