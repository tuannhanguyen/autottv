package com.ttv.at.test;
import java.util.ArrayList;
import java.util.Date;

public class testarea {
	String name;
	public String get_name() { return name; }
	
	ArrayList<test> tests;
	public ArrayList<test> get_tests() { return tests; }
	
	int total, total_passed, total_failed, total_selected, run_passed, run_failed, run_total;
	public int get_total() { return total; }
	public int get_total_passed() { return total_passed; }
	public int get_total_failed() {return total_failed; }
	public int get_total_selected() {
		total_selected = 0;
		for (test scan_test : tests)
 			if (scan_test.get_selection_status() == status_selection.SELECTED || 
 					scan_test.get_selection_status() == status_selection.INIT ||
							(scan_test.get_selection_status() == status_selection.INIT_1_TIME &&
									scan_test.get_run_status() == status_run.NOT_RUN))
				total_selected ++;
		return total_selected;
	}
	public int get_run_passed() { return run_passed; }
	public int get_run_failed() { return run_failed; }
	public int get_run_total() { return run_total; }
	public void reset_counter() {
		total = 0;
		total_passed = 0;
		total_failed = 0;
		total_selected = 0;
		run_passed = 0;
		run_failed = 0;
		run_total = 0;
	} 
	
	public testarea (String name, ArrayList<test> tests) {
		this.name = name;
		this.tests = tests;
		update_test_count();
	}
	
	test running_test = null;
	int running_test_index = -1;
	public int execute(int run_round) {
		stop = false;
		pause = false;
		run_total = 0;
		for (int index = 0 ; index < tests.size() ; index ++) {
			test cur_test = tests.get(index);

			if (cur_test.get_selection_status() == status_selection.SELECTED ||
					cur_test.get_selection_status() == status_selection.INIT ||
					(cur_test.get_selection_status() == status_selection.INIT_1_TIME &&
							cur_test.get_run_status() == status_run.NOT_RUN)) {
				run_total ++;
				running_test = cur_test;
				running_test_index = index;
				
				//reset message
				running_test.set_message("");
				running_test.set_start_time(new Date());
				running_test.execute();
				running_test.set_end_time(new Date());
				update_test_count();
				
				if (pause) {
					if (running_test.get_run_status() == status_run.PASSED) {
						run_passed ++;
						resume_index = index + 1;
					}
					running_test.pause();
					break;
				}
				
				if (stop) {
					stop = false;
					break;
				}
				if (run_round == 0) {
					if (running_test.get_run_status() == status_run.PASSED)
						run_passed ++;
					else if (running_test.get_run_status() == status_run.FAILED)
						run_failed ++;
				}
				else {

					if (running_test.get_run_status() == status_run.PASSED) {
						run_passed ++;
						run_failed --;
					}
				}
				
			}
			//else
			//	cur_test.reset();
		}
		running_test = null;
		running_test_index = -1;
		return run_total;
	}
	public void resume(int run_round) {
		stop = false;
		pause = false;
		if (resume_index >= 0 && resume_index < tests.size()) {
			int start_index = resume_index;
			resume_index = -1;
			for (int index = start_index ; index < tests.size() ; index ++) {
				running_test = tests.get(index);
				if (running_test.get_selection_status() == status_selection.SELECTED ||
						running_test.get_selection_status() == status_selection.INIT ||
								(running_test.get_selection_status() == status_selection.INIT_1_TIME &&
										running_test.get_run_status() == status_run.NOT_RUN)) {
					running_test_index = index;
					if (index == start_index)
						running_test.resume();
					else
						running_test.execute();
					update_test_count();
					
					if (pause){
						if (running_test.get_run_status() == status_run.PASSED) {
							run_passed ++;
							resume_index = index + 1;
						}
						running_test.pause();
						break;
					}
					
					if (stop) {
						stop = false;
						break;
					}

					if (run_round == 0) {
						if (running_test.get_run_status() == status_run.PASSED)
							run_passed ++;
						else if (running_test.get_run_status() == status_run.FAILED)
							run_failed ++;
					}
					else {

						if (running_test.get_run_status() == status_run.PASSED) {
							run_passed ++;
							run_failed --;
						}
					}
				}
			}
			running_test = null;
			running_test_index = -1;
		}
	}
	/*
	public void clean_result() {
		reset_counter();
		for (int i = 0 ; i < tests.size() ; i ++)
			tests.get(i).clean_result();
	}*/
	
	boolean stop = false;
	public void stop() {
		stop = true;
		if (running_test != null)
			running_test.stop();
	}

	int resume_index = -1;
	boolean pause = false;
	public void pause() {
		pause = true;
		if (running_test != null) {
			resume_index = running_test_index;
			running_test.pause();
		}
	}
	
	public void update_test_count() {
		total = 0;
		total_passed = 0;
		total_failed = 0;
		total_selected = 0;
		if (tests != null)
			for (test scan_test : tests) {
				total ++;
				if (scan_test.get_run_status() == status_run.PASSED)
					total_passed ++;
				else if (scan_test.get_run_status() == status_run.FAILED)
					total_failed++;
				
				if (scan_test.get_selection_status() == status_selection.SELECTED ||
						scan_test.get_selection_status() == status_selection.INIT ||
								(scan_test.get_selection_status() == status_selection.INIT_1_TIME &&
										scan_test.get_run_status() == status_run.NOT_RUN))
					total_selected ++;
			}
	}
	
	public void select_all_tests() {
		if (tests != null) {
			for (int index = 0 ; index < tests.size() ; index ++) {
				tests.get(index).set_selection_status(status_selection.SELECTED);
			}
			update_test_count();
		}
	}
	public void deselect_all_tests() {
		if (tests != null) {
			for (int index = 0 ; index < tests.size() ; index ++) {
				tests.get(index).set_selection_status(status_selection.NOT_SELECTED);
			}
			update_test_count();
		}
	}
	
	public void toggle_selection (int index) {
		if (index >= 0 && index < tests.size()) {
			test cur_test = tests.get(index);
			// If test is selected
			if (cur_test.get_selection_status() != status_selection.INIT && 
					cur_test.get_selection_status() != status_selection.INIT_1_TIME)
				if (cur_test.get_selection_status() == status_selection.SELECTED)
					tests.get(index).set_selection_status(status_selection.NOT_SELECTED);
				else
					tests.get(index).set_selection_status(status_selection.SELECTED);
			else if (cur_test.get_selection_status() == status_selection.INIT_1_TIME)
				if (cur_test.get_run_status() == status_run.NOT_RUN)
					tests.get(index).set_selection_status(status_selection.NOT_SELECTED);
				else
					tests.get(index).set_selection_status(status_selection.SELECTED);
			update_test_count();
		}
		
	}

	public void select_test(int index) {
		if (index >= 0 && index < tests.size()) {
			tests.get(index).set_selection_status(status_selection.SELECTED);
			update_test_count();
		}
	}
	public void deselect_test(int index) {
		if (index >= 0 && index < tests.size()) {
			tests.get(index).set_selection_status(status_selection.NOT_SELECTED);
			update_test_count();
		}
	}
	
	public void toggle_found_by_manual (int index) {

		if (index >= 0 && index < tests.size()) {
			tests.get(index).toggle_found_by_manual ();
			update_test_count();
		}
	}
	public void set_severity (com.ttv.at.test.test.severity newSeverity, int index) {

		if (index >= 0 && index < tests.size()) {
			tests.get(index).set_severity (newSeverity);
			update_test_count();
		}
	}
	
	public void clear() {
		// Clear list
		if (tests != null) {
			for (int index = 0 ; index < tests.size() ; index ++)
				tests.get(index).clear();
			tests.clear();
		}
		
		// Clear data
		name = null;
		tests = null;
		running_test = null;
	}
	
}
