package com.google.appengine.webmail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.webmail.Util;

@SuppressWarnings("serial")
public class InboxServlet extends BaseServlet {

	private static final Logger logger = Logger.getLogger(InboxServlet.class
			.getCanonicalName());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		logger.log(Level.INFO, "User name is: "
				+ req.getSession().getAttribute("username"));
		logger.log(Level.INFO, "Obtaining inbox listing");
		String searchFor = req.getParameter("q");
		PrintWriter out = resp.getWriter();
		Iterable<Entity> entities = null;
		if (searchFor == null || searchFor.equals("") || searchFor == "*") {
//			entities = Inbox.getAllInboxes("Inbox");
			entities = Inbox.getInboxesForProperty("Inbox", "recipient", (String) req.getSession().getAttribute("username"));
			out.println(Util.writeJSON(entities));
		} else {
			Entity e = Inbox.getInbox(searchFor);
			if (e != null) {
				Set<Entity> result = new HashSet<Entity>();
				result.add(e);
				out.println(Util.writeJSON(result));
			}
		}
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO, "Creating Inbox");
		PrintWriter out = resp.getWriter();

		String sender = (String) req.getSession().getAttribute("username");
		String recipient = req.getParameter("recipient");
		String subject = req.getParameter("subject");
		String body = req.getParameter("body");
		Date receivedOn = new Date();
		try {
			Inbox.createOrUpdateInbox(sender, recipient, subject, body,
					receivedOn);
		} catch (Exception e) {
			String msg = Util.getErrorResponse(e);
			out.print(msg);
		}
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String productkey = req.getParameter("id");
		Key key = KeyFactory.createKey("Inbox", productkey);
		PrintWriter out = resp.getWriter();
		try {
			Util.deleteEntity(key);
			out.println("Inbox entry deleted successfully.");
		} catch (Exception e) {
			String msg = Util.getErrorResponse(e);
			out.print(msg);
		}

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		if (action.equalsIgnoreCase("delete")) {
			doDelete(req, resp);
			return;
		} else if (action.equalsIgnoreCase("put")) {
			doPut(req, resp);
			return;
		}
	}

}