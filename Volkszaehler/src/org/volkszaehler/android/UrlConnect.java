/**
* Class that Connects to the Internet
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class UrlConnect implements Runnable {

	private JsonController jsController;
	private String uuid;
	private String params;
	private String operation;
	public final static String[] modes = { "Delete", "Edit", "Make" };

	private List<NameValuePair> entities;

	/**
	 * @param fragment String that will be added after the global saved uuid
	 * @return String for the path
	 */
	private String getPath(String fragment) {
		String retVal = jsController.getServerName();
		if (uuid != null && fragment != null) {
			retVal = retVal + fragment + uuid + ".json";
		} else {
			retVal = retVal + "/channel.json";
		}
		if (params != null) {
			retVal = retVal + params;
		}
		return retVal;
	}

	/**
	 * Constructor
	 * @param jsController the Controller that makes this Object
	 * @param uuid String, the uuid of the Channel, can be null
	 * @param operation String, if null the get-Operation will be executed
	 */
	public UrlConnect(JsonController jsController, String uuid, String operation) {
		this.jsController = jsController;
		this.uuid = uuid;
		this.operation = operation;
	}

	/**
	 * Saves the given parameter in the global params variable 
	 * @param params String
	 */
	public void addParams(String params) {
		this.params = params;
	}

	/**
	 * Saves the given parameter in the global entities variable.
	 * Needs to be executed when a channel is being edited
	 * @param entities List<NameValuePair>
	 */
	public void addEntities(List<NameValuePair> entities) {
		this.entities = entities;
	}

	@Override
	public void run() {
		if (operation == null) {
			executeRead();
		} else if (operation.equals(modes[0])) {
			executeDelete(); // delete
		} else if (operation.equals(modes[2])) {
			executePost(false); // make
		} else if (operation.equals(modes[1])) {
			executePost(true); // edit
		}
	}

	/**
	 * Reads the data of a channel if a uuid is set, else it will read
	 * the channels. Calls the setConverter-method of the given JsonController
	 * when finished
	 */
	private void executeRead() {
		StringBuilder builder = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(getPath("/data/"));
			HttpResponse response = client.execute(get);
			if (response != null) {
				builder = new StringBuilder();
				InputStream content = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line = reader.readLine();
				while (line != null) {
					builder.append(line);
					line = reader.readLine();
				}
			}
		} catch (ClientProtocolException e) {
			jsController.setErrorMessage("Client Protocol Exception");
		} catch (IOException e) {
			jsController.setErrorMessage("IO Exception");
		}
		jsController.setConverter(builder);
	}

	/**
	 * If editMode is true, a channel with the given uuid will be edited
	 * else a new channel will be made
	 * @param editMode
	 */
	private void executePost(boolean editMode) {
		String path = "";
		if (editMode == false) {
			// make channel public
			entities.add(new BasicNameValuePair("public", "true")); 
			path = getPath(null);
		} else {
			// activate edit mode
			entities.add(new BasicNameValuePair("operation", "edit")); 
			path = getPath("/channel/");
		}
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(path);
			post.setEntity(new UrlEncodedFormEntity(entities));
			responseHandler(client.execute(post));
		} catch (ClientProtocolException e) {
			jsController.setErrorMessage("Client Protocol Exception");
		} catch (IOException e) {
			jsController.setErrorMessage("IO Exception");
		}
	}

	/**
	 * Tries to delete a channel with the given uuid, doesn't work yet
	 */
	private void executeDelete() {
		try {
			HttpClient client = new DefaultHttpClient();
			String path = getPath("/channel/");
			HttpDelete delete = new HttpDelete(path);
			responseHandler(client.execute(delete));
		} catch (ClientProtocolException e) {
			jsController.setErrorMessage("Client Protocol Exception");
		} catch (IOException e) {
			jsController.setErrorMessage("IO Exception");
		}
	}

	/**
	 * Private Method, simply reads the StatusCode of the response.
	 * If the code is 200 the setUrlSuccess-method of the given
	 * JsonController will be called and the urlSuccess-value will be
	 * set to true
	 * @param response
	 */
	private void responseHandler(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != 200) {
			jsController.setErrorMessage(status.getReasonPhrase());
			jsController.setUrlSuccess(false);
		} else {
			jsController.setUrlSuccess(true);
		}
	}
}