package com.ttv.at.test;

import java.util.ArrayList;

public class testobject {
	String key;
	public String get_key() { return key; }
	
	String runtime;
	public String get_runtime() { return runtime; }
	
	ArrayList<testobjectproperties> all_properties;
	public ArrayList<testobjectproperties> get_all_properties() { return all_properties; }
	
	
	public testobject(String key, ArrayList<property> properties) {
		this.key = key;
		all_properties = testobjectproperties.load_from_properties (properties);
	}
	
	public boolean update_ref_object (ArrayList<testobject> testobjects) {
		boolean load_ref_object = true;
		if (all_properties != null)
			for (testobjectproperties scan_prop : all_properties)
				if (!scan_prop.update_ref_object(testobjects))
					load_ref_object = false;
		return load_ref_object;
	}
	
	static public final int max_relation_allow = 10;
	public int get_rel_max (int start_index) {
		if (start_index > max_relation_allow)
			return start_index;
		int max_depth = 0;
		if (all_properties != null)
		for (testobjectproperties scan_prop : all_properties)
			if (scan_prop.get_ref_object() != null) {
				int _cur_depth = scan_prop.get_ref_object().get_rel_max(start_index + 1);
				if (max_depth < _cur_depth)
					max_depth = _cur_depth;
			}
		return max_depth;
	}
}
