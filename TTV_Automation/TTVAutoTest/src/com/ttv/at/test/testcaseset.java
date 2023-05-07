package com.ttv.at.test;

import java.util.ArrayList;

public class testcaseset {
	// static instance for management
	static ArrayList<testcaseset> instance_list = new ArrayList<testcaseset>();
	static public void add_intsance (testcaseset instance) {
		instance_list.add(instance);
	}
	static public testcaseset get_instance (String name) {
		for (testcaseset instance:instance_list)
			if (instance.check_name(name))
				return instance;
		return null;
	}
	static public boolean check_instance_existance (String name) {
		for (testcaseset instance:instance_list)
			if (instance.check_name(name))
				return true;
		return false;
	}
	static public void remove_instance (String name) {
		for (int i = 0; i < instance_list.size() ; i ++) { 
			testcaseset instance = instance_list.get(i);
			if (instance.check_name(name))
				instance_list.remove(i);
		}
	}
	static public void clear_instance () {
		instance_list.clear();
	}
	
	
	// owner properties/methods
	String name;
	public String get_name() {return name;}
	
	ArrayList<testcase> testcaselist;
	public ArrayList<testcase> get_testcaselist() {return testcaselist;}
	
	// This list is using for checking if the testlibelement is support with the runtime execution
	ArrayList<testlibrary> testlibrarylist;
	public ArrayList<testlibrary> get_testlibrarylist() {return testlibrarylist;}
	
	public boolean check_name(String name_to_check) {
		if (name != null && name_to_check != null) {
			if (name.toLowerCase().equals(name_to_check.toLowerCase()) || 
					name.replace('/', '\\').toLowerCase().equals(name_to_check.replace('/', '\\').toLowerCase()))
				return true;
		}
		return false;
	}
	
	public testcaseset (String name,ArrayList<testcase> testcaselist, ArrayList<testlibrary> testlibrarylist) {
		this.name = name;
		this.testcaselist = testcaselist;
		this.testlibrarylist = testlibrarylist;
	}
	
}
