package com.google.appengine.webmail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class HomeServlet extends BaseServlet {

	private static final Logger logger = Logger.getLogger(HomeServlet.class
			.getCanonicalName());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
		logger.log(Level.INFO, "User name is: "
				+ req.getSession().getAttribute("username"));
		logger.log(Level.INFO, "Obtaining inbox listing");
		String action = req.getParameter("action");
		PrintWriter out = resp.getWriter();
		String message = null;
		String username = null;
		if (action != null && action.equals("login")) {
			username = req.getParameter("username");
			login(req.getSession(), username);
			message = "User " + username + " has logged in.";
		} else if (action != null && action.equals("logout")) {
			username = logout(req.getSession());
			message = "User " + username + " has logged out.";
		}
		out.print("{\"data\": \"" + message + "\", \"username\": \"" + username + "\"}");
	}

	private String logout(HttpSession session) {
		String username = (String) session.getAttribute("username");
		session.removeAttribute("username");
		return username;
	}

	private void login(HttpSession session, String username) {
		session.setAttribute("username", username);
	}

}