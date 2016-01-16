package com.eap.sdy51.ge2015;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alepapadop on 1/12/16.
 */
public class CompatibleAdapter extends ArrayAdapter<Device> {

    ArrayList<Device> _comp_devices;
    ArrayList<Synapses> _synaps;
    Context _context;
    private TaskCompletedInterface _listener;
    private Integer _device_id;


    public CompatibleAdapter(Context context, ArrayList<Device> comp_devices, ArrayList<Synapses> synaps, Integer device_id) {
        super(context, 0, comp_devices);

        _context = context;
        _comp_devices = comp_devices;
        _synaps = synaps;
        _listener = (TaskCompletedInterface)context;
        _device_id = device_id;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Device device = getItem(position);



        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.compatible_adapter, parent, false);
        }

        TextView device_name_tv = (TextView) convertView.findViewById(R.id.device_name);

        device_name_tv.setText(device.devicename);

        Button but = (Button) convertView.findViewById(R.id.device_button);
        but.setText("Create Synapse");
        but.setBackgroundColor(Color.GREEN);


        int flag = 0;
        for (Synapses synaps : _synaps) {
            ArrayList<Integer> device_ids = synaps.devices_ids;
            for (Integer dev_id : device_ids) {
                if (dev_id == device.deviceid) {
                    but.setText("Delete Synapse");
                    but.setBackgroundColor(Color.RED);

                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                break;
            }
        }


        if (flag == 1) {

            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Device deva = getItem(position);

                    for (Synapses synaps : _synaps) {
                        ArrayList<Integer> device_ids = synaps.devices_ids;
                        for (Integer dev_id : device_ids) {
                            if (dev_id == deva.deviceid) {

                                HttpDeleteSynapsisAsyncTask create = new HttpDeleteSynapsisAsyncTask(_listener);
                                create.execute(synaps.synapse_id);
                                break;

                            }
                        }
                    }

                }
            });
        } else {

                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Device dev = getItem(position);
                        //HttpCreateSynapsisAsyncTask create = new HttpCreateSynapsisAsyncTask(_listener);
                        //create.execute(_device_id, device_id);
                        Device devb = getItem(position);
                        HttpCreateSynapsisAsyncTask create = new HttpCreateSynapsisAsyncTask(_listener);
                        create.execute(_device_id, devb.deviceid);

                    }
                });

        }

        return convertView;
    }

}
