package com.dank.festivalapp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.dank.festivalapp.lib.RunningOrderDataSource;
import com.dank.festivalapp.lib.Band;

public class TimerService extends IntentService {

	private static int CHECKUP_INTERVAL = 60 * 1000;  
	public static RunningOrderDataSource datasource;
	private int mID = 0;
	private String festivalID = "";

	public TimerService() {
		super("");
		datasource = new RunningOrderDataSource(this);
		datasource.open();
	}
	

	private void notify(String band, String msg)
	{
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_launcher)
			    .setContentTitle(band)
			    .setContentText( msg );
		
		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(mID++, mBuilder.build());
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		festivalID = intent.getStringExtra(BandActivity.EXTRA_MESSAGE);

		long currTimeStamp = System.currentTimeMillis();
		// TODO FAKE TIMER
//		try {
//			Date fakeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-08-10 19:38");
//			currTimeStamp = fakeDate.getTime();
//			Log.w("CURRENT TIME", "" + currTimeStamp);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		List<Band> nextBands = datasource.getNextRunningBands(new Date(currTimeStamp), 1, festivalID);
		HashMap<String, Boolean> alreadyWarned = new HashMap<String, Boolean>();
		
		while( nextBands.size() > 0)
		{
			currTimeStamp = System.currentTimeMillis(); 			
			int timerInterval = getSharedPreferences("FestivalApp", MODE_PRIVATE).getInt(MainActivity.WARN_TIMER, 15);
						
			List<Band> nextWatchBands = 
					datasource.getNextWatchRunningBandWithinTimeInterval(currTimeStamp, timerInterval, festivalID);  
		
			for (Band band:nextWatchBands)
			{
				if (! alreadyWarned.containsKey(band.getBandname() + band.getStartTime().toString() ))
				{
					notify(band.getBandname(), band.getShortTimeIntervall() + " (" + band.getStageName() + ")");
					// prevent a second warning for this band
					alreadyWarned.put( band.getBandname() + band.getStartTime().toString(), true);
				}
			}
			
			try {
				Thread.sleep(CHECKUP_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextBands = datasource.getNextRunningBands(new Date(currTimeStamp), 1, festivalID );
		}
		
	}

}
