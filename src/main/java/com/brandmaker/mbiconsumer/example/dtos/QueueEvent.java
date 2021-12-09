package com.brandmaker.mbiconsumer.example.dtos;

import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * 
 * Event Structure picked from the internal JMS queue
 * 
 * @author axel.amthor
 *
 */
@Component
public class QueueEvent {

	public String getSystemBaseUri() {
		return systemBaseUri;
	}

	public void setSystemBaseUri(String systemBaseUri) {
		this.systemBaseUri = systemBaseUri;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	private String systemBaseUri;
	private String systemId;
	private String customerId;
	private String namespace;

	private String module;
	private String operation;
	private String entity;
	private long timestamp;
	private Map<String, Object> objectId;
	private Map<String, Object> data;
	private String userId;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> getObjectId() {
		return objectId;
	}

	public void setObjectId(Map<String, Object> objectId) {
		this.objectId = objectId;
	}

	public Optional<Map<String, Object>> getData() {
		return Optional.ofNullable(data);
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Optional<String> getUserId() {
		return Optional.ofNullable(userId);
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();

		String t = gson.toJson(this);
		
		return t;
	}

	public JSONObject toJson() {

		return new JSONObject(toString());
	}
}