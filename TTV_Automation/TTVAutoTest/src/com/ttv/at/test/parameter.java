package com.ttv.at.test;

import java.util.ArrayList;
import java.util.Random;

public class parameter {
	boolean readonly = false;
	public boolean is_readonly() {return readonly;}
	
	String key;
	public String get_key() {
		return key;
	}
	public boolean check_key (String expected_key) {
		String original_key = get_key();
		if (original_key != null && expected_key != null) {
			original_key = original_key.toLowerCase();
			if (original_key.length() == expected_key.length() &&
					original_key.equals(expected_key.toLowerCase()))
				return true;
		}
		return false;
	}
	
	String description;
	public String get_description () { return description; }
	
	param_type type = param_type.STRING;
	public param_type get_type() {
		return this.type;
	}
	
	String string_value = null;
	ArrayList<String> array_string_values = null;
	int array_string_values_current_index = -1;
	public ArrayList<String> get_array_string_values() { return array_string_values; }
	public int get_array_string_values_current_index() { return array_string_values_current_index; }
	public String get_value() {
		if (this.type == param_type.STRING)
			return string_value;
		else if (this.type == param_type.ARRAY_STRING) {
			if (array_string_values == null)
				return null;
			else if (array_string_values_current_index >= 0 && 
					array_string_values_current_index < array_string_values.size())
				return array_string_values.get(array_string_values_current_index);
		}
		return null;
	}
	
	public void clear() {
		if (readonly)
			return;
		source_copy_from = null;
		string_value = null;
		if (array_string_values != null) {
			array_string_values.clear();
			array_string_values_current_index = -1;
		}
	}
	
	public void put_value(String value) {
		if (readonly)
			return;
		this.type = param_type.ARRAY_STRING;
		if (array_string_values == null) {
			array_string_values = new ArrayList<String>();
			array_string_values_current_index = 0;
		}
		if (array_string_values_current_index < 0)
			array_string_values_current_index = 0;
		array_string_values.add(value);
	}
	
	public void move (int array_string_values_current_index) {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index < (array_string_values.size() - 1)) {
			this.array_string_values_current_index = array_string_values_current_index;
		}
	}
	public void move_first () {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0)
			array_string_values_current_index = 0;
		if (source_copy_from != null)
			source_copy_from.move_first();
	}
	public void move_last () {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0)
			array_string_values_current_index = (array_string_values.size() - 1);
		if (source_copy_from != null)
			source_copy_from.move_last();
	}
	public void move_next () {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index < (array_string_values.size() - 1))
			array_string_values_current_index++;
		if (source_copy_from != null)
			source_copy_from.move_next();
	}
	public void move_previous () {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index > 0)
			array_string_values_current_index--;
		if (source_copy_from != null)
			source_copy_from.move_previous();
	}
	public void move_random() {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index < (array_string_values.size() - 1)) {

			// random click
			// -- get random number
			Random randomGen = new Random();
			array_string_values_current_index = randomGen.nextInt(array_string_values.size());
		}
		if (source_copy_from != null)
			source_copy_from.move(array_string_values_current_index);
	}
	public void move_indexno(int move_index) {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index < (array_string_values.size() - 1) && move_index < (array_string_values.size())) {
			
			// -- get index number
			array_string_values_current_index = move_index;
		}
		if (source_copy_from != null)
			source_copy_from.move(array_string_values_current_index);
	}
	public boolean check_end () {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() > 0 &&
				array_string_values_current_index >= (array_string_values.size() - 1))
			return true;
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null &&
				array_string_values.size() == 0)
			return true;
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values == null)
			return true;
		if (this.type != param_type.ARRAY_STRING)
			return true;
		return false;
	}
	public int get_array_size() {
		if (this.type == param_type.ARRAY_STRING && 
				array_string_values != null)
			return array_string_values.size();
		if (this.type == param_type.STRING &&
				string_value != null)
			return 1;
		return 0;
	}
	
	public parameter(String key) {
		this.key = key;
		this.type = param_type.STRING;
	}
	
	public parameter(String key, param_type type) {
		this.key = key;
		this.type = type;
	}
	
	public parameter(String key, String value) {
		this.key = key;
		this.type = param_type.STRING;
		this.string_value = value;
	}
	
	public parameter(String key, String value, String description){
		this.key = key;
		this.type = param_type.STRING;
		this.string_value = value;
		this.description = description;
	}
	
	public parameter(String key, ArrayList<String> array_string_values) {
		this.key = key;
		this.type = param_type.ARRAY_STRING;
		this.array_string_values = array_string_values;
	}
	
	parameter source_copy_from = null;
	public void copy_from(parameter source) {
		if (readonly)
			return;
		// Clear before copy
		clear();
		
		// Start copy
		source_copy_from = source;
		this.type = source.get_type();
		if (source.get_type() == param_type.STRING)
			this.string_value = source.get_value();
		else if(source.get_type() == param_type.ARRAY_STRING)
			if (source.get_array_string_values() != null){
				this.array_string_values = (ArrayList<String>) source.get_array_string_values().clone();
				array_string_values_current_index = source.get_array_string_values_current_index();
			}
	}
	
	public void copy_from(String value) {
		if (readonly)
			return;
		// Clear before copy
		clear();
		source_copy_from = null;
		if (this.type == param_type.STRING)
			this.string_value = value;
		else if(this.type == param_type.ARRAY_STRING){
			if (value == null)
				clear();
			else {
				if (array_string_values != null) {
					array_string_values.clear();
					array_string_values.add(value);
					array_string_values_current_index = 0;
				}
				else {
					array_string_values = new ArrayList<String>();
					array_string_values.add(value);
					array_string_values_current_index = 0;
				}
			}
		}
	}
	
	public enum param_type {
		STRING, ARRAY_STRING
	}
}


