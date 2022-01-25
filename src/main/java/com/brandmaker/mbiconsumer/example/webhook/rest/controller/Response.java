package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.util.List;


import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

	public List getEvents() {
		return events;
	}

	public void setEvents(List events) {
		this.events = events;
	}
}
