/**
* EditorActivity
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditorActivity extends Activity {

	private JsonController jsController;
	private Boolean isEditing;
	private ArrayAdapter<VlzChannel> adapter;
	private Spinner spinner_channel;
	private EditorSpinControl spin_color;
	private EditorSpinControl spin_type;
	private VlzChannel vChannel;
	private EditText editor_etx_title;
	private EditText editor_etx_resolution;

	public final static String[] colors = { "black", "blue", "gray", "green",
			"red", "white", "yellow" };
	public final static String[] types = { "power", "electric meter", "valve",
			"temperature", "powersensor", "gas" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);

		isEditing = getIntent().getBooleanExtra(MainActivity.is_editing, false);

		// get global Storage from ApplicationContext
		
		GlobalStorage globalStorage = (GlobalStorage) getApplicationContext();

		if (isEditing == true) {
			// make a JsonController with global saved values
			jsController = new JsonController(globalStorage);
		} else {
			// make a JsonController with the values the user gave us
			String server = getIntent().getStringExtra(MainActivity.server_name);
			// don't overwrite the values in the globalStorage
			jsController = new JsonController(globalStorage, server, false);
		}

		// just show the server name
		
		EditText editor_etx_server = ((EditText) findViewById(R.id.editor_etx_server));
		editor_etx_server.setText(jsController.getServerName());
		editor_etx_server.setEnabled(false);

		// apply lists and selection on the Spinners 
		
		spin_color = new EditorSpinControl(this, R.id.editor_spinner_color, colors);
		spin_type = new EditorSpinControl(this, R.id.editor_spinner_type, types);

		// save the EditTexts in variables
		
		editor_etx_title = (EditText) findViewById(R.id.editor_etx_title);
		editor_etx_resolution = (EditText) findViewById(R.id.editor_etx_resolution);

		if (isEditing == true) {
			// the next three methods define the Spinner for channel selection
			assignSpinner(globalStorage);
			setListener();	
			selectionChanged();
		} else {
			// disable some views used for editing
			disableViews();
		}
	}
	
	/**
	 * Disable some views if no new channel is made
	 */
	private void disableViews() {
		(findViewById(R.id.editor_btn_reset)).setEnabled(false);
		(findViewById(R.id.editor_btn_delete)).setEnabled(false);
		(findViewById(R.id.editor_spinner_channel)).setVisibility(View.GONE);
		(findViewById(R.id.editor_txv_channel)).setVisibility(View.GONE);		
	}

	/**
	 * Create a Listener for the spinner. If the selection has changed
	 * call the selectionChanged-method
	 */
	private void setListener() {
		OnItemSelectedListener listener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				selectionChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				int position = adapter.getPosition(vChannel);
				spinner_channel.setSelection(position);
			}
		};
		spinner_channel.setOnItemSelectedListener(listener);
	}

	/**
	 * Fill the Spinner for the channel selection with the channels
	 * from the globalStorage
	 * @param globalStorage
	 */
	private void assignSpinner(GlobalStorage globalStorage) {
		spinner_channel = (Spinner) findViewById(R.id.editor_spinner_channel);
		List<VlzChannel> list = globalStorage.getSelectedItems();
		adapter = new ArrayAdapter<VlzChannel>(this, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_channel.setAdapter(adapter);
	}

	/**
	 * Notify the adapter for the channel-Spinner that the values changed
	 */
	public void notifySpinner() {
		if (isEditing == true) {
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * Trigger the deletion of the channel
	 * @param view
	 */
	public void onClickDelete(View view) {
		EditChannelAsync eca = new EditChannelAsync(this, jsController);
		eca.triggerDelete(vChannel);
		eca.execute();
	}

	/**
	 * Set the channel that is being edited to the selected channel
	 * from the channel-Spinner
	 */
	public void selectionChanged() {
		vChannel = (VlzChannel) spinner_channel.getSelectedItem();
		reset();
	}

	/**
	 * Fill in the EditTexts and the Spinners for color and type
	 * the values of the selected channel
	 */
	private void reset() {
		editor_etx_title.setText(vChannel.getTitle());
		String resolution = String.valueOf(vChannel.getResolution());
		editor_etx_resolution.setText(resolution);
		spin_color.setSelected(vChannel.getColor());
		spin_type.setSelected(vChannel.getType());
	}

	/**
	 * Calls the reset-method
	 * @param view
	 */
	public void onClickReset(View view) {
		reset();
	}

	/**
	 * Save button. Looks if user input is valid. If user input is valid calls
	 * the saveValues-method, else makes an error message
	 * @param view
	 */
	public void onClickSave(View view) {
		String title = editor_etx_title.getText().toString();
		String color = spin_color.getSelected();
		String type = spin_type.getSelected();
		try {
			String res = editor_etx_resolution.getText().toString();
			int resolution = Integer.parseInt(res);
			if (title.isEmpty() == false && resolution > -1) {
				saveValues(title, color, type, resolution);
			} else {
				Toast.makeText(this, "Invalid Values", Toast.LENGTH_SHORT).show();
			}
		} catch (NumberFormatException ex) {
			Toast.makeText(this, "Invalid Resolution", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Make a EditChannelAsync-Task to save the values.
	 * The EditChannelAsync uses the JsonController for the client-to-server
	 * communication
	 * @param title
	 * @param color
	 * @param type
	 * @param resolution
	 */
	private void saveValues(String title, String color, String type, int resolution) {
		EditChannelAsync eca = new EditChannelAsync(this, jsController);
		eca.setEditMode(isEditing, vChannel);
		eca.addParams(title, color, type, resolution);
		eca.execute();
	}

	/**
	 * Return to main Activity, obsolete -> Use Hardware buttons
	 * @param view
	 */
	public void onClickReturn(View view) {
		finish();
	}
}