package com.ttv.at.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ttv.at.test.parameter;

public class util {
	static final SimpleDateFormat get_full_message_format = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
	static final SimpleDateFormat get_short_message_format = new SimpleDateFormat("yyyy/MM/dd\nHH:mm");
	
	/******** START TEST SUITE ********/
	static final String start_testsuite_simple_html_begin_line = 
			"\n<center><h4> ****** ****** ****** ****** ****** ******</h4></center>" +
			"\n<center><h2> Running Testsuite (simple log) </h2></center>" +
			"<center><h3> Start ";
	static final String start_testsuite_simple_html_end_line = "</h3></center>" +
			"\n<center><table border=1><tr><td bgcolor=#C0C0C0 style=\"width:80px\"><b>Result</b></td> \n" +
			"<td bgcolor=#C0C0C0><b>Description</b></td></tr>";
	static public String create_testsuite_start_simple_html_line () {
		return start_testsuite_simple_html_begin_line + get_full_message_format.format(new Date()) + start_testsuite_simple_html_end_line;
	}

	static final String start_testsuite_detail_html_begin_line = 
			"\n<center><h4> ****** ****** ****** ****** ****** ******</h4></center>" +
			"\n<center><h2> Running Testsuite (detail log) </h2></center>" +
			"<center><h3> Start ";
	static final String start_testsuite_detail_html_end_line = "</h3></center>" +
			"\n<center><table border=1><tr><td bgcolor=#C0C0C0 style=\"width:80px\"><b>Result</b></td> \n" +
			"<td bgcolor=#C0C0C0><b>Description</b></td></tr>";
	static public String create_testsuite_start_detail_html_line () {
		return start_testsuite_detail_html_begin_line + get_full_message_format.format(new Date()) + start_testsuite_detail_html_end_line;
	}
	/**********************************/
	

	
	/******** RESUME TEST SUITE ********/
	static final String resume_testsuite_simple_html_begin_line = 
			"\n<center><h4> ****** ****** ****** ****** ****** ******</h4></center>" +
			"\n<center><h2> Resume Testsuite </h2></center>" +
			"<center><h3> Start ";
	static final String resume_testsuite_simple_html_end_line = "</h3></center>" +
			"<center><table border=1><tr><td bgcolor=#C0C0C0><b>TestCaseID</b></td> \n" +
			"<td bgcolor=#C0C0C0><b>Result</b></td><td bgcolor=#C0C0C0 style=\"width:80px\"><b>Description</b></td></tr>";
	static public String create_testsuite_resume_simple_html_line () {
		return resume_testsuite_simple_html_begin_line + get_full_message_format.format(new Date()) + resume_testsuite_simple_html_end_line;
	}
	
	static final String resume_testsuite_detail_html_begin_line = "\n</table></center>" +
			"\n<center><h4> ****** ****** ****** ****** ****** ******</h4></center>" +
			"\n<center><h2> Resume Testsuite </h2></center>" +
			"<center><h3> Start ";
	static final String resume_testsuite_detail_html_end_line = "</h3></center>" +
			"<center><table border=1><tr><td bgcolor=#C0C0C0><b>TestCaseID</b></td> \n" +
			"<td bgcolor=#C0C0C0><b>Result</b></td><td bgcolor=#C0C0C0 style=\"width:80px\"><b>Description</b></td></tr>";
	static public String create_testsuite_resume_detail_html_line () {
		return resume_testsuite_detail_html_begin_line + get_full_message_format.format(new Date()) + resume_testsuite_detail_html_end_line;
	}
	/***********************************/
	
	
	
	/******** END TEST SUITE ********/
	static public String create_testsuite_end_simple_html_line () {
		return "\n</table></center><center><h2> End " + get_full_message_format.format(new Date()) + "</h2></center>" + 
			"\n</center><center><h3> --- --- --- --- --- --- --- </h3></center>";
	}
	static public String create_testsuite_end_simple_html_line (int total_passed, int total_failed) {
		return "\n</table></center><center><h2> End " + get_full_message_format.format(new Date()) + "</h2></center>" + 
				"\n</center><center><h3> --- --- PASSED: "+total_passed+" --- --- --- </h3></center>" +
				"\n</center><center><h3> --- --- FAILED: "+total_failed+" --- --- --- </h3></center>" +
			"\n</center><center><h3> --- --- --- --- --- --- --- </h3></center>";
	}
	
	static public String create_testsuite_end_detail_html_line () {
		return "\n</table></center><center><h2> End " + get_full_message_format.format(new Date()) + "</h2></center>" + 
			"\n</center><center><h3> --- --- --- --- --- --- --- </h3></center>";
	}
	static public String create_testsuite_end_detail_html_line (int total_passed, int total_failed) {
		return "\n</table></center><center><h2> End " + get_full_message_format.format(new Date()) + "</h2></center>" + 
				"\n</center><center><h3> --- --- PASSED: "+total_passed+" --- --- --- </h3></center>" +
				"\n</center><center><h3> --- --- FAILED: "+total_failed+" --- --- --- </h3></center>" +
			"\n</center><center><h3> --- --- --- --- --- --- --- </h3></center>";
	}
	/********************************/
	
	
	

	/******** START TEST ********/
	static public String create_test_start_simple_html_line (String test_message, String unique_name) {
		return "<tr><td COLSPAN=3><a id=\"" + unique_name + "\" name=\"" + unique_name + "\"></a><span class=\"start_test\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	
	static public String create_test_start_detail_html_line (String test_message, String unique_name) {
		return "<tr><td COLSPAN=3><a id=\"" + unique_name + "\" name=\"" + unique_name + "\"></a><span class=\"start_test\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	/****************************/
	
	

	/******** TEST PASSED ********/
	static public String create_test_passed_simple_html_line (String test_message) {
		return "<tr><td><span class=\"test_passed\"><b> PASSED </b></span></td>\n" +
			"<td><span class=\"test_passed\"><b>" + test_message + "</b></span></td></tr>\n</tr>\n";
	}
	static public String create_test_passed_detail_html_line (String test_message) {
		return "<tr><td><span class=\"test_passed\"><b> PASSED </b></span></td>\n" +
			"<td><span class=\"test_passed\"><b>" + test_message + "</b></span></td></tr>\n</tr>\n";
	}

	static public String create_test_passed_simple_html_line (String test_message, ArrayList<parameter> returns) {
		String content = "<tr><td><span class=\"test_passed\"><b> PASSED </b></span></td>\n" +
				"<td><span class=\"test_passed\"><b>" + test_message + "</b></span>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						content = content + "<br><span>" + scan_param.get_value() + "</span>";
					else
						content = content + "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		
		content = content + "</td></tr>\n</tr>\n";
		return content;
	}
	static public String create_test_passed_detail_html_line (String test_message, ArrayList<parameter> returns) {
		String content = "<tr><td><span class=\"test_passed\"><b> PASSED </b></span></td>\n" +
				"<td><span class=\"test_passed\"><b>" + test_message + "</b></span>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						content = content + "<br><span>" + scan_param.get_value() + "</span>";
					else
						content = content + "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		
		content = content + "</td></tr>\n</tr>\n";
		return content;
	}
	
	static public String create_result_passed_html_line (Date start_time, String test_name, String unique_name, String duration_minutes) {
		return "<tr>\n<td class=\"datetime\">"+get_short_message_format.format(start_time)+"</td>" + 
			"<td><a id=\"" +unique_name + "\" name=\"" +unique_name + "\" href=\"./simple_log.html#" + unique_name + "\" target=\"content\" class=\"testcasepassed\"><b>"+test_name+"</b></a><br>" +
				"<a href=\"./detail_log.html#" + unique_name + "\" target=\"content\" class=\"testcasepassed\">(detail)</a><span class=\"datetime\">duration: "+duration_minutes+" mins</span></td>" + 
			"<td class=\"testcasepassed\">Passed</td><td></td>\n</tr>";
	}
	/*****************************/
	
	

	/******** TEST FAILED ********/
	static public String create_test_failed_simple_html_line (String test_message) {
		return "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_test_failed_detail_html_line (String test_message) {
		return "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_test_failed_simple_html_line (String test_message, ArrayList<parameter> returns) {
		String content = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
				"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						content = content + "<br><span>" + scan_param.get_value() + "</span>";
					else
						content = content + "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		
		content = content + "</td></tr>\n";
		return content;
	}
	static public String create_test_failed_detail_html_line (String test_message, ArrayList<parameter> returns) {
		String content = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
				"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						content = content + "<br><span>" + scan_param.get_value() + "</span>";
					else
						content = content + "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		
		content = content + "</td></tr>\n";
		return content;
	}
	
	static public String create_test_failed_simple_html_line (String test_message, String before_failed_path, String after_failed_path) {
		String s_message = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	static public String create_test_failed_detail_html_line (String test_message, String before_failed_path, String after_failed_path) {
		String s_message = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	
	static public String create_test_failed_simple_html_line (String test_message, ArrayList<parameter> returns, String before_failed_path, String after_failed_path) {
		String s_message = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						s_message += "<br><span>" + scan_param.get_value() + "</span>";
					else
						s_message += "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	static public String create_test_failed_simple_html_line (String test_message, ArrayList<parameter> returns, String before_failed_path, String after_failed_path, String Posible_Issue) {
		String s_message = "<tr><td><span class=\"knownfailed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"knownfailed\"><b>Posible Issue : " + Posible_Issue + ",</b><br> " + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						s_message += "<br><span>" + scan_param.get_value() + "</span>";
					else
						s_message += "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	
	static public String create_test_failed_detail_html_line (String test_message, ArrayList<parameter> returns, String before_failed_path, String after_failed_path) {
		String s_message = "<tr><td><span class=\"test_failed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"test_failed\"><b>" + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						s_message += "<br><span>" + scan_param.get_value() + "</span>";
					else
						s_message += "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	
	static public String create_test_failed_detail_html_line (String test_message, ArrayList<parameter> returns, String before_failed_path, String after_failed_path, String Posible_Issue) {
		String s_message = "<tr><td><span class=\"knownfailed\"><b> FAILED </b></span></td>\n" +
		"<td><span class=\"knownfailed\"><b>Posible Issue : " + Posible_Issue + ",</b><br> " + test_message + "</b></span>\n";
		if (before_failed_path  != null && before_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + before_failed_path + "\"> before failed </a>";
		if (after_failed_path != null && after_failed_path.length() > 0)
			s_message += "</br><a href = \"images\\" + after_failed_path + "\"> after failed </a>";
		
		if (returns != null && returns.size() > 0)
			for (parameter scan_param : returns) {
				if (scan_param != null && scan_param.get_value() != null) {
					if (scan_param.get_key() == null || scan_param.get_key().length() == 0)
						s_message += "<br><span>" + scan_param.get_value() + "</span>";
					else
						s_message += "<br><span>" + scan_param.get_key() + ":" + scan_param.get_value() + "</span>";
				}
			}
		s_message += "</td></tr>\n<tr/>\n";
		return s_message;
	}
	
	static public String create_result_failed_html_line (Date start_time, String test_name, String unique_name, String duration_minutes) {
		return "<tr>\n<td class=\"datetime\">"+get_short_message_format.format(start_time)+"</td>" + 
			"<td><a name=\"" +unique_name + "\" href=\"./simple_log.html#" + unique_name + "\" target=\"content\" class=\"testcasefailed\"><b>"+test_name+"</b></a>" +
				"<a href=\"./detail_log.html#" + unique_name + "\" target=\"content\" class=\"testcasefailed\">(detail)</a><br><span class=\"datetime\">duration: "+duration_minutes+" mins</span></td>" + 
			"<td class=\"testcasefailed\">Failed</td><td></td>\n</tr>";
	}
	static public String create_result_failed_html_line (Date start_time, String test_name, String unique_name, String duration_minutes, String Posible_Issue) {
		return "<tr>\n<td class=\"datetime\">"+get_short_message_format.format(start_time)+"</td>" + 
			"<td><a name=\"" +unique_name + "\" href=\"./simple_log.html#" + unique_name + "\" target=\"content\" class=\"knownfailed\"><b>"+test_name+",</b></a>" +
				"<a href=\"./detail_log.html#" + unique_name + "\" target=\"content\" class=\"knownfailed\">(detail)</a><br><span class=\"datetime\">duration: "+duration_minutes+" mins</span></td>" + 
			"<td class=\"knownfailed\">Failed</td><td class=\"knownfailed\"><a href=\"https://issuetracker.marketlive.com/browse/"+Posible_Issue+"\" target=\"windowname\">"+Posible_Issue+"</a></td>\n</tr>";
	}
	/*****************************/
	

	
	
	/******** START TEST ELEMENT ********/
	static public String create_test_element_start_simple_html_line (int step_index, String step_description) {
		return "<tr><td>"+step_index+"</td><td COLSPAN=3><span class=\"start_lib\"><b>" + step_description + "</b></span></td></tr>\n";
	}
	static public String create_test_element_start_detail_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"start_lib\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	/************************************/
	
	
	
	
	/******** TEST ELEMENT PASSED ********/
	static public String create_test_element_passed_simple_html_line (int step_index, String test_message) {
		return "<tr><td><span class=\"lib_passed\">step "+step_index+" passed</span></td><td COLSPAN=3><span class=\"lib_passed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_test_element_passed_detail_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"lib_passed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	/*************************************/

	
	
	
	/******** TEST ELEMENT FAILED ********/
	static public String create_test_element_failed_simple_html_line (int step_index, String step_description) {
		return "<tr><td><span class=\"lib_failed\">step "+step_index+" failed</span></td><td COLSPAN=3><span class=\"lib_failed\"><b>" + step_description + "</b></span></td></tr>\n";
	}
	static public String create_test_element_warned_simple_html_line (int step_index, String step_description) {
		return "<tr><td><span class=\"lib_warned\">step "+step_index+" failed</span></td><td COLSPAN=3><span class=\"lib_warned\"><b>" + step_description + "</b></span></td></tr>\n";
	}
	static public String create_test_element_failed_detail_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"lib_failed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_test_element_warned_detail_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"lib_warned\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	/******** TEST ELEMENT FAILED ********/
	
	static public String create_lib_element_start_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"start_act\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_lib_element_passed_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"act_passed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_lib_element_passed_html_line_with_image (String test_message, String image_path) {
		return "<tr><td COLSPAN=3><a href = \"images\\" + image_path + "\"><span class=\"act_passed\"><b>" + test_message + "</b></span></a></td></tr>\n";
	}
	static public String create_lib_element_failed_html_line (String test_message) {
		return "<tr><td COLSPAN=3><span class=\"act_failed\"><b>" + test_message + "</b></span></td></tr>\n";
	}
	static public String create_lib_element_failed_html_line_with_image (String test_message, String image_path) {
		return "<tr><td COLSPAN=3><a href = \"images\\" + image_path + "\"><span class=\"act_failed\"><b>" + test_message + "</b></span></a></td></tr>\n";
	}
}
