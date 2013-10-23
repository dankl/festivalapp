package com.dank.festivalapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dank.festivalapp.lib.News;
import com.dank.festivalapp.lib.NewsDataSource;

public class NewsDetailsActivity extends Activity {

	Integer newsID = -1;
	
	String festivalID = "";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_news);
		
		Intent intent = getIntent();
		newsID = Integer.parseInt( intent.getStringExtra(NewsActivity.EXTRA_MESSAGE) );
		
		Log.w("NewsDetailsActivity", newsID.toString() );
		
		festivalID = getSharedPreferences("FestivalApp", MODE_PRIVATE).
				getString(MainActivity.FESTIVAL_ID, "none");
		
		drawContent();		
		setupActionBar();
	
		
	}

	private void drawContent()
	{
		NewsDataSource datasource = new NewsDataSource(this);
		datasource.open();
		News news = datasource.getNews(newsID);
		
		 // Create the text view
		TextView textViewSubject = (TextView) findViewById(R.id.subject);	    
	    textViewSubject.setText(news.getSubject());
	    
		TextView textViewDate = (TextView) findViewById(R.id.date);
	    textViewDate.setText( news.getDateAsFormatedString() );

		TextView textViewMsg = (TextView) findViewById(R.id.msg);
		textViewMsg.setText( news.getMessage() );
		
		// handle button appearance
		Button prevButton = (Button) findViewById(R.id.prevButton);
			
		if (datasource.getPrevNewsID(newsID, festivalID) == -1)
			prevButton.setVisibility(View.INVISIBLE);
		else 
			prevButton.setVisibility(View.VISIBLE);
		
		Button nextButton = (Button) findViewById(R.id.nextButton);		
		if (datasource.getNextNewsID(newsID, festivalID) == -1)
			nextButton.setVisibility(View.INVISIBLE);
		else 
			nextButton.setVisibility(View.VISIBLE);
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
		getMenuInflater().inflate(R.menu.news_view, menu);
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
	
    public void prevNews(View view) 
    {
    	int id = NewsActivity.datasource.getPrevNewsID(newsID, festivalID);
    	if (id != -1)
    	{
    		newsID = id;
    		drawContent();
    	}
    }    

    public void nextNews(View view) 
    {
    	int id = NewsActivity.datasource.getNextNewsID(newsID, festivalID);
    	if (id != -1)
    	{
    		newsID = id;
    		drawContent();
    	}	
    }

}
