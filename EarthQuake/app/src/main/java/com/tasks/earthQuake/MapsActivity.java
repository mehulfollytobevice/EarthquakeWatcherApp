package com.tasks.earthQuake;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tasks.earthQuake.Activites.QuakesList;
import com.tasks.earthQuake.Model.EarthQuake;
import com.tasks.earthQuake.UI.CustomInfoWindow;
import com.tasks.earthQuake.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {
    private LocationListener locationListener;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
//    there should be public(only one) instance of the json requestqueue
    private RequestQueue requestQueue;
//    creating an array of BitmapDescriptor
    private BitmapDescriptor[] iconColors;
    private Button showListBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        You can have location listener and location manager here also

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        showListBtn=findViewById(R.id.earthList);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, QuakesList.class));
            }
        });
        iconColors=new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                // BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        };

        mapFragment.getMapAsync(this);
        requestQueue= Volley.newRequestQueue(this);
        getEarthquakes();

    }

    private void getEarthquakes() {

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Constants.URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EarthQuake earthQuake = new EarthQuake();
                JSONArray features= null;
                try {
                    features = response.getJSONArray("features");
                    for(int i=0;i<Constants.LIMIT;i++){
                        JSONObject properties=features.getJSONObject(i).getJSONObject("properties");
//                        Log.d("Earthquakes", "onResponse: "+properties.getString("place"));
                        // get geometry object
                        JSONObject geometry=features.getJSONObject(i).getJSONObject("geometry");
                        //get the coordinates
                        JSONArray coordinates=geometry.getJSONArray("coordinates");
                        Double lat=coordinates.getDouble(1);
                        Double lon=coordinates.getDouble(0);
                       // Log.d("Earthqu", "onResponse: "+lat+" "+lon);

//                        making earthquake object
                        earthQuake.setPlace(properties.getString("place"));
                        earthQuake.setMag(properties.getDouble("mag"));
                        earthQuake.setType(properties.getString("type"));
                        earthQuake.setTime(properties.getLong("time"));
                        earthQuake.setUrl(properties.getString("detail"));
                        earthQuake.setLat(lat);
                        earthQuake.setLon(lon);
                        java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
                        String formattedDate=dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());

                        //Add marker
                        MarkerOptions markerOptions=new MarkerOptions();
                        markerOptions.position(new LatLng(lat,lon))
                                .title(earthQuake.getPlace())
                                .icon(iconColors[Constants.randomInt(iconColors.length,0)])
                                .snippet("Mag:"+earthQuake.getMag()+" Date:"+formattedDate);
// adding circles around the serious earthQuakes
                        if(earthQuake.getMag()>4.5){
                            CircleOptions circleOptions=new CircleOptions();
                            circleOptions.center(new LatLng(lat,lon))
                                    .radius(30000)
                                    .fillColor(Color.RED);
//                            Adding circle to the map
                            mMap.addCircle(circleOptions);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }
                        Marker marker=mMap.addMarker(markerOptions);//add directly to the map
                        marker.setTag(earthQuake.getUrl());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),1));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
//        you can have the location manager and the location listener here also
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LocationSEE", "onLocationChanged: "+location.toString());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Add a marker in Sydney and move the camera
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getQuakeDetails(marker.getTag().toString());
//        Toast.makeText(this,"Earthquake coming!!!",Toast.LENGTH_LONG).show();
    }
//Things are moving in invisible loops
    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequestt=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String finalurl="";
                try {
                    JSONObject jsonObject=response.getJSONObject("properties").getJSONObject("products");
                    JSONArray geoserve=jsonObject.getJSONArray("geoserve");
                    for (int i=0;i<geoserve.length();i++){
                        JSONObject geoserveJSONObject=geoserve.getJSONObject(i);
                        JSONObject contentObj=geoserveJSONObject.getJSONObject("contents");
                        JSONObject geoJson=contentObj.getJSONObject("geoserve.json");
//                        finally got the url
                        finalurl=geoJson.getString("url");

                    }
//                    Log.d("urlQuake", "onResponse: "+finalurl);
                    getMoreDetails(finalurl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequestt);
    }
    public void getMoreDetails(String url){
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                builder=new AlertDialog.Builder(MapsActivity.this);
                View view=getLayoutInflater().inflate(R.layout.popup,null);
                Button dismiss=view.findViewById(R.id.dismissBut);
                Button close=view.findViewById(R.id.dismissPopup);
                TextView surCity=view.findViewById(R.id.popList);
                WebView more=view.findViewById(R.id.popInfoView);
                JSONArray cities= null;
//                using String Builder class
                StringBuilder stringBuilder=new StringBuilder();
                try {
//                    .has() method returns true or false
                    if (response.has("tectonicSummary") && response.getJSONObject("tectonicSummary")!=null){
                        JSONObject tectonic=response.getJSONObject("tectonicSummary");
                        if (tectonic.has("text") && tectonic.getString("text") != null){
                            //text is a String object not a json object
                            String text= tectonic.getString("text");
//                            putting stuff in the webView
                            more.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);
                        }

                    }

                    cities = response.getJSONArray("cities");
                    for (int i=0;i<cities.length();i++){
                        JSONObject citiesobj=cities.getJSONObject(i);
// String builder allows us to actually accumulate all the data through the for loop and then put it all together in the textview
                        stringBuilder.append("City:"+citiesobj.getString("name")+"\n"+"Distance:"
                                +citiesobj.getString("distance")+"\n"
                                +"Population:"+citiesobj.getString("population"));
                        stringBuilder.append("\n\n");
                    }


                    surCity.setText(stringBuilder);
                    builder.setView(view);
                    dialog=builder.create();
                    dialog.show();
                    dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
