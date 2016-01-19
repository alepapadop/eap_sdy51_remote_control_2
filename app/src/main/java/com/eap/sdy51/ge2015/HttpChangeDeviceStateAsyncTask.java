package com.eap.sdy51.ge2015;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * an asynchronous HTTP GET operation to change the device's current state from the database.
 */

public class HttpChangeDeviceStateAsyncTask extends AsyncTask<Integer, Void, Integer[]>{

	private TaskCompletedInterface listener;
	
	public HttpChangeDeviceStateAsyncTask(TaskCompletedInterface ac)
	{
		listener=ac;
	}
	
	/*
	 * our background task returns an integer array, which contains the following elements
	 * 0= the device state changed (or -1 if the state was not changed)
	 * 1= the new value (or -1 if the state wasn't changed)
	 * 2= the old value (needed so we can revert back to original state in case of failure)
	 */
	@Override
	protected Integer[] doInBackground(Integer... params) {
		
		HttpClient httpclient = new DefaultHttpClient();
        String http_str = "http://150.140.15.50/sdy51/2015/change_plug_state.php?device="+params[0]+"&plug="+params[1]+"&value="+params[2];
        HttpGet httpget = new HttpGet(http_str);
        /*
         * there is no device with id=-1 in the database, so if this stays the same, we have failed to update 
         */
        Integer[] result={-1,-1,-1, -1, -1};
        
        try {
          
            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);
            
            //read the response
            String responseText = EntityUtils.toString(response.getEntity());
            Log.i("Response", http_str);
            Log.i("Response", responseText);
            JSONObject data = new JSONObject(responseText);
            JSONObject status = data.getJSONObject("status");
            String status_result=status.getString("result");
            
            /*
             * prepare the result to send back to the activity. The result is an array with
             * the following elements:
             * 0 = the device state
             * 1 = the new, updated value of the state
             * 2 = the old value of the state, in case we need to fall back to it

             */
            if (status_result.equals("Success"))
            {      	
            	result[0]=params[1]; //the device state
            	result[1]=status.getInt("new_value");
            	result[2]=params[3];

            }
            else {
            	/*
            	 * leave the state and new value to -1, to indicate failure. Only pass
            	 * back the initial value, so we can fall back to it
            	 */
                result[2] = params[3];

            }
            // always return the state
            result[3] = params[1];

            // in order to execute some extra checks
            result[4] = params[4];


        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;              
	}
	
	@Override
	protected void onPostExecute(Integer[] result) {
		listener.onUpdateStateCompleted(result);
    }


}
