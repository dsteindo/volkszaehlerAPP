/**
* GraphActivity 
*
* @copyright Copyright (c) 2014, VolkszahelerAPP
* @package org.volkszaehler.android
* @license http://opensource.org/licenses/gpl-license.php GNU Public License
*/

package org.volkszaehler.android;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GraphActivity extends Activity {

	private boolean sameDay;
	private GlobalStorage globalStorage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		sameDay = getIntent().getBooleanExtra(MainActivity.is_same_day, false);
		
		globalStorage = (GlobalStorage) getApplicationContext();
		
		List<VlzChannel> channels = globalStorage.getSelectedItems();
		List<VlzChannel> valid = new ArrayList<VlzChannel>();
		
		for (VlzChannel vlzChannel : channels) {
			if (vlzChannel != null && vlzChannel.hasValues()) {
				if (vlzChannel.getTuples().size() > 1) {
					valid.add(vlzChannel);
				}
			}
		}
		
		if(valid.size() > 0) {
			generateGraph(valid);
		} else {
			Toast.makeText(this, "No enough Values", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This method draws the graph. First of all it makes a series out of
	 * every valid channel and then formats the labels.
	 * Please visit http://android-graphview.org/ for more information
	 * @param valid List of valid VlzChannels
	 */
	private void generateGraph(List<VlzChannel> valid) {
		GraphView graph = new LineGraphView(this, "example");
		
		graph.getGraphViewStyle().setVerticalLabelsAlign(Align.LEFT);
		
		if (sameDay == true) {
			String name = MainActivity.some_time;
			long timestamp = getIntent().getLongExtra(name, 0);
			String d = VlzData.formatDate(timestamp, DateFormat.LONG);
			graph.setTitle("Graph for " + d);
		} 
		
		for (VlzChannel channel : valid) {
			List<VlzData> liste = channel.getTuples();
			if (liste.size() > 1) {
				String name = channel.getTitle() + " (" + channel.getType() + ")";
				int color = Color.BLUE;
				try {
					color = Color.parseColor(channel.getColor());
				} catch (IllegalArgumentException ex) {
					String error = "Invalid Color: " + channel.getColor();
					Toast.makeText(this, error, Toast.LENGTH_LONG).show();
				}
				graph.addSeries(createSeries(name, color, liste));
			}
		}

		addLabelFormatter(graph);
		
		graph.setShowLegend(true);
		graph.setLegendAlign(LegendAlign.BOTTOM);
		graph.getGraphViewStyle().setLegendWidth(550);
		graph.getGraphViewStyle().setLegendMarginBottom(40);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graphView);
		layout.addView(graph);
		
	}
	
	/**
	 * Makes a new series with the given Parameters. Adds every VlzData-Object 
	 * from the List<VlzData> to the series. The x-Axis is for time and the
	 * y-Axis is for the value.
	 * @param name of the channel
	 * @param color of the channel
	 * @param liste List<VlzData>
	 * @return GraphViewSeries
	 */
	private GraphViewSeries createSeries(String name, int color ,List<VlzData> liste) {
		int size = liste.size();
		GraphViewData[] array = new GraphViewData[size];
		for (int i = 0; i < size; i++) {
			VlzData vlz = liste.get(i);
			array[i] = new GraphViewData(vlz.getTimestamp(), vlz.getValue());
		}
		//int red = (value >> 16) & 0xFF;
		//int green = (value >> 8) & 0xFF;
		//int blue = value & 0xFF;
		//int result = Color.argb(0x90, red, green, blue);
		GraphViewSeriesStyle style = new GraphViewSeriesStyle(color, 2);
		GraphViewSeries series = new GraphViewSeries(name, style, array);
		return series;
	}
	
	/**
	 * Format the Labels, for more information visit http://android-graphview.org/
	 * @param graph
	 */
	private void addLabelFormatter(GraphView graph) {
		graph.setCustomLabelFormatter(new CustomLabelFormatter() {
			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					if (sameDay == true) {
						return VlzData.formatTime((long) value);
					}
					return VlzData.formatDateTime((long) value);
				}
				return null; // let graphview generate Y-axis label for us
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph, menu);
		return true;
	}
}
