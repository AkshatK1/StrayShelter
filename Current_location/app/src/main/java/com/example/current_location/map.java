package com.example.current_location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class map extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TaskLoadedCallback {


    private static final int PERMISSION_REQUEST_CODE = 1;

    Location location;
    double Lat, longitude;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
//
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (ContextCompat.checkSelfPermission(map.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(map.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(map.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);


            }
        }


    }

    //
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final DatabaseReference rootref;
        rootref = FirebaseDatabase.getInstance().getReference().child("orders");

        rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    User l = d.getValue(User.class);
                    Lat = l.getLatitude();

//                    l.setLatitude(String.valueOf(Lat));
                    //Toast.makeText(map.this,dataSnapshot.child("Latitude").getValue().toString(),Toast.LENGTH_LONG).show();
                    longitude = l.getLongitude();
//                    l.setLongitude(String.valueOf(longitude));
                    LatLng newyork = new LatLng(Lat, longitude);
                    googleMap.setMyLocationEnabled(true);
                    //  BitmapDescriptor discripter= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                    //googleMap.setInfoWindowAdapter(new CustomInfoWindow(map.this));
                    googleMap.addMarker(new MarkerOptions().position(newyork)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.cow)));
                }


//                googleMap.addMarker(place1);
//                googleMap.addMarker(place2);

                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                if (location != null) {
//                    Currentlat = location.getLatitude();
//                    Currentlong = location.getLongitude();
//
//
//                    Toast.makeText(getApplicationContext(), Currentlat + "\n" + Currentlong, Toast.LENGTH_LONG).show();
//
//                }

//


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //on marker click
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                final double lat = position.latitude;
                final double longi = position.longitude;
                Toast.makeText(map.this, "Lat " + lat + " " + "Long " + longi, Toast.LENGTH_LONG).show();

                rootref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User user = dataSnapshot.getValue(User.class);
                        if(lat == user.getLatitude() && longi == user.getLongitude())
                        {
                            final Dialog d = new Dialog(map.this);
                            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            //d.setTitle("Select");
                            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                            d.setContentView(R.layout.custominfo);

                            ImageView photo = d.findViewById(R.id.img);
                            TextView address = d.findViewById(R.id.address);
                            TextView details = d.findViewById(R.id.details);

                            Picasso.get().load(user.getPhotoUrl()).into(photo);
                            address.setText(user.getAddress());
                            details.setText(user.getOrderDetails());
                            d.show();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
                return false;
            }
        });

    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//

    @Override
    public void onTaskDone(Object... values) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
