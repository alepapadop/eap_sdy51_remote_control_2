package com.eap.sdy51.ge2015;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

public class DeviceActivity extends Activity implements TaskCompletedInterface {
	private int deviceid;
	private String devicename;
    private String device_type;
    private int prev_radio;
    private int user_conf;
    private boolean _recreate_activity = false;
    private Button _but = null;
    private ListView _lv = null;
    private CompatibleAdapter _comp_adapter =null;
    private ArrayList<Synapses> _synapses;
    private  final DeviceActivity _my_class = this;
    private ArrayList<Device> _only_synapse_devices;
    private boolean _auto_update = false;
    private SeekBar _sb = null;
    private int _progress_step = 0;
    private int _seekbar_oldval = 0;
    private int _cur_plug_id = 0;
    private View _selected_widget = null;
    private int _new_val_sw;
    private int _old_val_sw;
    private int _checked_id;



    //private ArrayList<CustomViewObject> custom_views = new ArrayList<CustomViewObject>();
    private ArrayList<CustomViewObject> custom_views = null;


	private ArrayList<DeviceState> states;

	/* this flag enables the widget listeners to connect to the DB and perform update actions,
	 * as initiated by the user, only after the initial states have been retrieved and the
	 * widgets' values have been set accordingly.
	*/
	private boolean readytoupdate = false;

    private final Handler handler = new Handler();

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if(isNetworkAvailable())
                    {
                        HttpGetDeviceStateAsyncTask gd = new HttpGetDeviceStateAsyncTask(_my_class);
                        gd.execute(deviceid);

                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                        startActivity(intent);
                    }
                }
            });
            //Log.i("jjj", "jjjjjjjj");

            handler.postDelayed(mStatusChecker, 10000);
        }
    };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device);

		//get the device details from the list activity
		Bundle b = getIntent().getExtras();
		deviceid = b.getInt("id");
		devicename = b.getString("name");
        device_type = b.getString("type");
        user_conf = b.getInt("user_conf");

		//set up the views
		TextView deviceLabel = (TextView)findViewById(R.id.deviceName);
		deviceLabel.setText(devicename + " " + device_type);

		//connect to the database and retrieve the device's current state
		if(isNetworkAvailable())
		{
			HttpGetDeviceStateAsyncTask gd = new HttpGetDeviceStateAsyncTask(this);
			gd.execute(deviceid);

        }
		else
		{
			Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
			startActivity(intent);
		}



	}





	//unused
	@Override
	public void onGetDevicesCompleted(ArrayList<Device> devices) {


	}




    @Override
    public void onCreateSynapses(Integer result) {

    }


    private int handler_get_msg()
    {
        Message msg = handler.obtainMessage();
        Bundle bundle = msg.getData();
        return bundle.getInt("X");
    }

    private void handler_add_msg(Integer val)
    {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("X", val);
        msg.setData(bundle);
        handler.sendMessage(msg);

    }



	/*
	 * when we have retrieved the device current state, set up the UI widgets
	 * with the appropriate values.
	 */

    private LinearLayout get_layout() {
        return (LinearLayout) findViewById(R.id.device_layout);
    }

    private void set_listener(Switch sw, DeviceState dss) {
        final DeviceState ds = dss;
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            int oldvalue, newvalue;




            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //if (readytoupdate) {

                    //handler.removeCallbacks(mStatusChecker);
                    _selected_widget = buttonView;


                    if (isChecked) {
                        oldvalue = 0;
                        newvalue = 1;
                    } else {
                        oldvalue = 1;
                        newvalue = 0;
                    }

                    _new_val_sw = newvalue;
                    _old_val_sw = oldvalue;
                    _cur_plug_id = ds.stateid;

                HttpGetSynapsesAll all = new HttpGetSynapsesAll(DeviceActivity.this);
                Synapses dummy = new Synapses();
                dummy.synapse_id = ds.stateid;
                Synapses dummy2 = new Synapses();
                dummy2.synapse_id = deviceid;
                _synapses.add(dummy);
                _synapses.add(dummy2);
                all.execute(_synapses);

                    //Log.i("Device Update","User changed the power state");
                    Integer[] parameters = {deviceid, ds.stateid, newvalue, oldvalue, 1}; //our power state, and its new value

                    if (isNetworkAvailable()) {
                        HttpChangeDeviceStateAsyncTask cs = new HttpChangeDeviceStateAsyncTask(DeviceActivity.this);
                        cs.execute(parameters);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                        startActivity(intent);
                    }


                //}
            }
        });
    }

    private int get_progress_step(SeekBar sb, DeviceState ds) {

        int progress = sb.getProgress();
        if (progress != sb.getMax()) {
            progress = (progress / ds.value_step + 1) * ds.value_step;
        }

        return progress;
    }

    private void set_listener(SeekBar sb, DeviceState dss) {
        final DeviceState ds = dss;

        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            private int oldvalue;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //if (readytoupdate) {
                //Log.i("Device Update","User changed the brightness");

                _selected_widget = seekBar;


                if (isNetworkAvailable()) {
                    Integer[] parameters = {deviceid, ds.stateid, get_progress_step(seekBar, ds), oldvalue, 1}; //our brightness state, and its new value
                    HttpChangeDeviceStateAsyncTask cs = new HttpChangeDeviceStateAsyncTask(DeviceActivity.this);
                    cs.execute(parameters);

                    _sb = seekBar;
                    _progress_step = get_progress_step(seekBar, ds);
                    _seekbar_oldval = oldvalue;
                    _cur_plug_id = ds.stateid;

                    HttpGetSynapsesAll all = new HttpGetSynapsesAll(DeviceActivity.this);
                    Synapses dummy = new Synapses();
                    dummy.synapse_id = ds.stateid;
                    Synapses dummy2 = new Synapses();
                    dummy2.synapse_id = deviceid;
                    _synapses.add(dummy);
                    _synapses.add(dummy2);
                    all.execute(_synapses);


                } else {
                    Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                    startActivity(intent);
                }
                // }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // save the starting state, so that if the update fails, we can revert.
                //if (readytoupdate)
                oldvalue = seekBar.getProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                TextView tx = get_textview_from_seekbar(seekBar, ds);
                tx.setText(ds.state_name + " " + get_progress_step(seekBar, ds) + "%");
                //seekBar.setProgress(get_progress_step(seekBar, ds));
                //Log.i("Device States", "Brightness changed to "+progress*states.get(1).value_step);
            }
        });
    }

    private void set_listener(RadioGroup rg, DeviceState dss) {
        final DeviceState ds = dss;

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //if (readytoupdate) {

                //Log.i("Device Update","User changed the power state");



                Integer[] parameters = {deviceid, ds.stateid, checkedId, prev_radio, 1}; //our power state, and its new value

                if (isNetworkAvailable()) {
                    HttpChangeDeviceStateAsyncTask cs = new HttpChangeDeviceStateAsyncTask(DeviceActivity.this);
                    cs.execute(parameters);
                } else {
                    Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
                    startActivity(intent);
                }

                _selected_widget = group;
                _checked_id = checkedId;
                _cur_plug_id = ds.stateid;

                HttpGetSynapsesAll all = new HttpGetSynapsesAll(DeviceActivity.this);
                Synapses dummy = new Synapses();
                dummy.synapse_id = ds.stateid;
                Synapses dummy2 = new Synapses();
                dummy2.synapse_id = deviceid;
                _synapses.add(dummy);
                _synapses.add(dummy2);
                all.execute(_synapses);

                // }
            }
        });
    }

    @Override
    public void onGetSynapsesAll(HashMap<Integer, ArrayList<Integer>> result) {


        int flag = 0;
        Integer master_plug_id = 0;
        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : result.entrySet()) {
            Integer device_id = entry.getKey();
            ArrayList<Integer> plug_ids = entry.getValue();


            flag = 0;

            if (deviceid == device_id) {
         //       master_plug_id = plug_id;
            }

            if (deviceid == device_id) {
                continue;
            }

            for (Integer plug_id : plug_ids) {

                //Log.i("ssss", "device_id " + device_id + " plug_id" + plug_id);



                            Integer[] parameters;

                            if (_selected_widget instanceof SeekBar) {
                                parameters = new Integer[]{device_id, plug_id, _progress_step, _seekbar_oldval, 0}; //our brightness state, and its new value
                            } else if (_selected_widget instanceof Switch) {
                                parameters = new Integer[]{device_id, plug_id, _new_val_sw, _old_val_sw, 1}; //our power state, and its new value
                            } else if (_selected_widget instanceof RadioGroup) {
                                //Log.i("xxxxx", "ssss");
                                parameters = new Integer[]{device_id, plug_id, _checked_id, prev_radio, 1}; //our power state, and its new value
                            } else {
                                parameters = new Integer[]{0};
                                assert false;
                            }
                            HttpChangeDeviceStateAsyncTask cs2 = new HttpChangeDeviceStateAsyncTask(DeviceActivity.this);
                            cs2.execute(parameters);

                        }

        }
    }

    private void set_listeners() {
        for (CustomViewObject custom_view : custom_views) {

            View view = custom_view.custom_view_get_widget();

            switch (custom_view.custom_view_get_view_type()) {
                case SWITCH:
                    set_listener((Switch)view, custom_view.get_device_state());
                    break;
                case SEEKBAR:
                    set_listener((SeekBar)view, custom_view.get_device_state());
                    break;
                case RADIO:
                    set_listener((RadioGroup)view, custom_view.get_device_state());
                    break;
                default:
                    assert false;
                    break;
            }
        }
    }



    private void clear_ui() {

        LinearLayout layout = get_layout();

        if (custom_views != null) {
            if (custom_views.size() > 0) {
                for (CustomViewObject custom_ciew : custom_views) {
                    View view = custom_ciew.custom_view_get_complete_widget();

                    layout.removeView(view);
                }
            }
        }

        if (_but != null) {
            layout.removeView(_but);
            _but = null;
        }

        if (_lv != null) {
            layout.removeView(_lv);
            _lv = null;
        }


    }

    private void create_ui() {

        LinearLayout layout = get_layout();

        custom_views = new ArrayList<CustomViewObject>();

        for (int i=0; i < states.size(); i++)
        {
            DeviceState ds = states.get(i);

            CustomViewObject custom_view = new CustomViewObject(getApplicationContext(), ds);
            layout.addView(custom_view.custom_view_get_complete_widget());
            custom_views.add(custom_view);

            if (custom_view.custom_view_get_view_type() == CustomViewObject.VIEW_TYPE.RADIO) {
                prev_radio = ((RadioGroup)custom_view.custom_view_get_widget()).getCheckedRadioButtonId();
            }

            if (user_conf == 0) {
                custom_view.custom_view_get_widget().setEnabled(false);
            } else {
                custom_view.custom_view_get_widget().setEnabled(true);
            }
        }

        Button but = new Button(getApplicationContext());
        _but = but;
        _but.setText("Configure compatible devices");
        _but.setTextColor(Color.BLACK);
        _but.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(but);

        if (user_conf == 0) {
            but.setEnabled(false);
        }
        _but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent but_intent = new Intent(getApplicationContext(), CompatibleDevices.class);
                but_intent.putExtra("device_id", deviceid);
                but_intent.putExtra("device_name", devicename);
                but_intent.putExtra("device_type", device_type);

                startActivity(but_intent);
            }
        });

        set_listeners();


        ListView lv = new ListView(this);
        _lv = lv;
        layout.addView(_lv);



        CompatibleAdapter comp_adapter = new CompatibleAdapter(this, _only_synapse_devices, _synapses, deviceid);
        _comp_adapter = comp_adapter;
        _lv.setAdapter(comp_adapter);
    }

	@Override
	public void onGetStatesCompleted(ArrayList<DeviceState> fetchedstates) {
		states = fetchedstates;
        handler_add_msg(1);

        HttpGetCompatibleDevicesAsyncTask comp = new HttpGetCompatibleDevicesAsyncTask(this);
        comp.execute(deviceid);

		readytoupdate = true;
	}

    @Override
    public void onDeleteSynapses(Integer result) {


        if (result == 1) {
            Toast.makeText(getApplicationContext(), "Synapse deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Could not delete synapse", Toast.LENGTH_SHORT).show();
        }

        _comp_adapter.clear();


        HttpGetCompatibleDevicesAsyncTask comp = new HttpGetCompatibleDevicesAsyncTask(this);
        comp.execute(deviceid);


    }




    @Override
    public void onGetSynapses(ArrayList<Synapses> result) {

        _synapses = result;
        LinearLayout layout = get_layout();
        ArrayList<Device> only_synapse_devices = new ArrayList<Device>();
        int flag = 0;

        for (Device dev : _comp_devices) {
            flag = 0;
            for (Synapses syn : result) {
                for (Integer id : syn.devices_ids) {
                    if (id == dev.deviceid) {
                        only_synapse_devices.add(dev);
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1) {
                    break;
                }
            }
        }

        _only_synapse_devices = only_synapse_devices;

        ui();

        handler_add_msg(0);
    }

    private void ui() {


        clear_ui();
        create_ui();

    }

    private ArrayList<Device> _comp_devices;

    @Override
    public void onGetCompatibleDevices(ArrayList<Device> devices) {

        _comp_devices = devices;

        HttpGetSynapsisAsyncTask comp = new HttpGetSynapsisAsyncTask(this);
        comp.execute(deviceid);
    }


	/*
	 * when we have retrieved the result of an update operation (initiated by the user)
	 * ensure that we correctly update the UI widgets, or fall back to the original values
	 * if the update to the DB was not sucessful.
	 */

    private void update_view(Switch sw, Integer[] updateinfo, DeviceState ds) {

        if (updateinfo[0] != -1) {
            if (updateinfo[1] == 1) {
                sw.setChecked(true);
            } else {
                sw.setChecked(false);
            }
        } else {
            if (updateinfo[2] == 1) {
                sw.setChecked(true);
            } else {
                sw.setChecked(false);
            }
        }
    }

    private TextView get_textview_from_seekbar(SeekBar sb, DeviceState ds) {
        LinearLayout ll = (LinearLayout)sb.getParent();
        TextView tx = null;

        int child_num = ll.getChildCount();
        for (int i = 0; i < child_num; ++i) {
            View view = ll.getChildAt(i);

            if (view.getId() == ds.stateid) {
                tx = (TextView) view;
            }
        }

        return tx;
    }

    private void update_view(SeekBar sb, Integer[] updateinfo, DeviceState ds) {

        TextView tx = get_textview_from_seekbar(sb, ds);

        if (updateinfo[0] != -1) {
            sb.setProgress(updateinfo[1]);
            if (tx != null) {
                tx.setText(ds.state_name + " " + updateinfo[1] + "%");
            }
            //brt.setText("Brightness: " + updateinfo[1]);
        } else {
            sb.setProgress(updateinfo[2]);
            if (tx != null) {
                tx.setText(ds.state_name + " " + updateinfo[2] + "%");
            }
            //brt.setText("Brightness: "+updateinfo[2]);
        }
    }

    private void update_view(RadioGroup rg, Integer[] updateinfo, DeviceState ds) {
        if (updateinfo[0] != -1) {
            RadioButton rb = (RadioButton)rg.findViewById(updateinfo[1]);
            rb.setChecked(true);
        } else {
            RadioButton rb = (RadioButton)rg.findViewById(updateinfo[2]);
            rb.setChecked(true);
        }
    }


	@Override
	public void onUpdateStateCompleted(Integer[] updateinfo) {

        if (updateinfo[4] == 1) {
            for (CustomViewObject custom_view : custom_views) {
                if (custom_view.get_state_id_() == updateinfo[3]) {
                    switch (custom_view.custom_view_get_view_type()) {
                        case SWITCH:
                            update_view((Switch) custom_view.custom_view_get_widget(), updateinfo, custom_view.get_device_state());
                            break;
                        case SEEKBAR:
                            update_view((SeekBar) custom_view.custom_view_get_widget(), updateinfo, custom_view.get_device_state());
                            break;
                        case RADIO:
                            update_view((RadioGroup) custom_view.custom_view_get_widget(), updateinfo, custom_view.get_device_state());
                            break;
                        default:
                            assert false;
                            break;
                    }
                }
            }
        } else {
            ;
        }
	}


    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}




	//@Override
	public void onResume()
	{

		super.onResume();
        if(isNetworkAvailable())
        {
            HttpGetDeviceStateAsyncTask gd = new HttpGetDeviceStateAsyncTask(this);
            gd.execute(deviceid);

        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), NoConnectionActivity.class);
            startActivity(intent);
        }

        handler.post(mStatusChecker);

	}

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacks(mStatusChecker);
    }


}
