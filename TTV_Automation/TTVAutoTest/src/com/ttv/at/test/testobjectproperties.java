package com.ttv.at.test;

import java.util.ArrayList;

import com.ttv.at.test.action.util;


public class testobjectproperties {
	
	public testobjectproperties (ArrayList<property> properties) {
		this.properties = properties;
	}

	ArrayList<property> properties;
	public ArrayList<property> get_properties () { return properties;}
	
	testobject ref_object = null;
	public testobject get_ref_object() { return ref_object;}
	
	ref_object_type ref_type = ref_object_type.RIGHT;
	public ref_object_type get_ref_type() {return ref_type;}
	
	public boolean update_ref_object (ArrayList<testobject> testobjects) {
		String ref_id = get_value("related object");
		if (ref_id != null && ref_id.length() > 0) {
			ref_id = ref_id.toLowerCase();
			for (int i = 0 ; i < testobjects.size() ; i ++) {
				testobject cur_testobject = testobjects.get(i);
				String scan_key = cur_testobject.get_key().toLowerCase();
				if (scan_key.length() == ref_id.length() && scan_key.equals(ref_id)){
					ref_object = cur_testobject;

					String ref_type_text = get_value("related type");
					if (ref_type_text != null && ref_type_text.length() > 0) {
						ref_type_text = ref_type_text.toLowerCase();
						if (ref_type_text.equals("up"))
							ref_type = ref_object_type.UP;
						else if (ref_type_text.equals("down"))
							ref_type = ref_object_type.DOWN;
						else if (ref_type_text.equals("left"))
							ref_type = ref_object_type.LEFT;
						else if (ref_type_text.equals("parent"))
							ref_type = ref_object_type.PARENT;
						else if (ref_type_text.equals("child"))
							ref_type = ref_object_type.CHILD;
					}
					return true;
				}
			}
			return false;
		}
		else
			return true;
	}
	
	public boolean check (String key, String value) {
		// Scanning in properties
		for (int index = 0 ; index  < properties.size() ; index ++) {
			property current_property = properties.get(index);
			if (current_property.get_key().toLowerCase().equals(key.toLowerCase()) &&
					current_property.get_value().toLowerCase().equals(value.toLowerCase()))
				return true;
		}
		return false;
	}
	
	public String get_value (String key) {
		// Scanning in properties
		for (int index = 0 ; index  < properties.size() ; index ++) {
			property current_property = properties.get(index);
			if (current_property.get_key().toLowerCase().equals(key.toLowerCase()))
				return current_property.get_value();
		}
		return null;
	}
	
	public int get_int_value (String key) {
		// Scanning in properties
			for (int index = 0 ; index  < properties.size() ; index ++) {
				property current_property = properties.get(index);
				if (current_property.get_key().toLowerCase().equals(key.toLowerCase()))
					return current_property.get_int_value();
			}
		return -1;
	}

	public enum ref_object_type {
		NONE, LEFT, RIGHT, UP, DOWN, PARENT, CHILD
	}

	static public ArrayList<testobjectproperties> load_from_properties (ArrayList<property> properties) {
		if (properties != null && properties.size() > 0) {
			ArrayList<testobjectproperties> props_return = new ArrayList<testobjectproperties>();
			
			// scan if object not using opt1-
			ArrayList<property> non_opt = new ArrayList<property>();
			for (property prop : properties) {
				if ((!util.reg_compare(prop.get_key(), "opt(\\d+)-.*")))
					non_opt.add(prop);
			}
			
			if (non_opt.size() > 0)
				props_return.add(new testobjectproperties(non_opt));
			
			// scan if object using opt
			for (int i = 1 ; i < 100 ; i ++) {
				String filter_string = "opt" + i + "-";
				ArrayList<property> opt = new ArrayList<property>();
				for (property prop : properties)
					if (prop.get_key().startsWith(filter_string))
						opt.add(prop);
				int opt_len = filter_string.length();
				if (opt.size() > 0) {
					for (property prop : opt)
						prop.set_key(prop.get_key().substring(opt_len));
					props_return.add(new testobjectproperties(opt));
				}
				else
					break;
			}
			return props_return;
		}
		return null;
	}
}
