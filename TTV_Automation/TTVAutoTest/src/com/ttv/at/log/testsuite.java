package com.ttv.at.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class testsuite {
	com.ttv.at.test.testsuite testsuite_instance;
	public com.ttv.at.test.testsuite get_testsuite_instance () {return testsuite_instance;}
	
	Date start_time = null;
	public Date get_start_time() { return start_time; }
	
	Date end_time = null;
	public Date get_end_time() { return end_time; }
	
	String end_message;
	public String get_end_message() { return end_message; }
	public void set_end_message(String end_message) { this.end_message = end_message; }
	
	boolean passed;
	public boolean get_passed() { return passed;}
	public void set_passed(boolean passed) { this.passed = passed; }
	
	int run_passed = 0, run_failed = 0;
	public void set_run_passed(int run_passed) { this.run_passed = run_passed; }
	public void set_run_failed(int run_failed) { this.run_failed = run_failed; }
	public int get_run_passed() { return run_passed; }
	public int get_run_failed() { return run_failed; }
	
	
	ArrayList<test> tests = new ArrayList<test>();
	test last_test = null;
	public ArrayList<test> get_tests() { return tests; }
	public void append_test (test e_test) { tests.add(e_test); last_test = e_test;}
	public void update_test_result (int passed, String message_result, String before_action_image, String after_action_image) {
		if (last_test != null)
			last_test.set_result(passed, message_result, before_action_image, after_action_image);
	}
	
	String before_failed_image = null;
	public String get_before_failed_image() { return before_failed_image; }
	public void set_before_failed_image(String before_failed_image) { this.before_failed_image = before_failed_image; }
	String after_failed_image = null;
	public String get_after_failed_image() { return after_failed_image; }
	public void set_after_failed_image(String after_failed_image) { this.after_failed_image = after_failed_image; }
	
	public testsuite (com.ttv.at.test.testsuite testsuite_instance) {
		this.testsuite_instance = testsuite_instance;
	}
	
	public testsuite () {
		
	}

	static SimpleDateFormat get_full_message_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	public String get_full_end_message() {
		if (end_time == null)
			end_time = new Date();
		return " --- " + get_full_message_format.format(end_time) + " : " + end_message;
	}

}
