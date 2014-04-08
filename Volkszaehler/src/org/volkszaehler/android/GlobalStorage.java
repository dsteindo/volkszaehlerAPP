/**
* Global Storage for the Android Activity
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.util.List;

import android.app.Application;

public class GlobalStorage extends Application {
	private String jsServer;

	private int executionTime; // Time in Seconds
	private int valuesLimiter; // Tuples

	private List<VlzChannel> shortlist;
	private List<VlzChannel> selectedItems;

	public String getJsServer() {
		return jsServer;
	}

	public void setJsServer(String jsServer) {
		this.jsServer = jsServer;
	}

	public int getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public int getValuesLimiter() {
		return valuesLimiter;
	}

	public void setValuesLimiter(int valuesLimiter) {
		this.valuesLimiter = valuesLimiter;
	}

	public List<VlzChannel> getShortlist() {
		return shortlist;
	}

	public void setShortlist(List<VlzChannel> shortlist) {
		this.shortlist = shortlist;
	}

	public List<VlzChannel> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<VlzChannel> selectedItems) {
		this.selectedItems = selectedItems;
	}
}