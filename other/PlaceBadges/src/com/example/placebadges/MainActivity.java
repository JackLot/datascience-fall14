package com.example.placebadges;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	
	public static final String LAT_STRING = "LATITUDE";
	public static final String LONG_STRING = "LONGITUDE";
	static private final String TAG = "PlaceBadges";
	private Context mApplicationContext;
	
	private ArrayList<Badge> badges = new ArrayList<Badge>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mApplicationContext = getApplicationContext();
		
		final Button getNewPlaceBTN = (Button)findViewById(R.id.addPlaceBTN);
		
		getNewPlaceBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	LayoutInflater inflater = getLayoutInflater();
        		final View myDialog = inflater.inflate(R.layout.add_location, null);
        		
        		final EditText latET = (EditText) myDialog.findViewById(R.id.lat);
        		final EditText longET = (EditText) myDialog.findViewById(R.id.longit);
        		
        		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
        		.setView(myDialog)
        		.setTitle("Add Location")
        		.setCancelable(true)
        		.setPositiveButton("Set Location", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) 
        			{
        				String lat_s = latET.getText().toString().replaceAll("\\s+","");
        				String long_s = longET.getText().toString().replaceAll("\\s+","");
        				long lat = Long.parseLong(lat_s);
        				long longit = Long.parseLong(long_s);
        				
        				if(Math.abs(lat) >= 0 && Math.abs(lat) <= 90 && Math.abs(longit) >= 0 && Math.abs(longit) <= 180){
        					
        					new GeoNamesAsync()
        						.execute("http://api.geonames.org/findNearbyPlaceNameJSON?lat="+lat_s+"&lng="+long_s+"&username=echoninja");   					
        					
        					dialog.dismiss();
        				}
        				else{
        					Toast.makeText(mApplicationContext, "Invalid lat/long (out of range)", Toast.LENGTH_LONG).show();
        				}
        			
        			}
        		})
        		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog,int id) {
        				dialog.cancel();
        			}
        		}).create();
            
        		alertDialog.show();
            }
        });
		
	}
	
	public void addBadgeToLayout(Badge badge)
	{
		LinearLayout scroll = (LinearLayout) findViewById(R.id.placeBadges);
		LayoutInflater inflater =  (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.badge, null);
		
		TextView country = (TextView) view.findViewById(R.id.countryName);
		country.setText("Country: " + badge.country);
		
		TextView place = (TextView) view.findViewById(R.id.placeName);
		place.setText("Place: " + badge.place);
		
		ImageView flag = (ImageView) view.findViewById(R.id.flag);
		
		//LOAD IMAGE VIA AN ASYNC TASK nicely using the Picasso library
		Picasso.with(mApplicationContext).load("http://www.geonames.org/flags/x/" + badge.countryCode.toLowerCase() + ".gif").into(flag);
	    
		scroll.addView(view);
	}
			
		
	public class GeoNamesAsync extends AsyncTask<String, Void, String> {

		private static final String TAG = "PlaceBadges:GeoNamesService";

		@Override
		protected String doInBackground(String... params) {
			try{
				return getPlace(params[0]);
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String response) {
						
			Log.i(TAG, "JSONRESPONSE: " + response);
			
			//Parse JSON result
			try {
				
				JSONArray result = new JSONObject(response).getJSONArray("geonames");
				
				if(result.length() <= 0){
					Toast.makeText(mApplicationContext, "No named country at this location", Toast.LENGTH_LONG).show();
				}else{
					
					JSONObject place = (JSONObject)result.get(0);
					
					Badge badge = new Badge(place.getString("countryName"), place.getString("countryCode"), place.getString("name"));
					
					if(!badges.contains(badge)){
						badges.add(badge);
						addBadgeToLayout(badge);
						Log.i(TAG, "Adding badge: " + badge.toString());
						Toast.makeText(mApplicationContext, "Added new badge!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(mApplicationContext, "You already have this location badge", Toast.LENGTH_LONG).show();
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(mApplicationContext, "No named country at this location", Toast.LENGTH_LONG).show();
			}
		}

		private String getPlace(String urlString) throws IOException {
			
			HttpURLConnection connection = null;
			
			try {

				// URL for updating the Twitter status
				URL url = new URL(urlString);

				connection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(connection.getInputStream());
				String response = readIS(in);
				
				if (response != null) {
					Log.d(TAG, "The GeoName seems to have been aquired successfully!");
				}

				return response;

			} catch (MalformedURLException e) {
				//throw new IOException("Invalid URL.", e);
				Toast.makeText(mApplicationContext, "Malformed URL. Please restart the application", Toast.LENGTH_LONG).show();
				return "{\"geonames\":[]}";
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}
			
		private String readIS(InputStream in){
			StringBuffer stringBuffer = new StringBuffer();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = new String();
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line + "\n");
				}
			} catch (IOException e) {
				Log.i(TAG, "Error reponse from geonames");
				stringBuffer.append("Failed");
			}
			return stringBuffer.toString();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			
			Log.i(TAG, "Delete all PlaceBadges");
			badges.clear();
			LinearLayout scroll = (LinearLayout) findViewById(R.id.placeBadges);
			
			if(scroll.getChildCount() > 0) 
			    scroll.removeAllViews(); 
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
