/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.communication;

import java.io.*;
import java.net.*;

import org.json.*;

import android.content.*;
import android.util.*;

import com.dinkydetails.consultantfinder.common.*;

/**
 * Class to implement the communication module to download directories from the remote JSON
 */
public class DownloadDirectoryList {
	
	/** Constant to save TAG for this class */
	private static final String TAG = DownloadDirectoryList.class.getSimpleName();
	
	/** Variable to save the context widget */
	private Context context = null;
	
	/** Constructor */
	public DownloadDirectoryList(Context context) {
		this.context = context;
	}
	
	/** 
	 * Method to send the request message and receive the response message 
	 **/
    public boolean request() {
    	
    	String url = Constants.URL_DOWNLOAD_DIRECTORIES;
    	Log.d(TAG, url);
    	
		String response = connect(url);
		Log.d(TAG, response);
		
		if (response == null || response.equals("")) {
			return false;
		}
		
		JSONObject message = null;
		JSONArray directories = null;
		
		try {
			message = new JSONObject(response);
			directories = message.getJSONArray("Directory");
			
			if (directories != null && directories.length() > 0) {
				Utilities.clearDirectories(context);
				
				for (int i = 0; i < directories.length(); i++) {
					JSONObject directory = directories.getJSONObject(i);
					Utilities.insertDirectory(context, directory);
				}
			}
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		
		return true;
	}
    
    /**
     * Method to get the response against the specific remote url
     */
	private String connect(String path) {
		URL url = null;
		HttpURLConnection http_connection = null;
		InputStream inputstream = null;
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		int result = -1;

		try {
			url = new URL(path);

			http_connection = (HttpURLConnection) url.openConnection();

			http_connection.setConnectTimeout(Constants.TIMEOUT);
			
			result = http_connection.getResponseCode();

			if (result == HttpURLConnection.HTTP_OK) {

				inputstream = http_connection.getInputStream();

				reader = new BufferedReader(new InputStreamReader(inputstream));

				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			}
			else {
				Log.d("HTTPConnection Fail", "" + result);
			}

		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			try {
				reader.close();
				reader = null;
				inputstream.close();
				inputstream = null;
				http_connection.disconnect();
				http_connection = null;
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}

		return sb.toString();
	}

}
