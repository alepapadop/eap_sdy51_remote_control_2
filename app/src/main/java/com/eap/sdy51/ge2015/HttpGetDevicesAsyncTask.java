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
 * an asynchronous HTTP GET operation to retrieve all the known devices from the database.
 */
public class HttpGetDevicesAsyncTask extends AsyncTask<Void, Void, ArrayList<Device>>{
	Context c;
	private TaskCompletedInterface listener;
	
	public HttpGetDevicesAsyncTask(TaskCompletedInterface ac)
	{
		listener=ac;
	}
	
	@Override
	protected ArrayList doInBackground(Void... params) {
		
		HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/get_devices.php");
        ArrayList<Device> result = new ArrayList();
        
        try {
          
            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);
            
            //read the response
            String responseText = EntityUtils.toString(response.getEntity());
            //Log.i("Response", responseText);
            JSONObject data = new JSONObject(responseText);
            JSONObject status = data.getJSONObject("status");
            String status_result=status.getString("result");
                      
            if (status_result.equals("Success"))
            {
            	
            	JSONArray devicelist = data.getJSONArray("devices");
            	for (int i=0; i<devicelist.length(); i++)
            	{
            		Device d = new Device();
            		JSONObject details = (JSONObject) devicelist.get(i);
            		d.deviceid = details.getInt("device_id");
            		d.devicename = details.getString("device_name");
            		d.devicetype = details.getString("device_type");
                    d.user_conf = details.getInt("user_configurable");

					//if (d.devicename.contains("alex")) {
                     //   result.add(d);
                    //}
            		//Log.i("Get Devices", d.deviceid+", "+d.devicename+", "+d.devicetype);
            	}
            	
            }
               

        } catch (Exception e) {
            e.printStackTrace();
        }
		return result;              
	}
	
	@Override
	protected void onPostExecute(ArrayList<Device> result) {
		listener.onGetDevicesCompleted(result);
    }


}
