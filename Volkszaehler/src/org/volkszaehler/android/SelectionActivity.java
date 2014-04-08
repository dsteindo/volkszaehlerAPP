/**
* Selection Activity for channel selection
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectionActivity extends Activity {

	// http://theopentutorials.com/tutorials/android/listview/android-multiple-selection-listview/

	private GlobalStorage globalStorage;
	private ListView listView;
	private ArrayAdapter<VlzChannel> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selection);

		// The user should not be able to open this Activity without
		// loading channels and having at least one channel for selection
		
		listView = (ListView) findViewById(R.id.selection_listView);

		globalStorage = (GlobalStorage) getApplicationContext();

		List<VlzChannel> list = globalStorage.getShortlist();

		// Add the list of channels that can be selected to the 
		// listView-Widget
		
		int layout = android.R.layout.simple_list_item_multiple_choice;
		adapter = new ArrayAdapter<VlzChannel>(this, layout, list);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
		
		// Check all elements that are selected
		
		List<VlzChannel> selected = globalStorage.getSelectedItems();
		
		for(VlzChannel vChannel : selected) {
			int position = adapter.getPosition(vChannel);
			listView.setItemChecked(position, true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selection, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		// First add all selected channels to the checked-Array
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		List<VlzChannel> selectedItems = new ArrayList<VlzChannel>();
		for (int i = 0; i < checked.size(); i++) {
			int position = checked.keyAt(i);
			if (checked.valueAt(i) == true) {
				selectedItems.add(adapter.getItem(position));
			}
		}
		// Control if the user selected one channel
		if (selectedItems.size() > 0) {
			globalStorage.setSelectedItems(selectedItems);
			finish();
		} else {
			String text = "Check at least one Channel";
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
	}
}
