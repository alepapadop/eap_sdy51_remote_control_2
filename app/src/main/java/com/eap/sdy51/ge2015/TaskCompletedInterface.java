package com.eap.sdy51.ge2015;

import java.util.ArrayList;

/*
 * helper interface to allow the asynchronous tasks to pass results back to the
 * calling activities
 */

public interface TaskCompletedInterface {
	void onGetDevicesCompleted(ArrayList<Device> devices);
	void onGetStatesCompleted(ArrayList<DeviceState> states);
	void onUpdateStateCompleted(Integer[] result);
	void onGetCompatibleDevices(ArrayList<Device> devices);
	void onGetSynapses(ArrayList<Synapses> result);
	void onCreateSynapses(Integer result);
	void onDeleteSynapses(Integer result);

}
