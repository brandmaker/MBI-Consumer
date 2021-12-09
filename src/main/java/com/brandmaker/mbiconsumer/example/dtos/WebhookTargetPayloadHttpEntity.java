package com.brandmaker.mbiconsumer.example.dtos;

import org.json.JSONObject;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The serializable entity which is sent to the webhook target at the end of a webhook dispatch
 */
@Component
public class WebhookTargetPayloadHttpEntity {

	public final static String  PROP_SYSTEMBASEURI = "systemBaseUri";
	public final static String  PROP_CUSTOMERID = "customerId";
	public final static String  PROP_SYSTEMID = "systemId";
	public final static String  PROP_NAMESPACE = "namespace";
	
	  
    private String systemBaseUri;
    private String systemId;
    private String customerId;
    private String namespace;
    private List<Event> events;

    public WebhookTargetPayloadHttpEntity(Map<String, Object> message) {
		// FIXME Auto-generated constructor stub
	}

	public WebhookTargetPayloadHttpEntity() {
	}

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

    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "WebhookTargetPayloadHttpEntity{" +
                "systemBaseUri='" + systemBaseUri + '\'' +
                ", systemId='" + systemId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", namespace='" + namespace + '\'' +
                ", events=" + events +
                '}';
    }

    public static WebhookTargetPayloadHttpEntity newInstance(String systemId, String customerId, String originSystemUri, @Nullable String namespace, List<Event> events) {
        WebhookTargetPayloadHttpEntity webhookTargetPayloadHttpEntity = new WebhookTargetPayloadHttpEntity();
        webhookTargetPayloadHttpEntity.setSystemId(systemId);
        webhookTargetPayloadHttpEntity.setCustomerId(customerId);
        webhookTargetPayloadHttpEntity.setSystemBaseUri(originSystemUri);
        webhookTargetPayloadHttpEntity.setNamespace(namespace);

        List<Event> webhookTargetPayloadHttpEntityEvents = events.stream()
	        .sorted(Comparator.comparingLong(Event::getTimestamp))
	        .map(Event::newInstance)
	        .collect(Collectors.toList());
        webhookTargetPayloadHttpEntity.setEvents(webhookTargetPayloadHttpEntityEvents);

        return webhookTargetPayloadHttpEntity;
    }

    public static WebhookTargetPayloadHttpEntity newInstanceForTestEvent(String systemId, String customerId, String originSystemUri, @Nullable String namespace) {
        WebhookTargetPayloadHttpEntity webhookTargetPayloadHttpEntity = new WebhookTargetPayloadHttpEntity();
        webhookTargetPayloadHttpEntity.setSystemId(systemId);
        webhookTargetPayloadHttpEntity.setCustomerId(customerId);
        webhookTargetPayloadHttpEntity.setSystemBaseUri(originSystemUri);
        webhookTargetPayloadHttpEntity.setNamespace(namespace);

        webhookTargetPayloadHttpEntity.setEvents(List.of(Event.newInstanceForTestEvent()));
        return webhookTargetPayloadHttpEntity;
    }

    public static class Event  {

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
            return "Event{" +
                    "module='" + module + '\'' +
                    ", operation='" + operation + '\'' +
                    ", entity='" + entity + '\'' +
                    ", timestamp=" + timestamp +
                    ", objectId=" + objectId +
                    ", data=" + data +
                    ", userId='" + userId + '\'' +
                    '}';
        }

        private static Event newInstance(Event event) {
            Event webhookTargetPayloadEvent = new Event();
            webhookTargetPayloadEvent.setEntity(event.getEntity());
            webhookTargetPayloadEvent.setModule(event.getModule());
            webhookTargetPayloadEvent.setObjectId(event.getObjectId());
            webhookTargetPayloadEvent.setOperation(event.getOperation());
            webhookTargetPayloadEvent.setTimestamp(event.getTimestamp());
            event.getData().ifPresent(webhookTargetPayloadEvent::setData);
            event.getUserId().ifPresent(webhookTargetPayloadEvent::setUserId);
            return webhookTargetPayloadEvent;
        }

        private static Event newInstanceForTestEvent() {
            Event webhookTargetPayloadEvent = new Event();
            webhookTargetPayloadEvent.setEntity("TEST");
            webhookTargetPayloadEvent.setModule("ADMINISTRATION");
            webhookTargetPayloadEvent.setObjectId(Collections.emptyMap());
            webhookTargetPayloadEvent.setOperation("TEST");
            webhookTargetPayloadEvent.setTimestamp(Instant.now().toEpochMilli());
            return webhookTargetPayloadEvent;
        }

		public JSONObject toJson() {
			Gson gson = new Gson();
			
			String t = gson.toJson(this);
			
			return new JSONObject(t);
		}
    }

	public JSONObject toJson() {
		Gson gson = new Gson();
		
		String t = gson.toJson(this);
		
		return new JSONObject(t);
		
	}
}
