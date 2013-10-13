package com.dank.festivalapp;

import android.view.View;
import android.widget.Button;


public class RunningOrderBandDetailsActivity extends BandDetailsActivity {

	protected void draw()
	{
		drawContent();
		
		Button prevButton = (Button) findViewById(R.id.prevButton);		
		prevButton.setVisibility(View.INVISIBLE);
		
		Button nextButton = (Button) findViewById(R.id.nextButton);		
		nextButton.setVisibility(View.INVISIBLE);		
	}
}
