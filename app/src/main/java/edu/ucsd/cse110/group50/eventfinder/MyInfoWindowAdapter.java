package edu.ucsd.cse110.group50.eventfinder;


import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

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
 * Adapter Class for InfoWindow
 * Takes in a single event and display details on MapView
 *
 * @author Tian Qiu
 * @since 2016-11-27
 */
class MyInfoWindowAdapter implements InfoWindowAdapter{

    private final View myContentsView;
    private Context context;

    /**
     * Constructor
     * @param c Current context
     */
    MyInfoWindowAdapter(Context c){
        context = c;
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myContentsView = li.inflate(R.layout.custom_info_contents, null);
        }
        /**
         * Set information to be displayed
         *
         * @param marker Marker that represents a single event.
         */
        @Override
        public View getInfoContents(Marker marker) {

                TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.windowTitle));
                tvTitle.setText(marker.getTitle());
                String[] infoString = marker.getSnippet().split("\n");
                TextView tvDate = ((TextView)myContentsView.findViewById(R.id.windowDate));
                tvDate.setText(infoString[2]);
                TextView tvTime = ((TextView)myContentsView.findViewById(R.id.windowTime));
                TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.windowDescription));
                tvTime.setText(infoString[1]);
                tvDescription.setText(infoString[0]);
                return myContentsView;
        }
        /**
         * Set informationWindow
         *
         * @param marker Marker that represents a single event.
         */
        @Override
        public View getInfoWindow(Marker marker) {
                return null;
        }

}
