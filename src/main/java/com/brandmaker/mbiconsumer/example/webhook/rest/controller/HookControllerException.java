package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import org.slf4j.LoggerFactory;


public class HookControllerException extends Exception {


	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HookControllerException.class);

	public HookControllerException(String msg) {
		super (msg);

		LOGGER.info("In HookControllerException");
	}

}
