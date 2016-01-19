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
public class HttpGetSynapsisAsyncTask extends AsyncTask<Integer, Void, ArrayList<Synapses>> {
    Context c;
    private TaskCompletedInterface listener;

    public HttpGetSynapsisAsyncTask(TaskCompletedInterface ac)
    {
        listener=ac;
    }

    @Override
    protected ArrayList<Synapses> doInBackground(Integer... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/get_synapses.php?device=" + params[0] + "&mode=other");

        ArrayList<Synapses> result = new ArrayList<Synapses>();

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

                int num_of_synapses = status.getInt("active_synapses");
                JSONArray synapses = data.getJSONArray("synapses");
                for (int i = 0; i < synapses.length(); i++) {

                    Synapses synapses_obj = new Synapses();

                    JSONObject synapsis_obj = (JSONObject) synapses.get(i);
                    JSONObject details = synapsis_obj.getJSONObject("details");

                    synapses_obj.synapse_id = details.getInt("synapse_id");


                    JSONArray participants = details.getJSONArray("participants");

                    for (int j = 0; j < participants.length(); ++j) {
                        JSONObject participants_obj = (JSONObject) participants.get(j);
                        synapses_obj.devices_ids.add(participants_obj.getInt("device_id"));

                        if (!synapses_obj.devices_id_plugs.containsKey(participants_obj.getInt("device_id"))) {
                            ArrayList<Integer> list = new ArrayList<Integer>();
                            list.add(participants_obj.getInt("plug_id"));
                            synapses_obj.devices_id_plugs.put(participants_obj.getInt("device_id"), list);
                        } else {
                            ArrayList<Integer> list = synapses_obj.devices_id_plugs.get(participants_obj.getInt("device_id"));
                            list.add(participants_obj.getInt("plug_id"));
                        }


                        //synapses_obj.devices_id_plugs.put(participants_obj.getInt("device_id"), participants_obj.getInt("plug_id"));


                    }
                    result.add(synapses_obj);
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Synapses> result) {
        listener.onGetSynapses(result);
    }
}
