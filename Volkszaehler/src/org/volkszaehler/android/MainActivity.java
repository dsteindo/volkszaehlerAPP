/**
* Andoid MainActivity
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity  {

	private GlobalStorage globalStorage;

	private SharedPreferences prefs;

	private List<View> views;

	private EditText etx_server;

	private JsonController jsController;
	private ClsDateSetLogic dateSetLogic;

	public static final String server_name = "org.volkszaehler.android.SERVER";
	public static final String is_editing = "org.volkszaehler.android.EDITING";
	public static final String is_same_day = "org.volkszaehler.android.ISSAME";
	public static final String some_time = "org.volkszaehler.android.SOMETIME";
	
	private boolean readPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.etx_server = (EditText) findViewById(R.id.etx_server);

		EditText etx_start_time = (EditText) findViewById(R.id.etx_start_time);
		EditText etx_end_time = (EditText) findViewById(R.id.etx_end_time);

		this.dateSetLogic = new ClsDateSetLogic(this, etx_start_time, etx_end_time);

		globalStorage = (GlobalStorage) getApplicationContext();
		
		initializeViewController();

		jsController = new JsonController(globalStorage);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		readPrefs = true;
	}

	/**
	 * Fills the global List<view> views with GUI-Elements that 
	 * will be enabled when the globalStorage contains selected Channels.
	 * Calls the setEnabled-method
	 */
	private void initializeViewController() {
		views = new ArrayList<View>();
		views.add(findViewById(R.id.btn_select));
		views.add(findViewById(R.id.btn_showTable));
		views.add(findViewById(R.id.btn_drawGraph));
		views.add(findViewById(R.id.btn_editChannel));
		boolean enabled = globalStorage.getSelectedItems() != null;
		setEnabled(enabled);
	}

	/**
	 * Enables or disables the views
	 * @param enabled boolean, true if views should be enabled
	 */
	public void setEnabled(boolean enabled) {
		for(View view : views) {
			view.setEnabled(enabled);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (readPrefs == true) {
			String serverName = prefs.getString("server_name","demo.volkszaehler.org");
			etx_server.setText(serverName);
			try {
				int timeout = Integer.parseInt(prefs.getString("timeout", "15"));
				int maxTuples = Integer.parseInt(prefs.getString("max_tuples","1000"));
				saveValues(timeout, maxTuples);
			} catch (Exception ex) {
				Toast.makeText(this, "Check your Preferences",Toast.LENGTH_SHORT).show();
			}
			readPrefs = false;
		}
	}

	/**
	 * Method which is called from the onResume-method after reading
	 * the preferences. Writes the timeout and the max tuples amount in the
	 * globalStorage if they are valid
	 * @param timeout execution time in seconds, minimum 10, standard 15
	 * @param maxTuples amount of tuples, minimum 10, standard 1000
	 */
	private void saveValues(int timeout, int maxTuples) {
		if (timeout < 10) {
			Toast.makeText(this, "Error: Timout less than 10 Seconds",
					Toast.LENGTH_SHORT).show();
			timeout = 15;
		}
		globalStorage.setExecutionTime(timeout);
		if (maxTuples < 10) {
			Toast.makeText(this, "Error: Tuples less than 10",
					Toast.LENGTH_SHORT).show();
			maxTuples = 1000;
		}
		globalStorage.setValuesLimiter(maxTuples);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// http://www.vogella.com/tutorials/AndroidActionBar/article.html
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			// R.id.menu_settings will be defined if you create a menu_settings
			// item in the res/menu/activity_main.xml
			readPrefs = true;
			Intent intent = new Intent(this, Preferences.class);
			this.startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * The SelectionActivity will appear at the phone
	 * @param view button pressed
	 */
	public void onClickSelect(View view) {
		if (globalStorage.getShortlist() != null) {
			Intent intent = new Intent(this, SelectionActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(this,"Unmöglicher Fehler", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * The server name will be saved in the preferences
	 * @param view button pressed
	 */
	public void onClickSave(View view) {
		String server = etx_server.getText().toString();
		SharedPreferences.Editor editor = this.prefs.edit();

		editor.putString("server_name", server);

		editor.commit();

		Toast.makeText(this, "Server Name saved", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Makes a new GetChannelAsync Instance and executes this AsyncTask.
	 * Also makes a new Instance of the JsonController with the server name
	 * read from the user input
	 * @param view button pressed
	 */
	public void onClickFetch(View view) {
		String server = etx_server.getText().toString();
		this.jsController = new JsonController(globalStorage, server, true);
		GetChannelAsync gca = new GetChannelAsync(this, jsController);
		gca.execute();
	}

	/**
	 * Makes a new TableActivity and calls the fetchDataAndStartIntent-method
	 * @param view button pressed
	 */
	public void onClickShowTable(View view) {
		Intent intent = new Intent(this, TableActivity.class);
		fetchDataAndStartIntent(intent);
	}

	/**
	 * Makes a new GraphActivity, asks the DateSetLogic controller if the end and
	 * start time are on the same day and gives the GraphActivity this extra information.
	 * Also adds the time of one to the extra informations.
	 * Calls the fetchDataAndStartIntent-method 
	 * @param view
	 */
	public void onClickDrawGraph(View view) {
		Intent intent = new Intent(this, GraphActivity.class);
		intent.putExtra(is_same_day, dateSetLogic.isSameDay());
		long timestamp = dateSetLogic.getCalendar().getTimeInMillis();
		intent.putExtra(some_time, timestamp);
		fetchDataAndStartIntent(intent);
	}

	/**
	 * This method calls the GetDataAsync-Task and gives the intent-parameter
	 * to the AsyncTask. Also calls the setTimestamps-method of the AsyncTask 
	 * and gives the start and end time. 
	 * @param intent that should be started
	 */
	private void fetchDataAndStartIntent(Intent intent) {
		long start = dateSetLogic.getCalendar(ClsDateSetLogic.start_mode).getTimeInMillis();

		long end = dateSetLogic.getCalendar(ClsDateSetLogic.end_mode).getTimeInMillis();

		GetDataAsync gda = new GetDataAsync(jsController, this, intent);
		gda.setTimestamps(start, end);
		gda.execute();
	}

	public void onClickTimeSet(View view) {
		if (view.getId() == R.id.btn_start_time) {
			dateSetLogic.setMode(ClsDateSetLogic.start_mode);
		} else if (view.getId() == R.id.btn_end_time) {
			dateSetLogic.setMode(ClsDateSetLogic.end_mode);
		}
		dateSetLogic.showDialog(0);
	}

	/**
	 * Calls the startEditorAcitvity-method with the server name
	 * read from the JsonController to edit a channel
	 * @param view
	 */
	public void onClickEditChannel(View view) {
		startEditorActivity(true, jsController.getServerName());
	}

	/**
	 * Calls the startEditorActivity-method with the server name
	 * read from the user input to make a channel
	 * @param view
	 */
	public void onClickMakeChannel(View view) {
		startEditorActivity(false, etx_server.getText().toString());
	}

	/**
	 * Gets called by either onClickEditChannel or onClickMakeChannel.
	 * Starts the EditorActivity
	 * @param edit 
	 * @param server
	 */
	private void startEditorActivity(Boolean edit, String server) {
		Intent intent = new Intent(this, EditorActivity.class);
		intent.putExtra(server_name, server);
		intent.putExtra(is_editing, edit);
		startActivity(intent);
	}
}