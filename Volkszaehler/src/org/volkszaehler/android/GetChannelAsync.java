/**
* AsyncTask to load channels
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class GetChannelAsync extends AsyncTask<Void, Void, List<VlzChannel>> {

	private Context context;
	private JsonController jsController;
	private ProgressDialog progressDialog;

	/**
	 * @param context MainActivity most likely
	 * @param jsonController
	 */
	public GetChannelAsync(Context context, JsonController jsonController) {
		this.context = context;
		this.jsController = jsonController;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String message = "The channels are loaded, please wait a moment";
		progressDialog = ProgressDialog.show(context, "One Moment Please", message);
	}

	@Override
	protected List<VlzChannel> doInBackground(Void... params) {
		return jsController.getChannels();
	}

	@Override
	protected void onPostExecute(List<VlzChannel> result) {
		super.onPostExecute(result);
		progressDialog.dismiss();

		if(result != null && result.size() > 0) {
			GlobalStorage globalStorage = (GlobalStorage) context.getApplicationContext();
			globalStorage.setShortlist(result);
			List<VlzChannel> first = new ArrayList<VlzChannel>();
			first.add(result.get(0));
			globalStorage.setSelectedItems(first);
			((MainActivity)context).setEnabled(true);
		} else {
			String error = jsController.getErrorMessage();
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
			((MainActivity)context).setEnabled(false);
		}
	}
}