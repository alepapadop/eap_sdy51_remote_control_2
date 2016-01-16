package com.eap.sdy51.ge2015;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alepapadop on 1/13/16.
 */
public class Synapses {
    int synapse_id;
    ArrayList<Integer> devices_ids;
    HashMap<Integer, Integer> devices_id_plugs; //device_id - plug_id


    Synapses() {
        devices_ids = new ArrayList<Integer>();
        devices_id_plugs = new HashMap<Integer, Integer>();



    }
}
