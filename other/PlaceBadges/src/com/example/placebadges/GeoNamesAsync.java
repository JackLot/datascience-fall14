package com.example.placebadges;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GeoNamesAsync extends AsyncTask<String, Void, String> {

	//public AsyncResponse delegate = null;
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
	protected void onPostExecute(String result) {
		//delegate.processFinish(result);
	}

	private static String getPlace(String urlString) throws IOException {
		
		HttpURLConnection connection = null;
		
		try {

			// URL for updating the Twitter status
			URL url = new URL(urlString);

			connection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(connection.getInputStream());
			String response = readIS(in);
			
			if (response != null) {
				Log.d(TAG, "The tweet seems to have been posted successfully!");
			}

			return response;

		} catch (MalformedURLException e) {
			throw new IOException("Invalid URL.", e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
		
	private static String readIS(InputStream in){
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
