/**
* Android Widget to choose a year, month and day
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/


package org.volkszaehler.android;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

public class ClsDatePicker implements DatePickerDialog.OnDateSetListener {

	private ClsDateSetLogic dateSetLogic;

	private Boolean clickOnce;

	private int year;
	private int month;
	private int day;

	public ClsDatePicker(ClsDateSetLogic dateSetLogic) {
		this.dateSetLogic = dateSetLogic;
	}

	public void resetClickOnce() {
		clickOnce = true;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	@Override
	public void onDateSet(DatePicker arg0, int year, int month, int day) {
		if (clickOnce == true) {
			this.year = year;
			this.month = month;
			this.day = day;
			// Call the ClsTimePicker
			dateSetLogic.showDialog(1);
			clickOnce = false;
		}
	}
}