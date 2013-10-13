package com.dank.festivalapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dank.festivalapp.lib.DataBaseHelper;

public class SettingsActivity extends Activity {

	final Context context = this;	
	static final int TIME_DIALOG_ID = 0;
	public final static String EXTRA_MESSAGE = "com.example.festivalapp.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();

		// handle private running list
		CheckBox privateRunningOrderCheckBox = (CheckBox) findViewById(R.id.privateRunningOrderCheckBox);
		privateRunningOrderCheckBox.setChecked(
				getSharedPreferences("FestivalApp", MODE_PRIVATE).getBoolean(RunningOrderActivity.PRIVATE_RUNNING_ORDER, false));;

				privateRunningOrderCheckBox.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
								SharedPreferences.Editor editor = sharedPreferences.edit();

								if (((CheckBox) v).isChecked())
									editor.putBoolean(RunningOrderActivity.PRIVATE_RUNNING_ORDER, true);
								else  
									editor.putBoolean(RunningOrderActivity.PRIVATE_RUNNING_ORDER, false);							

								editor.commit();

							}
						});

				// handle clean up
				RelativeLayout cleanUpAll = (RelativeLayout) findViewById(R.id.relative3);
				cleanUpAll.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {

								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );

								Resources res = getResources();

								// set dialog message
								alertDialogBuilder
								.setMessage( res.getString(R.string.cleanup_question) )
								.setCancelable(false)
								.setPositiveButton(res.getString(R.string.yes), 
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {									
										cleanUp();
										Resources res1 = getResources();
										Toast.makeText(getApplicationContext(), 
												res1.getString(R.string.cleanup_success), 
												Toast.LENGTH_SHORT).show();
									}
								})
								.setNegativeButton(res.getString(R.string.no),
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {										
										dialog.cancel();
									}
								});

								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}
						});

				// handle warning time interval
				TextView textViewWarnTime = (TextView) findViewById(R.id.warn_time_interval);
				textViewWarnTime.setText("" + 
						getSharedPreferences("FestivalApp", MODE_PRIVATE)
						.getInt(MainActivity.WARN_TIMER, 15) + " min");

				RelativeLayout warnTime = (RelativeLayout) findViewById(R.id.relative4);		
				warnTime.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {

								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
								Resources res = getResources();

								final EditText input = new EditText(context);

								// set dialog message
								alertDialogBuilder
								.setMessage( res.getString(R.string.warn_time_text) )
								.setCancelable(false)
								.setView(input)
								.setPositiveButton(res.getString(R.string.yes), 
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										String msg = input.getText().toString().trim();
										int warnTime = Integer.parseInt(msg);

										SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
										SharedPreferences.Editor editor = sharedPreferences.edit();
										editor.putInt(MainActivity.WARN_TIMER, warnTime );
										editor.commit();

										TextView textViewWarnTime = (TextView) findViewById(R.id.warn_time_interval);
										textViewWarnTime.setText("" + warnTime + " min");
									}
								})
								.setNegativeButton(res.getString(R.string.no),
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {										
										dialog.cancel();
									}
								});

								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}
						});


				// Handle update interval
				TextView textViewUpdateTime = (TextView) findViewById(R.id.update_time_news_timeinterval);
				textViewUpdateTime.setText("" + 
						getSharedPreferences("FestivalApp", MODE_PRIVATE)
						.getInt(MainActivity.UPDATE_INTERVAL, 24) + " h");

				RelativeLayout updateTime = (RelativeLayout) findViewById(R.id.relative2);		
				updateTime.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {

								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( context );
								Resources res = getResources();

								final EditText input = new EditText(context);

								// set dialog message
								alertDialogBuilder
								.setMessage( res.getString(R.string.update_time_news_text) )
								.setCancelable(false)
								.setView(input)
								.setPositiveButton(res.getString(R.string.yes), 
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										String msg = input.getText().toString().trim();
										int updateTime = Integer.parseInt(msg);

										SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
										SharedPreferences.Editor editor = sharedPreferences.edit();
										editor.putInt(MainActivity.UPDATE_INTERVAL, updateTime );
										editor.commit();

										TextView textViewWarnTime = (TextView) findViewById(R.id.update_time_news_timeinterval);
										textViewWarnTime.setText("" + updateTime + " h");
									}
								})
								.setNegativeButton(res.getString(R.string.no),
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {										
										dialog.cancel();
									}
								});

								AlertDialog alertDialog = alertDialogBuilder.create();
								alertDialog.show();
							}
						});

				// handle festival choise				
				TextView festivalIDText = (TextView) findViewById(R.id.festival_ID);
				festivalIDText.setText( 
						getSharedPreferences("FestivalApp", MODE_PRIVATE).
						getString(MainActivity.FESTIVAL_ID, "none") );

				Intent intent = getIntent();
				final String[] providerArray = intent.getStringArrayExtra(SettingsActivity.EXTRA_MESSAGE) ;

				RelativeLayout festivalChoise = (RelativeLayout) findViewById(R.id.relative5);
				festivalChoise.setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {

								final String festivalIDold = getSharedPreferences("FestivalApp", MODE_PRIVATE).
										getString(MainActivity.FESTIVAL_ID, "none");
																
								AlertDialog.Builder Dialog = new AlertDialog.Builder( context );
								Dialog.setTitle("Select Option");

								LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View dialogView = li.inflate(R.layout.provider_option, null);
								Dialog.setView(dialogView);

								Dialog.setPositiveButton(R.string.yes,
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										TextView festivalIDText = (TextView) findViewById(R.id.festival_ID);
										festivalIDText.setText( 
												getSharedPreferences("FestivalApp", MODE_PRIVATE).
												getString(MainActivity.FESTIVAL_ID, "none") );
									}
								});

								Dialog.setNegativeButton(R.string.no,
										new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										// reset to old state
										SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
										SharedPreferences.Editor editor = sharedPreferences.edit();
										editor.putString(MainActivity.FESTIVAL_ID, festivalIDold );
										editor.commit();
									}
								});
								Dialog.show();

								final Spinner spinner = (Spinner) dialogView.findViewById(R.id.viewSpin);
								List<String> providerList = new ArrayList<String>(Arrays.asList(providerArray));

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										context,
										android.R.layout.simple_spinner_item, providerList);

								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								spinner.setAdapter(adapter);

								spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

									public void onItemSelected(AdapterView<?> parent, View arg1,
											int arg2, long arg3) 
									{
										String spinnerItem = (String) spinner.getSelectedItem();
										SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
										SharedPreferences.Editor editor = sharedPreferences.edit();
										editor.putString(MainActivity.FESTIVAL_ID, spinnerItem );
										editor.commit();
									}

									public void onNothingSelected(AdapterView<?> arg0) {
									}
								});								
								
							} // onClick
						}); // OnClickListener

	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:

			int interval = getSharedPreferences("FestivalApp", MODE_PRIVATE).getInt(MainActivity.UPDATE_INTERVAL, 24*60);			
			TimePickerDialog tpd = new TimePickerDialog(context,
					mTimeSetListener, interval/60, interval%60, true);

			tpd.setTitle("News Time Interval");

			return tpd;

		}
		return null;
	}

	/**
	 * Callback received when the user "picks" a time in the dialog 
	 * 
	 **/
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
			new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hour, int minute) {            	
			SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(MainActivity.UPDATE_INTERVAL, hour * minute);
			editor.commit();

			TextView uptimeTimeIntervall = (TextView) findViewById(R.id.update_time_news_timeinterval);

			if (minute > 10)
				uptimeTimeIntervall.setText(hour + ":" + minute);
			else 
				uptimeTimeIntervall.setText(hour + ":0" + minute);
		}
	};


	private void cleanUp()
	{
		// cleanup config for all activities
		SharedPreferences sharedPreferences = getSharedPreferences("FestivalApp", MODE_PRIVATE);      	
		Editor editor = sharedPreferences.edit();
		editor.clear();	        	
		editor.commit();

		// clean up database
		class MainDataSource{
			private DataBaseHelper dbHelper;
			private SQLiteDatabase database;

			public MainDataSource(Context context)
			{
				dbHelper = DataBaseHelper.getInstance(context);
				database = dbHelper.getWritableDatabase();
			}

			public void dropAllTables()
			{
				List<String> tables = new ArrayList<String>();
				Cursor cursor = database.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					String tableName = cursor.getString(1);
					if (!tableName.equals("android_metadata") &&
							!tableName.equals("sqlite_sequence"))
						tables.add(tableName);
					cursor.moveToNext();
				}
				cursor.close();

				for(String tableName:tables) {
					database.execSQL("DROP TABLE IF EXISTS " + tableName);
				}
			}
		} // MainDataSource

		MainDataSource mds = new MainDataSource(this);
		mds.dropAllTables();
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
		getMenuInflater().inflate(R.menu.settings_running_order, menu);
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

}
