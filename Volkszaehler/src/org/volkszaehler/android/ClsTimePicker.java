/**
* Android Widget to choose a hour and minute
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import android.app.TimePickerDialog;
import android.widget.TimePicker;

public class ClsTimePicker implements TimePickerDialog.OnTimeSetListener {

	private ClsDateSetLogic dateSetLogic;

	private Boolean clickOnce;

	private int hour;
	private int minute;

	public ClsTimePicker(ClsDateSetLogic dateSetLogic) {
		this.dateSetLogic = dateSetLogic;
	}

	public void resetClickOnce() {
		clickOnce = true;
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hour, int minute) {
		if (clickOnce == true) {
			this.hour = hour;
			this.minute = minute;
			// Fill the collected values in the EditText-Input 
			dateSetLogic.refreshEditText();
			clickOnce = false;
		}
	}
}