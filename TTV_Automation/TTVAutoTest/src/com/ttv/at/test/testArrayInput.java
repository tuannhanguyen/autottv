package com.ttv.at.test;

import java.util.ArrayList;

public class testArrayInput {
	String key = null;
	public String get_key() { return key; }
	
	ArrayList<String> values = null;;
	public ArrayList<String> get_values() { return values;}
	

	// CONTRUCTOR
	public testArrayInput(String name) {
		key = name;
	}
	
	public void copy_values (ArrayList<String> source) {
		if (source == null && values != null) {
			values.clear();
			values = null;
		}
		if (source != null)
			values = (ArrayList<String>)source.clone();
	}
	public ArrayList<String> copy_values () {
		if (values != null)
			return (ArrayList<String>)values.clone();
		return null;
	}
	public void append_values(ArrayList<String> source) {
		if (source != null && source.size() > 0) {
			if (values == null)
				values = new ArrayList<String>();
			for (int i = 0 ; i < source.size() ; i ++){
				String source_value = source.get(i);
				// Check if source_value is not in values before
				boolean bFound = false;
				for (int value_index = 0 ; value_index < values.size() ; value_index ++)
					if (values.get(value_index).equals(source_value)) {
						bFound = true;
						break;
					}
				if (!bFound)
					values.add(source_value);
			}
		}
	}
	
	public void clear() {
		if (values != null)
			values.clear();
	}
}
