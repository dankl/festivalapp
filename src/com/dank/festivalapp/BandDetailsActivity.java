package com.dank.festivalapp;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dank.festivalapp.lib.Band;
import com.dank.festivalapp.lib.BandsDataSource;

import com.dank.festivalapp.lib.Pair;

public class BandDetailsActivity extends Activity {

	private int bandID = 0;
	public static BandsDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_band);

		datasource = new BandsDataSource(this);
		datasource.open();

		Intent intent = getIntent();
		bandID = Integer.parseInt( intent.getStringExtra(BandActivity.EXTRA_MESSAGE) ) ;

		draw();
		setupActionBar();
	}

	protected void draw()
	{
		drawContent();
		setPrevNextButtons();				
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
		getMenuInflater().inflate(R.menu.bands_info, menu);
		return true;
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

	protected void setPrevNextButtons()
	{
		// handle button appearance
		Button prevButton = (Button) findViewById(R.id.prevButton);
		
		Pair<Integer, Integer> p = datasource.getNeighbourBandIDs(bandID);
		
		if (p.first == -1)
			prevButton.setVisibility(View.INVISIBLE);
		else 
			prevButton.setVisibility(View.VISIBLE);

		Button nextButton = (Button) findViewById(R.id.nextButton);		
		if (p.second == -1)
			nextButton.setVisibility(View.INVISIBLE);
		else 
			nextButton.setVisibility(View.VISIBLE);
	}

	protected void drawContent()
	{
		Band band = datasource.getBandInfos(bandID);

		// Create the text view
		TextView textViewName = (TextView) findViewById(R.id.name);	    
		textViewName.setText(band.getBandname() );

		TextView textViewflavor = (TextView) findViewById(R.id.flavor);	    
		textViewflavor.setText( band.getFlavorsAsString() );

		TextView textViewInfo = (TextView) findViewById(R.id.info);	    
		textViewInfo.setText( band.getDescription() );

		// handle watch/don't watch
		ImageView imageView = (ImageView) findViewById(R.id.imageWatch);	
		if ( band.watch() )
			imageView.setImageResource(R.drawable.pentagram);						
		else 
			imageView.setImageResource(R.drawable.pentagram_grey);

		imageView.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						datasource.updateBandLike(bandID);
						drawContent();
					}
				});		
		
		// handle images
		File sdroot = Environment.getExternalStorageDirectory();
		File logoFile = new File (sdroot.getAbsolutePath() + "/FestivalApp/" + band.getLogoFile() );

		ImageView logoView = (ImageView) findViewById(R.id.logo);

		if (! logoFile.isFile() )
			logoView.setVisibility(View.INVISIBLE);
		else
		{
			logoView.setVisibility(View.VISIBLE);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bm = BitmapFactory.decodeFile(logoFile.toString(), options);
			logoView.setImageBitmap(bm); 
		}

		File fotoFile = new File (sdroot.getAbsolutePath() + "/FestivalApp/" + band.getFotoFile() );
		ImageView fotoView = (ImageView) findViewById(R.id.bandfoto);

		if (! fotoFile.isFile() )
			fotoView.setVisibility(View.INVISIBLE);
		else
		{
			fotoView.setVisibility(View.VISIBLE);
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bm = BitmapFactory.decodeFile(fotoFile.toString(), options);
			fotoView.setImageBitmap(bm);
		}
	}

	public void prevBand(View view) 
	{
		Pair<Integer, Integer> p = datasource.getNeighbourBandIDs(bandID);
		if (p.first != -1)
		{
			bandID = p.first;
			drawContent();
			setPrevNextButtons();
		}
	}


	public void nextBand(View view) 
	{
		Pair<Integer, Integer> p = datasource.getNeighbourBandIDs(bandID);
		if (p.second != -1)
		{
			bandID = p.second;
			drawContent();
			setPrevNextButtons();
		}	
	}

	public void watchBand(View view) 
	{
		datasource.updateBandLike(bandID);
		drawContent();
	}

	private String bandNameToWikipedianame(String name)
	{
		String res = "";

		String[] words = name.split("\\s");
		for (int i = 0; i < words.length; i++) 
		{
			if (i > 0) res += "_";  
			res += words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();        	
		}

		return res;
	}

	public void openWikipediaLink(View view) {
		Band band = datasource.getBandInfos(bandID);
		String wikipediaUrl = "http://de.m.wikipedia.org/wiki/" + bandNameToWikipedianame(band.getBandname() );

		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( wikipediaUrl));
		startActivity(browserIntent);
	}

	public void openMetalArchivesLink(View view) {
		Band band = datasource.getBandInfos(bandID);
		String url = "http://www.metal-archives.com/bands/" + bandNameToWikipedianame(band.getBandname() );

		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ));
		startActivity(browserIntent);
	}

	
	public void openBandPage(View view) {
		Band band = datasource.getBandInfos(bandID);

		Log.w("openBandPage", band.getUrl() );
		
		if (band.getUrl() != null )
		{
			Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( band.getUrl() ));
			startActivity(browserIntent);
		}
	}
}
