/**
* Controller Class for time manipulation
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/


package org.volkszaehler.android;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

public class ClsDateSetLogic {

	private Context context;

	private EditText etx_start_time;
	private EditText etx_end_time;

	private ClsDatePicker datePicker;
	private ClsTimePicker timePicker;

	private String mode;

	private DateFormat df;

	public static final String start_mode = "START";
	public static final String end_mode = "END";

	/**
	 * Make a new Instance of the ClsDateSetLogic Class
	 * @param context MainActivity
	 * @param etx_start_time EditText 
	 * @param etx_end_time EditText
	 */
	public ClsDateSetLogic(Context context, EditText etx_start_time, EditText etx_end_time) {
		this.context = context;
		this.etx_start_time = etx_start_time;
		this.etx_end_time = etx_end_time;

		// make new Instances of ClsDatePicker and ClsTimePicker
		
		datePicker = new ClsDatePicker(this);
		timePicker = new ClsTimePicker(this);
		
		this.df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);

		resetEditText();
	}

	/**
	 * Sets the end time to the current time and the start time 
	 * to the current time - 60 minutes
	 */
	private void resetEditText() {
		Calendar cal = Calendar.getInstance();
		// set the etx_end_time to current time
		etx_end_time.setText(df.format(cal.getTime()));
		// subtract 60 minutes from now
		cal.add(Calendar.MINUTE, -60);
		// set the etx_start_time to the current time - 60 minutes
		etx_start_time.setText(df.format(cal.getTime()));
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Calls the ClsDatePicker-dialog if the dialogID is 0 to
	 * set the year, month and day. Else calls the ClsTimePicker-dialog
	 * if the dialogID is 1 to set the hour and minute. Does 
	 * nothing if the dialogID is not valid.
	 * @param dialogID dialog that should be shown
	 */
	public void showDialog(int dialogID) {
		Calendar c = getCalendar();

		if (dialogID == 0) {
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			datePicker.resetClickOnce();
			// make a new Android Widget DatePickerDialog and feed it with
			// the context, the ClsDatePicker and the time informations
			DatePickerDialog dpd = new DatePickerDialog(context, datePicker, year, month, day);
			dpd.show();
		} else if (dialogID == 1) {
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			timePicker.resetClickOnce();
			// same here with the TimePickerDialog
			TimePickerDialog tpd = new TimePickerDialog(context, timePicker, hour, minute, is24HourView());
			tpd.show();
		}
	}

	/**
	 * @return true if the day is the same
	 */
	public Boolean isSameDay() {
		DateFormat df;
		df = DateFormat.getDateInstance(DateFormat.LONG);
		String start = df.format(getCalendar(start_mode).getTime());
		String end = df.format(getCalendar(end_mode).getTime());
		return start.equals(end);
	}
	
	/**
	 * Reads the text of the EditText-Input in dependency of the global mode.
	 * If mode is start_mode the start time will be read, else the 
	 * end time will be read 
	 * @return text, null when mode is not valid
	 */
	private String getText() {
		String s = null;
		if (this.mode.equals(start_mode)) {
			s = etx_start_time.getText().toString();
		} else if (this.mode.equals(end_mode)) {
			s = etx_end_time.getText().toString();
		}
		return s;
	}

	/**
	 * @return clock format
	 */
	public Boolean is24HourView() {
		String s = getText();
		return !s.contains("AM") && !s.contains("PM");
	}

	/**
	 * Reads the start or end time in dependency of the global mode
	 * @return calendar
	 */
	public Calendar getCalendar() {
		String s = getText();

		Calendar c = Calendar.getInstance();

		try {
			c.setTime(df.parse(s));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 *  Reads the start or end time in dependency of the mode
	 * @param mode that should be read
	 * @return calendar
	 */
	public Calendar getCalendar(String mode) {
		this.mode = mode;
		return getCalendar();
	}

	/**
	 * Reads the values of the date and timePicker, makes an
	 * error message if the start time is after the end time.
	 */
	public void refreshEditText() {
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		int day = datePicker.getDay();
		int hour = timePicker.getHour();
		int minute = timePicker.getMinute();

		Calendar result = Calendar.getInstance();
		result.set(year, month, day, hour, minute);

		if (this.mode.equals(start_mode)) {
			if (result.compareTo(getCalendar(end_mode)) == -1) {
				etx_start_time.setText(df.format(result.getTime()));
			} else {
				String text = "Start Time must be smaller than End Time";
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		} else if (this.mode.equals(end_mode)) {
			if (result.compareTo(getCalendar(start_mode)) == 1) {
				etx_end_time.setText(df.format(result.getTime()));
			} else {
				String text = "End Time must be bigger than Start Time";
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		}
	}
}