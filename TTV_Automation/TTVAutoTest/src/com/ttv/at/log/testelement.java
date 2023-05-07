package com.ttv.at.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class testelement {
	Date start_time = null;
	public Date get_start_time() { return start_time; }
	
	Date end_time = null;
	public Date get_end_time() { return end_time; }
	
	String message_start;
	public String get_message_start() { return message_start; }
	
	String message_result;
	public String get_message_result() { return message_result; }
	
	// passed = 0: failed
	// passed = 1: passed
	// passed = 2: ignore case
	int passed;
	public int get_passed() { return passed; }
	

	ArrayList<action> actions = new ArrayList<action>();
	action last_action = null;
	public ArrayList<action> get_actions() { return actions; }
	public void append_action (action e_action) { actions.add(e_action); last_action = e_action;}
	public void update_action_result (boolean passed, String message_result, String before_action_image, String after_action_image) {
		if (last_action != null)
			last_action.set_result(passed, message_result, before_action_image, after_action_image);
	}
	
	/*public action (Date time, String message_start, boolean passed) {
		this.time = time;
		this.message_start = message_start;
		this.passed = passed;
		this.before_action_image = null;
		this.after_action_image = null;
	}/**/
	public testelement (String message_start) {
		this.start_time = new Date();
		this.message_start = message_start;
		this.passed = 0;
	}
	/*
	public action (String message_start, boolean passed, String before_action_image, String after_action_image) {
		this.time = new Date();
		this.message_start = message_start;
		this.passed = passed;
		this.before_action_image = before_action_image;
		this.after_action_image = after_action_image;
	}/**/
	
	public void set_result(int passed, String message_result) {
		this.end_time = new Date();
		this.passed = passed;
		this.message_result = message_result;
	}
	
	
	static SimpleDateFormat get_full_message_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	public String get_full_message_start () {
		if (start_time == null)
			start_time = new Date();
		return " -------- " + get_full_message_format.format(start_time) + " : " + message_start;
	}
	public String get_full_message_result () {
		if (end_time == null)
			end_time = new Date();
		return " -------- " + get_full_message_format.format(end_time) + " : " + message_result;
	}
}
