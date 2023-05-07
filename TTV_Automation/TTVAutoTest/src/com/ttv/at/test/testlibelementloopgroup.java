package com.ttv.at.test;

import java.util.ArrayList;

public class testlibelementloopgroup {
	String name = null;
	public String get_name() { return name; }
	
	ArrayList<testlibelement> testlibelements = null;
	public ArrayList<testlibelement> get_testlibelements() { return testlibelements; }
	
	public testlibelementloopgroup (testlibelement action) {
		testlibelements = new ArrayList<testlibelement>();
		testlibelements.add(action);
	}
	
	public testlibelementloopgroup (String name, ArrayList<testlibelement> testlibelements) {
		this.name = name;
		this.testlibelements = testlibelements;
		
		for (int i = 0 ; i < testlibelements.size() ; i ++)
			testlibelements.get(i).set_parent_group(this);
	}
	
	public void clear() {
		// Clear list
		if (testlibelements != null)
			testlibelements.clear();
		
		// Clear data
		name = null;
		testlibelements = null;
	}
}
