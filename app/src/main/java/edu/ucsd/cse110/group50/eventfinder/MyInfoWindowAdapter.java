package edu.ucsd.cse110.group50.eventfinder;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Tian on 11/27/2016.
 * A customized infoWindowAdapter for the markers
 * Source: http://android-er.blogspot.com/2013/01/create-custom-info-contents-for-by.html
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
     * Setup text fields for the infoWindow
     * @param marker Clicked marker
     * @return Customized info view
     */
    @Override
    public View getInfoContents(Marker marker) {
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.windowTitle));
        tvTitle.setText(marker.getTitle());
        String[] infoString = marker.getSnippet().split("\n"); //Split strings for information
        TextView tvDate = ((TextView)myContentsView.findViewById(R.id.windowDate));
        tvDate.setText(infoString[2]);
        TextView tvTime = ((TextView)myContentsView.findViewById(R.id.windowTime));
        TextView tvDescription = ((TextView)myContentsView.findViewById(R.id.windowDescription));
        tvTime.setText(infoString[1]);
        tvDescription.setText(infoString[0]);
        return myContentsView;
    }

    /**
     * Not implemented
     * @param marker Clicked marker
     * @return Customized info view
     */
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
