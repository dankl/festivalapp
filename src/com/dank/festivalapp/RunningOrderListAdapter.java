package com.dank.festivalapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dank.festivalapp.lib.Band;

public class RunningOrderListAdapter extends BaseAdapter {

	private ArrayList< Band > data;
	private static LayoutInflater inflater=null;
	private boolean showStages = true;
	
	public RunningOrderListAdapter(Activity a, ArrayList< Band > d, boolean showStages) {        
        this.data = d;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showStages = showStages;
    }
	
	public int getCount() {
        return data.size();
	}
	
	public Object getItem(int position) {
		return data.get(position);
	}
	
	public long getItemId(int position) {
        return position;
	}

	public View getView (int position, View convertView, ViewGroup parent) {
		
        View vi = convertView;
        
        if(convertView==null)
            vi = inflater.inflate(R.layout.details_running_order, null);

        TextView time = (TextView)vi.findViewById(R.id.time);        
         
        TextView flavor = (TextView)vi.findViewById(R.id.flavor);
        flavor.setVisibility(View.INVISIBLE);
//        flavor.setText( data.get(position).getFlavorsAsString() );

        time.setText( data.get(position).getShortTimeIntervall() ); 
        
        TextView bandName = (TextView)vi.findViewById(R.id.bandName);
        bandName.setText( data.get(position).getBandname() );
        
        TextView stage = (TextView)vi.findViewById(R.id.stage);
        if (!showStages)
        	stage.setVisibility(View.INVISIBLE);
        else 
           	stage.setText(data.get(position).getStageName() );
        	
        ImageView imageView = (ImageView)vi.findViewById(R.id.imageWatch);
        
        if (data.get(position).watch() )
			imageView.setVisibility(View.VISIBLE);
        else 
			imageView.setVisibility(View.INVISIBLE);
        	
		
        return vi;    
	}	
	    
}
