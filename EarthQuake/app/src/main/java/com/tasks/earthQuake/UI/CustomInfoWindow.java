package com.tasks.earthQuake.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.tasks.earthQuake.R;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private View view;
    private LayoutInflater layoutInflater;
    private Context context;
    private TextView title;
    private TextView magnitude;
    private Button more_info;


    public CustomInfoWindow(Context context) {
        this.context = context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.custom_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        title=view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        magnitude=view.findViewById(R.id.mag);
        magnitude.setText(marker.getSnippet());
        return view;

    }
}
