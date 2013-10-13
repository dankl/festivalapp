package com.dank.festivalapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dank.festivalapp.lib.Band;
import com.dank.festivalapp.lib.News;
import com.dank.festivalapp.lib.NewsDataSource;
import com.dank.festivalapp.lib.RunningOrderDataSource;

public class MainActivity extends Activity {

	public final static String LAST_UPDATE = "LAST_UPDATE";
	public final static String UPDATE_INTERVAL = "UPDATE_INTERVAL";
	public final static String WARN_TIMER = "WARN_TIMER";
	public final static String FESTIVAL_ID = "FESTIVAL_ID";

	private final static String PROVIDER_PACKAGE_PREFIX = "com.dank.festivalapp.";
	private final static String PROVIDER_SERVICE = ".ProviderService";
	
	
	/**
	 * returns the clean provider name for the given provider application
	 * @param applicationName
	 * @return
	 * @throws NameNotFoundException 
	 */
	private String getProviderName(String applicationName) throws NameNotFoundException
	{
		Resources res = getPackageManager().getResourcesForApplication( applicationName );

		if (res == null)
			throw new NameNotFoundException();

		int id = res.getIdentifier( applicationName + ":string/app_name", null, null);
		return res.getString(id);
	}

	/**
	 * returns the application name for the given festivalID
	 * @param festivalID
	 * @return
	 */
	private String getApplicationName(String festivalID)
	{
		List<String> allProvider = null;
		try {
			allProvider = getAllDataProvider();

			for (String p : allProvider)
				if (festivalID.equals( getProviderName(p) ))
					return p;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * returns the festival logo as a drawable for the given application
	 * @param applicationName
	 * @return
	 * @throws NameNotFoundException
	 */
	private Drawable getFestivalLogo( String applicationName) throws NameNotFoundException
	{
		Resources res = getPackageManager().getResourcesForApplication( applicationName );

		if (res == null)
			throw new NameNotFoundException();
		    
		return res.getDrawable( res.getIdentifier( applicationName + ":drawable/festival_logo", null, null) );
	}
	
	/**
	 * returns the application name of the current data provider, 
	 * if no one was defined, the first provider found will be taken
	 * @return
	 */
	private String getCurrDataProvider()
	{		
		String festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");

		List<String> allProvider = null;
		try {
			allProvider = getAllDataProvider();

			// in case of first use, the data provider is set to the first list element
			if ( festivalID.equals("none") )
			{	
				SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(MainActivity.FESTIVAL_ID, getProviderName( allProvider.get(0) ) );
				editor.commit();
			}

			for (String p : allProvider)
				if (festivalID.equals( getProviderName(p) ))
					return p;			

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null; // TODO handle no provider installed
	}

	/**
	 * returns a list of all installed festival provider
	 * @throws NameNotFoundException
	 */
	private List<String> getAllDataProvider() throws NameNotFoundException
	{		
		List<String> provider = new ArrayList<String>();

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		for(PackageInfo pack : packages)
		{
			if (pack.packageName.contains( PROVIDER_PACKAGE_PREFIX ))
				provider.add( pack.packageName );	    	
		}

		return provider;    
	}

	/** 
	 * draw the logo dependent of the used festival provider
	 */
	private void drawLogo(String festivalID)
	{
		ImageView imageView = (ImageView) findViewById(R.id.imageView1);

		try {
			imageView.setImageDrawable( getFestivalLogo( getApplicationName(festivalID )) );			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getCurrDataProvider();

		long currTimeStamp = System.currentTimeMillis();

		String festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");
		
		drawLogo( festivalID );
		 
		drawInfoBox(festivalID, currTimeStamp);
		
	}
	
	/**
	 * returns a formated String containing all running Bands, and the next cnt playing Bands
	 * @param currTimeStamp
	 * @param festivalID
	 * @return
	 */
	private String currBandMsg(long currTimeStamp, String festivalID, int cnt)
	{
		RunningOrderDataSource datasource = new RunningOrderDataSource(this);
		datasource.open();
		
		List<Band> currBands = datasource.getCurrentRunningBands(new Date(currTimeStamp), festivalID);
		List<Band> nextBands = datasource.getNextRunningBands(new Date(currTimeStamp), cnt, festivalID);
		
		String msg = "";
		if (currBands.size() > 0)
		{
			msg = "Now:\n";					
			for(Band band : currBands)
				msg += band.getShortTimeIntervall() + " " + band.getBandname() + "\n";
		} 
		// show next playing bands
		if (nextBands.size() > 0)
		{
			msg = "Next:\n";					
			for(Band band : nextBands)
				msg += band.getShortTimeIntervall() + " " + band.getBandname() + "\n";
		}

		return msg;
	}
	
	/**
	 * returns the latest news for the given festival as a formated string
	 * @param festivalID
	 * @return
	 */
	private void drawLatestNews(String festivalID)
	{
		NewsDataSource newsdatasource = new NewsDataSource(this);
		newsdatasource.open();
		News news = newsdatasource.getLatestNews(festivalID);
		newsdatasource.close();
				
		if (news != null)
		{
			TextView textViewSubject = (TextView) findViewById(R.id.subject);
			textViewSubject.setText(news.getSubject());
					
			TextView textViewDate = (TextView) findViewById(R.id.date);
			textViewDate.setText(  news.getDateAsFormatedString() );
		};
	}
	
	/**
	 * draw the info box
	 * - show the latest news and some infos for the chosen festival
	 * @param festivalID
	 * @param currTimeStamp
	 */
	private void drawInfoBox(String festivalID, long currTimeStamp)
	{
		RunningOrderDataSource datasource = new RunningOrderDataSource(this);
		datasource.open();
		Date openDate = datasource.getTimeFirstAct(festivalID);
		
		String info = "";
		
		// set last news in front
		drawLatestNews(festivalID);
		
		if (openDate != null)
		{		
			long diff = openDate.getTime() - currTimeStamp;
			long days = diff / ( 24 * 60 * 60 * 1000);
			
			if (days > 0) 
			{
				// show days till festival
				info += "\n " + days + " Day";
				
				if (days >= 1) info += "s \n";
			} 
			else
			{	// its time for the festival
				// start all festival days actions
				// - short band, views
				// - start timer service
				
				info += currBandMsg(currTimeStamp, festivalID, 2);
		
				// start timer service
				Intent timerService = new Intent(this, TimerService.class);
				timerService.putExtra(SettingsActivity.EXTRA_MESSAGE, 
						getSharedPreferences("FestivalApp", MODE_PRIVATE).
						getString(MainActivity.FESTIVAL_ID, "none"));

				startService(timerService);	
			}			
		}
		
		TextView textViewInfo = (TextView) findViewById(R.id.info);
		textViewInfo.setText(info);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);

		getCurrDataProvider();

		long currTimeStamp = System.currentTimeMillis();

		// Update local data cache
		updateDataCache(currTimeStamp);

		String festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");
		
		drawLogo( festivalID );
		 
		drawInfoBox(festivalID, currTimeStamp);
	}

	
	/**
	 * update all local cached data
	 * check for the last update, when the last update to old start new update
	 * @param currTimeStamp
	 */
	private void updateDataCache(long currTimeStamp)
	{		
		SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
		long lastTimestamp = sharedPreferences.getLong(LAST_UPDATE, 0);  

		int maxAge = sharedPreferences.getInt(UPDATE_INTERVAL, 24) * 60 * 60 * 1000; // we compare milliseconds		

		Log.d("updateDataCache", "" + currTimeStamp + " - " + lastTimestamp + " > " + maxAge + " ?");

		if ( currTimeStamp - lastTimestamp > maxAge)
		{
			Intent intent = new Intent( getCurrDataProvider() + PROVIDER_SERVICE );			
			startService(intent);	
						
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong(LAST_UPDATE, currTimeStamp);
			editor.commit();
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);

			List<String> providerList = new ArrayList<String>(); // list contains the clear names for the provider			
			List<String> allProvider = null;
			
			try {
				allProvider = getAllDataProvider();
				for (String p : allProvider )
					providerList.add( getProviderName( p ) );
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			intent.putExtra(SettingsActivity.EXTRA_MESSAGE, 
					providerList.toArray(new String[providerList.size()]) );

			startActivity(intent);
			return true;

		case R.id.action_manual_update:

			Intent intentProvider = new Intent( getCurrDataProvider() + PROVIDER_SERVICE );
			startService(intentProvider);	

			// set new timestamp for last update
			SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);	    		
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong(LAST_UPDATE, System.currentTimeMillis());
			editor.commit();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void openNews(View view) {
		Intent intent = new Intent(this, NewsActivity.class);	    
		startActivity(intent);
	}

	public void openLineup(View view) {
		Intent intent = new Intent(this, BandActivity.class);	    
		startActivity(intent);
	}

	public void openRunningOrder(View view) {
		Intent intent = new Intent(this, RunningOrderActivity.class);	    
		startActivity(intent);
	}
}
