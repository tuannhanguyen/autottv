package com.ttv.at.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class test {
	static SimpleDateFormat date_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	static SimpleDateFormat duration_format = new SimpleDateFormat("HH'h' mm'm' ss's'");
	static {
		duration_format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	String unique_name = null;
	public String get_unique_name() { return unique_name;}
	
	Date start_time = null;
	public Date get_start_time() { return start_time; }
	
	Date end_time = null;
	public Date get_end_time() { return end_time; }
	
	public String get_duration_minutes () {
		if (start_time != null && end_time != null) {
			double secs = (double)(end_time.getTime() - start_time.getTime());
			return secs + "";
//			long hours = secs / 3600;
//			secs = secs % 3600;
//			long mins = secs / 60;
//			secs = secs % 60;
//			if (mins <= 1)
//				return ""+mins;
//			else {
//				float min = mins + secs/60;
//				return String.format("%.1g%n", min);
//			}
		}
		return null;
	}
	
	String test_name;
	public String get_test_name() { return test_name; }
	
	String end_message;
	public String get_end_message() { return end_message; }
	public void set_end_message(String end_message) { this.end_message = end_message; }

	// passed = 0: failed
	// passed = 1: passed
	// passed = 2: ignore case
	int passed;
	public int get_passed() { return passed;}
	public void set_passed(int passed) { this.passed = passed; }
	
	ArrayList<testelement> testelements = new ArrayList<testelement>();
	testelement last_testelement = null;
	public ArrayList<testelement> get_testelements() { return testelements; }
	public void append_testelement (testelement e_testelement) { testelements.add(e_testelement); last_testelement = e_testelement;}
	public void update_testelement_result (int passed, String message_result) {
		if (last_testelement != null)
			last_testelement.set_result(passed, message_result);
	}
	
	String before_failed_image = null;
	public String get_before_failed_image() { return before_failed_image; }
	public void set_before_failed_image(String before_failed_image) { this.before_failed_image = before_failed_image; }
	String after_failed_image = null;
	public String get_after_failed_image() { return after_failed_image; }
	public void set_after_failed_image(String after_failed_image) { this.after_failed_image = after_failed_image; }
	
	public test (Date start_time, String test_name, int passed) {
		// unique_name = date_format.format(start_time) + " - " + test_name;
		unique_name = String.valueOf(start_time.getTime());
		this.start_time = start_time;
		this.test_name = test_name;
		this.passed = passed;
	}
	public test (String test_name, int passed) {
		this.start_time = new Date();
		// unique_name = date_format.format(start_time) + " - " + test_name;
		unique_name = String.valueOf(start_time.getTime());
		this.test_name = test_name;
		this.passed = passed;
	}
	public test (Date start_time, String test_name) {
		// unique_name = date_format.format(start_time) + " - " + test_name;
		unique_name = String.valueOf(start_time.getTime());
		this.start_time = start_time;
		this.test_name = test_name;
	}
	public test (String test_name) {
		this.start_time = new Date();
		// unique_name = date_format.format(start_time) + " - " + test_name;
		unique_name = String.valueOf(start_time.getTime());
		this.test_name = test_name;
	}
	
	public void set_result (int passed, String end_message) {
		this.end_time = new Date();
		set_passed(passed);
		set_end_message(end_message);
	}
	
	public void set_result (int passed, String end_message, String before_failed_image, String after_failed_image) {
		this.end_time = new Date();
		set_passed(passed);
		set_end_message(end_message);
		set_before_failed_image(before_failed_image);
		set_after_failed_image(after_failed_image);
	}

	
	public String get_full_start_message() {
		if (start_time == null)
			start_time = new Date();
		return " --- " + date_format.format(start_time) + " : Start test " + test_name;
	}
	public String get_full_end_message() {
		if (end_time == null)
			end_time = new Date();
		return " --- " + date_format.format(end_time) + " : " + end_message;
	}
	
	
	/*
	static ArrayList<test> tests = new ArrayList<test>();
	static public ArrayList<test> get_tests() { return tests; }
	static public void append_tests(test a_test) {tests.add(a_test); }
	*/
}
