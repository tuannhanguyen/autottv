package com.ttv.at.util.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JProgressBar;

import com.ttv.at.office.appPOI;
import com.ttv.at.office.sheetPOI;
import com.ttv.at.test.testsuiteset;

public class loader {

	static final int start_row = 3;
	static final int start_col = 1;
	static final int max_row = 65000;
	static final int max_col = 256;

	/******************************************************************************************************************/
	/************************************* LOAD SCHEDULE **************************************************************/
	static private String load_schedule_message;
	static public String get_load_schedule_message() { return load_schedule_message; }
	static public void append_load_schedule_message(String message) {
		if (load_schedule_message != null && load_schedule_message.length() > 0)
			load_schedule_message += "\n";
		else
			load_schedule_message = "";
		load_schedule_message += message;
	}
	static public com.ttv.at.test.testsuiteset load_schedule (String schedule_file_fullpath, JProgressBar mainProgress) {
		mainProgress.setMaximum(1);
		mainProgress.setValue(0);
		if (!appPOI.OpenFile(schedule_file_fullpath)) {
			append_load_schedule_message("Error in Opening Schedule File " + schedule_file_fullpath);
			return null;
		}
		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_load_schedule_message("There is no sheet in file " + schedule_file_fullpath);
			return null;
		}
		
		sheetPOI sheetTestSuiteList = null;
		sheetPOI sheetSchedule = null;
		for (sheetPOI scan_sheet : sheets) {
			if (scan_sheet.get_Name().toLowerCase().equals("testsuitelist"))
				sheetTestSuiteList = scan_sheet;
			else if (scan_sheet.get_Name().toLowerCase().equals("schedule"))
				sheetSchedule = scan_sheet;
			if (sheetTestSuiteList != null && sheetSchedule != null)
				break;
		}
		if (sheetTestSuiteList == null) {
			append_load_schedule_message("Unable to get sheet \"TestSuiteList\" in file " + schedule_file_fullpath);
			return null;
		}
		if (sheetSchedule == null) {
			append_load_schedule_message("Unable to get sheet \"Schedule\" in file " + schedule_file_fullpath);
			return null;
		}

		// ****** Open the Test suite list ******
		int COL_TESTSUITE_PREFIX = -1,
				COL_DATASET = -1,
				COL_SHORTCUT_NAME = -1;
		// Scan to get the column
		int blank_count = 0;
		for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
			String col_name = sheetTestSuiteList.getCellText(iColIndex, start_row);
			if ((col_name != null) && (col_name.length() > 0)) {
				col_name = col_name.toLowerCase();
				if (col_name.equals("testsuite prefix"))
					COL_TESTSUITE_PREFIX = iColIndex;
				else if (col_name.equals("dataset"))
					COL_DATASET	= iColIndex;
				else if (col_name.equals("shortcut name"))
					COL_SHORTCUT_NAME = iColIndex;
				blank_count = 0;
			}
			else
				blank_count ++;
			
			if (COL_TESTSUITE_PREFIX >= 0 &&
					COL_DATASET >= 0 &&
					COL_SHORTCUT_NAME >= 0)
				break;
			
			if (blank_count > 5)
				break;
		}
		if (COL_TESTSUITE_PREFIX == -1) {
			append_load_schedule_message("Unable to find column Testsuite Prefix");
			return null;
		}
		if (COL_DATASET == -1) {
			append_load_schedule_message("Unable to find column Dataset");
			return null;
		}
		if (COL_SHORTCUT_NAME == -1) {
			append_load_schedule_message("Unable to find column Shortcut Name");
			return null;
		}
		
		// Loading short cut name
		ArrayList<String> arrPrefixName = new ArrayList<String>();
		ArrayList<String> arrDataSet = new ArrayList<String>();
		ArrayList<String> arrShortCutName = new ArrayList<String>();
		blank_count = 0;
		for (int row_index = start_row + 1 ; row_index < max_row ; row_index ++) {
			// get suitename
			String prefixName = sheetTestSuiteList.getCellText(COL_TESTSUITE_PREFIX, row_index);
			String dataSet = sheetTestSuiteList.getCellText(COL_DATASET, row_index);
			String shortcutName = sheetTestSuiteList.getCellText(COL_SHORTCUT_NAME, row_index);
			if (prefixName != null && 
					prefixName.length() > 0 &&
					dataSet != null &&
					dataSet.length() > 0 &&
					shortcutName != null &&
					shortcutName.length() > 0) {
				arrPrefixName.add(prefixName);
				arrDataSet.add(dataSet);
				arrShortCutName.add(shortcutName);

				blank_count = 0;
			}
			blank_count ++;
			
			if (blank_count > 5)
				break;
		}
		
		// ****** Open Schedule sheet ******
		int COL_TESTSUITE = -1,
				COL_SCHEDULE = -1,
				COL_TYPE = -1;
		blank_count = 0;
		for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
			String col_name = sheetSchedule.getCellText(iColIndex, start_row);
			if ((col_name != null) && (col_name.length() > 0)) {
				col_name = col_name.toLowerCase();
				if (col_name.equals("testsuite"))
					COL_TESTSUITE = iColIndex;
				else if (col_name.equals("time"))
					COL_SCHEDULE	= iColIndex;
				else if (col_name.equals("scheduletype") || col_name.equals("schedule type"))
					COL_TYPE	= iColIndex;
				blank_count = 0;
			}
			else
				blank_count ++;

			if (COL_TESTSUITE >= 0 &&
					COL_SCHEDULE >= 0 &&
					COL_TYPE >= 0)
				break;
			
			if (blank_count > 5)
				break;
		}
		if (COL_TESTSUITE == -1) {
			append_load_schedule_message("Unable to find column Testsuite in sheet Schedule");
			return null;
		}
		if (COL_SCHEDULE == -1) {
			append_load_schedule_message("Unable to find column Time in sheet Schedule");
			return null;
		}

		ArrayList<String> schedule_list_shortcutname = new ArrayList<String>();
		ArrayList<String> schedule_list_type = new ArrayList<String>();
		ArrayList<String> schedule_list_time = new ArrayList<String>();
		ArrayList<Date> schedule_list_date = new ArrayList<Date>();
		ArrayList<com.ttv.at.test.schedule> schedule_list_schedule = new ArrayList<com.ttv.at.test.schedule>();
		blank_count = 0;
		for (int row_index = start_row + 1 ; row_index < max_row ; row_index ++) {
			String shortcutName = sheetSchedule.getCellText(COL_TESTSUITE, row_index);
			String schedule_type = sheetSchedule.getCellText(COL_TYPE, row_index);
			String schedule_time = sheetSchedule.getCellText(COL_SCHEDULE, row_index);
			Date schedule_date_value = sheetSchedule.getCellDateValue(COL_SCHEDULE, row_index);
			if (shortcutName != null && 
					shortcutName.length() > 0) {
				boolean found_shortcut_name = false;
				for (String actual_shortcut_name : arrShortCutName) {
					if (actual_shortcut_name.toLowerCase().equals(shortcutName.toLowerCase())){
						found_shortcut_name = true;
						break;
					}
				}
				
				if (found_shortcut_name) {
					schedule_list_shortcutname.add(shortcutName);
					if (schedule_list_shortcutname.size() == 1 && 
							(schedule_time == null || schedule_time.length() == 0 ||
									schedule_type == null || schedule_type.length() == 0)) {
						append_load_schedule_message("Please declare schedule for the first line");
						return null;
					}
					// Load Schedule_Time as merge cells
					if (schedule_time != null && schedule_time.length() > 0) {
						int mercell_row_count = sheetSchedule.getMergeCells_RowCount(COL_SCHEDULE, row_index);
						if (mercell_row_count > 0)
							for (int i = 0 ; i < mercell_row_count ; i ++){
								schedule_list_time.add(schedule_time);
								schedule_list_date.add(schedule_date_value);
							}
					}
					else if (schedule_list_time.size() < schedule_list_shortcutname.size()) {
						schedule_list_time.add(schedule_time);
						schedule_list_date.add(schedule_date_value);
					}
					
					// Load schedule_type as merge cells
					if (schedule_type != null && schedule_type.length() > 0) {
						int mercell_row_count = sheetSchedule.getMergeCells_RowCount(COL_TYPE, row_index);
						for (int i = 0 ; i < mercell_row_count ; i ++)
							schedule_list_type.add(schedule_type);
					}
					else if (schedule_list_type.size() < schedule_list_shortcutname.size())
						schedule_list_type.add(schedule_type);
					
					com.ttv.at.test.schedule new_schedule = null;
					if (schedule_list_date.get(schedule_list_shortcutname.size() - 1) != null)
						new_schedule = new com.ttv.at.test.schedule(schedule_list_type.get(schedule_list_shortcutname.size() - 1), schedule_list_date.get(schedule_list_shortcutname.size() - 1));
					else
						new_schedule = new com.ttv.at.test.schedule(schedule_list_type.get(schedule_list_shortcutname.size() - 1), schedule_list_time.get(schedule_list_shortcutname.size() - 1));
					
					if (new_schedule.is_useful())
						schedule_list_schedule.add(new_schedule);
					else {
						append_load_schedule_message("Please check schedule specification at line " + row_index);
						return null;
					}
				}
				else {
					append_load_schedule_message("Unable to find \""+shortcutName+"\" of sheet \"Schedule\" in sheet \"TestSuiteList\"");
					return null;
				}
				blank_count = 0;
			}
			else
				blank_count ++;
			
			if (blank_count > 5)
				break;
				
		}
		
		mainProgress.setMaximum(schedule_list_shortcutname.size());
		
		// Loading the schedule
		com.ttv.at.test.testsuiteset load_tetsuiteset = new com.ttv.at.test.testsuiteset();
		for (int index = 0 ; index < schedule_list_shortcutname.size() ; index ++) {
			String schedule_shortcutname = schedule_list_shortcutname.get(index);
			// Loading the schedule
			for (int scan_index = 0 ; scan_index < arrShortCutName.size() ; scan_index ++) {
				String prefixName = arrPrefixName.get(scan_index);
				String dataSet = arrDataSet.get(scan_index);
				String shortCutName = arrShortCutName.get(scan_index);
				if (shortCutName.toLowerCase().equals(schedule_shortcutname.toLowerCase())) {
					// Check if need to load the test case
					update_testcase_set (prefixName, com.ttv.at.test.testsetting.get_default_script_folder());
					com.ttv.at.test.testcaseset current_tc_set = com.ttv.at.test.testcaseset.get_instance(prefixName);

					if (update_testcase_set_warning_message != null && update_testcase_set_warning_message.length() > 0) {
						append_load_schedule_message (update_testcase_set_warning_message);
						return null;
					}

					// Load Data Set
					String ts_full_path = com.ttv.at.test.testsetting.get_default_script_folder() + com.ttv.at.util.os.os_file_separator + prefixName + "test_suite.xls";
					File ts_file = new File(ts_full_path);
					if (!ts_file.exists()) {
						ts_full_path = com.ttv.at.test.testsetting.get_default_script_folder() + com.ttv.at.util.os.os_file_separator + prefixName + "test_suite.xlsx";
						if (!ts_file.exists()){
							append_load_schedule_message ("Test suite file '"+ts_full_path+"' is not exists for prefix " + prefixName);
							return null;
						}
					}
					com.ttv.at.test.testsuite_dataset load_data_set = reload_testsuite_load_setting (dataSet, ts_full_path);

					if (load_data_set == null){
						append_load_schedule_message ("Unable to load data set '"+dataSet+"' of test suite " + ts_full_path);
						return null;
					}
					
					// Load test suite
					com.ttv.at.test.testsuite load_test_suite = load_test_suite (ts_full_path, current_tc_set, load_data_set, null, schedule_shortcutname);
					load_test_suite.set_path_testsuite_file(ts_full_path);
					if (load_test_suite_warning_message != null && load_test_suite_warning_message.length() > 0) {
						append_load_schedule_message("Error in loading testsuite " + load_test_suite_warning_message);
						break;
					}
					
					load_tetsuiteset.append_schedule(load_test_suite, schedule_list_schedule.get(index));
					break;
				}
			}
			
			mainProgress.setValue(index + 1);
		}
		
		
		return load_tetsuiteset;
	}

	/******************************************************************************************************************/
	/************************************* RELOAD TEST SUITE **********************************************************/
	static private String reload_testsuite_warning_message;
	static public String get_reload_testsuite_warning_message() { return reload_testsuite_warning_message; }
	static private void append_reload_testsuite_warning_message(String message) {
		if (reload_testsuite_warning_message != null && reload_testsuite_warning_message.length() > 0)
			reload_testsuite_warning_message += "\n";
		else
			reload_testsuite_warning_message = "";
		reload_testsuite_warning_message += message;
	}
	static public void reload_testsuite (com.ttv.at.test.testsuite reload_testsuite, JProgressBar mainProgress, String tc_set_name) {
		mainProgress.setMaximum(5);
		reload_testsuite_warning_message = "";
		if (reload_testsuite != null && 
				reload_testsuite.get_dataset() != null &&
				reload_testsuite.get_dataset().get_name() != null &&
				reload_testsuite.get_tc_set() != null &&
				reload_testsuite.get_path_testsuite_file() != null) {
			
			// Reload setting
			com.ttv.at.test.testsuite_dataset reload_data_set = reload_testsuite_load_setting (reload_testsuite.get_dataset().get_name(), reload_testsuite.get_path_testsuite_file());
			
			if (reload_data_set != null) {
				// Apply new data set
				reload_testsuite.set_dataset (reload_data_set);

				if (mainProgress != null)
					mainProgress.setValue(mainProgress.getValue() + 1);
				
				// APPLY SETTING for data_set
				reload_data_set.apply_env();
				
				if (mainProgress != null)
					mainProgress.setValue(mainProgress.getValue() + 1);
				
				// Re-map tests
				if (reload_testsuite.get_testareas() != null && reload_testsuite.get_testareas().size() > 0)
					for (com.ttv.at.test.testarea scan_Area : reload_testsuite.get_testareas())
						if (scan_Area.get_tests() != null && scan_Area.get_tests().size() > 0)
							for (com.ttv.at.test.test scan_test : scan_Area.get_tests()) {

								if (scan_test.get_tc_instance() != null) {
									com.ttv.at.test.testcase found_new_tc = null;
									for (com.ttv.at.test.testcase new_testcase: com.ttv.at.test.testcaseset.get_instance(tc_set_name).get_testcaselist())
										if (scan_test.get_tc_instance().get_name().equals(new_testcase.get_name())){
											found_new_tc = new_testcase;
											break;
										}
									if (found_new_tc != null)
										scan_test.set_tc_instance(found_new_tc);
									else
										append_reload_testsuite_warning_message ("Test case " + scan_test.get_tc_instance().get_name() + " is not found.");
								}
							}

				mainProgress.setValue(5);
			}
		}
	}
	static com.ttv.at.test.testsuite_dataset reload_testsuite_load_setting (String DataSet_name, String testsuite_file_fullpath) {

		if (!appPOI.OpenFile(testsuite_file_fullpath)) {
			append_reload_testsuite_warning_message("Error in Opening File " + testsuite_file_fullpath);
			return null;
		}

		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_reload_testsuite_warning_message("There is no sheet in file " + testsuite_file_fullpath);
			return null;
		}
		
		// Scan all sheet to get test cases
		sheetPOI conf_sheet = null;
		int COL_NAME_INDEX = -1;
		int COL_DESCRIPTION_INDEX = -1;
		int COL_VALUE_INDEX = -1;
		boolean bFoundConfsheet = false;
		boolean bFoundConfCol = false;
		

		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			String sheet_name = current_sheet.get_Name();
			if (sheet_name.toLowerCase().equals("test configuration")){
				bFoundConfsheet = true;
				conf_sheet = current_sheet;
				int blank_count = 0;
				for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
					String col_name = current_sheet.getCellText(iColIndex, start_row);
					if ((col_name != null) && (col_name.length() > 0)) {
						col_name = col_name.toUpperCase();
						if (col_name.equals("NAME"))
							COL_NAME_INDEX = iColIndex;
						else if (col_name.equals("DESCRIPTION"))
							COL_DESCRIPTION_INDEX	= iColIndex;
						else if (col_name != null && col_name.length() > 0 &&
								(!col_name.equals("COMMENT")) && 
								(!col_name.equals("NO")) &&
								col_name.toLowerCase().equals(DataSet_name.toLowerCase())) {
							bFoundConfCol = true;
							COL_VALUE_INDEX = iColIndex;
							break;
						}
					}
					else
						blank_count ++;
					
					if (blank_count > 1)
						break;
				}
				break;
			}
		}
		

		if (!bFoundConfsheet)
			append_reload_testsuite_warning_message("Unable to find sheet \"Test Configuration \" in file " + testsuite_file_fullpath);
		else if (!bFoundConfCol)
			append_reload_testsuite_warning_message("Unable to find dataset \""+DataSet_name+"\" of sheet \"Test Configuration \" in file " + testsuite_file_fullpath);
		
		
		ArrayList<com.ttv.at.test.parameter> conf_values = new ArrayList<com.ttv.at.test.parameter>();
		int blank_count = 0;
		for (int iRowIndex = start_row + 1 ; iRowIndex < max_row ; iRowIndex ++) {
			// - get Name
			String value_name = conf_sheet.getCellText(COL_NAME_INDEX, iRowIndex);
			String value = conf_sheet.getCellText(COL_VALUE_INDEX, iRowIndex);
			String description = null;
			if (COL_DESCRIPTION_INDEX >= 0)
				description = conf_sheet.getCellText(COL_DESCRIPTION_INDEX, iRowIndex);
			if (value_name != null && value_name.length() > 0 &&
					value != null && value.length() > 0) {
				// Add value
				if (description != null && description.length() > 0)
					conf_values.add(new com.ttv.at.test.parameter(value_name, value, description));
				else
					conf_values.add(new com.ttv.at.test.parameter(value_name, value));
				blank_count = 0;
			} else {
				if (blank_count > 10)
					break;
				blank_count ++;
			}
		}
		
		// Return the new dataset
		return new com.ttv.at.test.testsuite_dataset(DataSet_name, conf_values);
	}
	
	static private String reload_testcaseset_warning_message;
	static public String get_reload_testcaseset_warning_message() { return reload_testcaseset_warning_message; }
	static private void append_reload_testcaseset_warning_message(String message) {
		if (reload_testcaseset_warning_message != null && reload_testcaseset_warning_message.length() > 0)
			reload_testcaseset_warning_message += "\n";
		else
			reload_testcaseset_warning_message = "";
		reload_testcaseset_warning_message += message;
	}
	static public boolean reload_testcaseset (String tc_set_name) {
		com.ttv.at.test.testcaseset.remove_instance(tc_set_name);
		
		// Reload test case
		if (update_testcase_set (tc_set_name, com.ttv.at.test.testsetting.get_default_script_folder()))
			return true;
		else {
			append_reload_testcaseset_warning_message ("Error on reload test case set : " + update_testcase_set_warning_message);
			return false;
		}
		
	}
	
	/******************************************************************************************************************/
	/************************************* LOAD TEST SUITE ************************************************************/

	static private String load_testsuites_warning_message;
	static public String get_load_testsuites_warning_message() { return load_testsuites_warning_message; }
	static private void append_load_testsuites_warning_message(String message) {
		if (load_testsuites_warning_message != null && load_testsuites_warning_message.length() > 0)
			load_testsuites_warning_message += "\n";
		else
			load_testsuites_warning_message = "";
		load_testsuites_warning_message += message;
	}
	static public com.ttv.at.test.testsuiteset load_testsuites (String testsuite_file, String testcase_prefix, JProgressBar mainProgress) {
		load_testsuites_warning_message = "";
		if (mainProgress != null) {
			mainProgress.setMaximum(5);
			mainProgress.setValue(0);
		}
		
		// Load test case
		update_testcase_set (testcase_prefix, com.ttv.at.test.testsetting.get_default_script_folder());
		com.ttv.at.test.testcaseset current_set = com.ttv.at.test.testcaseset.get_instance(testcase_prefix);
		
		if (update_testcase_set_warning_message != null && update_testcase_set_warning_message.length() > 0) {
			append_load_testsuites_warning_message (update_testcase_set_warning_message);
			return null;
		}
		
		if (current_set == null) {
			append_load_testsuites_warning_message ("Test case set is not found after loaded");
		}
		
		// SCAN IF TESTSUITE FILE HAS MULTI DATA SET
		ArrayList<com.ttv.at.test.testsuite_dataset> data_sets = load_testsuites_load_all_Setting (testsuite_file);
		
		if (data_sets != null && data_sets.size() > 0) {
			if (mainProgress != null) {
				mainProgress.setMaximum(data_sets.size() * 4 + 1);
				mainProgress.setValue(1);
			}

			
			// Scan check that all new test cases
			for (com.ttv.at.test.testcase cur_test : current_set.get_testcaselist()) {
				if (cur_test.get_test_call_test_depth(0) >= com.ttv.at.test.testcase.max_test_call_test_depth)
					append_load_testsuites_warning_message("Error in loading testcase: " + "test case '" + cur_test.get_name() + "' has loop forever.\n");
			}
			
			ArrayList<com.ttv.at.test.testsuite> testsuites = new ArrayList<com.ttv.at.test.testsuite>();
			// LOAD TEST SUITE FOR EACH DATA SET
			for (int i = 0 ; i < data_sets.size() ; i ++) {
				com.ttv.at.test.testsuite_dataset data_set = data_sets.get(i);
				
				if (mainProgress != null)
					mainProgress.setValue(mainProgress.getValue() + 1);
				
				// load test suite selection
				com.ttv.at.test.testsuite loading_test_suite = null;
				
				// APPLY SETTING for data_set
				data_set.apply_env();
				

				// Check the compatible of action lib with the dataset
				for (com.ttv.at.test.testlibrary scan_lib : current_set.get_testlibrarylist()) {
					com.ttv.at.test.result check_result = scan_lib.check_compatible ();
					if (check_result.get_result() != com.ttv.at.test.status_run.PASSED)
						append_load_testsuites_warning_message("Lib Error" + scan_lib.get_name() + " - Error in loading testsuite " + check_result.get_message());
				}
				
				if (i == 0)
					loading_test_suite = load_test_suite (testsuite_file, current_set, data_set, "RUN", null);
				else
					loading_test_suite = load_test_suite (testsuite_file, current_set, data_set, null, null);
				loading_test_suite.set_path_testsuite_file(testsuite_file);

				if (load_test_suite_warning_message != null && load_test_suite_warning_message.length() > 0) {
					append_load_testsuites_warning_message("Error in loading testsuite " + load_test_suite_warning_message);
					break;
				}
				
				if (mainProgress != null)
					mainProgress.setValue(mainProgress.getValue() + 1);
				
				// add tessuite to arraylist
				testsuites.add(loading_test_suite);
			}
			
			if (mainProgress != null)
				mainProgress.setValue(mainProgress.getMaximum());
			return new com.ttv.at.test.testsuiteset(testsuites);
		}
		else {
			append_load_testsuites_warning_message("Check if there is no data set");
			return null;
		}
	}
	static private ArrayList<com.ttv.at.test.testsuite_dataset> load_testsuites_load_all_Setting (String testsuite_file_fullpath) {

		if (!appPOI.OpenFile(testsuite_file_fullpath)) {
			append_load_testsuites_warning_message("Error in Opening File " + testsuite_file_fullpath);
			return null;
		}

		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_load_testsuites_warning_message("There is no sheet in file " + testsuite_file_fullpath);
			return null;
		}

		// Scan all sheet to get test cases
		int COL_NAME_INDEX = -1;
		int COL_DESCRIPTION_INDEX = -1;
		ArrayList<Integer> VALUE_COLS = new ArrayList<Integer>();
		ArrayList<String> NAME_COLS = new ArrayList<String>();
		sheetPOI conf_sheet = null;
		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			String sheet_name = current_sheet.get_Name();
			if (sheet_name.toLowerCase().equals("test configuration")){
				conf_sheet = current_sheet;
				int not_match_count = 0;
				for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
					String col_name = current_sheet.getCellText(iColIndex, start_row);
					if ((col_name != null) && (col_name.length() > 0)) {
						if (col_name.toUpperCase().equals("NAME"))
							COL_NAME_INDEX = iColIndex;
						else if (col_name.toUpperCase().equals("DESCRIPTION"))
							COL_DESCRIPTION_INDEX	= iColIndex;
						else if (col_name != null && col_name.length() > 0 && 
								(!col_name.toUpperCase().equals("NO")) && (!col_name.toUpperCase().equals("COMMENT"))) {
							VALUE_COLS.add(iColIndex);
							NAME_COLS.add(col_name);
						}
						else
							not_match_count ++;
					}
					else
						not_match_count ++;
					
					if (not_match_count > 1)
						break;
				}
				break;
			}
		}
		
		if (COL_NAME_INDEX >= 0 && VALUE_COLS.size() > 0) {
			ArrayList<com.ttv.at.test.testsuite_dataset> testsuite_dataset_res = new ArrayList<com.ttv.at.test.testsuite_dataset>();
			for (int i = 0 ; i < VALUE_COLS.size() ; i ++) {
				Integer COL_VALUE_INDEX = VALUE_COLS.get(i);
				ArrayList<com.ttv.at.test.parameter> conf_values = new ArrayList<com.ttv.at.test.parameter>();
				
				int blank_count = 0;
				for (int iRowIndex = start_row + 1 ; iRowIndex < max_row ; iRowIndex ++) {
					// - get Name
					String value_name = conf_sheet.getCellText(COL_NAME_INDEX, iRowIndex);
					String value = conf_sheet.getCellText(COL_VALUE_INDEX, iRowIndex);
					String description = null;
					if (COL_DESCRIPTION_INDEX >= 0)
						description = conf_sheet.getCellText(COL_DESCRIPTION_INDEX, iRowIndex);
					if (value_name != null && value_name.length() > 0 &&
							value != null && value.length() > 0) {
						// Add value
						if (description != null && description.length() > 0)
							conf_values.add(new com.ttv.at.test.parameter(value_name, value, description));
						else
							conf_values.add(new com.ttv.at.test.parameter(value_name, value));
						blank_count = 0;
					} else {
						if (blank_count > 10)
							break;
						blank_count ++;
					}
				}
				
				testsuite_dataset_res.add(new com.ttv.at.test.testsuite_dataset(NAME_COLS.get(i), conf_values));
			}
		
			return testsuite_dataset_res;
		}
		return null;
	}

	static private String load_test_suite_warning_message;
	static public String get_load_test_suite_warning_message() { return load_test_suite_warning_message; }
	static private void append_load_test_suite_warning_message(String message) {
		if (load_test_suite_warning_message != null && load_test_suite_warning_message.length() > 0)
			load_test_suite_warning_message += "\n";
		else
			load_test_suite_warning_message = "";
		load_test_suite_warning_message += message;
	}
	static public com.ttv.at.test.testsuite load_test_suite (String test_suite_excel_file_full_path, com.ttv.at.test.testcaseset tc_set, com.ttv.at.test.testsuite_dataset data_set, String optional_selection, String init_suite_name) {
		load_test_suite_warning_message = "";
		// Open file
		if (!appPOI.OpenFile(test_suite_excel_file_full_path)) {
			append_load_test_suite_warning_message("Error in Opening File " + test_suite_excel_file_full_path);
			return null;
		}
		
		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_load_test_suite_warning_message("There is no sheet in file " + test_suite_excel_file_full_path);
			return null;
		}
		
		// Scan all sheet to get test cases 
		ArrayList<com.ttv.at.test.testcase> testcases = tc_set.get_testcaselist();
		ArrayList<com.ttv.at.test.testarea> loaded_testareas = new ArrayList<com.ttv.at.test.testarea>();
		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			String sheet_name = current_sheet.get_Name();
			if (!sheet_name.toLowerCase().equals("test configuration"))
			{
				int	COL_NAME_INDEX = -1,
					COL_RUN_INDEX = -1,
					COL_SUITE_SELECTION_INDEX = -1,
					COL_DESCRIPTION_INDEX = -1,
					COL_KNOWNISSUE_INDEX = -1,
					COL_SEVERITY_INDEX = -1,
					COL_INPUT1_INDEX = -1;
				
				// search column index in sheet before adding
				for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
					String col_name = current_sheet.getCellText(iColIndex, start_row);
					if ((col_name != null) && (col_name.length() > 0)) {
						col_name = col_name.toUpperCase();
						if (col_name.equals("NAME"))
							COL_NAME_INDEX	= iColIndex;
						else if (optional_selection != null && optional_selection.length() > 0 && col_name.equals(optional_selection.toUpperCase()))
							COL_RUN_INDEX	= iColIndex;
						else if (col_name.equals(data_set.get_name().toUpperCase()))
							COL_SUITE_SELECTION_INDEX	= iColIndex;
						else if (col_name.equals("DESCRIPTION"))
							COL_DESCRIPTION_INDEX	= iColIndex;
						else if (col_name.equals("SEVERITY"))
							COL_SEVERITY_INDEX	= iColIndex;
						else if (col_name.equals("KNOWN ISSUE"))
							COL_KNOWNISSUE_INDEX= iColIndex;
						else if (col_name.equals("INPUT1"))
							COL_INPUT1_INDEX	= iColIndex;
					}
					else
						break;
				}
				
				if (COL_NAME_INDEX == -1)
					append_load_test_suite_warning_message("Column Name is not found");
				if (COL_DESCRIPTION_INDEX == -1)
					append_load_test_suite_warning_message("Column Description is not found");
				if (COL_INPUT1_INDEX == -1)
					append_load_test_suite_warning_message("Column Input Type is not found");
				
				if (	COL_NAME_INDEX > -1 &&
						COL_DESCRIPTION_INDEX > -1 &&
						COL_INPUT1_INDEX > -1) {
					// Start loading test
					// Start process to add test cases
					ArrayList<com.ttv.at.test.test> testarea_tests = new ArrayList<com.ttv.at.test.test>();
					int blank_count = 0;
					for (int iRowIndex = start_row + 1 ; iRowIndex < max_row ; iRowIndex ++) {
						// - get Name
						String test_name = current_sheet.getCellText(COL_NAME_INDEX, iRowIndex);
						String known_issue = null;
						if (COL_KNOWNISSUE_INDEX > 0)
							known_issue = current_sheet.getCellText(COL_KNOWNISSUE_INDEX, iRowIndex);
						if (test_name != null && test_name.length() > 0) {
							blank_count = 0;
							test_name = test_name.toLowerCase();
							
							// checking name
							com.ttv.at.test.testcase tc_found = null;
							for (int t_index = 0 ; t_index < testcases.size() ; t_index ++)
								if (testcases.get(t_index).get_name().equals(test_name)) {
									tc_found = testcases.get(t_index);
									break;
								}
							
							if (tc_found == null) {
								appPOI.get_instance().Close();
								// appPOI.get_instance().Kill();
								append_load_test_suite_warning_message("Error in sheet " + current_sheet.get_Name() + " , test " + 
										test_name + " at row " + iRowIndex + " is not found in test case list");
							}
							else
							{
								// - get Run
								String sRun = "";
								com.ttv.at.test.status_selection run = com.ttv.at.test.status_selection.NOT_SELECTED;
								if (COL_RUN_INDEX >= 0) {
									sRun = current_sheet.getCellText(COL_RUN_INDEX, iRowIndex);
									if (sRun!=null && sRun.length() > 0)
										if (sRun.toLowerCase().equals("yes"))
											run = com.ttv.at.test.status_selection.SELECTED;
										else if (sRun.toLowerCase().equals("init"))
											run = com.ttv.at.test.status_selection.INIT;
										else if (sRun.toLowerCase().equals("init_1_time"))
											run = com.ttv.at.test.status_selection.INIT_1_TIME;
								}
								else if (COL_SUITE_SELECTION_INDEX >= 0) {
									sRun = current_sheet.getCellText(COL_SUITE_SELECTION_INDEX, iRowIndex);
									if (sRun!=null && sRun.length() > 0)
										if (sRun.toLowerCase().equals("yes"))
											run = com.ttv.at.test.status_selection.SELECTED;
										else if (sRun.toLowerCase().equals("init"))
											run = com.ttv.at.test.status_selection.INIT;
										else if (sRun.toLowerCase().equals("init_1_time"))
											run = com.ttv.at.test.status_selection.INIT_1_TIME;
								}
								
								// - get Description
								String sDescription = current_sheet.getCellText(COL_DESCRIPTION_INDEX, iRowIndex);
								
								// - get Severity
								com.ttv.at.test.test.severity valSeverity = com.ttv.at.test.test.severity.Medium;
								String strSeverity = null;
								if (COL_SEVERITY_INDEX >= 0)
									strSeverity = current_sheet.getCellText(COL_SEVERITY_INDEX, iRowIndex);
								if (strSeverity != null && strSeverity.length() > 0) {
									strSeverity = strSeverity.toLowerCase();
									if (strSeverity.equals("critical"))
										valSeverity = com.ttv.at.test.test.severity.Critical;
									else if (strSeverity.equals("high"))
										valSeverity = com.ttv.at.test.test.severity.High;
									else if (strSeverity.equals("medium"))
										valSeverity = com.ttv.at.test.test.severity.Medium;
									else if (strSeverity.equals("low"))
										valSeverity = com.ttv.at.test.test.severity.Low;
								}
								
								// - get input
								ArrayList<String> inputs = new ArrayList<String>();
								for (int iColInput = COL_INPUT1_INDEX ; iColInput < max_col; iColInput ++) {
									String input = current_sheet.getCellText(iColInput, iRowIndex);
									if (input != null && input.length() > 0) {
										if (input.equals("^NULL"))
											inputs.add(null);
										else if (input.equals("^BLANK"))
											inputs.add("");
										else
											inputs.add(input);
									}
									else
										break;
								}
								
								// - check if inputs is provide enough for test case
								if (inputs.size() < tc_found.get_inputs().size()) {
									appPOI.get_instance().Close();
									// appPOI.get_instance().Kill();
									append_load_test_suite_warning_message("Error in sheet " + current_sheet.get_Name() + " , test " + 
											test_name + " at row " + iRowIndex + " is not provide enough inputs, test case needs " + tc_found.get_inputs().size() + " inputs");
								}
								
								// Check dataset inputs
								if (!tc_found.check_dataset_inputs (data_set))
									append_load_test_suite_warning_message("Error of dataset '" + data_set.get_name() + "' for " + tc_found.get_check_dataset_inputs_msg());
								
								testarea_tests.add(new com.ttv.at.test.test(tc_found, inputs, run, sDescription, valSeverity, known_issue));
							}
						}
						else {
							if (test_name == null)
								break;
							if (blank_count > 5)
								break;
							blank_count ++;
						}
					}
					loaded_testareas.add(new com.ttv.at.test.testarea(current_sheet.get_Name(), testarea_tests));
				}
			}
		}
		appPOI.get_instance().Close();
		// appPOI.get_instance().Kill();
		String suite_name = init_suite_name;
		int script_pos_index = test_suite_excel_file_full_path.toLowerCase().lastIndexOf("script") + 7;
		if (suite_name == null || suite_name.length() == 0)
			suite_name = test_suite_excel_file_full_path.substring(script_pos_index, test_suite_excel_file_full_path.lastIndexOf('.')) + "_" + data_set.get_name();
		return new com.ttv.at.test.testsuite(suite_name, loaded_testareas, data_set, tc_set);
	}
	
	// Update testcase_set
	static private String update_testcase_set_warning_message;
	static public String get_update_testcase_set_warning_message() { return update_testcase_set_warning_message; }
	static private void append_update_testcase_set_warning_message(String message) {
		if (update_testcase_set_warning_message != null && update_testcase_set_warning_message.length() > 0)
			update_testcase_set_warning_message += "\n";
		else
			update_testcase_set_warning_message = "";
		update_testcase_set_warning_message += message;
	}
	static boolean update_testcase_set (String prefix, String ScriptFolder) {
		update_testcase_set_warning_message = "";
		if (!com.ttv.at.test.testcaseset.check_instance_existance(prefix)) {
			// Check exists of all file
			String testcase_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "test_cases.xlsx";
			String testlib_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "test_libraries.xlsx";
			String guiobj_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "gui_objects.xlsx";
			File testcase_file = new File(testcase_file_fullpath);
			File testlib_file = new File(testlib_file_fullpath);
			File guiobj_file = new File(guiobj_file_fullpath);
			

			if (!testcase_file.exists() || !testlib_file.exists() || !guiobj_file.exists()) {
				// Try other solution
				testcase_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "test_cases.xls";
				testlib_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "test_libraries.xls";
				guiobj_file_fullpath = ScriptFolder + com.ttv.at.util.os.os_file_separator + prefix + "gui_objects.xls";
				testcase_file = new File(testcase_file_fullpath);
				testlib_file = new File(testlib_file_fullpath);
				guiobj_file = new File(guiobj_file_fullpath);

				if (!testcase_file.exists() || !testlib_file.exists() || !guiobj_file.exists()) {
					if (!testcase_file.exists())
						append_update_testcase_set_warning_message ("File " + testcase_file_fullpath + " is not exists\n");
					if (!testlib_file.exists())
						append_update_testcase_set_warning_message ("File " + testlib_file_fullpath + " is not exists\n");
					if (!guiobj_file.exists())
						append_update_testcase_set_warning_message ("File " + guiobj_file_fullpath + " is not exists\n");
				}
			}
			
			// Start loading test cases
			if (update_testcase_set_warning_message == null || update_testcase_set_warning_message.length() == 0) {
				// Load GUI Object
				ArrayList<com.ttv.at.test.testobject> gui_objects = load_test_gui_objects (guiobj_file_fullpath);
				if (load_test_gui_objects_warning_message != null && load_test_gui_objects_warning_message.length() > 0) {
					append_update_testcase_set_warning_message (load_test_gui_objects_warning_message);
				}
				else {
					// Load Library
					ArrayList<com.ttv.at.test.testlibrary> test_libraries = load_test_libraries (testlib_file_fullpath, gui_objects);
					if (load_test_libraries_warning_message != null && load_test_libraries_warning_message.length() > 0) {
						append_update_testcase_set_warning_message (load_test_libraries_warning_message);
					}
					else {
						// Load Test case
						ArrayList<com.ttv.at.test.testcase> testcases = load_test_cases (testcase_file_fullpath, test_libraries);
						
						if (load_test_cases_warning_message != null && load_test_cases_warning_message.length() > 0) {
							append_update_testcase_set_warning_message (load_test_cases_warning_message);
							return false;
						}
						com.ttv.at.test.testcaseset.add_intsance(new com.ttv.at.test.testcaseset(prefix, testcases, test_libraries));
						return true;
					}
				}
			}
		}
		else
			return true;
		return false;
	}
	
	static private String load_test_cases_warning_message;
	static public String get_load_test_cases_warning_message() { return load_test_cases_warning_message; }
	static private void append_load_test_cases_warning_message(String message) {
		if (load_test_cases_warning_message != null && load_test_cases_warning_message.length() > 0)
			load_test_cases_warning_message += "\n";
		else
			load_test_cases_warning_message = "";
		load_test_cases_warning_message += message;
	}
	static public ArrayList<com.ttv.at.test.testcase> load_test_cases (String test_case_excel_file_full_path, ArrayList<com.ttv.at.test.testlibrary> test_libraries) {
		load_test_cases_warning_message = "";
		// Open file
		if (!appPOI.OpenFile(test_case_excel_file_full_path)) {
			append_load_test_cases_warning_message("Error in Opening File " + test_case_excel_file_full_path);
			return null;
		}
		
		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_load_test_cases_warning_message("There is no sheet in file " + test_case_excel_file_full_path);
			return null;
		}
		
		// Scan all sheet to get test cases 
		ArrayList<com.ttv.at.test.testcase> loaded_testcases = new ArrayList<com.ttv.at.test.testcase>();
		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			// String sheet_name = current_sheet.get_Name();
			int	COL_NAME_INDEX = -1,
				COL_LIBRARY_INDEX = -1,
				COL_RUNTYPE_INDEX = -1,
				COL_RETURN_INDEX = -1,
				COL_PARAM1_INDEX = -1,
				COL_LOOP_INDEX = -1,
				COL_DESCRIPTION_INDEX = -1;
			
			// search column index in sheet before adding
			for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
				String col_name = current_sheet.getCellText(iColIndex, start_row);
				if ((col_name != null) && (col_name.length() > 0)) {
					col_name = col_name.toUpperCase();
					if (col_name.equals("NAME"))
						COL_NAME_INDEX	= iColIndex;
					else if (col_name.equals("TEST LIBRARY"))
						COL_LIBRARY_INDEX	= iColIndex;
					else if (col_name.equals("RUN TYPE"))
						COL_RUNTYPE_INDEX	= iColIndex;
					else if (col_name.equals("RETURN"))
						COL_RETURN_INDEX	= iColIndex;
					else if (col_name.equals("PARAM1"))
						COL_PARAM1_INDEX	= iColIndex;
					else if (col_name.equals("LOOP"))
						COL_LOOP_INDEX	= iColIndex;
					else if (col_name.equals("DESCRIPTION"))
						COL_DESCRIPTION_INDEX	= iColIndex;
					
					if (	COL_NAME_INDEX > -1 &&
							COL_LIBRARY_INDEX > -1 &&
							COL_PARAM1_INDEX > -1)
						break;
				}
				else
					break;
			}
			
			if (COL_NAME_INDEX == -1)
				append_load_test_cases_warning_message("Column Name is not found");
			if (COL_LIBRARY_INDEX == -1)
				append_load_test_cases_warning_message("Column Test Library is not found");
			if (COL_PARAM1_INDEX == -1)
				append_load_test_cases_warning_message("Column Param1 is not found");
			if (COL_DESCRIPTION_INDEX == -1)
				append_load_test_cases_warning_message("Column Description is not found");
			
			if (	COL_NAME_INDEX > -1 &&
					COL_LIBRARY_INDEX > -1 &&
					COL_DESCRIPTION_INDEX > -1 &&
					COL_PARAM1_INDEX > -1) {
				// Start process to add test cases
				int blank_count = 0;
				for (int iRowIndex = start_row + 1 ; iRowIndex < max_row ; iRowIndex ++) {
					// Get Action
					String sLibrary = current_sheet.getCellText(COL_LIBRARY_INDEX, iRowIndex);
					if (sLibrary != null && sLibrary.length() > 0) {
						blank_count = 0;
						sLibrary = sLibrary.toLowerCase();
						
						// Check the startLib
						if (sLibrary.equals("inputs"))
						{
							// Get test from sheet
							com.ttv.at.test.testcase tcase = load_test_cases_get_test_from_sheet(
									current_sheet, 
									iRowIndex,
									COL_NAME_INDEX, 
									COL_LIBRARY_INDEX, 
									COL_RUNTYPE_INDEX, 
									COL_RETURN_INDEX, 
									COL_PARAM1_INDEX,
									COL_LOOP_INDEX,
									COL_DESCRIPTION_INDEX,
									test_libraries);
							
							if (tcase == null) {
								append_load_test_cases_warning_message("Error in add test " + load_test_cases_get_test_from_sheet_warning_message);
							}
							else {
								// check if test name is in list before adding to list
								boolean tc_found = false;
								for (int i_test_index = 0 ; i_test_index < loaded_testcases.size() ; i_test_index ++ )
									if (loaded_testcases.get(i_test_index).get_name().length() == tcase.get_name().length() && 
										loaded_testcases.get(i_test_index).get_name().equals(tcase.get_name())) {
										tc_found = true;
										break;
									}
								
								if (tc_found)
									append_load_test_cases_warning_message("Test case '" + tcase.get_name() + "' is duplicated ");
								else
									loaded_testcases.add(tcase);
							}
						}
					}
					else {
						if (sLibrary == null)
							break;
						if (blank_count > 5)
							break;
						blank_count ++;
					}
						
				}
				
			}
		}
		appPOI.get_instance().Close();
		// appPOI.get_instance().Kill();
		
		// Load instance for the call test element
		for (int i = 0 ; i < loaded_testcases.size() ; i ++) {
			com.ttv.at.test.testcase cur_testcase = loaded_testcases.get(i);
			for (int e_index = 0 ; e_index < cur_testcase.get_elements().size() ; e_index ++ ) {
				com.ttv.at.test.testelement cur_element = cur_testcase.get_elements().get(e_index);
				if (cur_element.get_instance_name().startsWith("call ")) {
					com.ttv.at.test.result check_res = cur_element.set_tc_instance(loaded_testcases);
					if (check_res.get_result() != com.ttv.at.test.status_run.PASSED) {
						append_load_test_cases_warning_message("\n Test case " + cur_testcase.get_name() + " error: " + check_res.get_message());
					}
				}
				else if (cur_element.get_instance_name().equals("callotherset")) {
					if (cur_element.get_inputs() != null && cur_element.get_inputs().size() >= 2) {
						// input 1 = set name (prefix)
						// input 2 = test case name
						String set_name = cur_element.get_inputs().get(0).get_value();
						String tc_name = cur_element.get_inputs().get(1).get_value().toLowerCase();
						cur_element.get_inputs().remove(1);
						cur_element.get_inputs().remove(0);
						
						String old_load_test_cases_warning_message = load_test_cases_warning_message;
						update_testcase_set (set_name, com.ttv.at.test.testsetting.get_default_script_folder());

						// Restore the old loading message
						if (old_load_test_cases_warning_message != null && old_load_test_cases_warning_message.length() > 0)
							load_test_cases_warning_message = old_load_test_cases_warning_message;
						if (update_testcase_set_warning_message != null && update_testcase_set_warning_message.length() > 0) {
							append_load_test_cases_warning_message (update_testcase_set_warning_message);
						}
						
						com.ttv.at.test.testcaseset select_set = com.ttv.at.test.testcaseset.get_instance(set_name);
						if (select_set == null)
							append_load_test_cases_warning_message ("test case set '"+set_name+"' is not found after adding");
						else {
							com.ttv.at.test.result check_res = cur_element.set_tc_instance(select_set.get_testcaselist(), tc_name);
							if (check_res.get_result() != com.ttv.at.test.status_run.PASSED) {
								append_load_test_cases_warning_message("\n Test case " + cur_testcase.get_name() + " error: " + check_res.get_message());
							}
						}
					}
					else {
						append_load_test_cases_warning_message("\n Call Other Set not provide enought input in test case " + cur_testcase.get_name());
					}
				}
			}
		}
		
		// print out the list of unsed-library to the Console
		System.out.println("****** ****** ****** ****** ****** ******");
		System.out.println("****** LIST OF UN_USED LIB ******");
		for (int  i = 0 ; i < used_libs.length && i < test_libraries.size() ; i ++)
			if (!used_libs[i])
				System.out.println(test_libraries.get(i).get_name() + " is not used");
		
		return loaded_testcases;
	}
	
	static private String load_test_cases_get_test_from_sheet_warning_message;
	static public String get_load_test_cases_get_test_from_sheet_warning_message() { return load_test_cases_get_test_from_sheet_warning_message; }
	static private void append_load_test_cases_get_test_from_sheet_warning_message(String message) {
		if (load_test_cases_get_test_from_sheet_warning_message != null && load_test_cases_get_test_from_sheet_warning_message.length() > 0)
			load_test_cases_get_test_from_sheet_warning_message += "\n";
		else
			load_test_cases_get_test_from_sheet_warning_message = "";
		load_test_cases_get_test_from_sheet_warning_message += message;
	}
	static private com.ttv.at.test.testcase load_test_cases_get_test_from_sheet(sheetPOI current_sheet, 
			int current_row,
			int COL_NAME_INDEX, 
			int COL_LIBRARY_INDEX, 
			int COL_RUNTYPE_INDEX, 
			int COL_RETURN_INDEX, 
			int COL_PARAM1_INDEX,
			int COL_LOOP_INDEX,
			int COL_DESCRIPTION_INDEX,
			ArrayList<com.ttv.at.test.testlibrary> test_libraries) {
		load_test_cases_get_test_from_sheet_warning_message = "";
		if (current_sheet == null) {
			append_load_test_cases_get_test_from_sheet_warning_message("current_sheet is null");
			return null;
		}
		
		// Get test test case name
		String tc_name = current_sheet.getCellText(COL_NAME_INDEX, current_row);
		if (tc_name == null || tc_name.length() == 0) {
			append_load_test_cases_get_test_from_sheet_warning_message("Test case Name is not available to this test library (Sheet " + 
			current_sheet.get_Name() + ", row " + current_row + ")");
			return null;
		}
		
		// Declare element returns
		ArrayList<com.ttv.at.test.parameter> lib_returns = new ArrayList<com.ttv.at.test.parameter>();
		
		// Get test case inputs
		ArrayList<com.ttv.at.test.parameter> inputs = new ArrayList<com.ttv.at.test.parameter>();
		for (int iColIndex = COL_PARAM1_INDEX ; iColIndex < max_col ; iColIndex ++) {
			String input_name = current_sheet.getCellText(iColIndex, current_row);
			if (input_name != null && input_name.length() > 0) {
				input_name = input_name.toLowerCase();
				// Check if input name is exist
				boolean found_in_inputs = false;
				for (int i_p_index = 0 ; i_p_index < inputs.size() ; i_p_index ++)
					if (inputs.get(i_p_index).get_key().length() == input_name.length() && 
						inputs.get(i_p_index).get_key().equals(input_name)) {
						found_in_inputs = true;
						break;
					}
				if (found_in_inputs) {
					append_load_test_cases_get_test_from_sheet_warning_message("Test case " + tc_name + " (Sheet " + 
							current_sheet.get_Name() + ", row " + current_row + ") has input '" + input_name + "' is duplicated, please check");
					return null;
				}
				else
					inputs.add(new com.ttv.at.test.parameter(input_name));
			}
			else
				break;
		}
		
		// Get return if available
		ArrayList<com.ttv.at.test.parameter> tc_returns = null;
		ArrayList<String> tc_returns_names = null;
		boolean[] tc_returns_validated = null;
		
		String lib_returns_names_str = null;
		if (COL_RETURN_INDEX >= 0)
			lib_returns_names_str = current_sheet.getCellText(COL_RETURN_INDEX, current_row);
		if (lib_returns_names_str != null && lib_returns_names_str.length() > 0) {
			lib_returns_names_str = lib_returns_names_str.toLowerCase();
			String[] lib_returns_names_arr = lib_returns_names_str.split("\\$");
			tc_returns_names = new ArrayList<String>();
			for (int i = 0 ; i < lib_returns_names_arr.length ; i ++)
				if (lib_returns_names_arr[i] != null &&
						lib_returns_names_arr[i].length() > 0)
					tc_returns_names.add(lib_returns_names_arr[i]);
			for (int i = 0 ; i < tc_returns_names.size() ; i ++) {
				if (tc_returns == null)
					tc_returns = new ArrayList<com.ttv.at.test.parameter>();
					tc_returns.add(new com.ttv.at.test.parameter(tc_returns_names.get(i)));
			}
			if (tc_returns != null)
				tc_returns_validated = new boolean[tc_returns.size()];
			for (int i = 0 ; i < tc_returns_validated.length ; i ++)
				tc_returns_validated[i] = false;
		}
		
		// Adding element
		ArrayList<com.ttv.at.test.testelement> elements = new ArrayList<com.ttv.at.test.testelement>();
		ArrayList<com.ttv.at.test.testelement> clear_state_elements = new ArrayList<com.ttv.at.test.testelement>();
		int end_row = -1;
		String Description = "";
		for (int i = current_row + 1 ; i < max_row ; i ++) {
			// Get Element name
			String element_name = current_sheet.getCellText(COL_LIBRARY_INDEX, i);
			String newDescription = current_sheet.getCellText(COL_DESCRIPTION_INDEX, i);
			if (newDescription != null && newDescription.length() > 0 && (!newDescription.equals(Description)))
				Description = newDescription;
			if (element_name != null && element_name.length() > 0 && 
				(!element_name.toLowerCase().equals("inputs")) && (!element_name.toLowerCase().equals("end"))) {
				
				// Check if start loop
				String loop_name = null;
				if (COL_LOOP_INDEX >= 0) {
					String temp = current_sheet.getCellText(COL_LOOP_INDEX, i);
					if (temp != null && temp.length() >0)
						loop_name = temp;
				}
				
				// Adding a loop item
				if (loop_name != null && loop_name.length() >0) {
					int iRowCount = current_sheet.getMergeCells_RowCount(COL_LOOP_INDEX, i);
					ArrayList<com.ttv.at.test.testelement> loopelements = new ArrayList<com.ttv.at.test.testelement>();
					for (int row_index = i ; row_index < i + iRowCount ; row_index ++) {
						element_name = current_sheet.getCellText(COL_LIBRARY_INDEX, row_index);
						if (element_name != null && element_name.length() > 0 && 
								(!element_name.toLowerCase().equals("inputs")) && (!element_name.toLowerCase().equals("end")))
						{
							com.ttv.at.test.testelement new_element = load_test_cases_get_test_get_element_from_sheet(element_name, 
									tc_name, 
									inputs, 
									tc_returns, 
									lib_returns, 
									current_sheet, 
									row_index, 
									COL_RUNTYPE_INDEX, 
									COL_RETURN_INDEX, 
									COL_PARAM1_INDEX, 
									test_libraries);
							if (new_element == null) {
								append_load_test_cases_get_test_from_sheet_warning_message (get_load_test_cases_get_test_get_element_from_sheet_warning_message());
								return null;
							}

							new_element.set_description (Description);
							// Add element to list
							elements.add(new_element);
							loopelements.add(new_element);
							
							// Check the return of tc
							if (tc_returns != null && 
									new_element.get_returns() != null) {
								for (int tc_returns_index = 0 ; tc_returns_index < tc_returns.size() ; tc_returns_index ++) {
									if (!tc_returns_validated[tc_returns_index]) {
										String tc_return_name = tc_returns.get(tc_returns_index).get_key();
										for (int lib_return_index = 0 ; lib_return_index < new_element.get_returns().size() ; lib_return_index ++) {
											if (new_element.get_returns().get(lib_return_index).get_key().equals(tc_return_name)) {
												tc_returns_validated[tc_returns_index] = true;
												break;
											}
										}
									}
								}
							}
						}
						else
							break;
					}
					
					new com.ttv.at.test.testelementloopgroup(loop_name, loopelements);
					// Set index to continue
					i = i + iRowCount - 1;
				}
				
				// Adding normal element item
				else {
					com.ttv.at.test.testelement new_element = load_test_cases_get_test_get_element_from_sheet(element_name, 
							tc_name, 
							inputs, 
							tc_returns, 
							lib_returns,  
							current_sheet, 
							i, 
							COL_RUNTYPE_INDEX, 
							COL_RETURN_INDEX, 
							COL_PARAM1_INDEX, 
							test_libraries);
					if (new_element == null) {
						append_load_test_cases_get_test_from_sheet_warning_message (get_load_test_cases_get_test_get_element_from_sheet_warning_message());
						return null;
					}
					new_element.set_description(Description);
					
					// Add element to list
					if (new_element.get_type() == com.ttv.at.test.run_type.CLEAR_STATE)
						clear_state_elements.add(new_element);
					else
						elements.add(new_element);
					
					// Check the return of tc
					if (tc_returns != null && 
							new_element.get_returns() != null) {
						for (int tc_returns_index = 0 ; tc_returns_index < tc_returns.size() ; tc_returns_index ++) {
							if (!tc_returns_validated[tc_returns_index]) {
								String tc_return_name = tc_returns.get(tc_returns_index).get_key();
								for (int lib_return_index = 0 ; lib_return_index < new_element.get_returns().size() ; lib_return_index ++) {
									if (new_element.get_returns().get(lib_return_index).get_key().equals(tc_return_name)) {
										tc_returns_validated[tc_returns_index] = true;
										break;
									}
								}
							}
						}
					}
				}
			}
			else {
				end_row = i - 1;
				break;
			}
		}
		
		// Check the return
		boolean tc_returns_ok = false;
		if (tc_returns == null || tc_returns.size() == 0)
			tc_returns_ok = true;
		else {
			tc_returns_ok = true;
			for (int i = 0 ; i < tc_returns_validated.length ; i ++)
				if (!tc_returns_validated[i])
					tc_returns_ok = false;
		}
			
		if (!tc_returns_ok) {
			append_load_test_cases_get_test_from_sheet_warning_message("Test case " + tc_name + ", (Sheet " + 
					current_sheet.get_Name() + ", row " + current_row + ") meet error: return of test case is not return from any element.");
			return null;
		}
		
		// Create test case
		com.ttv.at.test.testcase res_tc = new com.ttv.at.test.testcase(current_sheet.get_Name().toLowerCase() + "." + tc_name.toLowerCase(), elements, clear_state_elements, inputs, tc_returns, lib_returns, current_row, end_row);
		return res_tc;
	}
	
	static private String load_test_cases_get_test_get_element_from_sheet_warning_message;
	static public String get_load_test_cases_get_test_get_element_from_sheet_warning_message() { return load_test_cases_get_test_get_element_from_sheet_warning_message; }
	static private void append_load_test_cases_get_test_get_element_from_sheet_warning_message(String message) {
		if (load_test_cases_get_test_get_element_from_sheet_warning_message != null && load_test_cases_get_test_get_element_from_sheet_warning_message.length() > 0)
			load_test_cases_get_test_get_element_from_sheet_warning_message += "\n";
		else
			load_test_cases_get_test_get_element_from_sheet_warning_message = "";
		load_test_cases_get_test_get_element_from_sheet_warning_message += message;
	}
	static private com.ttv.at.test.testelement load_test_cases_get_test_get_element_from_sheet (String element_name,
			String tc_name,
			ArrayList<com.ttv.at.test.parameter> inputs,
			ArrayList<com.ttv.at.test.parameter> tc_returns,
			ArrayList<com.ttv.at.test.parameter> lib_returns, 
			sheetPOI current_sheet, 
			int current_row,
			int COL_RUNTYPE_INDEX, 
			int COL_RETURN_INDEX, 
			int COL_PARAM1_INDEX,
			ArrayList<com.ttv.at.test.testlibrary> test_libraries) {
		load_test_cases_get_test_get_element_from_sheet_warning_message = "";
		
		// Get test library
		com.ttv.at.test.testlibrary testlibrary_found = null;
		element_name = element_name.toLowerCase();
		for (int element_index = 0 ; element_index < test_libraries.size() && element_index < used_libs.length ; element_index ++)
			if (test_libraries.get(element_index).get_name().length() == element_name.length() && 
				test_libraries.get(element_index).get_name().equals(element_name)) {
				testlibrary_found = test_libraries.get(element_index);
				used_libs[element_index] = true;
				break;
			}
		
		if (testlibrary_found == null && 
				(!element_name.startsWith("call ")) && 
						(!element_name.equals("callotherset"))
								){
			testlibrary_found = load_test_cases_get_test_get_core_element(element_name, 
					current_sheet, 
					current_row,
					COL_RUNTYPE_INDEX, 
					COL_RETURN_INDEX, 
					COL_PARAM1_INDEX,
					test_libraries);
			if (testlibrary_found == null) {
				append_load_test_cases_get_test_get_element_from_sheet_warning_message("Test case " + tc_name + "(Sheet " + 
						current_sheet.get_Name() + ", row " + current_row + ") , library '"+element_name+"' is not found, please check");
				return null;
			}
		}

		// Get input
		ArrayList<com.ttv.at.test.parameter> element_inputs = new ArrayList<com.ttv.at.test.parameter>();
		for (int i_input_col = COL_PARAM1_INDEX ; i_input_col < max_col ; i_input_col ++) {
			String element_input_name = current_sheet.getCellText(i_input_col, current_row);
			
			if (element_input_name != null && element_input_name.length() > 0) {

				if (element_input_name.startsWith("^^"))
					element_inputs.add(new com.ttv.at.test.parameter("", element_input_name.substring(2)));
				else if (element_input_name.equals("^NULL"))
					element_inputs.add(new com.ttv.at.test.parameter(""));
				else if (element_input_name.equals("^BLANK"))
					element_inputs.add(new com.ttv.at.test.parameter("", ""));
				else
				{
					element_input_name = element_input_name.toLowerCase();
					com.ttv.at.test.parameter input_found = null;
					// Check if the input is in the input of test case
					for (int i_input_index = 0 ; i_input_index < inputs.size() ; i_input_index ++)
						if (inputs.get(i_input_index).check_key(element_input_name)) {
							input_found = inputs.get(i_input_index);
							break;
						}

					// check in the return list of test case
					if (input_found == null && tc_returns != null) {
						for (int i_tc_return_index = 0 ; i_tc_return_index < tc_returns.size() ; i_tc_return_index ++)
							if (tc_returns.get(i_tc_return_index).check_key(element_input_name)) {
								input_found = tc_returns.get(i_tc_return_index);
								break;
							}
					}
					
					// check in the return list of previous elements
					if (input_found == null && lib_returns != null) {
						for (int i_lib_return_index = 0 ; i_lib_return_index < lib_returns.size() ; i_lib_return_index ++)
							if (lib_returns.get(i_lib_return_index).check_key(element_input_name)) {
								input_found = lib_returns.get(i_lib_return_index);
								break;
							}
					}

					// check in the common data list
					if (input_found == null && element_input_name.startsWith("$"))
						input_found = new com.ttv.at.test.parameter(element_input_name);
					
					if (input_found == null) {
						append_load_test_cases_get_test_get_element_from_sheet_warning_message("Test case " + tc_name + "(Sheet " + current_sheet.get_Name() + 
						", row " + current_row + ") , library '"+element_name+"' which input '" + element_input_name + "' not found in input/return list, please check");
						return null;
					}
					
					element_inputs.add(input_found);
				}
			}
			else
				break;
		}
		
		// Check if inputs provide enough for test library
		if (testlibrary_found != null && element_inputs.size() < testlibrary_found.get_inputs().size()) {
			append_load_test_cases_get_test_get_element_from_sheet_warning_message("Test case " + tc_name + "(Sheet " + current_sheet.get_Name() + ", row " + current_row + ") , library '"+element_name + 
				"' which inputs is not provide enough for test lib, please check");
			return null;
		}
		

		
		// Get return
		// Get return if available
		ArrayList<com.ttv.at.test.parameter> element_returns = null;
		ArrayList<String> element_returns_names = null;
		boolean[] element_returns_validated = null;
		
		String element_returns_names_str = null;
		if (COL_RETURN_INDEX >= 0)
			element_returns_names_str = current_sheet.getCellText(COL_RETURN_INDEX, current_row);
		if (element_returns_names_str != null && element_returns_names_str.length() > 0) {
			element_returns_names_str = element_returns_names_str.toLowerCase();
			String[] element_returns_names_arr = element_returns_names_str.split("\\$");
			element_returns_names = new ArrayList<String>();
			for (int i = 0 ; i < element_returns_names_arr.length ; i ++)
				if (element_returns_names_arr[i] != null &&
						element_returns_names_arr[i].length() > 0)
					element_returns_names.add(element_returns_names_arr[i]);
			element_returns = new ArrayList<com.ttv.at.test.parameter>();
			if (element_returns != null)
				element_returns_validated = new boolean[element_returns_names.size()];
			for (int i = 0 ; i < element_returns_validated.length ; i ++)
				element_returns_validated[i] = false;
		}
		
		if (element_returns_names != null && element_returns_names.size() > 0) {
			for (int lib_returns_index = 0 ; lib_returns_index < element_returns_names.size() ; lib_returns_index ++) {
				String element_return_name = element_returns_names.get(lib_returns_index);
				com.ttv.at.test.parameter element_found = null;
				// Check each return
				// - Check if return is in inputs of test case
				for (com.ttv.at.test.parameter scan_input:inputs)
					if ((!scan_input.is_readonly()) && scan_input.check_key(element_return_name)) {
						element_found = scan_input;
						element_returns_validated[lib_returns_index] = true;
						break;
					}
				
				// check in the return list of test case
				if (element_found == null && tc_returns != null) {
					for (com.ttv.at.test.parameter tc_return:tc_returns)
						if (tc_return.check_key(element_return_name)) {
							element_found = tc_return;
							break;
						}
				}
				
				// check in the return list of previous element
				if (element_found == null && lib_returns != null) {
					for (com.ttv.at.test.parameter lib_return:lib_returns)
						if (lib_return.check_key(element_return_name)) {
							element_found = lib_return;
							break;
						}
				}
				
				if (element_found == null) {
					element_found = new com.ttv.at.test.parameter(element_return_name.toLowerCase());
				}
				element_returns.add(element_found);
				lib_returns.add(element_found);
			}
		}
		
		// get option
		com.ttv.at.test.run_type type = com.ttv.at.test.run_type.DEFAULT;
		String run_type_name = null;
		if (COL_RUNTYPE_INDEX > 0)
			run_type_name = current_sheet.getCellText(COL_RUNTYPE_INDEX, current_row);
		if (run_type_name != null && run_type_name.length() > 0) {
			run_type_name = run_type_name.toUpperCase();
			if (run_type_name.equals("IGNORE"))
				type = com.ttv.at.test.run_type.IGNORE;
			else if (run_type_name.equals("INVERSE"))
				type = com.ttv.at.test.run_type.INVERSE;
			else if (run_type_name.equals("LAST_PASSED_IGNORE"))
				type = com.ttv.at.test.run_type.LAST_PASSED_IGNORE;
			else if (run_type_name.equals("LAST_PASSED"))
				type = com.ttv.at.test.run_type.LAST_PASSED;
			else if (run_type_name.equals("LAST_FAILED_IGNORE"))
				type = com.ttv.at.test.run_type.LAST_FAILED_IGNORE;
			else if (run_type_name.equals("LAST_FAILED"))
				type = com.ttv.at.test.run_type.LAST_FAILED;
			else if (run_type_name.equals("LAST_NOT_RUN"))
				type = com.ttv.at.test.run_type.LAST_NOT_RUN;
			else if (run_type_name.equals("LAST_NOT_RUN_IGNORE"))
				type = com.ttv.at.test.run_type.LAST_NOT_RUN_IGNORE;
			else if (run_type_name.equals("CLEAR_STATE"))
				type = com.ttv.at.test.run_type.CLEAR_STATE;
		}
		
		return new com.ttv.at.test.testelement(element_name, testlibrary_found, element_inputs, element_returns, type);
	}
	static private com.ttv.at.test.testlibrary load_test_cases_get_test_get_core_element(String action_name, 
			sheetPOI current_sheet,
			int current_row,
			int COL_RUNTYPE_INDEX,
			int COL_RETURN_INDEX,
			int COL_PARAM1_INDEX,
			ArrayList<com.ttv.at.test.testlibrary> test_libraries){
		action_name = action_name.replaceFirst("core.", "");
		
		// Get return
		ArrayList<com.ttv.at.test.parameter> action_returns = null;
		
		String returns_names_str = null;
		if (COL_RETURN_INDEX >= 0)
			returns_names_str = current_sheet.getCellText(COL_RETURN_INDEX, current_row);
		if (returns_names_str != null && returns_names_str.length() > 0) {
			returns_names_str = returns_names_str.toLowerCase();
			String[] returns_names_arr = returns_names_str.split("\\$");
			action_returns = new ArrayList<com.ttv.at.test.parameter>();
			for (int i = 0 ; i < returns_names_arr.length ; i ++)
				if (returns_names_arr[i] != null &&
						returns_names_arr[i].length() > 0)
					action_returns.add(new com.ttv.at.test.parameter(returns_names_arr[i]));
		}
		
		// Get input
		ArrayList<com.ttv.at.test.parameter> action_inputs = new ArrayList<com.ttv.at.test.parameter>();
		for (int i_input_col = COL_PARAM1_INDEX ; i_input_col < max_col ; i_input_col ++) {
			String action_input_name = current_sheet.getCellText(i_input_col, current_row);
			if (action_input_name != null && action_input_name.length() > 0) {

				if (action_input_name.startsWith("^^"))
					action_inputs.add(new com.ttv.at.test.parameter("", action_input_name.substring(2)));
				else if (action_input_name.equals("^NULL"))
					action_inputs.add(new com.ttv.at.test.parameter(""));
				else if (action_input_name.equals("^BLANK"))
					action_inputs.add(new com.ttv.at.test.parameter("", ""));
				else
				{
					action_input_name = action_input_name.toLowerCase();
					action_inputs.add(new com.ttv.at.test.parameter(action_input_name));
				}
			}
			else
				break;
		}
		//Create Libelement
		ArrayList<com.ttv.at.test.testlibelement> testlibelements = new ArrayList<com.ttv.at.test.testlibelement>();
		com.ttv.at.test.testlibelement new_core = new com.ttv.at.test.testlibelement(action_name, action_inputs, action_returns, null, com.ttv.at.test.run_type.DEFAULT);
		com.ttv.at.test.result core_validation = new_core.check_compatible();
		if (core_validation.get_result() == com.ttv.at.test.status_run.PASSED)
			testlibelements.add(new_core);
		else
			return null;
		
		
		//Create Library
		ArrayList<com.ttv.at.test.parameter> lib_returns = action_returns;
		ArrayList<com.ttv.at.test.parameter> lib_inputs = action_inputs;
		String library_name = "core." + action_name;
		com.ttv.at.test.testlibrary test_library = new com.ttv.at.test.testlibrary(library_name, lib_inputs, lib_returns, testlibelements, action_returns);
		
		//Add Library to List
		
		return test_library;
	}
	
	/******************************************************************************************************************/
	/************************************* LOAD GUI LIBRARIES *********************************************************/
	static private String load_test_libraries_warning_message;
	static public String get_load_test_libraries_warning_message() { return load_test_libraries_warning_message; }
	static private void append_load_test_libraries_warning_message(String message) {
		if (load_test_libraries_warning_message != null && load_test_libraries_warning_message.length() > 0)
			load_test_libraries_warning_message += "\n";
		else
			load_test_libraries_warning_message = "";
		load_test_libraries_warning_message += message;
	}
	static public ArrayList<com.ttv.at.test.testlibrary> load_test_libraries(String test_library_excel_file_full_path, ArrayList<com.ttv.at.test.testobject> gui_objects) {
		load_test_libraries_warning_message = "";
		
		// Open file
		if (!appPOI.OpenFile(test_library_excel_file_full_path)) {
			append_load_test_libraries_warning_message("Error in Opening File " + test_library_excel_file_full_path);
			return null;
		}
		
		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			appPOI.get_instance().Close();
			// appPOI.get_instance().Kill();
			append_load_test_libraries_warning_message("There is no sheet in file " + test_library_excel_file_full_path);
			return null;
		}
		
		ArrayList<com.ttv.at.test.testlibrary> loaded_testlibraries = new ArrayList<com.ttv.at.test.testlibrary>();
		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			String sheet_name = current_sheet.get_Name();
			if (!sheet_name.toLowerCase().startsWith(".help")) {
				int COL_NAME_INDEX = -1,
					COL_ACTION_INDEX = -1,
					COL_OBJECT_INDEX = -1,
					COL_RETURN_INDEX = -1,
					COL_PARAM1_INDEX = -1,
					COL_RUNTYPE_INDEX = -1,
					COL_LOOP_INDEX = -1;
				
				// search column index in sheet before adding
				int blank_count = 0;
				for (int iColIndex = start_col ; iColIndex < max_col ; iColIndex ++) {
					String col_name = current_sheet.getCellText(iColIndex, start_row);
					if ((col_name != null) && (col_name.length() > 0)) {
						col_name = col_name.toUpperCase();
						if (col_name.equals("NAME"))
							COL_NAME_INDEX	= iColIndex;
						else if (col_name.equals("ACTION"))
							COL_ACTION_INDEX	= iColIndex;
						else if (col_name.equals("OBJECT"))
							COL_OBJECT_INDEX	= iColIndex;
						else if (col_name.equals("RETURN"))
							COL_RETURN_INDEX	= iColIndex;
						else if (col_name.equals("PARAM1"))
							COL_PARAM1_INDEX	= iColIndex;
						else if (col_name.equals("RUN TYPE"))
							COL_RUNTYPE_INDEX	= iColIndex;
						else if (col_name.equals("LOOP"))
							COL_LOOP_INDEX	= iColIndex;
						
						if (	COL_NAME_INDEX > -1 &&
								COL_ACTION_INDEX > -1 &&
								COL_OBJECT_INDEX > -1 &&
								COL_PARAM1_INDEX > -1)
							break;
						
						if (col_name == null || col_name.length() == 0)
							blank_count ++;
						else
							blank_count = 0;
						if (blank_count > 5)
							break;
					}
					else
						break;
				}
				
				if (COL_NAME_INDEX == -1)
					append_load_test_libraries_warning_message("Column Name is not found");
				if (COL_ACTION_INDEX == -1)
					append_load_test_libraries_warning_message("Column Action is not found");
				if (COL_OBJECT_INDEX == -1)
					append_load_test_libraries_warning_message("Column Object is not found");
				if (COL_PARAM1_INDEX == -1)
					append_load_test_libraries_warning_message("Column Param1 is not found");
				
				
				if (	COL_NAME_INDEX > -1 &&
						COL_ACTION_INDEX > -1 &&
						COL_OBJECT_INDEX > -1 &&
						COL_PARAM1_INDEX > -1) {
					// Start process to add test libraries
					blank_count = 0;
					for (int iRowIndex = start_row + 1 ; iRowIndex < max_row ; iRowIndex ++) {
						// Get Action
						String sAction = current_sheet.getCellText(COL_ACTION_INDEX, iRowIndex);
						if (sAction != null && sAction.length() > 0) {
							blank_count = 0;
							sAction = sAction.toLowerCase();
							
							// Check the startLib
							if (sAction.equals("startlib"))
							{
								// Get test from sheet
								com.ttv.at.test.testlibrary tlib = load_test_libraries_get_library_from_sheet(
										current_sheet, 
										iRowIndex,
										COL_NAME_INDEX, 
										COL_ACTION_INDEX, 
										COL_OBJECT_INDEX, 
										COL_RUNTYPE_INDEX, 
										COL_RETURN_INDEX, 
										COL_PARAM1_INDEX, 
										COL_LOOP_INDEX, 
										gui_objects);
								
								if (tlib == null) {
									append_load_test_libraries_warning_message("Error in add test " + load_test_libraries_get_library_from_sheet_warning_message);
								}
								else {
									// check if test library is not in list before adding to list
									boolean lib_found = false;
									for (int i_test_index = 0 ; i_test_index < loaded_testlibraries.size() ; i_test_index ++ )
										if (loaded_testlibraries.get(i_test_index).get_name().equals(tlib.get_name())) {
											lib_found = true;
											break;
										}
									
									if (lib_found)
										append_load_test_libraries_warning_message("Test library '" + tlib.get_name() + "' is duplicated ");
									else
										loaded_testlibraries.add(tlib);
								}
							}
						}
						else {
							if (blank_count > 5)
								break;
							blank_count ++;
						}
							
					}
				}
			}
		}

		appPOI.get_instance().Close();
		
		// Set all un-used lib to false
		if (loaded_testlibraries != null) {
			used_libs = new boolean[loaded_testlibraries.size()];
			for (boolean used_lib: used_libs)
				used_lib = false;
		}
		
		// Match the action lib before end loading test library
		for (com.ttv.at.test.testlibrary cur_testlibrary: loaded_testlibraries)
			for (com.ttv.at.test.testlibelement cur_testlibelement: cur_testlibrary.get_testlibelements())
				if (cur_testlibelement.get_name().toLowerCase().startsWith("call ")) {
					com.ttv.at.test.result check_res = cur_testlibelement.set_lib_instance(loaded_testlibraries);
					if (check_res.get_result() != com.ttv.at.test.status_run.PASSED) {
						append_load_test_libraries_warning_message("\n Library " + cur_testlibrary.get_name() + " error: " + check_res.get_message());
					}
					// Set used for current lib
					try {
						used_libs[Integer.parseInt(check_res.get_return_text())] = true;
					} catch (Exception e) {}
				}
		
		// appPOI.get_instance().Kill();
		
		// Display the list of un-used GUI object to the comsole
		System.out.println("****** ****** ****** ****** ****** ******");
		System.out.println("****** LIST OF UN_USED OBJECT ******");
		for (int i = 0 ; i < used_objects.length ; i ++)
			if (!used_objects[i])
				System.out.println(gui_objects.get(i).get_key() + " is not used");
		
		return loaded_testlibraries;
	}
	static boolean[] used_libs;

	static private String load_test_libraries_get_library_from_sheet_warning_message;
	static public String get_load_test_libraries_get_library_from_sheet_warning_message() { return load_test_libraries_get_library_from_sheet_warning_message; }
	static private void append_load_test_libraries_get_library_from_sheet_warning_message(String message) {
		if (load_test_libraries_get_library_from_sheet_warning_message != null && load_test_libraries_get_library_from_sheet_warning_message.length() > 0)
			load_test_libraries_get_library_from_sheet_warning_message += "\n";
		else
			load_test_libraries_get_library_from_sheet_warning_message = "";
		load_test_libraries_get_library_from_sheet_warning_message += message;
	}
	static private com.ttv.at.test.testlibrary load_test_libraries_get_library_from_sheet( sheetPOI current_sheet, 
			int current_row,
			int COL_NAME_INDEX, 
			int COL_ACTION_INDEX, 
			int COL_OBJECT_INDEX, 
			int COL_RUNTYPE_INDEX, 
			int COL_RETURN_INDEX, 
			int COL_PARAM1_INDEX, 
			int COL_LOOP_INDEX,
			ArrayList<com.ttv.at.test.testobject> gui_objects) {
		load_test_libraries_get_library_from_sheet_warning_message = "";
		if (current_sheet == null) {
			append_load_test_libraries_get_library_from_sheet_warning_message("current_sheet is null");
			return null;
		}
		
		// Get test library name
		String library_name = current_sheet.getCellText(COL_NAME_INDEX, current_row);
		if (library_name == null || library_name.length() == 0) {
			append_load_test_libraries_get_library_from_sheet_warning_message("Library Name is not available to this test library (Sheet " + 
			current_sheet.get_Name() + ", row " + current_row + ")");
			return null;
		}
		library_name = library_name.toLowerCase();
		
		// Get return if available
		ArrayList<com.ttv.at.test.parameter> lib_returns = null;
		ArrayList<String> lib_returns_names = null;
		boolean[] lib_returns_validated = null;
		
		String lib_returns_names_str = null;
		if (COL_RETURN_INDEX >= 0)
			lib_returns_names_str = current_sheet.getCellText(COL_RETURN_INDEX, current_row);
		if (lib_returns_names_str != null && lib_returns_names_str.length() > 0) {
			lib_returns_names_str = lib_returns_names_str.toLowerCase();
			String[] lib_returns_names_arr = lib_returns_names_str.split("\\$");
			lib_returns_names = new ArrayList<String>();
			for (int i = 0 ; i < lib_returns_names_arr.length ; i ++)
				if (lib_returns_names_arr[i] != null &&
						lib_returns_names_arr[i].length() > 0)
					lib_returns_names.add(lib_returns_names_arr[i]);
			for (int i = 0 ; i < lib_returns_names.size() ; i ++) {
				if (lib_returns == null)
					lib_returns = new ArrayList<com.ttv.at.test.parameter>();
				lib_returns.add(new com.ttv.at.test.parameter(lib_returns_names.get(i)));
			}
			if (lib_returns != null)
				lib_returns_validated = new boolean[lib_returns.size()];
			for (int i = 0 ; i < lib_returns_validated.length ; i ++)
				lib_returns_validated[i] = false;
		}
		
		// Get inputs
		ArrayList<com.ttv.at.test.parameter> inputs = new ArrayList<com.ttv.at.test.parameter>();
		for (int iColIndex = COL_PARAM1_INDEX ; iColIndex < max_col ; iColIndex ++) {
			String input_name = current_sheet.getCellText(iColIndex, current_row);
			if (input_name != null && input_name.length() > 0) {
				// Check if direct input (start with "^^")
				input_name = input_name.toLowerCase();
				// Check if input name is exist
				boolean found_in_inputs = false;
				for (int i_p_index = 0 ; i_p_index < inputs.size() ; i_p_index ++)
					if (inputs.get(i_p_index).get_key().length() == input_name.length() &&
							inputs.get(i_p_index).get_key().equals(input_name)) {
						found_in_inputs = true;
						break;
					}
				if (found_in_inputs) {
					append_load_test_libraries_get_library_from_sheet_warning_message("Library " + library_name + " (Sheet " + 
							current_sheet.get_Name() + ", row " + current_row + ") has input '" + input_name + "' is duplicated, please check");
					return null;
				}
				else
					inputs.add(new com.ttv.at.test.parameter(input_name));
			}
			else
				break;
		}
		
		// Adding actions
		ArrayList<com.ttv.at.test.testlibelement> lib_elements = new ArrayList<com.ttv.at.test.testlibelement>();
		ArrayList<com.ttv.at.test.parameter> lib_element_returns = new ArrayList<com.ttv.at.test.parameter>();
		int end_row = -1;
		for (int i = current_row + 1 ; i < max_row ; i ++) {
			// Get Action Name
			String action_name = current_sheet.getCellText(COL_ACTION_INDEX, i);
			if (action_name != null && action_name.length() > 0 
					&& (!action_name.toLowerCase().equals("startlib")) && (!action_name.toLowerCase().equals("endlib"))) {

				// Check if start loop
				String loop_name = null;
				if (COL_LOOP_INDEX >= 0) {
					String temp = current_sheet.getCellText(COL_LOOP_INDEX, i);
					if (temp != null && temp.length() >0)
						loop_name = temp;
				}
				
				if (loop_name != null && loop_name.length() >0) {
					// ADDING a LOOP
					int iRowCount = current_sheet.getMergeCells_RowCount(COL_LOOP_INDEX, i);
					ArrayList<com.ttv.at.test.testlibelement> loopelements = new ArrayList<com.ttv.at.test.testlibelement>();
					for (int row_index = i ; row_index < i + iRowCount ; row_index ++) {
						action_name = current_sheet.getCellText(COL_ACTION_INDEX, row_index);
						if (action_name != null && action_name.length() > 0 && 
								(!action_name.toLowerCase().equals("inputs")) && (!action_name.toLowerCase().equals("end")))
						{
							com.ttv.at.test.testlibelement new_lib_element = load_test_libraries_get_library_get_libelement_from_sheet(action_name,
									library_name,
									inputs,
									lib_returns,
									lib_element_returns, 
									current_sheet, 
									row_index, 
									COL_OBJECT_INDEX,
									COL_RUNTYPE_INDEX,
									COL_RETURN_INDEX, 
									COL_PARAM1_INDEX,
									gui_objects);
							if (new_lib_element == null) {
								append_load_test_libraries_get_library_from_sheet_warning_message(
										get_load_test_libraries_get_library_get_libelement_from_sheet_warning_message());
								return null;
							}

							// Check the return of library
							if (lib_returns != null && 
									new_lib_element.get_Return() != null) {
								for (int lib_returns_index = 0 ; lib_returns_index < lib_returns.size() ; lib_returns_index ++)
									if (new_lib_element.get_Return().get_key().equals(lib_returns_names.get(lib_returns_index)))
										lib_returns_validated[lib_returns_index] = true;
							}
							
							// Add action to list
							lib_elements.add(new_lib_element);
							loopelements.add(new_lib_element);
						}
						else
							break;
					}
					
					new com.ttv.at.test.testlibelementloopgroup(loop_name, loopelements);
					// Set index to continue
					i = i + iRowCount - 1;
				}
				else {
					com.ttv.at.test.testlibelement new_libelement = load_test_libraries_get_library_get_libelement_from_sheet(action_name,
							library_name,
							inputs,
							lib_returns,
							lib_element_returns, 
							current_sheet, 
							i,
							COL_OBJECT_INDEX,
							COL_RUNTYPE_INDEX,
							COL_RETURN_INDEX, 
							COL_PARAM1_INDEX,
							gui_objects);
					if (new_libelement == null) {
						append_load_test_libraries_get_library_from_sheet_warning_message(
								get_load_test_libraries_get_library_get_libelement_from_sheet_warning_message());
						return null;
					}
					else {
						lib_elements.add(new_libelement);
					}
				}
			} else {
				end_row = i - 1;
				break;
			}
		}
		
		// Create test library
		com.ttv.at.test.testlibrary res_lib = new com.ttv.at.test.testlibrary(current_sheet.get_Name().toLowerCase() + "." + library_name, inputs, lib_returns, lib_elements, lib_element_returns, current_row, end_row);
		return res_lib;
	}
	
	static private String load_test_libraries_get_library_get_libelement_from_sheet_warning_message;
	static public String get_load_test_libraries_get_library_get_libelement_from_sheet_warning_message() { return load_test_libraries_get_library_get_libelement_from_sheet_warning_message; }
	static private void append_load_test_libraries_get_library_get_libelement_from_sheet_warning_message(String message) {
		if (load_test_libraries_get_library_get_libelement_from_sheet_warning_message != null && load_test_libraries_get_library_get_libelement_from_sheet_warning_message.length() > 0)
			load_test_libraries_get_library_get_libelement_from_sheet_warning_message += "\n";
		else
			load_test_libraries_get_library_get_libelement_from_sheet_warning_message = "";
		load_test_libraries_get_library_get_libelement_from_sheet_warning_message += message;
	}
	static private com.ttv.at.test.testlibelement load_test_libraries_get_library_get_libelement_from_sheet(String action_name,
			String library_name,
			ArrayList<com.ttv.at.test.parameter> inputs,
			ArrayList<com.ttv.at.test.parameter> lib_returns,
			ArrayList<com.ttv.at.test.parameter> lib_element_returns, 
			sheetPOI current_sheet, 
			int current_row,
			int COL_OBJECT_INDEX,
			int COL_RUNTYPE_INDEX, 
			int COL_RETURN_INDEX, 
			int COL_PARAM1_INDEX,
			ArrayList<com.ttv.at.test.testobject> gui_objects) {
		
		// Get Action name
		load_test_libraries_get_library_get_libelement_from_sheet_warning_message = "";
		if (action_name != null && action_name.length() > 0 
				&& (!action_name.toLowerCase().equals("startlib")) && (!action_name.toLowerCase().equals("endlib"))) {
			// Get GUI object
			String gui_object_name = current_sheet.getCellText(COL_OBJECT_INDEX, current_row);
			com.ttv.at.test.testobject gui_object_found = null;
			if (gui_object_name != null && gui_object_name.length() > 0){
				gui_object_name = gui_object_name.toLowerCase();
				for (int i_object_index = 0 ; i_object_index < gui_objects.size() ; i_object_index ++)
					if (gui_objects.get(i_object_index).get_key().length() == gui_object_name.length() &&
						gui_objects.get(i_object_index).get_key().equals(gui_object_name)) {
						gui_object_found = gui_objects.get(i_object_index);
						used_objects[i_object_index] = true;
						break;
					}
				if (gui_object_found == null) {
					append_load_test_libraries_get_library_get_libelement_from_sheet_warning_message("Library " + library_name + ", action '"+action_name+"' (Sheet " + 
							current_sheet.get_Name() + ", row " + current_row + ") has gui object '" + gui_object_name + "' is not found in gui repository, please check");
					return null;
				}
			}
			
			// Get input
			ArrayList<com.ttv.at.test.parameter> action_inputs = new ArrayList<com.ttv.at.test.parameter>();
			for (int i_input_col = COL_PARAM1_INDEX ; i_input_col < max_col ; i_input_col ++) {
				String action_input_name = current_sheet.getCellText(i_input_col, current_row);
				if (action_input_name != null && action_input_name.length() > 0) {

					if (action_input_name.startsWith("^^"))
						action_inputs.add(new com.ttv.at.test.parameter("", action_input_name.substring(2)));
					else if (action_input_name.equals("^NULL"))
						action_inputs.add(new com.ttv.at.test.parameter(""));
					else if (action_input_name.equals("^BLANK"))
						action_inputs.add(new com.ttv.at.test.parameter("", ""));
					else
					{
						action_input_name = action_input_name.toLowerCase();
						com.ttv.at.test.parameter input_found = null;
						// Check if the input is in the input of test library
						for (int i_input_index = 0 ; i_input_index < inputs.size() ; i_input_index ++)
							if (inputs.get(i_input_index).get_key().length() == action_input_name.length() &&
								inputs.get(i_input_index).get_key().equals(action_input_name)) {
								input_found = inputs.get(i_input_index);
								break;
							}
						
						// Check library returns
						if (input_found == null && lib_returns != null) {
							for (int lib_returns_index = 0 ; lib_returns_index < lib_returns.size() ; lib_returns_index ++)
								if (lib_returns.get(lib_returns_index).get_key().equals(action_input_name)) {
									input_found = lib_returns.get(lib_returns_index);
									break;
								}
						}
						
						// check library element returns
						if (input_found == null) {// check in the return list of actions
							for (int i_return_index = 0 ; i_return_index < lib_element_returns.size() ; i_return_index ++)
								if (lib_element_returns.get(i_return_index).get_key().length() == action_input_name.length() &&
										lib_element_returns.get(i_return_index).get_key().equals(action_input_name)) {
									input_found = lib_element_returns.get(i_return_index);
									break;
								}
						}
						
						if (input_found == null) {
							append_load_test_libraries_get_library_get_libelement_from_sheet_warning_message("Library " + library_name + " (Sheet " + 
									current_sheet.get_Name() + ", row " + current_row + ") has test lib action '" + action_name + "' which input '" + action_input_name + "' not found in input/return list, please check");
							return null;
						}
						
						action_inputs.add(input_found);
					}
				}
				else
					break;
			}
			
			// Get return
			ArrayList<com.ttv.at.test.parameter> action_returns = null;
			ArrayList<String> returns_names = null;
			boolean[] returns_validated = null;
			
			String returns_names_str = null;
			if (COL_RETURN_INDEX >= 0)
				returns_names_str = current_sheet.getCellText(COL_RETURN_INDEX, current_row);
			if (returns_names_str != null && returns_names_str.length() > 0) {
				returns_names_str = returns_names_str.toLowerCase();
				String[] returns_names_arr = returns_names_str.split("\\$");
				returns_names = new ArrayList<String>();
				for (int i = 0 ; i < returns_names_arr.length ; i ++)
					if (returns_names_arr[i] != null &&
							returns_names_arr[i].length() > 0)
						returns_names.add(returns_names_arr[i]);

				if (returns_names != null)
					returns_validated = new boolean[returns_names.size()];
				for (int i = 0 ; i < returns_validated.length ; i ++)
					returns_validated[i] = false;
				
				// Validate the returns
				for (int i = 0 ; i < returns_names.size() ; i ++) {
					String action_return_name = returns_names.get(i).toLowerCase();
					com.ttv.at.test.parameter action_return = null;
					// Check if return is in list or the return of test library
					if (lib_returns != null) {
						for (int lib_returns_index = 0 ; lib_returns_index < lib_returns.size() ; lib_returns_index ++)
							if (action_return_name.equals(lib_returns.get(lib_returns_index).get_key())) {
								action_return = lib_returns.get(lib_returns_index);
								break;
							}
					}
					// Check if return is in list or the action returns
					if (lib_element_returns != null) {
						for (int i_return_index = 0 ; i_return_index < lib_element_returns.size(); i_return_index ++ )
							if (lib_element_returns.get(i_return_index).get_key().length() == action_return_name.length() &&
									lib_element_returns.get(i_return_index).get_key().equals(action_return_name)) {
								action_return = lib_element_returns.get(i_return_index);
								break;
							}
					}
					// If still, new parameter
					if (action_return == null)
						action_return = new com.ttv.at.test.parameter(action_return_name);
					
					// Add to returns
					if (action_returns == null)
						action_returns = new ArrayList<com.ttv.at.test.parameter>();
					action_returns.add(action_return);
					lib_element_returns.add(action_return);
				}
			}
			
			// get option
			com.ttv.at.test.run_type type = com.ttv.at.test.run_type.DEFAULT;
			String run_type_name = null;
			if (COL_RUNTYPE_INDEX > 0)
				run_type_name = current_sheet.getCellText(COL_RUNTYPE_INDEX, current_row);
			if (run_type_name != null && run_type_name.length() > 0) {
				run_type_name = run_type_name.toUpperCase();
				if (run_type_name.equals("IGNORE"))
					type = com.ttv.at.test.run_type.IGNORE;
				else if (run_type_name.equals("INVERSE"))
					type = com.ttv.at.test.run_type.INVERSE;
				else if (run_type_name.equals("LAST_PASSED_IGNORE"))
					type = com.ttv.at.test.run_type.LAST_PASSED_IGNORE;
				else if (run_type_name.equals("LAST_PASSED"))
					type = com.ttv.at.test.run_type.LAST_PASSED;
				else if (run_type_name.equals("LAST_FAILED_IGNORE"))
					type = com.ttv.at.test.run_type.LAST_FAILED_IGNORE;
				else if (run_type_name.equals("LAST_FAILED"))
					type = com.ttv.at.test.run_type.LAST_FAILED;
				else if (run_type_name.equals("LAST_NOT_RUN"))
					type = com.ttv.at.test.run_type.LAST_NOT_RUN;
				else if (run_type_name.equals("LAST_NOT_RUN_IGNORE"))
					type = com.ttv.at.test.run_type.LAST_NOT_RUN_IGNORE;
			}
			
			// new libelement
			return new com.ttv.at.test.testlibelement(action_name, action_inputs, action_returns, gui_object_found, type);
		}
		else
			append_load_test_libraries_get_library_get_libelement_from_sheet_warning_message("Action Name is null or blank");
		return null;
	}

	/******************************************************************************************************************/
	/************************************* LOAD GUI OBJECTS ***********************************************************/
	static private String load_test_gui_objects_warning_message;
	static public String get_load_test_gui_objects_warning_message() { return load_test_gui_objects_warning_message; }
	static private void append_load_test_gui_objects_warning_message(String message) {
		if (load_test_gui_objects_warning_message != null && load_test_gui_objects_warning_message.length() > 0)
			load_test_gui_objects_warning_message += "\n";
		else
			load_test_gui_objects_warning_message = "";
		load_test_gui_objects_warning_message += message;
	}
	static public ArrayList<com.ttv.at.test.testobject> load_test_gui_objects(String test_gui_object_excel_file_full_path) {
		load_test_gui_objects_warning_message = "";
		// Open file
		if (!appPOI.OpenFile(test_gui_object_excel_file_full_path)) {
			append_load_test_gui_objects_warning_message ("Error in Opening File " + test_gui_object_excel_file_full_path);
			return null;
		}
		
		// Scan in all sheets to load object
		ArrayList<sheetPOI> sheets = appPOI.get_instance().get_sheets();
		if ((sheets == null) || (sheets.size() <= 0)) {
			append_load_test_gui_objects_warning_message("There is no sheet in file " + test_gui_object_excel_file_full_path);
			return null;
		}

		ArrayList<com.ttv.at.test.testobject> loaded_testobjects = new ArrayList<com.ttv.at.test.testobject>();
		for (int i = 0 ; i < sheets.size() ; i ++) {
			sheetPOI current_sheet = sheets.get(i);
			String sheet_name = current_sheet.get_Name();
			// Scan the header to get the column for properties
			ArrayList<String> loaded_testobjects_properties = new ArrayList<String>();
			for (int scan_col = 1; scan_col < max_col ; scan_col ++) {
				// Get Column name
				String col_name = current_sheet.getCellText(scan_col, start_row);
				if ((col_name != null) && (col_name.length() > 0)) {
					// Check if column is not in list before Add colname to properties list
					boolean bFound = false;
					for (int col_name_index = 0 ; col_name_index < loaded_testobjects_properties.size() ; col_name_index ++)
						if (col_name.toLowerCase().equals(loaded_testobjects_properties.get(col_name_index).toLowerCase())) {
							bFound = true;
							break;
						}
					if (!bFound)
						loaded_testobjects_properties.add(col_name);
					else
						append_load_test_gui_objects_warning_message("In file" + test_gui_object_excel_file_full_path + 
								", sheet " + sheet_name + ", column " + col_name + " is duplicated.");
				}
				else
					break;
			}
			
			// Scan all row to add gui object of the sheet
			int blank_count = 0;
			for (int scan_row = (start_row + 1); scan_row < max_row; scan_row ++) {
				// get all the posible data
				String key_value = current_sheet.getCellText(start_col, scan_row);
				if ((key_value != null) && (key_value.length() > 0)) {
					key_value = (sheet_name + "." + key_value).toLowerCase();
					blank_count = 0;
					// Check the key_value is duplicated
					boolean object_duplicated = false;
					for (int iObjIndex = 0 ; iObjIndex < loaded_testobjects.size() ; iObjIndex ++)
						if(	loaded_testobjects.get(iObjIndex).get_key().length() == key_value.length() &&
							loaded_testobjects.get(iObjIndex).get_key().equals(key_value)) {
							object_duplicated = true;
							break;
						}
					
					if (object_duplicated)
						append_load_test_gui_objects_warning_message("In file" + test_gui_object_excel_file_full_path + 
								", sheet " + sheet_name + ", object " + key_value + " is duplicated.");
					else {
						// Adding data
						ArrayList<com.ttv.at.test.property> proses = new ArrayList<com.ttv.at.test.property>();
						for (int iColScan = 1 ; iColScan < loaded_testobjects_properties.size() ; iColScan ++) {
							String current_value = current_sheet.getCellText(start_col + iColScan, scan_row);
							if ((current_value != null) && (current_value.length() > 0)) {
								String curCol = loaded_testobjects_properties.get(iColScan);
								proses.add(new com.ttv.at.test.property(curCol, current_value));
							}
						}
						
						loaded_testobjects.add(new com.ttv.at.test.testobject(key_value, proses));
					}
				}
				else
				{
					// if (key_value == null)
					// 	break;
					if (blank_count > 10)
						break;
					blank_count ++;
				}
					
			}
		}
		
		// Close file
		appPOI.get_instance().Close();
		// appPOI.get_instance().Kill();
		
		// init used-objects array
		if (loaded_testobjects != null) {
			used_objects = new boolean[loaded_testobjects.size()];
			for (boolean used_object : used_objects)
				used_object = false;

		// Scan in loaded_testobjects to link the ref_object
		for (com.ttv.at.test.testobject scan_obj : loaded_testobjects) {
			if (!scan_obj.update_ref_object(loaded_testobjects))
				append_load_test_gui_objects_warning_message("Unable to update reference object for object " + scan_obj.get_key());
			if (scan_obj.get_all_properties() != null)
			for (com.ttv.at.test.testobjectproperties scan_oprop : scan_obj.get_all_properties()) {
				if (scan_oprop.get_ref_object() != null) {
					for (int i = 0 ; i < loaded_testobjects.size() ; i ++)
						if (loaded_testobjects.get(i).get_key().equals(scan_oprop.get_ref_object().get_key())) {
							used_objects[i] = true;
							break;
						}
				}
			}
		}
		
		// Check the loop forever in relation object;
		for (com.ttv.at.test.testobject scan_obj : loaded_testobjects) {
			if (scan_obj.get_rel_max(0) >= com.ttv.at.test.testobject.max_relation_allow)
				append_load_test_gui_objects_warning_message("The Relation of object '" + scan_obj.get_key() + "' seem to be over maximined allow");
		}
			
		// Scan to update all releated object
		}
		return loaded_testobjects;
	}
	static boolean[] used_objects;

}
