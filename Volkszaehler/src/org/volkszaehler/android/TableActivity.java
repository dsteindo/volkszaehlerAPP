/**
* Table Activity
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
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

public class TableActivity extends Activity {

	// http://examples.javacodegeeks.com/android/core/ui/expandablelistview/android-expandablelistview-example/
	
	private ExpandableListView table_lv_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table);

		this.table_lv_data = (ExpandableListView) this.findViewById(R.id.table_lv_data);

		fillList();
	}
	
	/**
	 * Read the selected channels from the globalStorage and fill 
	 * the listView with their data.
	 */
	private void fillList() {
		GlobalStorage globalStorage = (GlobalStorage) getApplicationContext();
		
		List<String> parentItems = new ArrayList<String>();
		List<List<VlzData>> childItems = new ArrayList<List<VlzData>>();
		
		List<VlzChannel> liste = globalStorage.getSelectedItems();

		for (VlzChannel vChannel : liste) {
			if (vChannel != null && vChannel.hasValues()) {
				parentItems.add(vChannel.toString());
				childItems.add(vChannel.getTuples());
			}
		}
		
	    table_lv_data.setDividerHeight(2);
	    table_lv_data.setGroupIndicator(null);
	    table_lv_data.setClickable(true);

	    TableListAdapter adapter = new TableListAdapter(parentItems, childItems);
	    adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
	    table_lv_data.setAdapter(adapter);
	}
}