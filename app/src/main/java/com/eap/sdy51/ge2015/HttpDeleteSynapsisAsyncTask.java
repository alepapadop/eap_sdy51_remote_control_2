package com.eap.sdy51.ge2015;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by alepapadop on 1/13/16.
 */
public class HttpDeleteSynapsisAsyncTask extends AsyncTask<Integer, Void, Integer> {
    Context c;
    private TaskCompletedInterface listener;

    HttpDeleteSynapsisAsyncTask(TaskCompletedInterface ac) {
        listener=ac;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/delete_synapse.php?id=" + params[0]);

        Integer result = 0;


        try {

            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);

            //read the response
            String responseText = EntityUtils.toString(response.getEntity());
            //Log.i("Response", responseText);
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
    protected void onPostExecute(Integer integer) {
        listener.onDeleteSynapses(integer);
    }
}
