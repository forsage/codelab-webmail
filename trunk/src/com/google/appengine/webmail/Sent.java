package com.google.appengine.webmail;

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class Sent {

	public static Entity createOrUpdateSent(String recipient, String subject,
			String body, Date sentOn) {
		String key = Long.toString(sentOn.getTime());
		Entity sent = getSent(key);
		if (sent == null) {
			sent = new Entity("Sent", key);
			sent.setProperty("subject", subject);
			sent.setProperty("body", body);
			sent.setProperty("sentOn", sentOn);
		}
		Util.persistEntity(sent);
		return sent;
	}

	public static Iterable<Entity> getAllSents() {
		Iterable<Entity> entities = Util.listEntities("Sent", null, null);
		return entities;
	}

	public static Entity getSent(String sentKey) {
		Key key = KeyFactory.createKey("Sent", sentKey);
		Entity entities = Util.findEntity(key);
		return entities;
	}

	public static Iterable<Entity> getSentsForUser(String kind, String userName) {
		Key ancestorKey = KeyFactory.createKey("User", userName);
		return Util.listChildren("Sent", ancestorKey);
	}

	public static Entity getSingleSent(String sentName) {
		Query q = new Query("Sent");
		q.addFilter("name", FilterOperator.EQUAL, sentName);
		List<Entity> results = Util.getDatastoreServiceInstance().prepare(q)
				.asList(FetchOptions.Builder.withDefaults());
		if (!results.isEmpty()) {
			return (Entity) results.remove(0);
		}
		return null;
	}
}
