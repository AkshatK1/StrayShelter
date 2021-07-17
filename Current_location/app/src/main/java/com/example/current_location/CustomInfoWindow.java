package com.example.current_location;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindow(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custominfo, null);

        TextView address = view.findViewById(R.id.address);
        TextView details = view.findViewById(R.id.details);
        ImageView img = view.findViewById(R.id.img);

//        name_tv.setText(marker.getTitle());
//        details_tv.setText(marker.getSnippet());

        User infoWindowData = (User) marker.getTag();
        User user = new User();

//        address.setText(map.getAddress());
//        details.setText(map.getDetails());
//
//        Glide.with(img.getContext())
//                .load(map.getPhotourl())
//                .into(img);

        return view;
    }
}