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

/**
 * Created by alepapadop on 1/13/16.
 */
public class HttpCreateSynapsisAsyncTask extends AsyncTask<Integer, Void, Integer> {
    Context c;
    private TaskCompletedInterface listener;

    HttpCreateSynapsisAsyncTask(TaskCompletedInterface ac) {
        listener=ac;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/create_synapse.php?deviceid1=" + params[0] + "&deviceid2=" + params[1]);

        Integer result = 0;


        try {

            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);

            //read the response
            String responseText = EntityUtils.toString(response.getEntity());
            Log.i("Response", responseText);
            JSONObject data = new JSONObject(responseText);
            JSONObject status = data.getJSONObject("status");
            String status_result=status.getString("result");


            if (status_result.equals("Success")) {
                result = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integers) {
        listener.onCreateSynapses(integers);
    }
}
