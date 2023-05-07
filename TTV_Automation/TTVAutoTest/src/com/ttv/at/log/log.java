package com.ttv.at.log;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import com.ttv.at.test.parameter;
import com.ttv.at.test.testsetting;

public class log {
	static SimpleDateFormat date_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	testsuiteset last_testsuiteset = null;
	ArrayList<testsuite> testsuites = new ArrayList<testsuite>();
	testsuite last_testsuite = null;
	public ArrayList<testsuite> get_testsuites() { return testsuites; }
	
	static final String start_log = "<html>\n<HEAD>\n<style>\n"+
			".start_test{\n	font-size: 12pt\n}\n.start_lib{\n	color:Green;\n	font-size: 10pt\n}\n" + 
			".start_act{\n	font-size: 8pt\n}\n" + 
			".test_passed{\n	color:Blue;\n	font-size: 12pt\n}\n" + 
			".test_failed{\n	color:Red;\n	font-size: 12pt\n}\n" + 
			".knownfailed{\n	color:magenta;\n	font-size: 10pt\n}\n" + 
			".lib_passed{\n	color:Blue;\n	font-size: 10pt\n}\n" + 
			".lib_failed{\n	color:Red;\n	font-size: 10pt\n}\n" + 
			".lib_warned{\n	color:magenta;\n	font-size: 10pt\n}\n" + 
			".act_passed{\n	color:Blue;\n	font-size: 8pt\n}\n" +
			".act_failed{\n	color:magenta;\n	font-size: 8pt\n}\n</style>\n</HEAD>\n<body>\n";
	
	static final String start_resultlog = "<HTML>\n<HEAD><style>\n" + 
			".datetime{\n" + "	color:Green;\n	font-size: 8pt\n}\n" + 
			".testcasepassed{\n	color:Blue;\n	font-size: 10pt\n}\n" + 
			".testcasefailed{\n	color:Red;\n	font-size: 10pt\n}\n " + 
			".knownfailed{\n	color:magenta;\n	font-size: 10pt\n}</style>\n</HEAD>\n <BODY>\n	";
	
	static final String html_index_content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">" + 
											"\n<HTML>" + 
											"\n<HEAD>" +
											"\n<script type=\"text/javascript\">" +
											"\n  var QueryString = function () {" +
											"\n  var query_string = {};" +
											"\n  var query = window.location.search.substring(1);" +
											"\n  var vars = query.split(\"&\");" +
											"\n  for (var i=0;i<vars.length;i++) {" +
											"\n    var pair = vars[i].split(\"=\");" +
													"\n    	// If first entry with this name" +
													"\n    if (typeof query_string[pair[0]] === \"undefined\") {" +
													"\n      query_string[pair[0]] = pair[1];" +
													"\n    	// If second entry with this name" +
													"\n    } else if (typeof query_string[pair[0]] === \"string\") {" +
													"\n      var arr = [ query_string[pair[0]], pair[1] ];" +
													"\n      query_string[pair[0]] = arr;" +
													"\n    	// If third or later entry with this name" +
													"\n    } else {" +
													"\n      query_string[pair[0]].push(pair[1]);" +
													"\n    }" +
													"\n  }" +
													"\n    return query_string;" +
													"\n} ();" +
													"\nfunction LoadPage(){" +
													"\n    document.getElementById('testresult').src = \"./test_result.html#\"+QueryString.name;" +
													"\n    document.getElementById('content').src = \"./simple_log.html#\"+QueryString.name;" +
													"\n}" +
													"\n</script>" +
													"\n<TITLE>Test Running Result</TITLE>" +
													"\n</HEAD>" +
													"\n<FRAMESET cols=\"20%,80%\" title=\"\" onLoad=\"LoadPage()\">" +
											"\n	<FRAME src=\"./test_result.html#\" name=\"testresult\" id=\"testresult\" title=\"All test case result\">" +
											"\n	<FRAME src=\"./simple_log.html#\" name=\"content\" id=\"content\" title=\"simple log\" scrolling=\"yes\"><NOFRAMES>" +
											"\n<H2>Frame Alert</H2>" +
											"\n<P>This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.<BR></NOFRAMES></FRAMESET></HTML>";
	// will be called in com.ttv.at.test.testsuiteset
	public void append_testsuiteset (com.ttv.at.test.testsuiteset testsuiteset_instance) {
		last_testsuiteset = new testsuiteset(testsuiteset_instance);
		// proces with event handle
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2)
			if (listeners[i]==log_event_listener.class)
				((log_event_listener)listeners[i+1]).newTestSuiteSetLogOccur(last_testsuiteset);
		
	}
	public void resume_testsuiteset (com.ttv.at.test.testsuiteset testsuiteset_instance) {

		last_testsuiteset = new testsuiteset(testsuiteset_instance);
		// proces with event handle
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2)
			if (listeners[i]==log_event_listener.class)
				((log_event_listener)listeners[i+1]).newTestSuiteSetLogOccur(last_testsuiteset);
		
	}
	
	public void end_testsuiteset () {
		if (last_testsuiteset != null) {

			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).endTestSuiteSetLogOccur(last_testsuiteset);
		}
	}
	
	
	// will be called in com.ttv.at.test.testsuite
	public void append_testsuite (com.ttv.at.test.testsuite testsuite_instance) {
		System.out.println("Start test suite " + testsuite_instance.get_name());
		last_testsuite = new testsuite(testsuite_instance);
		testsuites.add(last_testsuite);
		
		// process write simple log to html file
		File.create_file_write_line_if_file_not_exists(start_log, testsetting.get_default_log_simple_html_file());
		File.write_line(util.create_testsuite_start_simple_html_line(), testsetting.get_default_log_simple_html_file());
		
		// process write detail log to html file
		File.create_file_write_line_if_file_not_exists(start_log, testsetting.get_default_log_detail_html_file());
		File.write_line(util.create_testsuite_start_detail_html_line(), testsetting.get_default_log_detail_html_file());
		
		// process write test result file
		File.create_file_write_line_if_file_not_exists(start_resultlog, 
				testsetting.get_default_test_result_html_file());
		// WRITE TEST SUITE NAME ON RESULT FILE
		File.write_line("<center> <h4> Running test suite " + testsuite_instance.get_name() + " - " + date_format.format(new Date()) +"</h4></center>\n	<P/>\n	<table border=\"1\">\n" + 
		"<tr>\n	<td bgcolor=\"#C0C0C0\"><b>Date time</b></td>\n	<td bgcolor=\"#C0C0C0\"><b>Test Case</b></td>\n	<td bgcolor=\"#C0C0C0\"><b>Result</b></td>\n <td bgcolor=\"#C0C0C0\"><b>Known Issue</b></td>\n</tr>", testsetting.get_default_test_result_html_file());
		
		// process write index html file
		File.create_file_write_line_if_file_not_exists(html_index_content, 
				testsetting.get_default_index_html_file());
		
		// proces with event handle
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2)
			if (listeners[i]==log_event_listener.class)
				((log_event_listener)listeners[i+1]).newTestSuiteLogOccur(last_testsuite);
	}
	public void resume_testsuite (com.ttv.at.test.testsuite testsuite_instance) {
		System.out.println("Resume test suite " + testsuite_instance.get_name());
		last_testsuite = new testsuite(testsuite_instance);
		testsuites.add(last_testsuite);
		
		// process write simple log to html file
		File.write_line(util.create_testsuite_resume_simple_html_line(), testsetting.get_default_log_simple_html_file());

		// process write detail log to html file
		File.write_line(util.create_testsuite_resume_detail_html_line(), testsetting.get_default_log_detail_html_file());

		// WRITE TEST SUITE NAME ON RESULT FILE
		File.write_line("<center> <h4> Resuming test suite " + testsuite_instance.get_name() + " - " + date_format.format(new Date()) +"</h4></center>\n	<P/>\n	<table border=\"1\">\n" + 
		"<tr>\n	<td bgcolor=\"#C0C0C0\"><b>Date time</b></td>\n	<td bgcolor=\"#C0C0C0\"><b>Test Case</b></td>\n	<td bgcolor=\"#C0C0C0\"><b>Result</b></td>\n</tr>", testsetting.get_default_test_result_html_file());
		
		// proces with event handle
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2)
			if (listeners[i]==log_event_listener.class)
				((log_event_listener)listeners[i+1]).newTestSuiteLogOccur(last_testsuite);
	}
	// will be called in com.ttv.at.test.testsuite
	public void end_testsuite(int run_passed, int run_failed) {
		if (last_testsuite != null) {
			System.out.println("End test suite " + last_testsuite.get_testsuite_instance().get_name());
			
			// process write simple log to html file
			File.write_line(util.create_testsuite_end_simple_html_line(run_passed, run_failed), testsetting.get_default_log_simple_html_file());

			// process write detail log to html file
			File.write_line(util.create_testsuite_end_detail_html_line(run_passed, run_failed), testsetting.get_default_log_detail_html_file());

			// WRITE TEST SUITE NAME ON RESULT FILE
			File.write_line("</table>", testsetting.get_default_test_result_html_file());
			
			last_testsuite.set_run_passed(run_passed);
			last_testsuite.set_run_failed(run_failed);
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).endTestSuiteLogOccur(last_testsuite);
		}
	}
	
	// will be called in com.ttv.at.test.testsuite
	test last_test = null;
	test get_last_test() {
		if (last_test == null && last_testsuite != null && last_testsuite.get_tests().size() > 0)
			last_test = last_testsuite.get_tests().get(last_testsuite.get_tests().size() - 1);
		return last_test;
	}
	public void append_test(test a_test) {
		System.out.println("Running test " + a_test.get_test_name());
		step_index = 1;
		last_step_description = "";
		last_printed_step_description = "";
		if (testsuites.size() == 0) {
			last_testsuite = new testsuite();
			testsuites.add(last_testsuite);
		}
		last_test = a_test;
		last_testsuite.get_tests().add(a_test);
		
		// process write simple log to html file
		File.write_line(util.create_test_start_simple_html_line(a_test.get_full_start_message(), a_test.get_unique_name()), testsetting.get_default_log_simple_html_file());
		
		// process write detail log to html file
		File.write_line(util.create_test_start_detail_html_line(a_test.get_full_start_message(), a_test.get_unique_name()), testsetting.get_default_log_detail_html_file());
		
		// proces with event handle
		Object[] listeners = listenerList.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i=0; i<listeners.length; i+=2)
			if (listeners[i]==log_event_listener.class)
				((log_event_listener)listeners[i+1]).newTestLogOccur(a_test);
	}
	// will be called in com.ttv.at.test.test
	public String set_test_result(int passed, String end_message, ArrayList<parameter> returns) {

		if (last_test != null) {
			last_test.set_result(passed, end_message, last_action_before_filepath, last_action_after_filepath);
			
			// Finalize the step log
			if (!last_step_description.equals(last_printed_step_description)) {
				if (passed == 1)
					File.write_line(util.create_test_element_passed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
				else
					File.write_line(util.create_test_element_failed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
			}

			// process write log to html file
			if (passed == 1) {
				
				// process write simple log to html file
				File.write_line(util.create_test_passed_simple_html_line(last_test.get_full_end_message(), returns), testsetting.get_default_log_simple_html_file());
				
				// process write detail log to html file
				File.write_line(util.create_test_passed_detail_html_line(last_test.get_full_end_message(), returns), testsetting.get_default_log_detail_html_file());

				// process write result log to html file
				File.write_line(util.create_result_passed_html_line(last_test.get_start_time(), last_test.get_test_name(), last_test.get_unique_name(), last_test.get_duration_minutes()), testsetting.get_default_test_result_html_file());
			}
			else {
				// process write simple log to html file
				File.write_line(util.create_test_failed_simple_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath), testsetting.get_default_log_simple_html_file());
				
				// process write detail log to html file
				File.write_line(util.create_test_failed_detail_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath), testsetting.get_default_log_detail_html_file());
					
				// process write result log to html file
				File.write_line(util.create_result_failed_html_line(last_test.get_start_time(), last_test.get_test_name(), last_test.get_unique_name(), last_test.get_duration_minutes()), testsetting.get_default_test_result_html_file());
			}
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateTestLogOccur(last_test);
			
			return last_test.get_unique_name();
		}
		return null;
	}
	public String set_test_result(int passed, String end_message, ArrayList<parameter> returns, String know_issue) {

		if (last_test != null) {
			last_test.set_result(passed, end_message, last_action_before_filepath, last_action_after_filepath);
			
			// Finalize the step log
			if (!last_step_description.equals(last_printed_step_description)) {
				if (passed == 1)
					File.write_line(util.create_test_element_passed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
				else 
					File.write_line(util.create_test_element_failed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
			}
			// process write log to html file
			if (passed == 1) {
				
				// process write simple log to html file
				File.write_line(util.create_test_passed_simple_html_line(last_test.get_full_end_message(), returns), testsetting.get_default_log_simple_html_file());
				
				// process write detail log to html file
				File.write_line(util.create_test_passed_detail_html_line(last_test.get_full_end_message(), returns), testsetting.get_default_log_detail_html_file());

				// process write result log to html file
				File.write_line(util.create_result_passed_html_line(last_test.get_start_time(), last_test.get_test_name(), last_test.get_unique_name(), last_test.get_duration_minutes()), testsetting.get_default_test_result_html_file());
			}
			else {
				if (know_issue == null || know_issue.length() == 0) {
					// process write simple log to html file
					File.write_line(util.create_test_failed_simple_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath), testsetting.get_default_log_simple_html_file());
					
					// process write detail log to html file
					File.write_line(util.create_test_failed_detail_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath), testsetting.get_default_log_detail_html_file());
						
					// process write result log to html file
					File.write_line(util.create_result_failed_html_line(last_test.get_start_time(), last_test.get_test_name(), last_test.get_unique_name(), last_test.get_duration_minutes()), testsetting.get_default_test_result_html_file());
				}
				else {

					// process write simple log to html file
					File.write_line(util.create_test_failed_simple_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath, know_issue), testsetting.get_default_log_simple_html_file());
					
					// process write detail log to html file
					File.write_line(util.create_test_failed_detail_html_line(last_test.get_full_end_message(), returns, last_action_before_filepath, last_action_after_filepath, know_issue), testsetting.get_default_log_detail_html_file());
						
					// process write result log to html file
					File.write_line(util.create_result_failed_html_line(last_test.get_start_time(), last_test.get_test_name(), last_test.get_unique_name(), last_test.get_duration_minutes(), know_issue), testsetting.get_default_test_result_html_file());
				}
			}
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateTestLogOccur(last_test);
			
			return last_test.get_unique_name();
		}
		return null;
	}

	testelement last_testelement = null;
	public void append_testelement (testelement e_testelement) {
		if (last_test != null) {
			// add element
			last_testelement = e_testelement;
			last_test.append_testelement(e_testelement);

			// process write detail log to html file
			File.write_line(util.create_test_element_start_detail_html_line(e_testelement.get_full_message_start()), testsetting.get_default_log_detail_html_file());
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).newTestElementLogOccur(e_testelement);
			
		}
	}

	// passed = 0: failed
	// passed = 1: passed
	// passed = 2: ignore case
	String last_step_description = "";
	String last_printed_step_description = "";
	int step_index = 1;
	public void update_testelement_result(int passed, String message_result, String step_description) {
		if (last_testelement != null) {
			last_testelement.set_result(passed, message_result);
			
			/* process write simple log to html file
			*/

			// process write simple log to html file
			if (step_description != null && step_description.length() > 0 && (!step_description.equals(last_step_description))) {
				if (last_step_description != null && last_step_description.length() > 0) {
					if (passed == 1)
						File.write_line(util.create_test_element_passed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
					else if (passed == 0)
						File.write_line(util.create_test_element_failed_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
					else if (passed == 2)
						File.write_line(util.create_test_element_warned_simple_html_line(step_index, last_step_description), testsetting.get_default_log_simple_html_file());
					step_index ++;
				}
				last_printed_step_description = last_step_description;
				last_step_description = step_description;
			}

			// process write detail log to html file
			if (passed == 1)
				File.write_line(util.create_test_element_passed_detail_html_line(last_testelement.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			else if (passed == 0)
				File.write_line(util.create_test_element_failed_detail_html_line(last_testelement.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			else if (passed == 2)
				File.write_line(util.create_test_element_warned_detail_html_line(last_testelement.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateTestElementLogOccur(last_testelement);
		}
	}
	
	// will be called in com.ttv.at.test.testlibrary
	action last_action = null;
	String last_action_before_filepath = null;
	String last_action_after_filepath = null;
	public void append_action(action a_action) {
		if (last_testelement != null) {
			// add action
			last_action = a_action;
			last_testelement.append_action(a_action);

			// process write log to html file
			File.write_line(util.create_lib_element_start_html_line(a_action.get_full_message_start()), testsetting.get_default_log_detail_html_file());
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).newActionLogOccur(a_action);
		}
	}
	// will be called in com.ttv.at.test.testlibrary
	public void update_action_result(boolean passed, String message_result) {
		last_action_before_filepath = null;
		last_action_after_filepath = null;
		if (last_action != null) {
			last_action.set_result(passed, message_result, null, null);

			// process write log to html file
			if (passed)
				File.write_line(util.create_lib_element_passed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			else
				File.write_line(util.create_lib_element_failed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateActionLogOccur(last_action);
		}
	}
	public void update_action_result(boolean passed, String message_result, BufferedImage before_action_image, BufferedImage after_action_image) {
		if (last_action != null) {
			// Write Action image
			last_action_before_filepath = null;
			if (before_action_image != null) {
				try {
					last_action_before_filepath =  ((Long)((new Date()).getTime())).toString() + ".before.png";
					String last_action_before_fullpath = testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + last_action_before_filepath;
					java.io.File beforeFile = new java.io.File(last_action_before_fullpath);
					if (!javax.imageio.ImageIO.write(before_action_image, "PNG", beforeFile))
						last_action_before_filepath = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					last_action_before_filepath = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					last_action_before_filepath = null;
				}
			}
			last_action_after_filepath = null;
			if (after_action_image != null) {
				try {
					last_action_after_filepath = ((Long)((new Date()).getTime())).toString() + ".after.png";
					String last_action_before_fullpath = testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + last_action_after_filepath;
					java.io.File afterFile = new java.io.File(last_action_before_fullpath);
					if (!javax.imageio.ImageIO.write(after_action_image, "PNG", afterFile))
						last_action_after_filepath = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					last_action_after_filepath = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					last_action_after_filepath = null;
				}
			}
			
			last_action.set_result(passed, message_result, last_action_before_filepath, last_action_after_filepath);

			// process write log to html file
			if (last_action_before_filepath == null && last_action_after_filepath == null)
				if (passed)
					File.write_line(util.create_lib_element_passed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
				else
					File.write_line(util.create_lib_element_failed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			else if (last_action_after_filepath != null)
				if (passed)
					File.write_line(util.create_lib_element_passed_html_line_with_image(last_action.get_full_message_result(), last_action_after_filepath), testsetting.get_default_log_detail_html_file());
				else
					File.write_line(util.create_lib_element_failed_html_line_with_image(last_action.get_full_message_result(), last_action_after_filepath), testsetting.get_default_log_detail_html_file());
			else if (last_action_before_filepath != null)
				if (passed)
					File.write_line(util.create_lib_element_passed_html_line_with_image(last_action.get_full_message_result(), last_action_before_filepath), testsetting.get_default_log_detail_html_file());
				else
					File.write_line(util.create_lib_element_failed_html_line_with_image(last_action.get_full_message_result(), last_action_before_filepath), testsetting.get_default_log_detail_html_file());
			
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateActionLogOccur(last_action);
		}
	}
	
	// will be called in com.ttv.at.test.testlibrary
	public void update_action_result_print_image(boolean passed, String message_result, BufferedImage before_action_image, BufferedImage after_action_image) {
		if (last_action != null) {
			// Write Action image
			last_action_before_filepath = null;
			if (before_action_image != null) {
				try {
					last_action_before_filepath =  ((Long)((new Date()).getTime())).toString() + ".before.png";
					String last_action_before_fullpath = testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + last_action_before_filepath;
					java.io.File beforeFile = new java.io.File(last_action_before_fullpath);
					if (!javax.imageio.ImageIO.write(before_action_image, "PNG", beforeFile))
						last_action_before_filepath = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					last_action_before_filepath = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					last_action_before_filepath = null;
				}
			}
			last_action_after_filepath = null;
			if (after_action_image != null) {
				try {
					last_action_after_filepath = ((Long)((new Date()).getTime())).toString() + ".after.png";
					String last_action_before_fullpath = testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + last_action_after_filepath;
					java.io.File afterFile = new java.io.File(last_action_before_fullpath);
					if (!javax.imageio.ImageIO.write(after_action_image, "PNG", afterFile))
						last_action_after_filepath = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					last_action_after_filepath = null;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					last_action_after_filepath = null;
				}
			}
			
			// Continue with log
			last_action.set_result(passed, message_result, last_action_before_filepath, last_action_after_filepath);
			last_action.enable_print_after_action_image();

			if (last_action_after_filepath == null) {
				// process write log to html file
				if (passed)
					File.write_line(util.create_lib_element_passed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
				else
					File.write_line(util.create_lib_element_failed_html_line(last_action.get_full_message_result()), testsetting.get_default_log_detail_html_file());
			}
			else {

				if (passed)
					File.write_line(util.create_lib_element_passed_html_line_with_image(last_action.get_full_message_result(), last_action_after_filepath), testsetting.get_default_log_detail_html_file());
				else
					File.write_line(util.create_lib_element_failed_html_line_with_image(last_action.get_full_message_result(), last_action_after_filepath), testsetting.get_default_log_detail_html_file());
			}
			
			
			// proces with event handle
			Object[] listeners = listenerList.getListenerList();
			// Each listener occupies two elements - the first is the listener class
			// and the second is the listener instance
			for (int i=0; i<listeners.length; i+=2)
				if (listeners[i]==log_event_listener.class)
					((log_event_listener)listeners[i+1]).updateActionLogOccur(last_action);
		}
	}
	
	
	// Process new action log event
	// Create the listener list
	protected javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();

	// This methods allows classes to register for MyEvents
	public void addlog_event_listener(log_event_listener listener) {
		listenerList.add(log_event_listener.class, listener);
	}

	// This methods allows classes to unregister for MyEvents
	public void removelog_event_listener(log_event_listener listener) {
		listenerList.remove(log_event_listener.class, listener);
	}
	
	
	
	
	
	static private log instance = new log();
	static public log get_instance() { return instance;}
	
	public static class File {
		static public boolean create_file_write_line_if_file_not_exists(String text_to_write, String file_full_path) {
			try
			{
				java.io.File log_file = new java.io.File(file_full_path);
				if (!log_file.getParentFile().exists()) {
					log_file.getParentFile().mkdirs();
				}
				if (!log_file.exists())
				{
					if (!log_file.createNewFile()) {
						System.out.println("Can not create log file '" + file_full_path + "'.");
						return false;
					}
					else {
						java.io.FileOutputStream fout = new java.io.FileOutputStream(log_file, true);
						new java.io.PrintStream(fout).println (text_to_write);
						fout.close();
						return true;
					}
				}
				
			}
			catch (Exception ex)
			{
				System.out.println(" -- write log Exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return false;
		}
		static public boolean write_line(String text_to_write, String file_full_path) {
			try
			{
				java.io.File log_file = new java.io.File(file_full_path);
				if ((!log_file.exists()) && (!log_file.createNewFile()))
				{
					System.out.println("Can not create log file '" + file_full_path + "'.");
					return false;
				}
				java.io.FileOutputStream fout = new java.io.FileOutputStream(log_file, true);
				new java.io.PrintStream(fout).println (text_to_write);
				fout.close();
				return true;
			}
			catch (Exception ex)
			{
				System.out.println(" -- write log Exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return false;
		}
	}
}
