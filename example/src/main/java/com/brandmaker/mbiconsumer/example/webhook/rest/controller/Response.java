package com.brandmaker.mbiconsumer.example.webhook.rest.controller;



/**
 * <p>Response PoJo to automatically generate a JSON response through spring converter
 * 
 * @author axel.amthor
 *
 */
public class Response {

	private String message;
	private int code;
	
	Response(String msg, int c) {
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
}
