package com.inclass.tripchat.Activity.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inclass.tripchat.Activity.POJO.Places;
import com.inclass.tripchat.Activity.POJO.Trip;
import com.inclass.tripchat.Activity.Utils.DirectionsJSONParser;
import com.inclass.tripchat.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String tripId;
    DatabaseReference databaseTripsReference;
    ValueEventListener listener;
    ArrayList<Places> placesArrayList;
    Trip myTrip;
    LocationManager locationManager;
    LatLng initial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_maps);

        tripId = getIntent().getStringExtra("TripId");
        placesArrayList = new ArrayList<>();

        databaseTripsReference = FirebaseDatabase.getInstance().getReference("Trips").child(tripId);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.button_googleMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
                latLngArrayList.add(new LatLng(placesArrayList.get(0).getLatitude(),placesArrayList.get(0).getLongitude()));
                latLngArrayList.add(new LatLng(placesArrayList.get(placesArrayList.size()-2).getLatitude(),placesArrayList.get(placesArrayList.size()-2).getLongitude()));
                for(int i=1;i<placesArrayList.size()-2;i++){
                    Places place = placesArrayList.get(i);
                    latLngArrayList.add(new LatLng(place.getLatitude(),place.getLongitude()));
                }
                String uri = "";
                for(LatLng latLng:latLngArrayList){
                    if(TextUtils.isEmpty(uri)){
                        uri = String.format("http://maps.google.com/maps?saddr=%s, %s",
                                            String.valueOf(latLng.latitude).replace(",","."),
                                            String.valueOf(latLng.longitude).replace(",","."));
                    } else {
                        if(!uri.contains("&daddr")){
                            uri += String.format("&daddr=%s, %s",
                                    String.valueOf(latLng.latitude).replace(",","."),
                                    String.valueOf(latLng.longitude).replace(",","."));
                        }else {
                            uri += String.format("+to:%s, %s",
                                    String.valueOf(latLng.latitude).replace(",","."),
                                    String.valueOf(latLng.longitude).replace(",","."));
                        }
                    }
                }
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        //Uri.parse("http://maps.google.com/maps?saddr="+initial.latitude+","+initial.longitude+"&daddr="+myTrip.getDestination().getLatitude()+","+myTrip.getDestination().getLongitude()));
                        Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Log.d("MAP READY", "READY");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SET MAP","RETURN");
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        initial = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initial));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 15.0f ) );
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public void setMap() {
        mMap.clear();
        Log.d("SET MAP","ENTERED");

        Places initialPlace = new Places();
        initialPlace.setPlaceName("Current Location");
        initialPlace.setLatitude(initial.latitude);
        initialPlace.setLongitude(initial.longitude);
        placesArrayList.add(0, initialPlace);
        placesArrayList.add(myTrip.getDestination());
        placesArrayList.add(initialPlace);

        for (int i = 0; i < placesArrayList.size() - 1; i++) {
            LatLng src = new LatLng(placesArrayList.get(i).getLatitude(), placesArrayList.get(i).getLongitude());
            LatLng dest = new LatLng(placesArrayList.get(i + 1).getLatitude(), placesArrayList.get(i + 1).getLongitude());
            Log.d("Latlng 1",src.toString());
            Log.d("Latlng 2",dest.toString());
            mMap.addMarker(new MarkerOptions().position(src).title(placesArrayList.get(i).getPlaceName()));
            String url = getDirectionsUrl(src, dest);
            new DownloadTask().execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    @Override
    protected void onStart() {
        super.onStart();
        listener = databaseTripsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placesArrayList.clear();
                myTrip = dataSnapshot.getValue(Trip.class);
                if (myTrip.getPlacesArrayList() != null) {
                    Log.d("PlaceArray", myTrip.getPlacesArrayList().size() + "");
                    for (int i = 0; i < myTrip.getPlacesArrayList().size(); i++) {
                        placesArrayList.add(myTrip.getPlacesArrayList().get(i));
                    }
                }
                setMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseTripsReference.removeEventListener(listener);
    }


    private class DownloadTask extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... param) {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            List<List<HashMap<String, String>>> routes = null;

            try {
                // Fetching the data from web service
                URL url = new URL(param[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
                Log.d("JSON DATA", data);
                JSONObject jsonObject = new JSONObject(data);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jsonObject);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            } finally {
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            super.onPostExecute(result);
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            PolylineOptions lineOptions1 = new PolylineOptions();

            for(int i=0;i<result.size();i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);
                for(int j=0;j <path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);

                }

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.GREEN);
            }
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}