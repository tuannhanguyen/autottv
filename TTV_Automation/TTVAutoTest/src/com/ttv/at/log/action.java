package com.ttv.at.log;

import java.util.Date;
import java.text.SimpleDateFormat;


public class action {
	Date start_time = null;
	public Date get_start_time() { return start_time; }
	
	Date end_time = null;
	public Date get_end_time() { return end_time; }
	
	String message_start;
	public String get_message_start() { return message_start; }
	
	String message_result;
	public String get_message_result() { return message_result; }
	
	boolean passed;
	public boolean get_passed() { return passed; }
	
	String before_action_image;
	public String get_before_action_image() { return before_action_image; }
	
	String after_action_image;
	public String get_after_action_image() { return after_action_image; }
	
	boolean print_after_action_image = false;
	public void enable_print_after_action_image() {print_after_action_image = true;}
	public boolean get_print_after_action_image() { return print_after_action_image;}
	
	/*public action (Date time, String message_start, boolean passed) {
		this.time = time;
		this.message_start = message_start;
		this.passed = passed;
		this.before_action_image = null;
		this.after_action_image = null;
	}/**/
	public action (String message_start) {
		this.start_time = new Date();
		this.message_start = message_start;
		this.passed = true;
		this.before_action_image = null;
		this.after_action_image = null;
	}
	/*
	public action (String message_start, boolean passed, String before_action_image, String after_action_image) {
		this.time = new Date();
		this.message_start = message_start;
		this.passed = passed;
		this.before_action_image = before_action_image;
		this.after_action_image = after_action_image;
	}/**/
	public void set_result(boolean passed, String message_result, String before_action_image, String after_action_image) {
		this.end_time = new Date();
		this.passed = passed;
		this.message_result = message_result;
		this.before_action_image = before_action_image;
		this.after_action_image = after_action_image;
	}
	
	
	static SimpleDateFormat get_full_message_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	public String get_full_message_start () {
		if (start_time == null)
			start_time = new Date();
		return " ------ ------ " + get_full_message_format.format(start_time) + " : " + message_start;
	}
	public String get_full_message_result () {
		if (end_time == null)
			end_time = new Date();
		return " ------ ------ " + get_full_message_format.format(end_time) + " : " + message_result;
	}
	
	/*
	static ArrayList<action> action_logs = new ArrayList<action>();
	static public ArrayList<action> get_action_logs() { return action_logs; }
	static public void append_action_logs(Date time, String message, boolean passed) {
		action_logs.add(new action(time, message, passed));
	}
	static public void append_action_logs(String message, boolean passed) {
		action_logs.add(new action(message, passed));
	}
	static public void append_action_logs(action action_log) {
		action_logs.add(action_log);
	}
	*/
}
