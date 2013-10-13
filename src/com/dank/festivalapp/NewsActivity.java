package com.dank.festivalapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dank.festivalapp.lib.News;
import com.dank.festivalapp.lib.NewsDataSource;

public class NewsActivity extends ListActivity {

	public final static String EXTRA_MESSAGE = "com.example.festivalapp.MESSAGE";
	public static NewsDataSource datasource;
	private final static int MAX_LENGTH_NEWS = 40;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		
		datasource = new NewsDataSource(this);
		datasource.open();
		List<News> values = datasource.getAllNews(
				getSharedPreferences("FestivalApp", MODE_PRIVATE).
					getString(MainActivity.FESTIVAL_ID, "none"));
		
		if (values.size() > 0)
		{
			TextView textViewName = (TextView) findViewById(R.id.textView1);	    
			textViewName.setVisibility( View.INVISIBLE );
			drawList(values);
		} 		
			
		setupActionBar();	
	}

	public void drawList(List<News> values)
	{		
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		for (News news : values)
		{
			Map<String, String> t = new HashMap<String, String>();
			t.put(NewsDataSource.ID, news.getID().toString() );									
			t.put(NewsDataSource.DATE, news.getDateAsFormatedString() );			
			t.put(NewsDataSource.SUBJECT, news.getSubject() );
			
			if (news.getMessage().length() > MAX_LENGTH_NEWS )
				t.put(NewsDataSource.MSG, news.getMessage().substring(0, MAX_LENGTH_NEWS) + "..." );
			else 
				t.put(NewsDataSource.MSG, news.getMessage());
			
		    data.add(t);	
		}		    
	    
	    SimpleAdapter adapter = new SimpleAdapter(
	    		this,
	    		data,	    		
	    		R.layout.list_item_news,
	    		new String[] { NewsDataSource.SUBJECT, NewsDataSource.MSG, NewsDataSource.DATE },	    		
	    		new int[] { R.id.subject, R.id.msg, R.id.date } );
	 	    
	    setListAdapter(adapter);	  		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{	  
        super.onListItemClick(l, v, position, id);
        @SuppressWarnings("unchecked")
        HashMap<String, String> data = (HashMap<String, String>) getListAdapter().getItem(position);

		Intent intent = new Intent(this, NewsDetailsActivity.class);
    	String message = data.get(NewsDataSource.ID);
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
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

}
