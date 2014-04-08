/**
* VlzChannel Model
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.List;

public class VlzChannel {

	private String color;
	private String title;
	private String type;
	private String uuid;

	private int resolution;

	private long t_start;
	private long t_end;

	private List<VlzData> tuples;
	private VlzData min;
	private VlzData max;
	private boolean valuesSet;

	/**
	 * Constructor
	 */
	public VlzChannel() {
		super();
		this.valuesSet = false;
	}

	/**
	 * Set the start and end time of the VlzChannel-Object
	 * @param t_start long, start time
	 * @param t_end long, end time
	 */
	public void setTimestamp(long t_start, long t_end) {
		this.t_start = t_start;
		this.t_end = t_end;
	}

	/**
	 * @return VlzData with the smallest value
	 */
	public VlzData getMin() {
		return this.min;
	}

	/**
	 * @return VlzData with the biggest value
	 */
	public VlzData getMax() {
		return this.max;
	}

	/**
	 * @return List of all VlzData-Objects
	 */
	public List<VlzData> getTuples() {
		return this.tuples;
	}
	
	/**
	 * @return true if values are set
	 */
	public boolean hasValues() {
		return valuesSet;
	}

	/**
	 * Set the values of the VlzChannel-Object and set the hasValues-Flag to true
	 * @param min VlzData with the smallest value
	 * @param max VlzData with the biggest value
	 * @param tuples List of VlzData-Objects
	 */
	public void setValues(VlzData min, VlzData max, List<VlzData> tuples) {
		this.valuesSet = true;
		this.min = min;
		this.max = max;
		this.tuples = tuples;
	}

	/**
	 * 
	 * @param valuesLimiter int, the amount of tuples
	 * @return String "?from=start&to=end&tuples=valuesLimiter"
	 */
	public String getParams(int valuesLimiter) {
		String retVal = "?from=" + t_start + "&to=" + t_end; // More Logic here
		return retVal + "&tuples=" + valuesLimiter;
	}

	/**
	 * @return color 
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return title 
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return resolution
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * @param resolution
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public String toString() {
		return title;
	}
}