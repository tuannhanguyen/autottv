package com.ttv.at.test;

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
	
	testcase tc_instance;
	public testcase get_tc_instance() { return tc_instance; }
	public void set_tc_instance(testcase tc_instance) { this.tc_instance = tc_instance; }
	
	ArrayList<String> inputs;
	public ArrayList<String> get_inputs() { return inputs; }
	
	status_run run_status = status_run.NOT_RUN;
	public status_run get_run_status() { return run_status;}
	public void set_run_status(status_run run_status) {this.run_status = run_status;}
	
	status_selection selection_status;
	public status_selection get_selection_status() { return selection_status; }
	public void set_selection_status(status_selection selection_status) {
		if (this.selection_status == status_selection.INIT_1_TIME) {
			if (selection_status == status_selection.SELECTED)
				run_status = status_run.NOT_RUN;
			else
				run_status = status_run.STOP;
			
		}
		else if (this.selection_status != status_selection.INIT)
			this.selection_status = selection_status;
	}
	
	String description;
	public String get_description() { return description; }
	
	String probable_issue;
	public String get_probable_issue () { return probable_issue; }
	
	String unique_result_id;
	public String get_unique_result_id () { return unique_result_id; }
	
	Date starttime = null;
	public String get_start_time() {
		if (starttime != null) {
			return date_format.format(starttime).toString(); 
		}
		return "";
	}
	public void set_start_time(Date starttime) { this.starttime = starttime; }
	
	Date endtime = null;
	public String get_end_time() { 
		if (endtime != null) {
			return date_format.format(endtime).toString(); 
		}
		return "";
	}
	public void set_end_time(Date starttime) { this.endtime = starttime; }
	
	public String get_duration () {
		if (starttime != null && endtime != null) {
			long difference = endtime.getTime() - starttime.getTime();
			Date differneceDate = new Date (new Date(difference).toGMTString());
			return duration_format.format(differneceDate).toString();
		}
		return null;
	}
	
	public String get_duration_minutes () {
		if (starttime != null && endtime != null) {
			long secs = (endtime.getTime() - starttime.getTime());
			// double secs = (double)(endtime.getTime() - starttime.getTime());
			return "" + secs;
			//return String.format("%.1g%n", secs);
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
	
	String message;
	public String get_message() { return message;}
	public void set_message(String message) {this.message = message;}
	
	boolean found_by_manual = true;
	public boolean get_found_by_manual() {return found_by_manual;}
	public void toggle_found_by_manual() {
		if (found_by_manual)
			found_by_manual = false;
		else
			found_by_manual = true;
	}
	
	severity Severity = severity.Medium;
	public severity get_severity() { return Severity; }
	public void set_severity(severity newSeverity) {
		Severity = newSeverity;
		/*
		if (Severity == severity.Low)
			Severity = severity.Medium;
		else if (Severity == severity.Medium)
			Severity = severity.High;
		else if (Severity == severity.High)
			Severity = severity.Critical;
		else if (Severity == severity.Critical)
			Severity = severity.Low;*/
	}
	
	public test(testcase tc_instance, ArrayList<String> inputs, status_selection selected, String description) {
		this.tc_instance = tc_instance;
		this.inputs = inputs;
		this.selection_status = selected;
		this.description = description;
	}
	
	public test(testcase tc_instance, ArrayList<String> inputs, status_selection selected, String description, severity Severity) {
		this.tc_instance = tc_instance;
		this.inputs = inputs;
		this.selection_status = selected;
		this.description = description;
		this.Severity = Severity;
	}
	
	public test(testcase tc_instance, ArrayList<String> inputs, status_selection selected, String description, severity Severity, String probable_issue) {
		this.tc_instance = tc_instance;
		this.inputs = inputs;
		this.selection_status = selected;
		this.description = description;
		this.Severity = Severity;
		this.probable_issue = probable_issue;
	}

	public void debug_execute () {
		// 3. set input for tc_instance
		com.ttv.at.log.log.get_instance().append_test(new com.ttv.at.log.test(tc_instance.get_name()));
		if (!debug_input_copied)
			set_input_to_tc_instance ();
		
		// 4. execute tc_instance
		result run_res = tc_instance.debug_execute();
		

		// 5. Update status
		run_status = run_res.get_result();
		if (run_res.get_result() == status_run.PASSED)
			set_selection_status(status_selection.NOT_SELECTED);
		
		if (run_res.get_result() == status_run.PASSED)
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(1, run_res.get_message(), tc_instance.get_tc_returns());
		else {
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(0, run_res.get_message(), tc_instance.get_tc_returns(), probable_issue);
			message = run_res.get_message();
		}
	}
	
	// 1. Reset state
	// 2. Test case reset
	// 3. set input for tc_instance
	// 4. execute tc_instance
	// 5. Update status
	public void execute() {
		run_status = status_run.RUNNING;
		
		// 2. Test case clear data
		tc_instance.reset();
		
		// 3. set input for tc_instance
		com.ttv.at.log.log.get_instance().append_test(new com.ttv.at.log.test(tc_instance.get_name()));
		tc_instance.set_inputs(inputs);
		
		// 4. execute tc_instance
		result run_res = tc_instance.execute();
		
		// 5. Update status
		run_status = run_res.get_result();
		if (run_res.get_result() == status_run.PASSED)
			set_selection_status(status_selection.NOT_SELECTED);
		
		stop = false;
		pause = false;
		
		if (run_res.get_result() == status_run.PASSED)
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(1, run_res.get_message(), tc_instance.get_tc_returns());
		else {
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(0  , run_res.get_message(), tc_instance.get_tc_returns(), probable_issue);
			message = run_res.get_message();
		}
	}

	// 1. set input for tc_instance
	// 2. resume tc_instance
	// 3. Update status
	public void resume() {
		run_status = status_run.RUNNING;
		// 1. set input for tc_instance
		com.ttv.at.log.log.get_instance().append_test(new com.ttv.at.log.test("Resuming test " + tc_instance.get_name()));
		tc_instance.set_inputs(inputs);

		// 2. resume tc_instance
		result run_res = tc_instance.resume();

		// 5. Loop to execute if not passed
		for (int loop_run = 0 ; loop_run < 3 ; loop_run ++) {
			if (run_res.get_result() != status_run.PASSED &&
					(!stop) &&
					(!pause))
				run_res = tc_instance.execute();
			else
				break;
		}

		// 3. Update status
		run_status = run_res.get_result();
		if (run_res.get_result() == status_run.PASSED)
			set_selection_status(status_selection.NOT_SELECTED);
		
		stop = false;
		pause = false;
		
		if (run_res.get_result() == status_run.PASSED)
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(1, run_res.get_message(), tc_instance.get_tc_returns());
		else
			unique_result_id = com.ttv.at.log.log.get_instance().set_test_result(0, run_res.get_message(), tc_instance.get_tc_returns(), probable_issue);
		
	}

	/*
	public void clean_result() {
		run_status = status_run.NOT_RUN;
	}*/
	
	boolean stop = false;
	public void stop() {
		stop = true;
		tc_instance.stop();
	}
	
	boolean pause = false;
	public void pause() {
		pause = true;
		tc_instance.pause();
	}
	
	public void clear () {
		if (tc_instance != null)
			tc_instance.clear();
		
		// Clear list
		if (inputs != null)
			inputs.clear();
		
		// Clear data
		tc_instance = null;
		inputs = null;
		description = null;
	}
	
	public void reset() {
		set_selection_status(status_selection.NOT_SELECTED);
		run_status = status_run.NOT_RUN;
		starttime = null;
		endtime = null;
		message = "";
	}
	
	static public enum severity {
		Critical, High, Medium, Low
	}

	boolean debug_input_copied = false;
	public void reset_debug_input_copied () {
		debug_input_copied = false;
	}
	public void set_input_to_tc_instance () {
		tc_instance.set_inputs(inputs);
		debug_input_copied = true;
	}
}
