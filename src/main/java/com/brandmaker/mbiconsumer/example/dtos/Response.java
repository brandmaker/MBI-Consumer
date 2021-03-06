package com.brandmaker.mbiconsumer.example.dtos;

import java.util.List;


import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>Response PoJo to automatically generate a JSON response through spring converter
 *
 * @author axel.amthor
 *
 */
public class Response {

	private String message;
	private int code;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<Event> events;

	public Response(String msg, int c) {
		message = msg;
		code = c;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
