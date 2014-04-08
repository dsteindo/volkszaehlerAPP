/**
* Convert JSON-String to Objects
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonConverter {

	private JSONObject json;
	private JSONObject temp;
	
	/**
	 * Insert the JSON in the JsonConverter-Object
	 * @param builder String that represents the JSONObject
	 * @throws JSONException
	 */
	public JsonConverter(StringBuilder builder) throws JSONException {
		json = new JSONObject(builder.toString());
	}
	
	/**
	 * Make a List of VlzChannels from the JSON received at the Constructor.
	 * While working it calls the extractChannel-method and writes values
	 * to the global JSONObject temp
	 * @return List of VlzChannels
	 * @throws JSONException
	 */
	public List<VlzChannel> listChannels() throws JSONException {
		List<VlzChannel> channels = new ArrayList<VlzChannel>();
		JSONArray array = json.getJSONArray("channels");
		for (int i = 0; i < array.length(); i++) {
			temp = array.getJSONObject(i);
			channels.add(extractChannel());
		}
		return channels;
	}
	
	/**
	 * Read the Data from the JSON received at the Constructor.
	 * While working it calls the extractData-method and saves
	 * values to the global JSONObject temp
	 * @param vChannel VlzChannel that receives the Data
	 * @return success
	 * @throws JSONException
	 */
	public boolean injectData(VlzChannel vChannel) throws JSONException {
		boolean success = false;
		if (json.has("data")) {
			temp = json.getJSONObject("data");
			if (extract("rows", 0) != 0) {
				VlzData min = extractData(temp.getJSONArray("min"));
				VlzData max = extractData(temp.getJSONArray("max"));
				JSONArray jsonArray = temp.getJSONArray("tuples");
				List<VlzData> tuples = new ArrayList<VlzData>();
				for(int i = 0; i < jsonArray.length(); i++) {
					JSONArray array = jsonArray.getJSONArray(i);
					tuples.add(extractData(array));
				}	
				vChannel.setValues(min, max, tuples);
				success = true;
			}
		}
		return success;
	}
	
	/**
	 * Extract the VlzData-Object from the JSONArray
	 * @param array JSONArray that should be extracted
	 * @return VlzData-Object
	 * @throws JSONException
	 */
	private VlzData extractData(JSONArray array) throws JSONException {
		VlzData vData = new VlzData();
		vData.setTimestamp(array.getLong(0));
		vData.setValue(array.getDouble(1));
		return vData;
	}
	
	/**
	 * A method that creates a new VlzChannel-Object and fills
	 * its properties. It calls the extract-method
	 * @return VlzChannel that was created
	 * @throws JSONException
	 */
	private VlzChannel extractChannel() throws JSONException {
		VlzChannel vChannel = new VlzChannel();
		vChannel.setTitle(extract("title", null));
		vChannel.setType(extract("type", null));
		vChannel.setUuid(extract("uuid", null));
		vChannel.setColor(extract("color", "blue"));
		vChannel.setResolution(extract("resolution", 0));
		return vChannel;
	}
	
	/**
	 * Extracts a Value from the global JSONObject temp 
	 * @param name String, the Key
	 * @param value String, the default value
	 * @return value extracted or default value
	 * @throws JSONException
	 */
	private String extract(String name, String value) throws JSONException {
		if (temp.has(name)) {
			value = temp.getString(name);
		}
		return value;
	}
	
	/**
	 * Extracts a Value from the global JSONObject temp 
	 * @param name String, the key
	 * @param value Integer, the default value 
	 * @return value extracted or default value
	 * @throws JSONException
	 */
	private int extract(String name, int value) throws JSONException {
		if (temp.has(name)) {
			value = temp.getInt(name);
		}
		return value;
	}
}
