package edu.ucsd.cse110.group50.eventfinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

/**
 * Created by Tian on 11/27/2016.
 */
class MyInfoWindowAdapter implements InfoWindowAdapter{

    private final View myContentsView;
    private Context context;
    private Event event;
MyInfoWindowAdapter(Context c, Event e){
        context = c;
        event = e;
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myContentsView = li.inflate(R.layout.custom_info_contents, null);
        }

@Override
public View getInfoContents(Marker marker) {

        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.windowTitle));
        tvTitle.setText(marker.getTitle());
        TextView tvDate = ((TextView)myContentsView.findViewById(R.id.windowDate));
        tvDate.setText(event.getDate().getDate());
        TextView tvTime = ((TextView)myContentsView.findViewById(R.id.windowTime));
        TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.windowDescription));
        tvTime.setText(event.getDate().getTime());
        tvDescription.setText(event.getDescription());
        return myContentsView;
        }

@Override
public View getInfoWindow(Marker marker) {
        //Auto-generated method
        //Add more things here
        return null;
        }

}
