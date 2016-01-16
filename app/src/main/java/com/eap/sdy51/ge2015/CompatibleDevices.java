package com.eap.sdy51.ge2015;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CompatibleDevices extends Activity implements  TaskCompletedInterface {

    private Integer _device_id;
    private String _device_name;
    private String _device_type;
    ArrayList<Device> _comp_dev;
    ArrayList<Synapses> _synaps;
    ListView _lv;
    CompatibleAdapter _comp_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compatible_devices);

        _device_id = getIntent().getIntExtra("device_id", -1);
        _device_name = getIntent().getStringExtra("device_name");
        _device_type = getIntent().getStringExtra("device_type");

        if (_device_id == -1) {
            finish();
        }

        if (isNetworkAvailable())
        {
            HttpGetCompatibleDevicesAsyncTask compatible = new HttpGetCompatibleDevicesAsyncTask(this);
            compatible.execute(_device_id);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onGetDevicesCompleted(ArrayList<Device> devices) {



    }

    @Override
    public void onGetStatesCompleted(ArrayList<DeviceState> states) {

    }

    @Override
    public void onUpdateStateCompleted(Integer[] result) {

    }

    @Override
    public void onGetCompatibleDevices(ArrayList<Device> devices) {

        _comp_dev = devices;

        HttpGetSynapsisAsyncTask synaps = new HttpGetSynapsisAsyncTask(this);
        synaps.execute(_device_id);

    }

    @Override
    public void onGetSynapses(ArrayList<Synapses> result) {

        _synaps = result;


        CompatibleAdapter comp_adapter = new CompatibleAdapter(this, _comp_dev, _synaps, _device_id);
        ListView lv = (ListView) this.findViewById(R.id.listView1);
        _lv = lv;
        lv.setAdapter(comp_adapter);
        _comp_adapter = comp_adapter;
    }

    @Override
    public void onCreateSynapses(Integer result) {

        if (result == 1) {
            Toast.makeText(getApplicationContext(), "New synapse created", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Could not create synapse", Toast.LENGTH_LONG).show();
        }

        _comp_adapter.clear();

        if (isNetworkAvailable())
        {
            HttpGetCompatibleDevicesAsyncTask compatible = new HttpGetCompatibleDevicesAsyncTask(this);
            compatible.execute(_device_id);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onDeleteSynapses(Integer result) {

        if (result == 1) {
            Toast.makeText(getApplicationContext(), "Synapse deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Could not delete synapse", Toast.LENGTH_LONG).show();
        }

        _comp_adapter.clear();

        if (isNetworkAvailable())
        {
            HttpGetCompatibleDevicesAsyncTask compatible = new HttpGetCompatibleDevicesAsyncTask(this);
            compatible.execute(_device_id);
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
        }
    }
}
