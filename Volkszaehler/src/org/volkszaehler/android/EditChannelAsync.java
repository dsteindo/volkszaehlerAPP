/**
* AsyncTask to edit channels
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class EditChannelAsync extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private JsonController jsController;
	private String[] values;
	private ProgressDialog progressDialog;
	private int resolution;
	private boolean editMode;
	private boolean delete;
	private VlzChannel vChannel;

	/**
	 * Constructor, call setEditMode and addParams next.
	 * Call triggerDelete to delete the channel
	 * @param context EditorActivity
	 * @param jsController 
	 */
	public EditChannelAsync(Context context, JsonController jsController) {
		this.context = context;
		this.jsController = jsController;
		this.delete = false;
	}

	/**
	 * Set the editMode to true to change an existing channel, set it to
	 * false to make a new channel. The vChannel can be null if the 
	 * editMode is false 
	 * @param editMode
	 * @param vChannel
	 */
	public void setEditMode(boolean editMode, VlzChannel vChannel) {
		this.editMode = editMode;
		this.vChannel = vChannel;
	}

	/**
	 * Set the properties of the channel
	 * @param title
	 * @param color
	 * @param type
	 * @param resolution
	 */
	public void addParams(String title, String color, String type, int resolution) {
		values = new String[] { title, color, type };
		this.resolution = resolution;
	}

	/**
	 * Delete the given channel
	 * @param vChannel
	 */
	public void triggerDelete(VlzChannel vChannel) {
		this.vChannel = vChannel;
		delete = true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String text = "Channel is being ";
		if (delete == true) {
			text = text + "Deleted";
		} else if (editMode == true) {
			text = text + "Modified";
		} else {
			text = text + "Created";
		}
		progressDialog = ProgressDialog.show(context, "Working", text);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean success = false;
		if (delete == false) {
			success = jsController.editChannel(vChannel, values, resolution, editMode);
		} else {
			success = jsController.deleteChannel(vChannel);
		}
		return success;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		if (result == true && (delete == true || editMode == false)) {
			Toast.makeText(context, "Success! Refresh your Channels",Toast.LENGTH_SHORT).show();
			// close EditorActivity and return to MainActivity
			((Activity) context).finish();
		} else if (result == true) {
			Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
			// just update the channel
			writeValues();
		} else {
			Toast.makeText(context, jsController.getErrorMessage(),Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * If the values could be saved, change the channel locally
	 * so that it doesn't need to be fetched again
	 */
	private void writeValues() {
		vChannel.setTitle(values[0]);
		vChannel.setColor(values[1]);
		vChannel.setType(values[2]);
		vChannel.setResolution(resolution);
		// notify the MainActivity to change its fields
		((EditorActivity) context).notifySpinner();
	}
}
