package com.eap.sdy51.ge2015;

import java.util.ArrayList;

import com.eap.sdy51.ge2015.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements TaskCompletedInterface{

	ArrayList<Device> deviceList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get all the devices from the database
		if(isNetworkAvailable())
		{
			HttpGetDevicesAsyncTask getDevicesTask = new HttpGetDevicesAsyncTask(this);
			getDevicesTask.execute();
		}
		else
		{
			Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
			startActivity(intent);	
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	//when all devices have been fetched, we are ready to populate our UI (i.e. the list).
	@Override
	public void onGetDevicesCompleted(ArrayList<Device> devices) {
		
		deviceList=devices;
		
		ListView lv = (ListView) this.findViewById(R.id.listView1);
		ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1, deviceList);
		lv.setAdapter(adapter);
		
		//set up a listener to handle user clicks on the list items
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Log.i("List adapter", "Item click "+position);
				
				//pass the clicked item information to the device details activity, and start it
				Intent intent = new Intent(getApplicationContext(), DeviceActivity.class);
				intent.putExtra("id", deviceList.get(position).deviceid);
				intent.putExtra("name", deviceList.get(position).devicename);
				intent.putExtra("type", deviceList.get(position).devicetype);
                intent.putExtra("user_conf", deviceList.get(position).user_conf);
				startActivity(intent);			
			}
		});
		
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	//unused
	@Override
	public void onGetStatesCompleted(ArrayList<DeviceState> states) {
		// TODO Auto-generated method stub
		
	}
	//unused
	@Override
	public void onUpdateStateCompleted(Integer[] newvalue) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onGetCompatibleDevices(ArrayList<Device> devices) {

    }

    @Override
    public void onGetSynapses(ArrayList<Synapses> result) {

    }

    @Override
    public void onCreateSynapses(Integer result) {

    }

    @Override
    public void onDeleteSynapses(Integer result) {

    }

    @Override
	public void onResume()
	{
		super.onResume();
		//connect to the database and retrieve the device's current state
		if(isNetworkAvailable())
		{
			HttpGetDevicesAsyncTask getDevicesTask = new HttpGetDevicesAsyncTask(this);
			getDevicesTask.execute();
		}
		else
		{
			Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
			startActivity(intent);	
		}
	}
	
}
