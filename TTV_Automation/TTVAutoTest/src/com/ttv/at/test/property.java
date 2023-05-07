package com.ttv.at.test;

public class property {
	String key;
	public String get_key() { return key;}
	public void set_key (String key) {this.key = key;}
	
	String value;
	public String get_value() { return value;}

	int int_value = -1;
	public int get_int_value() {
		if (int_value == -1)
			try {
				int_value = Integer.parseInt(value);
			}
			catch (Exception eee) {
			}
		return int_value;
	}
	
	String description;
	public String get_description() { return description;}
	
	public property(String key, String value) {
		this.key = key;
		this.value = value;
		this.description = "";
	}
	public property(String key, String value, String description) {
		this.key = key;
		this.value = value;
		this.description = description;
	}
	
	public boolean check_key(String key) {
		if (this.key != null && key != null && this.key.toLowerCase().equals(key.toLowerCase()))
			return true;
		return false;
	}
	public boolean check_value(String value) {
		if (this.value != null && value != null && this.value.toLowerCase().equals(value.toLowerCase()))
			return true;
		return false;
	}
}
