package com.google.appengine.webmail;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class Inbox {

	public static void createOrUpdateInbox(String sender, String recipient,
			String subject, String body, Date receivedOn) {
		String key = Long.toString(receivedOn.getTime());
		Entity inbox = getInbox(key);
		if (inbox == null) {
			inbox = new Entity("Inbox", key);
			inbox.setProperty("sender", sender);
			inbox.setProperty("recipient", recipient);
			inbox.setProperty("subject", subject);
			inbox.setProperty("body", body);
			inbox.setProperty("receivedOn", receivedOn);
		}
		Util.persistEntity(inbox);
	}

	public static Iterable<Entity> getAllInboxes(String kind) {
		return Util.listEntities(kind, null, null);
	}

	public static Iterable<Entity> getInboxesForProperty(String kind, String propertyName, String propertyValue) {
		return Util.listEntities(kind, propertyName, propertyValue);
	}

	public static Entity getInbox(String inboxKey) {
		Key key = KeyFactory.createKey("Inbox", inboxKey);
		return Util.findEntity(key);
	}

	public static Iterable<Entity> getItems(String name) {
		Query query = new Query();
		Key parentKey = KeyFactory.createKey("Inbox", name);
		query.setAncestor(parentKey);
		query.addFilter(Entity.KEY_RESERVED_PROPERTY,
				Query.FilterOperator.GREATER_THAN, parentKey);
		Iterable<Entity> results = Util.getDatastoreServiceInstance()
				.prepare(query).asList(FetchOptions.Builder.withDefaults());
		return results;
	}
}
