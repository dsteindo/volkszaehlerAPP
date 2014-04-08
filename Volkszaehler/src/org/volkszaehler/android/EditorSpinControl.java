/**
* Class for smart use with Android Widget Spinner
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EditorSpinControl {

	private ArrayAdapter<String> adapter;
	private Spinner spinner;
	
	/**
	 * Create a new Instance
	 * @param activity EditorActivity
	 * @param id SpinnerID
	 * @param list of Strings, color or type
	 */
	public EditorSpinControl(Activity activity, int id, String[] list) {
		this.spinner = (Spinner) activity.findViewById(id);
		adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	/**
	 * Force the Spinner to show the item
	 * @param item
	 */
	public void setSelected(String item) {
		int posColor = adapter.getPosition(item);
		spinner.setSelection(posColor);
	}
	
	/**
	 * @return selected Item
	 */
	public String getSelected() {
		return (String) spinner.getSelectedItem();
	}
}
