package com.brandmaker.mbiconsumer.example.webhook.rest.controller;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>This is the JSON-to-POJO Class for the general request which is send to the
 * WebHook
 * 
 * <p>The request has the following format:
 * <pre>
 * {
 * 		"data": "event data as encoded JSON string",
 * 		"signature": "signature of data element"
 * }
 * </pre>
 * 
 * @author axel.amthor
 *
 */
public class HookRequestBody {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(HookRequestBody.class);

	private String data;
	private String signature;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		ObjectMapper Obj = new ObjectMapper();

		try {

			String jsonStr = Obj.writeValueAsString(this);
			JSONObject jo = new JSONObject(jsonStr);
			
			return jo.toString();
		}

		catch (IOException | JSONException e) {
			LOGGER.error("an error", e);
		}
		return null;
	}
	
	public String toString(int ind) {
		ObjectMapper Obj = new ObjectMapper();

		try {

			String jsonStr = Obj.writeValueAsString(this);
			JSONObject jo = new JSONObject(jsonStr);
			
			return jo.toString(ind);
		}

		catch (IOException | JSONException e) {
			LOGGER.error("an error", e);
		}
		return null;
	}

}
