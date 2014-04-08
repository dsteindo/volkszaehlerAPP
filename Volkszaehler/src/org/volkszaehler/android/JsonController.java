/**
* Controller Class
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

public class JsonController {

	private GlobalStorage globalStorage;

	private String server;
	private JsonConverter converter;
	private String errorMessage;

	private boolean urlSuccess;

	/**
	 * Converts parameter to a valid HTTP-String
	 * @param server String, the Volkszaehler server
	 * @return valid HTTP-String
	 */
	private String refactorServer(String server) {
		if (server == null || server.isEmpty() == true) {
			server = "http://demo.volkszaehler.org/middleware.php";
		} else if (server.startsWith("http://") == false) {
			server = "http://" + server;
		}
		if (server.endsWith("/middleware.php") == false) {
			server = server + "/middleware.php";
		}
		return server;
	}

	/**
	 * @return server
	 */
	public String getServerName() {
		return server;
	}

	/**
	 * Create a new JsonController Instance, which will fetch the server name
	 * from the given globalStorage 
	 * @param globalStorage
	 */
	public JsonController(GlobalStorage globalStorage) {
		this.globalStorage = globalStorage;
		this.server = globalStorage.getJsServer();
	}

	/**
	 * Create a new JsonController Instance, it will save the server name into the
	 * globalStorage if the saveServer parameter is set to true
	 * @param globalStorage
	 * @param server String, userInput
	 * @param saveServer 
	 */
	public JsonController(GlobalStorage globalStorage, String server, boolean saveServer) {
		this.globalStorage = globalStorage;
		this.server = refactorServer(server);
		if (saveServer == true) {
			globalStorage.setJsServer(this.server);
		}
	}

	/**
	 * Method that will be called to set the custom errorMessage
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Method that will be called from an UrlConnect Instance.
	 * If the parameter is false something gone wrong
	 * @param urlSuccess
	 */
	public void setUrlSuccess(boolean urlSuccess) {
		this.urlSuccess = urlSuccess;
	}

	/**
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * With this method the String as parameter will be used to
	 * make a new JsonConverter Instance, if the String does not contain
	 * valid JSON the JsonConverter will be set to null
	 * @param builder
	 */
	public void setConverter(StringBuilder builder) {
		try {
			converter = new JsonConverter(builder);
		} catch (JSONException e) {
			converter = null;
		}
	}

	/**
	 * A method that will execute a UrlConnect thread in the
	 * given executionTime read from the globalStorage
	 * @param urlConnect UrlConnect Instance that will be executed
	 */
	private void executeUrlConnect(UrlConnect urlConnect) {
		converter = null; // Delete old data
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future = executor.submit(urlConnect);
		try {
			// wait for task to complete
			int timer = globalStorage.getExecutionTime();
			future.get(timer, TimeUnit.SECONDS); 
		} catch (TimeoutException e) {
			errorMessage = "Timeout Exception";
		} catch (InterruptedException e) {
			errorMessage = "Interrupted Exception";
		} catch (ExecutionException e) {
			errorMessage = "Execution Exception";
		} finally {
			executor.shutdownNow(); // cleanup
		}
	}

	/**
	 * Method that will be used to read the Data of the selected VlzChannels 
	 * that are stored in the globalStorage. Calls the readChannel-method
	 * @param start long, start time
	 * @param end long, end time
	 * @return true, if one channel was read successfully
	 */
	public boolean channelReader(long start, long end) {
		boolean success = false;
		List<VlzChannel> list = globalStorage.getSelectedItems();
		for (VlzChannel vChannel : list) {
			vChannel.setTimestamp(start, end);
			success = readChannel(vChannel) || success;
		}
		return success;
	}

	/**
	 * Tries to read the data which belongs to the VlzChannel
	 * uses the injectData-method of the global JsonConverter Instance.
	 * Also creates an executes UrlConnect Instances to fetch the data
	 * @param vChannel
	 * @return true, if data was injected successfully into the given VlzChannel
	 */
	private boolean readChannel(VlzChannel vChannel) {
		boolean success = false;
		UrlConnect urlConnect = new UrlConnect(this, vChannel.getUuid(), null);
		String params = vChannel.getParams(globalStorage.getValuesLimiter());
		urlConnect.addParams(params); // Less Logic here
		executeUrlConnect(urlConnect);
		if (converter != null) {
			try {
				success = converter.injectData(vChannel);
				if (success == false) {
					errorMessage = "No rows found";
				}
			} catch (JSONException ex) {
				errorMessage = "JSON Exception: " + ex.getMessage();
			}
		}
		return success;
	}

	/**
	 * Creates an UrlConnect Instance to read the public channels.
	 * @return List<VlzChannel>, null if not successful
	 */
	public List<VlzChannel> getChannels() {
		UrlConnect urlConnect = new UrlConnect(this, null, null);
		executeUrlConnect(urlConnect);
		List<VlzChannel> channels = null;
		if (converter != null) {
			try {
				channels = converter.listChannels();
			} catch (JSONException ex) {
				errorMessage = "JSON Exception: " + ex.getMessage();
			}
		}
		return channels;
	}
	
	/**
	 * Tries to delete the given channel
	 * @param channel
	 * @return true, if successful
	 */
	public boolean deleteChannel(VlzChannel channel) {
		String uuid = channel.getUuid();
		UrlConnect urlConnect = new UrlConnect(this, uuid, UrlConnect.modes[0]);
		executeUrlConnect(urlConnect);
		return urlSuccess;
	}

	/**
	 * Tries to edit the properties of a Channel. Creates a UrlConnect instance 
	 * and calls the addEntities-method of the new instance. Calls the
	 * executeUrlConnect-method to run the new UrlConnect instance.
	 * If editMode is false a new Channel will be created
	 * @param channel that should be edited
	 * @param values String[] { title, color, type }
	 * @param resolution
	 * @param editMode 
	 * @return true if successful
	 */
	public boolean editChannel(VlzChannel channel, String[] values, int resolution, boolean editMode) {
		String uuid = null;
		String operation = UrlConnect.modes[2]; // make
		// make a list of NameValuePairs for the POST-Request
		List<NameValuePair> entities = new ArrayList<NameValuePair>();
		entities.add(new BasicNameValuePair("title", values[0]));
		entities.add(new BasicNameValuePair("color", values[1]));
		entities.add(new BasicNameValuePair("type", values[2]));
		entities.add(new BasicNameValuePair("resolution", String.valueOf(resolution)));
		if (editMode == true) {
			// the uuid is needet if the channel should be edited
			uuid = channel.getUuid();
			operation = UrlConnect.modes[1]; // edit
		}
		UrlConnect urlConnect = new UrlConnect(this, uuid, operation);
		urlConnect.addEntities(entities);
		executeUrlConnect(urlConnect);
		return urlSuccess;
	}
}