package com.ttv.at.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class testsuite {
	static public SimpleDateFormat simple_date_time_format = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	String name;
	public String get_name () { return name;}
	
	String path_gui_file = null;
	String path_testsuite_file = null;
	public String get_path_testsuite_file() {return path_testsuite_file;}
	public void set_path_testsuite_file (String path_testsuite_file) {this.path_testsuite_file = path_testsuite_file;}
	
	testsuite_dataset dataset = null;
	public testsuite_dataset get_dataset () {return dataset;}
	public void set_dataset (testsuite_dataset dataset) {this.dataset = dataset;}
	
	testcaseset tc_set;
	public void set_tc_set (testcaseset tc_set) {this.tc_set = tc_set;}
	public testcaseset get_tc_set () {return tc_set;}
	
	testarea running_testarea = null;
	int running_testarea_index = -1;
	ArrayList<testarea> testareas;
	public ArrayList<testarea> get_testareas() { return testareas; }
	
	public testsuite(String name, ArrayList<testarea> testareas, testsuite_dataset dataset, testcaseset tc_set) {
		this.testareas = testareas;
		this.name = name;
		this.dataset = dataset;
		this.tc_set = tc_set;
	}
	
	Date startTime = null;
	public Date get_startTime() { return startTime; }
	Date endTime = null;
	public Date get_endTime() { return endTime; }
	
	String summary_report_path = null;
	public String get_summary_report_path () { return summary_report_path; }
	public void set_summary_report_path (String summary_report_path) { this.summary_report_path = summary_report_path;}
	
	int run_passed = 0, run_failed = 0;
	int retry_cycle = 0;
	int total_run_tests = 0;
	public int get_total_run_tests () {return total_run_tests;}
	// return the total of running test
	String runtime_browser;
	public String get_runtime_browser () {return runtime_browser;}
	
	boolean in_running = false;
	public boolean get_in_running () { return in_running; }
	
	public void execute() {
		

		
		// Create log folder
		String log_title = System.getProperty("file.separator") + name +  simple_date_time_format.format(new Date());
		
		// Check to create log folder
		com.ttv.at.test.testsetting.init_default_log_folder(log_title);
		
		in_running = true;
		total_run_tests = 0;
		startTime = new Date();
		completed_tests_count = 0;
		stop = false;
		pause = false;
		com.ttv.at.log.log.get_instance().append_testsuite(this);
		testsetting.set_common_data(dataset);
		
		// reset counter of all area
		for (testarea scan_area : testareas)
			scan_area.reset_counter();
		
		runtime_browser = testsetting.get_runtime_browser();

		for (retry_cycle = 0 ; retry_cycle <= testsetting.get_autoresume() ; retry_cycle ++) {
			boolean no_test_executed = true;
			// Execute all test area
			for (int index = 0 ; index < testareas.size() ; index ++) {
				running_testarea = testareas.get(index);
				running_testarea_index = index;
				int run_tests = running_testarea.execute(retry_cycle);
				if (retry_cycle == 0) // Count for the first round only
					total_run_tests += run_tests;
				if (running_testarea.get_run_total() > 0)
					no_test_executed = false;
				if (pause) {
					running_testarea.pause();
					break;
				}
				if (stop) {
					running_testarea.stop();
					break;
				}
			}

			if (pause)
				break;
			if (stop) {
				stop = false;
				break;
			}
			if (no_test_executed)
				break;
		}
		
		run_passed = 0;
		run_failed = 0;
		for (int index = 0 ; index < testareas.size() ; index ++) {
			run_passed += testareas.get(index).get_run_passed();
			run_failed += testareas.get(index).get_run_failed();
		}
		
		running_testarea = null;
		running_testarea_index = -1;
		endTime = new Date();
		com.ttv.at.log.log.get_instance().end_testsuite(run_passed, run_failed);
		in_running = false;
	}
	public void resume() {
		
		// Create log folder
		String log_title = System.getProperty("file.separator") + name + "_" +  simple_date_time_format.format(new Date());
		
		// Check to create log folder
		com.ttv.at.test.testsetting.init_default_log_folder(log_title);
		
		in_running = true;
		stop = false;
		pause = false;
		com.ttv.at.log.log.get_instance().resume_testsuite(this);
		testsetting.set_common_data(dataset);
		if (resume_index >= 0 && resume_index < testareas.size()) {
			int start_index = resume_index;
			int start_cycle = retry_cycle;
			resume_index = -1;
			for (retry_cycle = start_cycle ; retry_cycle <= testsetting.get_autoresume() ; retry_cycle ++) {
				boolean no_test_executed = true;
				if (retry_cycle > start_cycle)
					start_index = 0;
				// Execute all test area
				for (int index = start_index ; index < testareas.size() ; index ++) {
					running_testarea = testareas.get(index);
					running_testarea_index = index;
					if (index == start_index)
						running_testarea.resume(retry_cycle);
					else
						running_testarea.execute(retry_cycle);
					if (running_testarea.get_run_total() > 0)
						no_test_executed = false;
					if (pause) {
						running_testarea.pause();
						break;
					}
					if (stop) {
						running_testarea.stop();
						break;
					}
				}

				if (pause)
					break;
				if (stop) {
					stop = false;
					break;
				}
				if (no_test_executed)
					break;
			}

			running_testarea = null;
			running_testarea_index = -1;
			endTime = new Date();
		}

		for (int index = 0 ; index < testareas.size() ; index ++) {
			run_passed += testareas.get(index).get_run_passed();
			run_failed += testareas.get(index).get_run_failed();
		}
		
		com.ttv.at.log.log.get_instance().end_testsuite(run_passed, run_failed);
		in_running = false;
	}
	/*
	public void clean_result() {
		for (int index = 0 ; index < testareas.size() ; index ++)
			testareas.get(index).clean_result();
	}
	*/
	
	boolean stop = false;
	public void stop() {
		stop = true;
		if (running_testarea != null)
			running_testarea.stop();
	}
	
	int resume_index = -1;
	public int get_resume_index () { return resume_index; }
	boolean pause = false;
	public void pause() {
		pause = true;
		if (running_testarea != null) {
			running_testarea.pause();
			resume_index = running_testarea_index;
		}
	}
	
	int total_number_run_test = 0;
	
	public int get_max_number_of_run_test () {
		total_number_run_test = 0;

		for (int index = 0 ; index < testareas.size() ; index ++) {
			testarea cur_Testarea = testareas.get(index);
			for (int i = 0 ; i < cur_Testarea.get_tests().size() ; i ++) {
				if (cur_Testarea.get_tests().get(i).get_selection_status() != status_selection.NOT_SELECTED)
					total_number_run_test ++;
			}
		}
		
		if (dataset != null) {
			int auto_resume = dataset.get_int_value("autoresume");
			if (auto_resume > 0)
				total_number_run_test = total_number_run_test * (auto_resume + 1);
		}
		
		return total_number_run_test;
	}
	
	int completed_tests_count = 0;
	public void update_completed_tests_count (int completed_tests_count) {
		this.completed_tests_count = completed_tests_count;
	}
	
	public void clear() {
		// Clear list
		if (testareas != null) {
			for (int index = 0 ; index < testareas.size() ; index ++)
				testareas.get(index).clear();
			testareas.clear();
		}
		
		// Clear data
		running_testarea = null;
		testareas = null;
	}
	
}
