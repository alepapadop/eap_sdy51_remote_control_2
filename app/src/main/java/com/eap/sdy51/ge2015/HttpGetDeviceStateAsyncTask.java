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
 * an asynchronous HTTP GET operation to retrieve the device's current state from the database.
 */

public class HttpGetDeviceStateAsyncTask extends AsyncTask<Integer, Void, ArrayList<DeviceState>>{
	Context c;
	private TaskCompletedInterface listener;
	
	public HttpGetDeviceStateAsyncTask(TaskCompletedInterface ac)
	{
		listener=ac;
	}
	
	@Override
	protected ArrayList doInBackground(Integer... params) {
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/get_plug_state.php?id="+params[0]);
        
        ArrayList<DeviceState> result = new ArrayList<DeviceState>();
        
        try {
          
            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);
            
            //read the response
            String responseText = EntityUtils.toString(response.getEntity());
            //Log.i("Response", responseText);
            JSONObject data = new JSONObject(responseText);
            JSONObject status = data.getJSONObject("status");
            String status_result=status.getString("result");
            
            //parse the JSON array of states, only if the status code was a success.       
            if (status_result.equals("Success"))
            {
            	
            	JSONArray statelist = data.getJSONArray("states");
            	for (int i=0; i < statelist.length(); i++)
            	{
            		DeviceState d = new DeviceState();
            		JSONObject details = (JSONObject) statelist.get(i);
            		d.stateid = details.getInt("state_id");             //plug
            		d.state_name = details.getString("description");
            		d.current_value = details.getInt("current_value");
            		d.value_step = details.getInt("value_step");
            		d.min = details.getInt("min_value");
            		d.max = details.getInt("max_value");
            		
            		//add the current state to the device list
            		result.add(d);
            		//Log.i("Get Devices", d.deviceid+", "+d.devicename+", "+d.devicetype);

            	}
            	
            }
               

        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;              
	}
	
	@Override
	protected void onPostExecute(ArrayList<DeviceState> result) {
		listener.onGetStatesCompleted(result);
    }


}
