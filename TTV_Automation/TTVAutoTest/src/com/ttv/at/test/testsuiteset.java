package com.ttv.at.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class testsuiteset {
	
	ArrayList<testsuite> testsuites = null;
	public ArrayList<testsuite> get_testsuites () { return testsuites;}
	
	ArrayList<schedule> schedules = null;
	public ArrayList<schedule> get_schedules () { return schedules;}
	
	ArrayList<status_selection> selections = null;
	public ArrayList<status_selection> get_selections () { return selections;}

	public void toggle_selection (int index) {
		if (schedules != null && schedules.size() > index)
			if (selections.get(index) == status_selection.SELECTED)
				selections.set(index, status_selection.NOT_SELECTED);
			else if (selections.get(index) == status_selection.NOT_SELECTED)
				selections.set(index, status_selection.SELECTED);
	}

	public testsuiteset () {
		testsuites = new ArrayList<testsuite>();
		schedules = new ArrayList<schedule>();
		selections = new ArrayList<status_selection>();
	}
	
	public testsuiteset (ArrayList<testsuite> testsuites) {
		this.testsuites = testsuites;
		if (testsuites != null) {
			if (schedules != null)
				schedules.clear();
			else
				schedules = new ArrayList<schedule>();
			if (selections != null)
				selections.clear();
			else
				selections = new ArrayList<status_selection>();
			
			for (int i = 0 ; i < testsuites.size() ; i ++) {
				if (i == 0)
					selections.add(status_selection.SELECTED);
				else
					selections.add(status_selection.NOT_SELECTED);
				schedules.add(null);
			}
		}
	}
	
	public void append_testsuite (testsuite new_testsuite) {
		if (testsuites == null)
			testsuites = new ArrayList<testsuite>();
		testsuites.add(new_testsuite);
		selections.add(status_selection.NOT_SELECTED);
		schedules.add(null);
	}
	
	public void append_schedule (testsuite new_testsuite, schedule schedule) {
		testsuites.add(new_testsuite);
		schedules.add(schedule);
		selections.add(status_selection.SELECTED);
	}
	
	public void append_testsuiteset (testsuiteset new_testsuiteset) {
		if (testsuites == null) {
			testsuites = new ArrayList<testsuite>();
			if (schedules != null)
				schedules.clear();
			else
				schedules = new ArrayList<schedule>();
			if (selections != null)
				selections.clear();
			else
				selections = new ArrayList<status_selection>();
		}
			
		for (testsuite scan_testsuite : new_testsuiteset.get_testsuites()) {
			testsuites.add(scan_testsuite);
			if (testsuites.size() == 1)
				selections.add(status_selection.SELECTED);
			else
				selections.add(status_selection.NOT_SELECTED);
			schedules.add(null);
		}
	}
	

	testsuite running_testsuite = null;
	int running_testsuite_index = -1;
	
	public void execute() {
		stop = false;
		pause = false;
		com.ttv.at.log.log.get_instance().append_testsuiteset(this);
		running_testsuite_index = -1;
		for (int i = 0 ; i < testsuites.size() ; i ++) {
			if (selections.get(i) == status_selection.SELECTED) {
				running_testsuite_index = i;
				running_testsuite = testsuites.get(running_testsuite_index);
				running_testsuite.execute();
				
				if (pause) {
					running_testsuite.pause();
					break;
				}
				if (stop) {
					running_testsuite.stop();
					break;
				}
			}
		}
		com.ttv.at.log.log.get_instance().end_testsuiteset();
	}
	
	public void execute_schedule () {
		while (true) {

			com.ttv.at.log.log.get_instance().append_testsuiteset(this);
			running_testsuite_index = -1;
			for (int i = 0 ; i < testsuites.size() ; i ++) {
				if (selections.get(i) == status_selection.SELECTED && schedules.get(i).is_in_exec_time()) {
					schedules.get(i).done_schedule();
					running_testsuite_index = i;
					running_testsuite = testsuites.get(running_testsuite_index);
					running_testsuite.execute();
					
					if (pause) {
						running_testsuite.pause();
						break;
					}
					if (stop) {
						running_testsuite.stop();
						break;
					}
					schedules.get(i).reset_schedule_time();
				}
			}
			if (pause || stop)
				break;
			com.ttv.at.log.log.get_instance().end_testsuiteset();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void resume() {
		stop = false;
		pause = false;
		com.ttv.at.log.log.get_instance().resume_testsuiteset(this);

		running_testsuite_index = -1;
		for (int i = resume_index ; i < testsuites.size() ; i ++) {
			running_testsuite_index = i;
			running_testsuite = testsuites.get(running_testsuite_index);
			running_testsuite.resume();
			
			if (pause) {
				running_testsuite.pause();
				break;
			}
			if (stop) {
				running_testsuite.stop();
				break;
			}
		}
		com.ttv.at.log.log.get_instance().end_testsuiteset();
	}
	

	boolean stop = false;
	public void stop() {
		stop = true;
		if (running_testsuite != null)
			running_testsuite.stop();
	}
	
	int resume_index = -1;
	public int get_resume_index () { return resume_index; }
	boolean pause = false;
	public void pause() {
		pause = true;
		if (running_testsuite != null) {
			running_testsuite.pause();
			resume_index = running_testsuite_index;
		}
	}

	public int get_max_number_of_run_test () {
		int total_max_run_test = 0;

		for (testsuite scan_testsuite : testsuites) {
			total_max_run_test = total_max_run_test + scan_testsuite.get_max_number_of_run_test();
		}
		return total_max_run_test;
	}
}


