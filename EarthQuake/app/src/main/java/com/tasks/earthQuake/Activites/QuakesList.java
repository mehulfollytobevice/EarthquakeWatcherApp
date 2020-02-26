package com.tasks.earthQuake.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tasks.earthQuake.Model.EarthQuake;
import com.tasks.earthQuake.R;
import com.tasks.earthQuake.Util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class QuakesList extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ListView listView;
    private RequestQueue requestQueue;
    private ArrayList<LatLng> quakeArrayList;
    // ArrayAdapter helps us to build the listview
    private ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);
        arrayList=new ArrayList<>();
        quakeArrayList=new ArrayList<>();
        listView=findViewById(R.id.listView);
        requestQueue= Volley.newRequestQueue(this);


        getallQuakes(Constants.URL);
    }

    public void getallQuakes(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray features= null;
                try {
                    EarthQuake earthQuake = new EarthQuake();
                    features = response.getJSONArray("features");
                    for(int i = 0; i< Constants.LIMIT; i++){
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

                        arrayList.add(earthQuake.getPlace());
//                        For some very grave reason you cannot get the whole earthquake object in an array so we have to store the lat and lon in different array
                        quakeArrayList.add(new LatLng(earthQuake.getLat(),earthQuake.getLon()));
//                        Log.d("Quake:", "onResponse: " +quakeArrayList.get(i).getPlace());
                    }

//                    for (EarthQuake earth:quakeArrayList){
//                        Log.d("newQuake", "onResponse: "+" "+earth.getPlace());
//                    }
                    arrayAdapter=new ArrayAdapter(QuakesList.this,android.R.layout.simple_list_item_1, android.R.id.text1,arrayList);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent=new Intent(QuakesList.this,newMaps.class);
//                            Log.d("PositionSee", "onItemClick: "+quakeArrayList.get(position).getPlace()+"  "+arrayList.get(position));
                            intent.putExtra("Lat",quakeArrayList.get(position).latitude);
                            intent.putExtra("Lon",quakeArrayList.get(position).longitude);
                            intent.putExtra("Place",arrayList.get(position));
                            startActivity(intent);
                        }
                    });
                    arrayAdapter.notifyDataSetChanged();

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
}
