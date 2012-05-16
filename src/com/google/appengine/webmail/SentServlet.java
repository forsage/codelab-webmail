/**
 * Copyright 2011 Google
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.appengine.webmail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class SentServlet extends BaseServlet {

	private static final Logger logger = Logger.getLogger(SentServlet.class
			.getCanonicalName());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		logger.log(Level.INFO, "Obtaining Item listing");
		String searchBy = req.getParameter("item-searchby");
		String searchFor = req.getParameter("q");
		PrintWriter out = resp.getWriter();
		if (searchFor == null || searchFor.equals("")) {
//			Iterable<Entity> entities = Sent.getAllSents();
			Iterable<Entity> entities = Inbox.getInboxesForProperty("Inbox", "sender", (String) req.getSession().getAttribute("username"));
			out.println(Util.writeJSON(entities));
		}
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String itemKey = req.getParameter("id");
		PrintWriter out = resp.getWriter();
		Iterable<Entity> entities = Util.listEntities("Inbox", "itemName",
				itemKey);
		try {
			for (Entity e : entities) {
				if (e != null)
					out.print("Cannot delete item as there are orders associated with this item.");
				return;
			}
			Entity e = Sent.getSingleSent(itemKey);
			Util.deleteEntity(e.getKey());
			out.print("Item deleted successfully.");
		} catch (Exception e) {
			String msg = Util.getErrorResponse(e);
			out.print(msg);
		}
	}

	/**
	 * Redirects to delete or insert entity based on the action in the HTTP
	 * request.
	 */
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