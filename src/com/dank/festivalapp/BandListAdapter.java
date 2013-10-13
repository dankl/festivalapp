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

public class BandListAdapter extends BaseAdapter {

	private ArrayList< Band > bandList;
	private static LayoutInflater inflater=null;
	
	public BandListAdapter(Activity a, ArrayList< Band > d) {        
        bandList = d;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);        
    }
	
	public int getCount() {
        return bandList.size();
	}
	
	public Object getItem(int position) {
		return bandList.get(position);
	}
	
	public long getItemId(int position) {
        return position;
	}

	public View getView (int position, View convertView, ViewGroup parent) {
		
        View vi = convertView;
        
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_item_bands, null);

        TextView name = (TextView)vi.findViewById(R.id.name);
        TextView flavor = (TextView)vi.findViewById(R.id.flavor); 
        ImageView imageView = (ImageView)vi.findViewById(R.id.imageWatch); 

        name.setText( bandList.get(position).getBandname() ); 
        flavor.setText( bandList.get(position).getFlavorsAsString() );
        
		if ( bandList.get(position).watch() )
			imageView.setVisibility(View.VISIBLE);
		else 
			imageView.setVisibility(View.INVISIBLE);
        return vi;    
	}	
	    
}
