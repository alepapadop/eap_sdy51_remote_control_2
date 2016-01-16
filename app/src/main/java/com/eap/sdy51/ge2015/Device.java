package com.eap.sdy51.ge2015;

import java.util.ArrayList;

public class Device {

	public int deviceid;
	public String devicename;
	public String devicetype;
	public int user_conf;
    ArrayList<Integer> synapses_id;
	
	public Device()
	{
		
	}
	
	//necessary for providing a string to feed the listview adapter (in device list)
	@Override
    public String toString() {
        return this.devicename+" [" + this.devicetype + "]";
    }
}

