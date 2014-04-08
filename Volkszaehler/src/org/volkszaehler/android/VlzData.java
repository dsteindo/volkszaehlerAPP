/**
* VlzData Model
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.text.DateFormat;
import java.util.Date;

public class VlzData {

	private long timestamp;
	private double value;
	
	private static int format = DateFormat.SHORT; 
	
	/**
	 * Constructor
	 */
	public VlzData ()  {
		super();
	}
	
	/**
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @param timestamp
	 * @return String in short DateTime format
	 */
	public static String formatDateTime(long timestamp) {
		DateFormat df = DateFormat.getDateTimeInstance(format, format);
		return df.format(new Date(timestamp));
	}
	
	/**
	 * @param timestamp
	 * @return String in short Time format
	 */
	public static String formatTime(long timestamp) {
		DateFormat df = DateFormat.getTimeInstance(format);
		return df.format(new Date(timestamp));
	}
	
	/**
	 * @param timestamp
	 * @param format
	 * @return Sting in Date format as given
	 */
	public static String formatDate(long timestamp, int format) {
		DateFormat df = DateFormat.getDateInstance(format);
		return df.format(new Date(timestamp));
	}
	
	@Override
	public String toString() {
		return formatDateTime(timestamp) + " ~ " + value;
	}	
}
