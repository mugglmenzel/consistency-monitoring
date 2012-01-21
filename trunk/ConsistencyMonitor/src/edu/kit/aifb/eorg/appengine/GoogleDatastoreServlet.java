package edu.kit.aifb.eorg.appengine;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


/**
 * Servlet for storing and reading data in the Google Datastore
 * 
 * @author Robin Hoffmann
 * 
 *         created on: 19.01.2012
 *         
 *         deploy on Google App Engine!
 *         
 */

public class GoogleDatastoreServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final Date date = new Date();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PrintWriter out = resp.getWriter();

		String value = req.getParameter("value");

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity data = new Entity("consistency.txt", "consistency.txt");
		data.setProperty("value", value);

		datastore.put(data);

		out.println("Stored successfully: " + "\nkey = consistency.txt"
				 + "\nvalue = " + value);
		out.close();

	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		PrintWriter out = resp.getWriter();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("consistency.txt", "consistency.txt");

		try {
			Entity data = datastore.get(key);
			String value = (String)data.getProperty("value");
			out.print(value+";");
		} catch (EntityNotFoundException e) {
			out.println(e.getMessage());
		}
		
		// Instanz feststellen:
		out.println("date = " + date);
		out.close();
	}
}