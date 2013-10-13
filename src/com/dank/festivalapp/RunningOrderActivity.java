package com.dank.festivalapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dank.festivalapp.lib.Band;
import com.dank.festivalapp.lib.RunningOrderDataSource;

public class RunningOrderActivity extends ListActivity implements OnItemSelectedListener  {

	public static RunningOrderDataSource datasource;
	private Spinner spinnerDate;
	private Spinner spinnerStage;
	private String festivalID = "";

	public final static String PRIVATE_RUNNING_ORDER = "PRIVATE_RUNNING_ORDER";

	private final static String ALL_STAGES_NAME = "All Stages";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_order);

		festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");
		
		datasource = new RunningOrderDataSource(this);
		datasource.open();
		List<String> allDates = datasource.getAllDays(festivalID);
		
		spinnerDate = (Spinner) findViewById(R.id.spinner1);
		spinnerStage = (Spinner) findViewById(R.id.spinner2);
		
		if (allDates.size() > 0)
		{
			TextView textViewName = (TextView) findViewById(R.id.textView1);	    
			textViewName.setVisibility( View.INVISIBLE );
			drawSpinner(allDates);
		} 
		else {
			// make spinner invisible
			spinnerDate.setVisibility(View.INVISIBLE);
			spinnerStage.setVisibility(View.INVISIBLE);
		}
		
		setupActionBar();
	}
	

	private void drawSpinner(List<String> allDates)
	{
		// Date Spinner
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allDates);

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerDate.setAdapter(spinnerAdapter);
		spinnerDate.setOnItemSelectedListener(this);

		// Stages Spinner
		ArrayList<String> allStages = datasource.getAllStages(festivalID);
		allStages.add(0, ALL_STAGES_NAME);
		
		ArrayAdapter<String> spinnerStagesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allStages);

		spinnerStagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerStage.setAdapter(spinnerStagesAdapter);
		spinnerStage.setOnItemSelectedListener(this);

		drawContent(allDates.get(0), ALL_STAGES_NAME );
	}

	private void drawContent(String date, String stage)
	{	
		String dbStage = null;
		Boolean showStages = true;
		if (stage.compareTo(ALL_STAGES_NAME) != 0)
		{
			dbStage = stage;
			showStages = false;
		}
			
		ArrayList<Band> data = datasource.getROatDay(
				date, 
				dbStage,
				getSharedPreferences("FestivalApp", MODE_PRIVATE).getBoolean(PRIVATE_RUNNING_ORDER, false),
				festivalID );

		RunningOrderListAdapter adapter = new RunningOrderListAdapter( this, data, showStages);
		setListAdapter(adapter);
		
	}


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.running_order, menu);
		return true;
	}



	public void onItemSelected(AdapterView<?> main, View view, int position, long Id) {

		String spinnerDateItem = (String) spinnerDate.getSelectedItem();
		String spinnerStageItem = (String) spinnerStage.getSelectedItem();

		drawContent(spinnerDateItem, spinnerStageItem);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{	  
        super.onListItemClick(l, v, position, id);
    
        Band band = (Band) getListAdapter().getItem(position);
				
		Intent intent = new Intent(this, RunningOrderBandDetailsActivity.class);
//		Log.w("Show Details", band.getID() + " " + band.getBandname());
    	intent.putExtra(BandActivity.EXTRA_MESSAGE, band.getID().toString() );
    	startActivity(intent);
	}

}
