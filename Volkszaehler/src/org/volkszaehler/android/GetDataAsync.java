/**
* AsyncTask to load data 
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetDataAsync extends AsyncTask<Void, Void, Boolean> {

	private JsonController jsController;
	private Context context;
	private ProgressDialog progressDialog;
	private Intent intent;

	private long start;
	private long end;

	/**
	 * This AsyncTask fetches the data for the selected Channels stored
	 * in the globalStorage of the context.
	 * Call setTimestamps next before execution
	 * @param jsController
	 * @param context MainActivity
	 * @param intent TableActivity or GraphActivity
	 */
	public GetDataAsync(JsonController jsController, Context context, Intent intent) {
		this.jsController = jsController;
		this.context = context;
		this.intent = intent;
	}

	public void setTimestamps(long start, long end) {
		this.start = start;
		this.end = end;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, "Fetch Data",
				"Lets fetch some Channels");
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		boolean success = jsController.channelReader(start, end);
		return success;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		progressDialog.dismiss();

		if (result == true) {
			context.startActivity(intent);
		} else {
			String errorMessage =  jsController.getErrorMessage();
			Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
		}
	}
}