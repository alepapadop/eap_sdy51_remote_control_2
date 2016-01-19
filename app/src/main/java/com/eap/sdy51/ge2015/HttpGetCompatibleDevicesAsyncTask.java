package com.eap.sdy51.ge2015;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by alepapadop on 1/12/16.
 */
public class HttpGetCompatibleDevicesAsyncTask extends AsyncTask<Integer, Void, ArrayList<Device>>
{
    Context c;
    private TaskCompletedInterface listener;

    public HttpGetCompatibleDevicesAsyncTask (TaskCompletedInterface ac)
    {
        listener = ac;
    }

    @Override
    protected ArrayList<Device> doInBackground(Integer... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/find_compatible_devices.php?id=" + params[0]);
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

                Iterator keys = data.keys();

                while (keys.hasNext()) {
                    // loop to get the dynamic key
                    String current_key = (String)keys.next();
                    Device dev = new Device();

                    JSONObject json_obj = data.getJSONObject(current_key);

                    try {
                        dev.deviceid = json_obj.getInt("device_id");
                        dev.devicename = current_key;

                        JSONObject plugs = data.getJSONObject("plugs");

                        dev.plug_id = new ArrayList<Integer>();

                        for (int i = 0; i < plugs.length(); ++i) {

                            dev.plug_id.add(plugs.getInt("plug_id"));

                        }


                        //dev.devicetype = json_obj.getString("device_type");
                    } catch (Exception e) {
                        ;
                    }

                    result.add(dev);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //for (Device d : result) {
            //if (d.devicename == "alepapadop") {
         //       Log.i("SS", d.devicename + " " + d.deviceid + " " + d.devicetype);
            //}
        //}

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Device> result) {
        listener.onGetCompatibleDevices(result);
    }

}
