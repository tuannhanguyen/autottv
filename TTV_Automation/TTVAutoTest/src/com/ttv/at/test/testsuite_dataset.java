package com.ttv.at.test;

import java.util.ArrayList;

public class testsuite_dataset {
	String name;
	public String get_name () {return name;}
	
	ArrayList<parameter> params = null;
	public ArrayList<parameter> get_params() {return params; }
	
	public testsuite_dataset (String name, ArrayList<parameter> params) {
		this.name = name;
		this.params = params;
	}
	
	public void apply_env () {
		// APPLY SETTING for data_set
		if (params != null && params.size() > 0)
			for (parameter prop : params) {
				// Check runtime to set default runtime
				if (prop.check_key("runtime"))
					testsetting.set_runtime(prop.get_value().toUpperCase());
				else if (prop.check_key("default browser"))
					testsetting.set_default_browser(prop.get_value().toUpperCase());
				else if (prop.check_key("timeout")) {
					int set_timeout = (int)Double.parseDouble(prop.get_value());
					if (set_timeout > 0)
						testsetting.set_timeout(set_timeout);
				}
				else if (prop.check_key("autoresume")) {
					int set_autoresume = (int)Double.parseDouble(prop.get_value());
					if (set_autoresume >= 0)
						testsetting.set_autoresume(set_autoresume);
				}
			}
	}
	
	// "autoresume"
	public int get_int_value (String key) {

		// APPLY SETTING for data_set
		if (params != null && params.size() > 0)
			for (parameter prop : params)
				if (prop.check_key(key)) {
					int set_autoresume = (int)Double.parseDouble(prop.get_value());
					if (set_autoresume >= 0)
						return set_autoresume;
				}
		return 0;
	}
	public String get_string_value (String key) {

		// APPLY SETTING for data_set
		if (params != null && params.size() > 0)
			for (parameter prop : params)
				if (prop.check_key(key))
					return prop.get_value();
		return null;
	}
}
