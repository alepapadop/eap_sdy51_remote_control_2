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
import java.util.HashMap;

/**
 * Created by alepapadop on 1/13/16.
 */
public class HttpGetSynapsesAll extends AsyncTask<ArrayList<Synapses>, Void, HashMap<Integer, ArrayList<Integer>>> {
    Context c;
    private TaskCompletedInterface listener;

    public HttpGetSynapsesAll(TaskCompletedInterface ac)
    {
        listener=ac;
    }

    @Override
    protected HashMap<Integer, ArrayList<Integer>> doInBackground(ArrayList<Synapses>... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(
                "http://150.140.15.50/sdy51/2015/get_synapses.php");

        HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();

        Synapses dummy2 = params[0].get(params[0].size() - 1);

        Integer device_id = dummy2.synapse_id;

        Synapses dummy = params[0].get(params[0].size() - 2);


        Integer plug_id = dummy.synapse_id;

        params[0].remove(params[0].size() - 1);
        params[0].remove(params[0].size() - 1);

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



                    JSONObject synapsis_obj = (JSONObject) synapses.get(i);
                    JSONObject details = synapsis_obj.getJSONObject("details");

                    int tmp_synapse_id = details.getInt("synapse_id");

                    for (Synapses syn : params[0]) {
                        if (syn.synapse_id == tmp_synapse_id) {

                            JSONArray participants = details.getJSONArray("participants");
                            int flag = 0;

                            for (int j = 0; j < participants.length(); ++j) {
                                JSONObject participants_obj = (JSONObject) participants.get(j);
                                if (participants_obj.getInt("device_id") == device_id && participants_obj.getInt("plug_id") == plug_id) {
                                    flag = 1;
                                }
                            }

                            if (flag == 1) {
                                for (int j = 0; j < participants.length(); ++j) {
                                    JSONObject participants_obj = (JSONObject) participants.get(j);
                                    Integer tmp_device_id = participants_obj.getInt("device_id");
                                    Integer tmp_device_plug = participants_obj.getInt("plug_id");

                                    if (!result.containsKey(tmp_device_id)) {
                                        ArrayList<Integer> list = new ArrayList<Integer>();
                                        list.add(tmp_device_plug);
                                        result.put(tmp_device_id, list);
                                    } else {
                                        ArrayList<Integer> list = result.get(tmp_device_id);
                                        list.add(tmp_device_plug);
                                    }


                                }
                            }

                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(HashMap<Integer, ArrayList<Integer>> result) {
        listener.onGetSynapsesAll(result);
    }
}
