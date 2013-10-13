package com.dank.festivalapp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
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
import com.dank.festivalapp.lib.BandsDataSource;

public class BandActivity extends ListActivity  implements OnItemSelectedListener {

	public final static String EXTRA_MESSAGE = "com.dank.festivalapp.MESSAGE";		
	public static BandsDataSource datasource;
	private Spinner spinner;
	private final static String ALL_FLAVORS_NAME = "All Flavors";
	private String festivalID = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_band);
		setupActionBar();
		
		festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");
		
		datasource = new BandsDataSource(this);
		datasource.open();
				
		if (datasource.getCountBands() > 0)
		{
			TextView textViewName = (TextView) findViewById(R.id.textView1);	    
			textViewName.setVisibility( View.INVISIBLE );
		}
		
		List<String> allFlavors = datasource.getAllFlavors(festivalID);		
		allFlavors.add(0, ALL_FLAVORS_NAME);
		
		// spinner
		spinner = (Spinner) findViewById(R.id.spinner1);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allFlavors);

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(this);
		
		drawContent(ALL_FLAVORS_NAME);
	}

	private void drawContent(String flavor)
	{
		// List content
		ArrayList<Band> allBands = null;
		
		if (flavor.compareTo(ALL_FLAVORS_NAME) == 0)
			allBands = datasource.getAllBandsWithFlavors(festivalID);
		else 
			allBands = datasource.getAllBandsWithFlavor(flavor, festivalID );
				
		BandListAdapter adapter = new BandListAdapter( this, allBands );
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
//		getMenuInflater().inflate(R.menu.band, menu);
		return true;
	}

	
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{	  
        super.onListItemClick(l, v, position, id);
        Band band = (Band) getListAdapter().getItem(position);
				
		Intent intent = new Intent(this, BandDetailsActivity.class);  
		Log.w("BAND", band.getBandname() + " "  + band.getID() );
    	intent.putExtra(EXTRA_MESSAGE, band.getID().toString() );
    	startActivity(intent);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> main, View view, int position, long Id) 
	{
		String spinnerValString = (String) spinner.getSelectedItem();
		drawContent(spinnerValString);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
