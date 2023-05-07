package com.ttv.at.util.test;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ttv.at.test.status_run;


public class report {
	
	static ArrayList<testsuite_report> exported_testsuites = null;
	
	static public boolean export_db (com.ttv.at.test.testsuite cur_testsuite, String content) {
		
		// Check if allow export DB
		boolean allow_export_DB = true;
		if (cur_testsuite != null && 
				cur_testsuite.get_startTime() != null && 
						cur_testsuite.get_endTime() != null &&
							cur_testsuite.get_dataset() != null)
			for (com.ttv.at.test.parameter scan_prop: cur_testsuite.get_dataset().get_params()) {
				if (scan_prop.check_key("Report DB Auto Save")) {
					if (scan_prop.get_value().toLowerCase().equals("false"))
						allow_export_DB = false;
					break;
				}
			}
		else
			allow_export_DB = false;
		
		if (allow_export_DB) {
			// ******** GET INFORMATION TO CONNECT DB ******** //
			String dbURL = null;
			String ReportDBType = null;
			String ReportDBTName = null;
			String ReportDBHost = null;
			String ReportDBUsername = null;
			String ReportDBPassword = null;
			boolean use_mySQL = false;
			boolean use_MSSQL = false;
			if (cur_testsuite.get_dataset().get_params() != null)
				for (com.ttv.at.test.parameter prop: cur_testsuite.get_dataset().get_params()) {
					String key = prop.get_key().toLowerCase();
					if (key.equals("report db type"))
						ReportDBType = prop.get_value().toLowerCase();
					else if (key.equals("report db name"))
						ReportDBTName = prop.get_value().toLowerCase();
					else if (key.equals("report db host name"))
						ReportDBHost = prop.get_value().toLowerCase();
					else if (key.equals("report db username"))
						ReportDBUsername = prop.get_value().toLowerCase();
					else if (key.equals("report db password"))
						ReportDBPassword = prop.get_value().toLowerCase();
					if (ReportDBType != null &&
							ReportDBHost != null &&
							ReportDBUsername != null &&
							ReportDBPassword != null)
						break;
				}
			if (ReportDBType != null && ReportDBType.equals("mysql") && 
					ReportDBHost != null &&
					ReportDBUsername != null &&
					ReportDBPassword != null) {
				dbURL = "jdbc:mysql://" + ReportDBHost + "/" + ReportDBTName;// ?user=" + ReportDBUsername + "&password=" + ReportDBPassword;
				use_mySQL = true;
			}
			else if (ReportDBType != null && ReportDBType.equals("microsoft sql") && 
					ReportDBHost != null &&
					ReportDBUsername != null &&
					ReportDBPassword != null) {
				ReportDBPassword = "DBpassword";
				dbURL = "jdbc:sqlserver://" + ReportDBHost + ":1433;databaseName="+ReportDBTName;
				use_MSSQL = true; 
			}
			else
				dbURL = null;
			if (dbURL != null && dbURL.length() > 0){

				// ******** CHECK IF TESTSUITE is in DB before ******** //
				testsuite_report found_testsuite = null;
				if (exported_testsuites != null && exported_testsuites.size() > 0) {
					for (testsuite_report scan_testsuite : exported_testsuites) {
						if (scan_testsuite.get_name().equals(cur_testsuite.get_name())) {
							found_testsuite = scan_testsuite;
							break;
						}
					}
				}
				
				if (found_testsuite == null) { // Export to new suite
					testsuite_report export_DB_suite = new testsuite_report(cur_testsuite, use_mySQL, use_MSSQL, dbURL, ReportDBUsername, ReportDBPassword, content);
					if (exported_testsuites == null)
						exported_testsuites = new ArrayList<testsuite_report>();
					exported_testsuites.add(export_DB_suite);
					return export_DB_suite.update_DB ();
				}
				else
					return found_testsuite.update_DB (cur_testsuite);
			}
		}
		

		
		return false;
	}
		

	static int excel_start_sheet_test_area = 1;
	static int excel_start_sheet_test_summary_row = 7;
	static public String export_excel_overall (com.ttv.at.test.testsuite cur_testsuite) {
		if (cur_testsuite != null)
			try
			{
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				//Copy File to folder Temp
				String url_Source = com.ttv.at.test.testsetting.get_default_conf_folder() + com.ttv.at.util.os.os_file_separator + "Report_Template.Overall";
				String url_Destination = com.ttv.at.test.testsetting.get_default_log_folder() + com.ttv.at.util.os.os_file_separator + cur_testsuite.get_name() + "Report.Overall.xls";
	
				com.ttv.at.office.appPOI.OpenFile(url_Source);
				com.ttv.at.office.appPOI.SaveAsExcelAndExit(url_Destination);
	
				com.ttv.at.office.appPOI.OpenFile(url_Destination);
	
				ArrayList<com.ttv.at.office.sheetPOI> sheets = new ArrayList<com.ttv.at.office.sheetPOI>();
				// Add summary sheet
				sheets.add(com.ttv.at.office.appPOI.get_instance().get_sheets().get(0));
				//Create sheet
				for (int i = 0; i < testareas.size(); i++) {
					if (i == 0)
						sheets.add(com.ttv.at.office.appPOI.get_instance().RenameSheet(excel_start_sheet_test_area, testareas.get(i).get_name()));
					else
						sheets.add(com.ttv.at.office.appPOI.get_instance().duplicate_sheet(testareas.get(0).get_name(), testareas.get(i).get_name()) );
				}
				
				// Prepare to summary for test suite
				com.ttv.at.office.sheetPOI summary_sheet = sheets.get(0);
				int total_passed = 0;
				int total_failed = 0;
				int total_not_run = 0;
	
				// Start row 7
				// Column
				//	1. No.
				//	2. Name TC
				//	3. Start
				//	4. End
				// 	5. Status
				//	6. Description
				
				Object normal_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(null, null, false, false, true);
				Object passed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.blue, null, true, false, true);
				Object failed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.red, null, true, false, true);
				Object notrun_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.gray, null, false, false, true);
				Object total_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, false, true);
				
				int startRow = 7;
				for (int i = 0; i < testareas.size(); i++) {
					
					// Prepare to summary for sheet
					com.ttv.at.office.sheetPOI cur_sheet = sheets.get(excel_start_sheet_test_area + i);
					String area_name = testareas.get(i).get_name();
					int total_area_passed = 0;
					int total_area_failed = 0;
					int total_area_not_run = 0;
	
					//Get tests
					ArrayList<com.ttv.at.test.test> tests = testareas.get(i).get_tests();
	
					//Insert value...
					for (int indexTest = 0; indexTest < tests.size(); indexTest++) {
						String No = Integer.toString(indexTest + 1) ;
						String NameTC = tests.get(indexTest).get_tc_instance().get_name();
						String Status = tests.get(indexTest).get_run_status().name();
						String StartTime = tests.get(indexTest).get_start_time();
						// String EndTime = tests.get(indexTest).get_end_time();
						String Duration = tests.get(indexTest).get_duration_minutes();
						String Description = tests.get(indexTest).get_message();
						
						cur_sheet.setCellText(0 , startRow + indexTest, No);
						cur_sheet.setCellFormat(0 , startRow + indexTest, normal_cell);
						
						cur_sheet.setCellText(1 , startRow + indexTest, NameTC);
						cur_sheet.setCellFormat(1 , startRow + indexTest, normal_cell);
						
						cur_sheet.setCellText(2 , startRow + indexTest, StartTime);
						cur_sheet.setCellFormat(2 , startRow + indexTest, normal_cell);
						
						cur_sheet.setCellText(3 , startRow + indexTest, Duration);
						cur_sheet.setCellFormat(3 , startRow + indexTest, normal_cell);
						
						if(Status.endsWith("FAILED")){
							Status = "FAILED";
						}
						cur_sheet.setCellText(4 , startRow + indexTest, Status);
	
						//Set Style for Status
						if (Status == "PASSED") {
							cur_sheet.setCellFormat(4 , startRow + indexTest, passed_cell);
							total_area_passed ++;
						}
						else if (Status.endsWith("FAILED")) {
							cur_sheet.setCellFormat(4 , startRow + indexTest, failed_cell);
							total_area_failed ++;
						}
						else {
							cur_sheet.setCellFormat(4 , startRow + indexTest, normal_cell);
							total_area_not_run ++;
						}
						


						if (Description != null && Description.length() > 0)
							cur_sheet.setCellText(5 , startRow + indexTest, Description);
						cur_sheet.setCellFormat(5 , startRow + indexTest, normal_cell);
					}
	
					total_passed += total_area_passed;
					total_failed += total_area_failed;
					total_not_run += total_area_not_run;
					
					// ****** print summary for test area *******
					summary_sheet.setCellText(1 , excel_start_sheet_test_summary_row + i, Integer.toString(i));
					summary_sheet.setCellFormat(1 , excel_start_sheet_test_summary_row + i, normal_cell);
					
					summary_sheet.setCellText(2 , excel_start_sheet_test_summary_row + i, area_name);
					summary_sheet.setCellFormat(2 , excel_start_sheet_test_summary_row + i, normal_cell);
	
					summary_sheet.setCellInt(3 , excel_start_sheet_test_summary_row + i, total_area_passed);
					summary_sheet.setCellFormat(3 , excel_start_sheet_test_summary_row + i, passed_cell);
	
					summary_sheet.setCellInt(4 , excel_start_sheet_test_summary_row + i, total_area_failed);
					summary_sheet.setCellFormat(4 , excel_start_sheet_test_summary_row + i, failed_cell);
	
					summary_sheet.setCellInt(5 , excel_start_sheet_test_summary_row + i, total_area_not_run);
					summary_sheet.setCellFormat(5 , excel_start_sheet_test_summary_row + i, notrun_cell);
	
					summary_sheet.setCellInt(6 , excel_start_sheet_test_summary_row + i, (total_area_passed + total_area_failed + total_area_not_run));
					summary_sheet.setCellFormat(6 , excel_start_sheet_test_summary_row + i, total_cell);
				}
				
				// summary for test area
				summary_sheet.setCellText(2 , excel_start_sheet_test_summary_row + testareas.size() + 2, "TOTAL");
				summary_sheet.setCellFormat(2 , excel_start_sheet_test_summary_row + testareas.size() + 2, normal_cell);
				
				summary_sheet.setCellText(3 , excel_start_sheet_test_summary_row + testareas.size() + 2, Integer.toString(total_passed));
				summary_sheet.setCellFormat(3 , excel_start_sheet_test_summary_row + testareas.size() + 2, passed_cell);
				
				summary_sheet.setCellText(4 , excel_start_sheet_test_summary_row + testareas.size() + 2, Integer.toString(total_failed));
				summary_sheet.setCellFormat(4 , excel_start_sheet_test_summary_row + testareas.size() + 2, failed_cell);
				
				summary_sheet.setCellText(5 , excel_start_sheet_test_summary_row + testareas.size() + 2, Integer.toString(total_not_run));
				summary_sheet.setCellFormat(5 , excel_start_sheet_test_summary_row + testareas.size() + 2, notrun_cell);
				
				summary_sheet.setCellText(6 , excel_start_sheet_test_summary_row + testareas.size() + 2, Integer.toString(total_passed + total_failed + total_not_run));
				summary_sheet.setCellFormat(6 , excel_start_sheet_test_summary_row + testareas.size() + 2, total_cell);
				
				com.ttv.at.office.appPOI.get_instance().SaveAndClose();
				
				return url_Destination;
			}catch(Exception e)
			{
				e.printStackTrace();
				return "-- Error: the report file '" + cur_testsuite.get_name() + "Report.xls" + "'Exception:" + e.getMessage();
			}
		return null;
	}

	static public String export_excel_summary (com.ttv.at.test.testsuite cur_testsuite) {
		// ****** CREATE EXCEL REPORT
		if (cur_testsuite != null)
			try
			{
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				//Copy File to folder Temp
				String url_Source = com.ttv.at.test.testsetting.get_default_conf_folder() + com.ttv.at.util.os.os_file_separator + "Report_Template.Summary.xls";
				String url_Destination = com.ttv.at.test.testsetting.get_default_log_folder() + com.ttv.at.util.os.os_file_separator + cur_testsuite.get_name() + ".Report.Summary.xls";
	
				com.ttv.at.office.appPOI.OpenFile(url_Source);
				com.ttv.at.office.appPOI.SaveAsExcelAndExit(url_Destination);
	
				com.ttv.at.office.appPOI.OpenFile(url_Destination);
				
				// Prepare to summary for test suite
				com.ttv.at.office.sheetPOI summary_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(0);
	
				// Start row 7
				// Column
				//	1. No.
				//	2. Name TC
				//	3. Start
				//	4. End
				// 	5. Status
				//	6. Description
				Object title_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, true, false);
				Object normal_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(null, null, false, false, true);
				Object passed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.blue, null, true, false, true);
				Object failed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.red, null, true, false, true);
				Object notrun_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.gray, null, false, false, true);
				Object total_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, false, true);
				
				int startRow = 7;
				for (int i = 0; i < testareas.size(); i++) {
					
					// Prepare to summary for sheet
					String area_name = testareas.get(i).get_name();
					// Write Area Header
					summary_sheet.setCellText(0 , startRow + 1, area_name);
					summary_sheet.setCellFormat(0 , startRow + 1, title_cell);
					startRow = startRow + 2;
	
					//Get tests
					ArrayList<com.ttv.at.test.test> tests = testareas.get(i).get_tests();
	
					//Insert value...
					int insert_count = 0;
					for (int indexTest = 0; indexTest < tests.size(); indexTest++)
						if (tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT && 
								tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME)
						{
							String No = Integer.toString(insert_count + 1) ;
							String NameTC = tests.get(indexTest).get_tc_instance().get_name();
							String Status = tests.get(indexTest).get_run_status().name();
							String StartTime = tests.get(indexTest).get_start_time();
							// String EndTime = tests.get(indexTest).get_end_time();
							String Duration = tests.get(indexTest).get_duration_minutes();
							String Description = tests.get(indexTest).get_message();
							
							summary_sheet.setCellText(0 , startRow + insert_count, No);
							summary_sheet.setCellFormat(0 , startRow + insert_count, normal_cell);
							
							summary_sheet.setCellText(1 , startRow + insert_count, NameTC);
							summary_sheet.setCellFormat(1 , startRow + insert_count, normal_cell);
							
							summary_sheet.setCellText(2 , startRow + insert_count, StartTime);
							summary_sheet.setCellFormat(2 , startRow + insert_count, normal_cell);
							
							summary_sheet.setCellText(3 , startRow + insert_count, Duration);
							summary_sheet.setCellFormat(3 , startRow + insert_count, normal_cell);
							
							if(Status.endsWith("FAILED")){
								Status = "FAILED";
							}
							summary_sheet.setCellText(4 , startRow + insert_count, Status);
		
							//Set Style for Status
							if (Status == "PASSED") {
								summary_sheet.setCellFormat(4 , startRow + insert_count, passed_cell);
							}
							else if (Status.endsWith("FAILED")) {
								summary_sheet.setCellFormat(4 , startRow + insert_count, failed_cell);
							}
							else {
								summary_sheet.setCellFormat(4 , startRow + insert_count, normal_cell);
							}
							
							if (Description != null && Description.length() > 0)
								summary_sheet.setCellText(5 , startRow + insert_count, Description);
							summary_sheet.setCellFormat(5 , startRow + insert_count, normal_cell);

							// increase index
							insert_count ++;
						}
					startRow = startRow + insert_count;
					
				}
				com.ttv.at.office.appPOI.get_instance().SaveAndClose();

				return url_Destination;
			}catch(Exception e)
			{
				e.printStackTrace();
				return "-- Error: the report file '" + cur_testsuite.get_name() + "Report.xls" + "'Exception:" + e.getMessage();
			}
		return null;
	}

	static SimpleDateFormat headDate = new SimpleDateFormat("yyyy/MM/dd");// HH:mm");
	static public String export_excel_summary2 (com.ttv.at.test.testsuite cur_testsuite) {
		// ****** CREATE EXCEL REPORT
		if (cur_testsuite != null)
			try
			{
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				// Copy File to folder Temp
				String url_Source = com.ttv.at.test.testsetting.get_default_conf_folder() + com.ttv.at.util.os.os_file_separator + "Report_Template.Summary2.xls";
				String url_Destination = com.ttv.at.test.testsetting.get_root_log_folder() + com.ttv.at.util.os.os_file_separator + cur_testsuite.get_tc_set().get_name() + ".Report.Summary.xls";

				// Check if file exists
				File Des_file = new File(url_Destination);
				String ExcelFile_Derectory_Path = Des_file.getParent();
				if (Des_file.exists()) {// FILE EXISTS
					com.ttv.at.office.appPOI.OpenFile(url_Destination);
					
					com.ttv.at.office.sheetPOI summary_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(0);
					com.ttv.at.office.sheetPOI links_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(1);
					com.ttv.at.office.sheetPOI jiras_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(2);

					Object title_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, true, false);
					Object normal_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(null, null, false, false, true);
					Object passed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.blue, null, true, false, true);
					Object failed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.red, null, true, false, true);
					// Object notrun_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.gray, null, false, false, true);
					// Object total_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, false, true);
					
					// ****** Find the latest result column ******
					// Find the column Description
					int startRow = 6;
					int DescriptionCol = -1;
					for (int i = 1 ; i < summary_sheet.get_max_col(startRow) ; i ++) {
						String HeaderTitle = summary_sheet.getCellText(i, startRow);
						if (HeaderTitle != null && HeaderTitle.toLowerCase().equals("description")) {
							DescriptionCol = i;
							break;
						}
					}
					
					// Move left all columns
					if (DescriptionCol > 0) {
						for (int iCol = DescriptionCol; iCol >= 2; iCol--) {
							summary_sheet.copyColumn(iCol, iCol + 1);
							summary_sheet.set_col_autosize (iCol + 1);
							links_sheet.copyColumn(iCol, iCol + 1);
							jiras_sheet.copyColumn(iCol, iCol + 1);
						}

						// Make new Method
						String colName = summary_sheet.get_col_name (DescriptionCol);
						summary_sheet.setCellMethod(DescriptionCol, 0, "COUNTIF(" + colName + "8:" + colName + "9988,\"PASSED\")");
						summary_sheet.setCellMethod(DescriptionCol, 1, "COUNTIF(" + colName + "8:" + colName + "9988,\"FAILED\")");
						summary_sheet.setCellMethod(DescriptionCol, 2, "COUNTIF(" + colName + "8:" + colName + "9988,\"NOT_RUN\")");
						summary_sheet.setCellMethod(DescriptionCol, 3, "SUM(" + colName + "1:" + colName + "3)");
						summary_sheet.setCellMethod(DescriptionCol, 4, "IF(" + colName + "1>0," + colName + "1/" + colName + "4, 0)");
						summary_sheet.setCellMethod(DescriptionCol, 5, "IF(" + colName + "1>0," + colName + "2/" + colName + "4, 0)");
					}
					DescriptionCol ++;
					
					// Clear col 2 old result
					for (int iRow = startRow + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
						summary_sheet.setCellText(2, iRow, "");
						links_sheet.setCellText(2, iRow, "");
						jiras_sheet.setCellText(2, iRow, "");
					}
					
					summary_sheet.setCellText(2, 6, cur_testsuite.get_runtime_browser() + "\n" + headDate.format(cur_testsuite.get_startTime()));
						
					// Get the short link to html file
					String short_html_link = "./" + (com.ttv.at.test.testsetting.get_default_log_folder() + "/index.html").substring(ExcelFile_Derectory_Path.length() + 1).replace('\\', '/');
					links_sheet.setCellText(2, 6, short_html_link);
					
					// Compare to the existing result to prepare data before adding
					// ****** Scan all area to check if need add more row for value
					for (int area_index = 0; area_index < testareas.size(); area_index++) {
						
						// Prepare to summary for sheet
						String area_name = testareas.get(area_index).get_name();
						ArrayList<com.ttv.at.test.test> tests = testareas.get(area_index).get_tests();
						
						// ****** Get the test case list of existing report
						int area_found_index = -1;
						for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
							String sheet_text_name = summary_sheet.getCellText(0 , iRow);
							if (sheet_text_name != null && sheet_text_name.length() > 0 && sheet_text_name.toLowerCase().equals(area_name.toLowerCase())) {
								area_found_index = iRow;
								break;
							}
						}
						
						if (area_found_index > 0) { // this area is existing area
							ArrayList<Integer> new_added_rows = new ArrayList<Integer>();
							
							
							// Get the Existing TC Name List
							ArrayList<String> existing_TC_List = new ArrayList<String>();
							for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
								String existing_name = summary_sheet.getCellText(1 , iRow);
								if (existing_name != null && existing_name.length() > 0)
									existing_TC_List.add(existing_name);
								else
									break;
							}
							// Get the current TC_name
							ArrayList<String> current_TC_List = new ArrayList<String>();
							ArrayList<Integer> current_TC_indexes = new ArrayList<Integer>();
							for (int iTest = 0 ; iTest < tests.size() ; iTest ++)
								if (tests.get(iTest).get_selection_status() != com.ttv.at.test.status_selection.INIT &&
										tests.get(iTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME){
									current_TC_List.add(tests.get(iTest).get_tc_instance().get_name());
									current_TC_indexes.add(iTest);
								}
							
							// Scan to check if new test need to be add
							ArrayList<Integer> test_found_indexes = new ArrayList<Integer>();
							for (int iTest = 0 ; iTest < current_TC_List.size() ; iTest ++) {
								// Count the duplicate
								int duplicate_found = 0;
								if (iTest > 0)
									for (int iScanDuplicate = 0 ; iScanDuplicate < iTest ; iScanDuplicate ++)
										if (current_TC_List.get(iScanDuplicate).equals(current_TC_List.get(iTest)))
											duplicate_found ++;
								
								// Find the test in area that match
								boolean test_found = false;
								int found_count = 0;
								for (int iExisting = 0 ; iExisting < existing_TC_List.size() ; iExisting ++) {
									if (existing_TC_List.get(iExisting).equals(current_TC_List.get(iTest)))
										found_count ++;
									if (found_count == (duplicate_found + 1)) {
										test_found = true;
										test_found_indexes.add(iExisting + area_found_index + 1);
										break;
									}
								}
								
								// if not match
								if (!test_found) {
									// Try to find position by using the Next test
									int next_test_pos = -1;
									if (iTest < (current_TC_List.size() - 1)) {
										// Get the next test case (this test will be the previous of the next test)
										String iNextName = current_TC_List.get(iTest + 1);
										boolean found_the_next_test = false;
										int duplicate_the_next_test_found = 0;
										for (int iScanDuplicate = 0 ; iScanDuplicate < iTest + 1 ; iScanDuplicate ++)
											if (current_TC_List.get(iScanDuplicate).equals(iNextName))
												duplicate_the_next_test_found ++;
										
										// Find the next test in area that match
										boolean next_test_found = false;
										int next_found_count = 0;
										for (int iExisting = 0 ; iExisting < existing_TC_List.size() ; iExisting ++) {
											if (existing_TC_List.get(iExisting).equals(iNextName))
												next_found_count ++;
											if (next_found_count == (duplicate_the_next_test_found + 1)) {
												next_test_found = true;
												next_test_pos = iExisting;
												break;
											}
										}
									}
									
									if (next_test_pos <= 0)
										next_test_pos = existing_TC_List.size() - 1;
									
									if (next_test_pos > 0) { // insert this test to the next_test_pos
										summary_sheet.insertRow(next_test_pos + area_found_index + 1);

										// Update Index for the item add this row
										for (int iscan_index_increase = 0 ; iscan_index_increase < test_found_indexes.size() ; iscan_index_increase ++)
											if (test_found_indexes.get(iscan_index_increase) >= next_test_pos + area_found_index + 1)
												test_found_indexes.set(iscan_index_increase, test_found_indexes.get(iscan_index_increase) + 1);
										
										// Test case Name
										summary_sheet.setCellText(1 , next_test_pos + area_found_index + 1, current_TC_List.get(iTest));
										summary_sheet.setCellFormat(1 , next_test_pos + area_found_index + 1, normal_cell);

										// Add index for the new item
										test_found_indexes.add(next_test_pos + area_found_index + 1);
										new_added_rows.add(next_test_pos + area_found_index + 1);
									}
									
									// Reload existing_TC_List
									existing_TC_List.clear();
									for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
										String existing_name = summary_sheet.getCellText(1 , iRow);
										if (existing_name != null && existing_name.length() > 0)
											existing_TC_List.add(existing_name);
										else
											break;
									}
								}
							}
							
							for (int iTest = 0 ; iTest < current_TC_List.size() ; iTest ++) {
								// Update result

								String Status = tests.get(current_TC_indexes.get(iTest)).get_run_status().name();
								String UniqueName = tests.get(current_TC_indexes.get(iTest)).get_unique_result_id();
								String Description = tests.get(current_TC_indexes.get(iTest)).get_message();

								// Test result
								if(Status.endsWith("FAILED")){
									Status = "FAILED";
								}
								summary_sheet.setCellText(2 , test_found_indexes.get(iTest), Status);
								if (UniqueName != null && UniqueName.length() > 0) {
									UniqueName = short_html_link + "?name=" + UniqueName;
									links_sheet.setCellText(2 , test_found_indexes.get(iTest), UniqueName);
								}
								
								if (Status == "PASSED") {
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), passed_cell);
								}
								else if (Status.endsWith("FAILED")) {
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), failed_cell);
									String Jira_id = tests.get(current_TC_indexes.get(iTest)).get_probable_issue();
									if (Jira_id != null && Jira_id.length() > 0) // Add Jira link
										jiras_sheet.setCellText(2 , test_found_indexes.get(iTest), Jira_id);
								}
								else
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), normal_cell);
								
								
								// Set Description
								if (Description != null && Description.length() > 0)
									summary_sheet.setCellText(DescriptionCol , test_found_indexes.get(iTest), Description);
								summary_sheet.setCellFormat(DescriptionCol , test_found_indexes.get(iTest), normal_cell);
							}
							
							// Update number
							int No_count = 0;
							for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
								String existing_name = summary_sheet.getCellText(1 , iRow);
								if (existing_name != null && existing_name.length() > 0) {
									No_count ++;
									summary_sheet.setCellText(0 , iRow, String.valueOf(No_count));
									summary_sheet.setCellFormat(0 , iRow, normal_cell);
								}
								else
									break;
							}
							
							
							// Update format
							for (int new_row : new_added_rows)
								for (int iCol = 3 ; iCol <= DescriptionCol ; iCol ++)
									summary_sheet.setCellFormat(iCol, new_row, normal_cell);
							
							summary_sheet.set_col_autosize (2);
						}
						else { // need to add this area to the report
							// count the row need to be insert
							int start_new_row = -1;
							
							if (area_index < (testareas.size() - 1)) {
								// Find the next test_area
								for (int next_area_index = area_index + 1 ; next_area_index < testareas.size() ; next_area_index ++) {
									String next_area_name = testareas.get(area_index + 1).get_name();
									for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
										String sheet_text_name = summary_sheet.getCellText(0 , iRow);
										if (sheet_text_name != null && sheet_text_name.length() > 0 && sheet_text_name.toLowerCase().equals(next_area_name.toLowerCase())) {
											start_new_row = iRow;
											break;
										}
									}
									if (start_new_row >= 0)
										break;
								}
							}

							if (start_new_row >= 0) { // If found the next area to add
								// insert new_rows_count at this row
								summary_sheet.insertRow (start_new_row, 2);
								links_sheet.insertRow (start_new_row, 2);
								jiras_sheet.insertRow (start_new_row, 2);
							}
							else {
								// Find the last position to add
								for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
									String test_name = summary_sheet.getCellText(1 , iRow);
									if (test_name != null && test_name.length() > 0)
										start_new_row = iRow + 1;
								}
								if (start_new_row >= 0)
									start_new_row = start_new_row + 2;
								else
									start_new_row = 8;
							}
							
							// ****** start add the result ******
							// Add test area name
							summary_sheet.setCellText(0 , start_new_row, area_name);
							summary_sheet.setCellFormat(0 , start_new_row, title_cell);
							
							//Insert value...
							int test_count = 0;
							for (int indexTest = 0; indexTest < tests.size(); indexTest++)
								if (tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT &&
										tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME){
									summary_sheet.insertRow (start_new_row + 1 + test_count, 1);
									links_sheet.insertRow (start_new_row + 1 + test_count, 1);
									jiras_sheet.insertRow (start_new_row + 1 + test_count, 1);
									
									String No = Integer.toString(indexTest + 1) ;
									String NameTC = tests.get(indexTest).get_tc_instance().get_name();
									String Status = tests.get(indexTest).get_run_status().name();
									String Description = tests.get(indexTest).get_message();
									String UniqueName = tests.get(indexTest).get_unique_result_id();
									
									// Number
									summary_sheet.setCellText(0 , start_new_row + 1 + test_count, No);
									summary_sheet.setCellFormat(0 , start_new_row + 1 + test_count, normal_cell);
									
									// Test case Name
									summary_sheet.setCellText(1 , start_new_row + 1 + test_count, NameTC);
									summary_sheet.setCellFormat(1 , start_new_row + 1 + test_count, normal_cell);
									
									// Test result
									if(Status.endsWith("FAILED")){
										Status = "FAILED";
									}
									summary_sheet.setCellText(2 , start_new_row + 1 + test_count, Status);
									if (UniqueName != null && UniqueName.length() > 0) {
										UniqueName = short_html_link + "?name=" + UniqueName;
										links_sheet.setCellText(2 , start_new_row + 1 + test_count, UniqueName);
									}
									if (Status == "PASSED") {
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, passed_cell);
									}
									else if (Status.endsWith("FAILED")) {
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, failed_cell);
										String Jira_id = tests.get(indexTest).get_probable_issue();
										if (Jira_id != null && Jira_id.length() > 0) // Add Jira link
											jiras_sheet.setCellText(2 , start_new_row + 1 + test_count, Jira_id);
									}
									else
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, normal_cell);
									
									// Set Description
									if (Description != null && Description.length() > 0)
										summary_sheet.setCellText(2 + 1 , start_new_row + 1 + test_count, Description);
									summary_sheet.setCellFormat(2 + 1 , start_new_row + 1 + test_count, normal_cell);
									
									// increase index
									test_count ++;
								}
							
							// Update format
							for (int new_row = start_new_row + 1 ; new_row < (start_new_row + 1 + test_count); new_row ++)
								for (int iCol = 2 ; iCol < 2 ; iCol ++)
									summary_sheet.setCellFormat(iCol, new_row, normal_cell);
						}
					}
					com.ttv.at.office.appPOI.get_instance().SaveAndClose();
					cur_testsuite.set_summary_report_path(url_Destination);
					return url_Destination;
				}
				
				else {// FILE NOT EXISTS

					com.ttv.at.office.appPOI.OpenFile(url_Source);
					com.ttv.at.office.appPOI.SaveAsExcelAndExit(url_Destination);
		
					com.ttv.at.office.appPOI.OpenFile(url_Destination);
					
					Object title_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, true, false);
					Object normal_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(null, null, false, false, true);
					Object passed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.blue, null, true, false, true);
					Object failed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.red, null, true, false, true);
					Object notrun_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.gray, null, false, false, true);
					Object total_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, false, true);
					
					// Prepare to summary for test suite
					com.ttv.at.office.sheetPOI summary_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(0);
					com.ttv.at.office.sheetPOI links_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(1);
					com.ttv.at.office.sheetPOI jiras_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(2);
		
					// Start row 7
					// Column
					//	1. No.
					//	2. Name TC
					//	3. Start
					//	4. End
					// 	5. Status
					//	6. Description
					summary_sheet.setCellText(2, 6, cur_testsuite.get_runtime_browser() + "\n" + headDate.format(cur_testsuite.get_startTime()));

					// Get the short link to html file
					String short_html_link = "./" + (com.ttv.at.test.testsetting.get_default_log_folder() + "/index.html").substring(ExcelFile_Derectory_Path.length() + 1).replace('\\', '/');
					links_sheet.setCellText(2, 6, short_html_link);
					
					int startRow = 7;
					for (int i = 0; i < testareas.size(); i++) {
						
						// Prepare to summary for sheet
						String area_name = testareas.get(i).get_name();
						// Write Area Header
						summary_sheet.setCellText(0 , startRow + 1, area_name);
						summary_sheet.setCellFormat(0 , startRow + 1, title_cell);
						startRow = startRow + 2;
		
						//Get tests
						ArrayList<com.ttv.at.test.test> tests = testareas.get(i).get_tests();
		
						//Insert value...
						int test_count = 0;
						for (int indexTest = 0; indexTest < tests.size(); indexTest++) 
							if (tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT &&
									tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME){
								String No = Integer.toString(test_count+1) ;
								String NameTC = tests.get(indexTest).get_tc_instance().get_name();
								String Status = tests.get(indexTest).get_run_status().name();
								String UniqueName = tests.get(indexTest).get_unique_result_id();
								String StartTime = tests.get(indexTest).get_start_time();
								// String EndTime = tests.get(indexTest).get_end_time();
								String Duration = tests.get(indexTest).get_duration_minutes();
								String Description = tests.get(indexTest).get_message();
								
								// Number
								summary_sheet.setCellText(0 , startRow + test_count, No);
								summary_sheet.setCellFormat(0 , startRow + test_count, normal_cell);
								
								// Test case Name
								summary_sheet.setCellText(1 , startRow + test_count, NameTC);
								summary_sheet.setCellFormat(1 , startRow + test_count, normal_cell);
								
								// Test result
								if(Status.endsWith("FAILED")){
									Status = "FAILED";
								}
								summary_sheet.setCellText(2 , startRow + test_count, Status);
								if (UniqueName != null && UniqueName.length() > 0) {
									UniqueName = short_html_link + "?name=" + UniqueName;
									links_sheet.setCellText(2 ,  startRow + test_count, UniqueName);
								}
								
								if (Status == "PASSED") {
									summary_sheet.setCellFormat(2 , startRow + test_count, passed_cell);
								}
								else if (Status.endsWith("FAILED")) {
									summary_sheet.setCellFormat(2 , startRow + test_count, failed_cell);
									String Jira_id = tests.get(indexTest).get_probable_issue();
									if (Jira_id != null && Jira_id.length() > 0) // Add Jira link
										jiras_sheet.setCellText(2 ,  startRow + test_count, Jira_id);
								}
								else
									summary_sheet.setCellFormat(2 , startRow + test_count, normal_cell);
								
								// Set Description
								if (Description != null && Description.length() > 0)
									summary_sheet.setCellText(3 , startRow + test_count, Description);
								summary_sheet.setCellFormat(3 , startRow + test_count, normal_cell);
								
								// increase 
								test_count ++;
							}
						startRow = startRow + test_count;
						
					}
					com.ttv.at.office.appPOI.get_instance().SaveAndClose();
					cur_testsuite.set_summary_report_path(url_Destination);
					return url_Destination;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				return "-- Error: the report file '" + cur_testsuite.get_tc_set().get_name() + "Report.xls" + "'Exception:" + e.getMessage();
			}
		return null;
	}
	static public String update_excel_summary2 (com.ttv.at.test.testsuite cur_testsuite) {
		// ****** CREATE EXCEL REPORT
		if (cur_testsuite != null)
			try
			{
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				// Copy File to folder Temp
				String url_Destination = cur_testsuite.get_summary_report_path();

				// Check if file exists
				File Des_file = new File(url_Destination);
				String ExcelFile_Derectory_Path = Des_file.getParent();
				if (Des_file.exists()) {// FILE EXISTS
					com.ttv.at.office.appPOI.OpenFile(url_Destination);
					
					com.ttv.at.office.sheetPOI summary_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(0);
					com.ttv.at.office.sheetPOI links_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(1);
					com.ttv.at.office.sheetPOI jiras_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(2);

					Object title_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, true, false);
					Object normal_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(null, null, false, false, true);
					Object passed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.blue, null, true, false, true);
					Object failed_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.red, null, true, false, true);
					// Object notrun_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.gray, null, false, false, true);
					// Object total_cell = com.ttv.at.office.appPOI.get_instance().create_cell_style(Color.magenta, null, true, false, true);
					
					// ****** Find the latest result column ******
					// Find the column Description
					int startRow = 6;
					int DescriptionCol = -1;
					for (int i = 1 ; i < summary_sheet.get_max_col(startRow) ; i ++) {
						String HeaderTitle = summary_sheet.getCellText(i, startRow);
						if (HeaderTitle != null && HeaderTitle.toLowerCase().equals("description")) {
							DescriptionCol = i;
							break;
						}
					}
					
					
					// Clear col 2 old result
					for (int iRow = startRow + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
						summary_sheet.setCellText(2, iRow, "");
						links_sheet.setCellText(2, iRow, "");
						jiras_sheet.setCellText(2, iRow, "");
					}
					
					summary_sheet.setCellText(2, 6, cur_testsuite.get_runtime_browser() + "\n" + headDate.format(cur_testsuite.get_startTime()));
						
					// Get the short link to html file
					String short_html_link = "./" + (com.ttv.at.test.testsetting.get_default_log_folder() + "/index.html").substring(ExcelFile_Derectory_Path.length() + 1).replace('\\', '/');
					links_sheet.setCellText(2, 6, short_html_link);
					
					// Compare to the existing result to prepare data before adding
					// ****** Scan all area to check if need add more row for value
					for (int area_index = 0; area_index < testareas.size(); area_index++) {
						
						// Prepare to summary for sheet
						String area_name = testareas.get(area_index).get_name();
						ArrayList<com.ttv.at.test.test> tests = testareas.get(area_index).get_tests();
						
						// ****** Get the test case list of existing report
						int area_found_index = -1;
						for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
							String sheet_text_name = summary_sheet.getCellText(0 , iRow);
							if (sheet_text_name != null && sheet_text_name.length() > 0 && sheet_text_name.toLowerCase().equals(area_name.toLowerCase())) {
								area_found_index = iRow;
								break;
							}
						}
						
						if (area_found_index > 0) { // this area is existing area
							ArrayList<Integer> new_added_rows = new ArrayList<Integer>();
							
							
							// Get the Existing TC Name List
							ArrayList<String> existing_TC_List = new ArrayList<String>();
							for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
								String existing_name = summary_sheet.getCellText(1 , iRow);
								if (existing_name != null && existing_name.length() > 0)
									existing_TC_List.add(existing_name);
								else
									break;
							}
							// Get the current TC_name
							ArrayList<String> current_TC_List = new ArrayList<String>();
							ArrayList<Integer> current_TC_indexes = new ArrayList<Integer>();
							for (int iTest = 0 ; iTest < tests.size() ; iTest ++)
								if (tests.get(iTest).get_selection_status() != com.ttv.at.test.status_selection.INIT &&
										tests.get(iTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME) {
									current_TC_List.add(tests.get(iTest).get_tc_instance().get_name());
									current_TC_indexes.add(iTest);
								}
							
							// Scan to check if new test need to be add
							ArrayList<Integer> test_found_indexes = new ArrayList<Integer>();
							for (int iTest = 0 ; iTest < current_TC_List.size() ; iTest ++) {
								// Count the duplicate
								int duplicate_found = 0;
								if (iTest > 0)
									for (int iScanDuplicate = 0 ; iScanDuplicate < iTest ; iScanDuplicate ++)
										if (current_TC_List.get(iScanDuplicate).equals(current_TC_List.get(iTest)))
											duplicate_found ++;
								
								// Find the test in area that match
								boolean test_found = false;
								int found_count = 0;
								for (int iExisting = 0 ; iExisting < existing_TC_List.size() ; iExisting ++) {
									if (existing_TC_List.get(iExisting).equals(current_TC_List.get(iTest)))
										found_count ++;
									if (found_count == (duplicate_found + 1)) {
										test_found = true;
										test_found_indexes.add(iExisting + area_found_index + 1);
										break;
									}
								}
								
								// if not match
								if (!test_found) {
									// Try to find position by using the Next test
									int next_test_pos = -1;
									if (iTest < (current_TC_List.size() - 1)) {
										// Get the next test case (this test will be the previous of the next test)
										String iNextName = current_TC_List.get(iTest + 1);
										boolean found_the_next_test = false;
										int duplicate_the_next_test_found = 0;
										for (int iScanDuplicate = 0 ; iScanDuplicate < iTest + 1 ; iScanDuplicate ++)
											if (current_TC_List.get(iScanDuplicate).equals(iNextName))
												duplicate_the_next_test_found ++;
										
										// Find the next test in area that match
										boolean next_test_found = false;
										int next_found_count = 0;
										for (int iExisting = 0 ; iExisting < existing_TC_List.size() ; iExisting ++) {
											if (existing_TC_List.get(iExisting).equals(iNextName))
												next_found_count ++;
											if (next_found_count == (duplicate_the_next_test_found + 1)) {
												next_test_found = true;
												next_test_pos = iExisting;
												break;
											}
										}
									}
									
									if (next_test_pos <= 0)
										next_test_pos = existing_TC_List.size() - 1;
									
									if (next_test_pos > 0) { // insert this test to the next_test_pos
										summary_sheet.insertRow(next_test_pos + area_found_index + 1);

										// Update Index for the item add this row
										for (int iscan_index_increase = 0 ; iscan_index_increase < test_found_indexes.size() ; iscan_index_increase ++)
											if (test_found_indexes.get(iscan_index_increase) >= next_test_pos + area_found_index + 1)
												test_found_indexes.set(iscan_index_increase, test_found_indexes.get(iscan_index_increase) + 1);

										// Test case Name
										summary_sheet.setCellText(1 , next_test_pos + area_found_index + 1, current_TC_List.get(iTest));
										summary_sheet.setCellFormat(1 , next_test_pos + area_found_index + 1, normal_cell);

										test_found_indexes.add(next_test_pos + area_found_index + 1);
										new_added_rows.add(next_test_pos + area_found_index + 1);
									}
									
									// Reload existing_TC_List
									existing_TC_List.clear();
									for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
										String existing_name = summary_sheet.getCellText(1 , iRow);
										if (existing_name != null && existing_name.length() > 0)
											existing_TC_List.add(existing_name);
										else
											break;
									}
								}
							}
							
							for (int iTest = 0 ; iTest < current_TC_List.size() ; iTest ++) {
								// Update result
								String Status = tests.get(current_TC_indexes.get(iTest)).get_run_status().name();
								String UniqueName = tests.get(current_TC_indexes.get(iTest)).get_unique_result_id();
								String Description = tests.get(current_TC_indexes.get(iTest)).get_message();

								// Test result
								if(Status.endsWith("FAILED")){
									Status = "FAILED";
								}
								summary_sheet.setCellText(2 , test_found_indexes.get(iTest), Status);
								if (UniqueName != null && UniqueName.length() > 0) {
									UniqueName = short_html_link + "?name=" + UniqueName;
									links_sheet.setCellText(2 , test_found_indexes.get(iTest), UniqueName);
								}
								
								if (Status == "PASSED") {
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), passed_cell);
								}
								else if (Status.endsWith("FAILED")) {
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), failed_cell);
									String Jira_id = tests.get(current_TC_indexes.get(iTest)).get_probable_issue();
									if (Jira_id != null && Jira_id.length() > 0) // Add Jira link
										jiras_sheet.setCellText(2 , area_found_index +  + 1, Jira_id);
								}
								else
									summary_sheet.setCellFormat(2 , test_found_indexes.get(iTest), normal_cell);
								
								
								// Set Description
								if (Description != null && Description.length() > 0)
									summary_sheet.setCellText(DescriptionCol , test_found_indexes.get(iTest), Description);
								summary_sheet.setCellFormat(DescriptionCol , test_found_indexes.get(iTest), normal_cell);
							}
							
							// Update number
							int No_count = 0;
							for (int iRow = area_found_index + 1 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
								String existing_name = summary_sheet.getCellText(1 , iRow);
								if (existing_name != null && existing_name.length() > 0) {
									No_count ++;
									summary_sheet.setCellText(0 , iRow, String.valueOf(No_count));
									summary_sheet.setCellFormat(0 , iRow, normal_cell);
								}
								else
									break;
							}
							
							
							// Update format
							for (int new_row : new_added_rows)
								for (int iCol = 3 ; iCol <= DescriptionCol ; iCol ++)
									summary_sheet.setCellFormat(iCol, new_row, normal_cell);
							
							summary_sheet.set_col_autosize (2);
						}
						else { // need to add this area to the report
							int start_new_row = -1;
							
							if (area_index < (testareas.size() - 1)) {
								// Find the next test_area
								for (int next_area_index = area_index + 1 ; next_area_index < testareas.size() ; next_area_index ++) {
									String next_area_name = testareas.get(area_index + 1).get_name();
									for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
										String sheet_text_name = summary_sheet.getCellText(0 , iRow);
										if (sheet_text_name != null && sheet_text_name.length() > 0 && sheet_text_name.toLowerCase().equals(next_area_name.toLowerCase())) {
											start_new_row = iRow;
											break;
										}
									}
									if (start_new_row >= 0)
										break;
								}
							}

							if (start_new_row >= 0) { // If found the next area to add
								// insert new_rows_count at this row
								summary_sheet.insertRow (start_new_row, 2);
								links_sheet.insertRow (start_new_row, 2);
								jiras_sheet.insertRow (start_new_row, 2);
							}
							else {
								// Find the last position to add
								for (int iRow = 0 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
									String test_name = summary_sheet.getCellText(1 , iRow);
									if (test_name != null && test_name.length() > 0)
										start_new_row = iRow + 1;
								}
								if (start_new_row >= 0)
									start_new_row = start_new_row + 2;
								else
									start_new_row = 8;
							}
							
							// ****** start add the result ******
							// Add test area name
							summary_sheet.setCellText(0 , start_new_row, area_name);
							summary_sheet.setCellFormat(0 , start_new_row, title_cell);
							
							//Insert value...
							int test_count = 0;
							for (int indexTest = 0; indexTest < tests.size(); indexTest++)
								if (tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT &&
										tests.get(indexTest).get_selection_status() != com.ttv.at.test.status_selection.INIT_1_TIME) {
									
									summary_sheet.insertRow (start_new_row + 1 + test_count, 1);
									links_sheet.insertRow (start_new_row + 1 + test_count, 1);
									jiras_sheet.insertRow (start_new_row + 1 + test_count, 1);
									
									String No = Integer.toString(indexTest + 1) ;
									String NameTC = tests.get(indexTest).get_tc_instance().get_name();
									String Status = tests.get(indexTest).get_run_status().name();
									String Description = tests.get(indexTest).get_message();
									String UniqueName = tests.get(indexTest).get_unique_result_id();
									
									// Number
									summary_sheet.setCellText(0 , start_new_row + 1 + test_count, No);
									summary_sheet.setCellFormat(0 , start_new_row + 1 + test_count, normal_cell);
									
									// Test case Name
									summary_sheet.setCellText(1 , start_new_row + 1 + test_count, NameTC);
									summary_sheet.setCellFormat(1 , start_new_row + 1 + test_count, normal_cell);
									
									// Test result
									if(Status.endsWith("FAILED")){
										Status = "FAILED";
									}
									summary_sheet.setCellText(2 , start_new_row + 1 + test_count, Status);
									if (UniqueName != null && UniqueName.length() > 0) {
										UniqueName = short_html_link + "?name=" + UniqueName;
										links_sheet.setCellText(2 , start_new_row + 1 + test_count, UniqueName);
									}
									if (Status == "PASSED") {
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, passed_cell);
									}
									else if (Status.endsWith("FAILED")) {
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, failed_cell);
										String Jira_id = tests.get(indexTest).get_probable_issue();
										if (Jira_id != null && Jira_id.length() > 0) // Add Jira link
											jiras_sheet.setCellText(2 , start_new_row + 1 + test_count, Jira_id);
									}
									else
										summary_sheet.setCellFormat(2 , start_new_row + 1 + test_count, normal_cell);
									
									// Set Description
									if (Description != null && Description.length() > 0)
										summary_sheet.setCellText(2 + 1 , start_new_row + 1 + test_count, Description);
									summary_sheet.setCellFormat(2 + 1 , start_new_row + 1 + test_count, normal_cell);
									
									// increase index
									test_count++;
								}
							
							// Update format
							for (int new_row = start_new_row + 1 ; new_row < (start_new_row + 1 + test_count); new_row ++)
								for (int iCol = 2 ; iCol < 2 ; iCol ++)
									summary_sheet.setCellFormat(iCol, new_row, normal_cell);
						}
					}
					com.ttv.at.office.appPOI.get_instance().SaveAndClose();
					cur_testsuite.set_summary_report_path(url_Destination);
					return url_Destination;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				return "-- Error: the report file '" + cur_testsuite.get_tc_set().get_name() + "Report.xls" + "'Exception:" + e.getMessage();
			}
		return null;
	}
	static public String export_excel_summary2_to_html(com.ttv.at.test.testsuite cur_testsuite, String excel_file_full_path) {
		if (excel_file_full_path != null && excel_file_full_path.length() > 0 && cur_testsuite != null) {
			File Des_file = new File(excel_file_full_path);
			if (Des_file.exists()) {// FILE EXISTS
				
				com.ttv.at.office.appPOI.OpenFile(excel_file_full_path);
				
				com.ttv.at.office.sheetPOI summary_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(0);
				com.ttv.at.office.sheetPOI links_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(1);
				com.ttv.at.office.sheetPOI jiras_sheet = com.ttv.at.office.appPOI.get_instance().get_sheets().get(2);
				
				// ****** get file name ******
				String html_full_path = excel_file_full_path + ".html";
				
				// ****** delete old file ******
				File html_file = new File (html_full_path);
				if (html_file.exists())
					html_file.delete();
				
				StringBuilder html_content = new StringBuilder();
				
				// ****** start add new html file ******
				
				// Get HistoryColumnCount
				int HistoryColumnCount = -1;
				String HCC_text = cur_testsuite.get_dataset().get_string_value("History.ColumnCount");
				if (HCC_text != null && HCC_text.length() >0)
					try {
						HistoryColumnCount = Integer.parseInt(HCC_text);
					}
					catch (Exception e) {}
				
				// Create file header
				/*html_content.append("<HTML>\n" + 
									"<HEAD><style>\n" + 
									".datetime{\n" + 
									"	color:Green;\n" + 
									"	font-size: 8pt\n" + 
									"}\n" + 
									".testcasepassed{\n" + 
									"	color:Blue;\n" + 
									"	font-size: 10pt\n" + 
									"}\n" + 
									".testcasefailed{\n" + 
									"	color:Red;\n" + 
									"	font-size: 10pt\n" + 
									"}\n" + 
									" .notrun{\n" + 
									"	color:magenta;\n" + 
									"	font-size: 10pt\n" + 
									"}\n" + 
									".summanypassed{\n" + 
									"	color:Blue;\n" + 
									"}\n" + 
									".summanyfailed{\n" + 
									"	color:Red;\n" + 
									"}\n" + 
									".summanynotrun{\n" + 
									"	color:magenta;\n" + 
									"}</style>\n" + 
									"</HEAD>\n" + 
									" <BODY>\n");*/
				html_content.append("<HTML>\n" + 
						"<HEAD>\n" + 
						"</HEAD>\n" + 
						" <BODY>\n");
				
				
				// Create line for test suite name
				html_content.append("<h2 style=\"text-align: left\"> " + cur_testsuite.get_tc_set().get_name() + " </h2>\n\n");
				
				// Scan to get all header
				html_content.append("<table border=\"1\">\n<tr>\n");
				


				int tests_col = -1;
				for (int iCol = 0 ; iCol < summary_sheet.get_max_col(6) ; iCol ++) {
					String col_text = summary_sheet.getCellText(iCol , 6);
					if (col_text != null && col_text.length() > 0) {
						if (!col_text.toLowerCase().equals("description")) {
							String col_link = links_sheet.getCellText(iCol , 6);
							if (HistoryColumnCount < 0 || iCol < HistoryColumnCount + 2) {
								tests_col = iCol;
							}
						}
					}
					else
						break;
				}
				
				// Write summary
				html_content.append("<tr>\n    <td colspan = \""+tests_col + 1+"\"></td>\n</tr>\n");
				
				// Write Passed
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right; color: Blue;\"><strong>Passed</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String number_of_passed_text = summary_sheet.getCellText(iCol , 0);
					if (number_of_passed_text != null && number_of_passed_text.length() > 0) {
						try {
							double number_of_passed = Double.parseDouble(number_of_passed_text);
							html_content.append("	<td style=\"text-align: center; color: Blue;\"><strong>"+(long)number_of_passed+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center; color: Blue;\"><strong>"+number_of_passed_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				// Write Failed
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right; color: Red;\"><strong>Failed</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String number_of_failed_text = summary_sheet.getCellText(iCol , 1);
					if (number_of_failed_text != null && number_of_failed_text.length() > 0) {
						try {
							double number_of_failed = Double.parseDouble(number_of_failed_text);
							html_content.append("	<td style=\"text-align: center; color: Red;\"><strong>"+(long)number_of_failed+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center; color: Red;\"><strong>"+number_of_failed_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				// Write not run
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right; color: magenta;\"><strong>Not Run</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String number_of_notrun_text = summary_sheet.getCellText(iCol , 2);
					if (number_of_notrun_text != null && number_of_notrun_text.length() > 0) {
						try {
							double number_of_notrun = Double.parseDouble(number_of_notrun_text);
							html_content.append("	<td style=\"text-align: center; color: magenta;\"><strong>"+(long)number_of_notrun+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center; color: magenta;\"><strong>"+number_of_notrun_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				// Write Total
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right\"><strong>Total</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String number_of_total_text = summary_sheet.getCellText(iCol , 3);
					if (number_of_total_text != null && number_of_total_text.length() > 0) {
						try {
							double number_of_total = Double.parseDouble(number_of_total_text);
							html_content.append("	<td style=\"text-align: center\"><strong>"+(long)number_of_total+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center\"><strong>"+number_of_total_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				// Write %Passed
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right; color: Blue;\"><strong> %Passed</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String perent_of_passed_text = summary_sheet.getCellText(iCol , 4);
					if (perent_of_passed_text != null && perent_of_passed_text.length() > 0) {
						try {
							Double percen_of_passed_value = summary_sheet.getCellNumber(iCol , 4)*100;
							DecimalFormat df = new DecimalFormat("#.##");
							String percen_of_passed = df.format(percen_of_passed_value) + "%";
							html_content.append("	<td style=\"text-align: center; color: Blue;\"><strong>"+percen_of_passed+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center; color: Blue;\"><strong>"+perent_of_passed_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				// Write %Failed
				html_content.append("<tr>\n");
				html_content.append("	<td colspan = \"2\" style=\"text-align: right; color: Red;\"><strong> %Failed</strong></td>\n");
				for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
					String perent_of_failed_text = summary_sheet.getCellText(iCol , 5);
					if (perent_of_failed_text != null && perent_of_failed_text.length() > 0) {
						try {
							double perent_of_failed_value = summary_sheet.getCellNumber(iCol , 5)*100;
							DecimalFormat df = new DecimalFormat("#.##");
							String perent_of_failed = df.format(perent_of_failed_value) + "%";
							html_content.append("	<td style=\"text-align: center; color: Red;\"><strong>"+perent_of_failed+"</strong></td>\n");
						}
						catch (Exception e) {
							html_content.append("	<td style=\"text-align: center; color: Red;\"><strong>"+perent_of_failed_text+"</strong></td>\n");
						}
					}
					else
						break;
				}
				html_content.append("</tr>\n");
				
				
				int description_col = -1;
				for (int iCol = 0 ; iCol < summary_sheet.get_max_col(6) ; iCol ++) {
					String col_text = summary_sheet.getCellText(iCol , 6);
					if (col_text != null && col_text.length() > 0) {
						if (col_text.toLowerCase().equals("description")) {
							description_col = iCol; // Description is last col

							html_content.append("	<td bgcolor=\"#C0C0C0\"><b>" + col_text + "</b></td>\n");
						}
						else {
							String col_link = links_sheet.getCellText(iCol , 6);
							if (HistoryColumnCount < 0 || iCol < HistoryColumnCount + 2) {
								if (col_link != null && col_link.length() > 0)
									html_content.append("	<td bgcolor=\"#C0C0C0\"><b><a href=\"" + col_link + "\" target=\"_blank\">" + col_text + "</a></b></td>\n");
								else
									html_content.append("	<td bgcolor=\"#C0C0C0\"><b>" + col_text + "</b></td>\n");
								tests_col = iCol;
							}
						}
					}
					else
						break;
				}
				
				if (HistoryColumnCount < 0 || HistoryColumnCount > description_col - 2)
					HistoryColumnCount = description_col - 2;
				
				// Create content row
				for (int iRow = 7 ; iRow <= summary_sheet.get_max_row() ; iRow ++) {
					// Find start point of test area
					String area_name = summary_sheet.getCellText(0 , iRow);
					if (area_name != null && area_name.length() > 0) { // Found a area
						// Write test area
						html_content.append("<tr>\n" + 
											"	<td colspan=\"" + description_col + "\">\n" + 
											"<h4 style=\"text-align: left\">" + area_name + "</h4>\n" +
											"	</td>\n" +
											"</tr>\n");
						
						// scan all tests of area
						for (int testRow = iRow + 1 ; testRow <= summary_sheet.get_max_row() ; testRow ++) {
							String test_no = summary_sheet.getCellText(0 , testRow);
							String test_name = summary_sheet.getCellText(1 , testRow);
							if (test_no != null && test_no.length() > 0 &&
									test_name != null && test_name.length() > 0) {
								iRow = testRow;
								
								// Write test no
								html_content.append("<tr>\n" +
										"	<td style=\"color: Green; font-size: 8pt;\">" + test_no + "</td>\n");
								// Write test name
								html_content.append("	<td>" + test_name + "</td>");
								
								// Write result
								for (int iCol = 2 ; iCol < (HistoryColumnCount + 2) ; iCol ++) {
									String result_text =  summary_sheet.getCellText(iCol , testRow);
									if (result_text == null || result_text.length() == 0)
										html_content.append("	<td/>\n");
									else {

										String test_result_link = links_sheet.getCellText(iCol , testRow);
										
										if (result_text.toLowerCase().equals("passed")) {
											if (test_result_link != null && test_result_link.length() > 0)
												html_content.append("	<td style=\"color: Blue; font-size: 10pt;\"><a href=\""+test_result_link+"\" target=\"_blank\"><strong>Passed<strong></a></td>\n");
											else
												html_content.append("	<td style=\"color: Blue; font-size: 10pt;\">Passed</td>\n");
										}
										else if (result_text.toLowerCase().equals("failed")) {
											String log_text = null;
											if (test_result_link != null && test_result_link.length() > 0)
												log_text = "	<td><a style=\"color: Red; font-size: 10pt;\" href=\""+test_result_link+"\" target=\"_blank\"><strong>Failed<strong></a>";
												// html_content.append("	<td style=\"color: Red; font-size: 10pt;\"><a href=\""+test_result_link+"\" target=\"_blank\"><strong>Failed<strong></a></td>\n");
											else
												log_text = "	<td style=\"color: Red; font-size: 10pt;\">Failed";
												// html_content.append("	<td style=\"color: Red; font-size: 10pt;\">Failed</td>\n");

											String jira_id = jiras_sheet.getCellText(iCol , testRow);
											if (jira_id != null && jira_id.length() > 0)
												log_text = log_text + "<a  style=\"color: magenta; font-size: 10pt;\" href=\"https://redmine.cloud9-solutions.com/browse/"+jira_id+"\" target=\"_blank\"><strong> ("+jira_id+") <strong></a>";
											
											log_text = log_text + "</td>\n";
											html_content.append(log_text);
										}
										else if (result_text.toLowerCase().equals("not_run")) {
											if (test_result_link != null && test_result_link.length() > 0)
												html_content.append("	<td style=\"color: magenta; font-size: 10pt;\"><a href=\""+test_result_link+"\" target=\"_blank\"><strong>Not Run<strong></a></td>\n");
											else
												html_content.append("	<td style=\"color: magenta; font-size: 10pt;\">Not Run</td>\n");
										}
										else {
											if (test_result_link != null && test_result_link.length() > 0)
												html_content.append("	<td><a href=\""+test_result_link+"\" target=\"_blank\"><strong>"+result_text+"<strong></a></td>\n");
											else
												html_content.append("	<td>"+result_text+"</td>\n");
										}
									}
								}
								// Write Description
								String description_text = summary_sheet.getCellText(description_col, testRow);
								if (description_text != null && description_text.length() > 0)
									html_content.append("	<td>" + description_text + "</td>\n</tr>");
								else
									html_content.append("	<td></td>\n</tr>");
							}
							else
								break;
						}
					}
				}
				
				// Close file
				html_content.append("</table>\n</BODY>\n</HTML>");
				com.ttv.at.log.log.File.write_line(html_content.toString(), html_full_path);

				// Send html content to reporter and tester email
				// get Report emails
				String report_emails = null;
				String tester_emails = null;
				if (cur_testsuite != null) {
					report_emails = cur_testsuite.get_dataset().get_string_value("report.email");
					tester_emails = cur_testsuite.get_dataset().get_string_value("tester.email");
				}
				if (report_emails != null && report_emails.length() > 0)
					send_email (com.ttv.at.test.testsetting.get_host_name() + " - Result summary of " + cur_testsuite.get_tc_set().get_name(), html_content.toString(), report_emails);
				if (tester_emails != null && tester_emails.length() > 0)
					send_email (com.ttv.at.test.testsetting.get_host_name() + " - Result summary of " + cur_testsuite.get_tc_set().get_name(), html_content.toString(), tester_emails);
				
				return html_full_path;
			}
		}
		
		return null;
	}
	
	static public String export_excel_history (com.ttv.at.test.testsuite cur_testsuite) {
		if (cur_testsuite != null && cur_testsuite.get_tc_set().get_name() != null) {
			// Create excel_path
			String excel_path = com.ttv.at.test.testsetting.get_root_log_folder() + "\\" + cur_testsuite.get_tc_set().get_name().replace("\\", "_").replace("/", "_") + ".xls";
		}
		return null;
	}

	static public String email_start (com.ttv.at.test.testsuite cur_suite) {

		// ******** ******** ******** send email ******** ******** ********
		// create subject
		String subject = com.ttv.at.test.testsetting.get_host_name() + " - Start Testsuite ";
		String header = "Start Testsuite ";
		
		subject = subject + "\"" + cur_suite.get_name() + "\"";
		header = header + "\"" + cur_suite.get_name() + "\"";
		
		StringBuilder config_data = null;
		for (int i = 0 ; i < cur_suite.get_dataset().get_params().size() && i < 5 ; i ++) {
			if (config_data == null)
				config_data = new StringBuilder("\n - " + cur_suite.get_dataset().get_params().get(i).get_key() + ": " + cur_suite.get_dataset().get_params().get(i).get_value());
			else {
				config_data.append("\n - ");
				config_data.append(cur_suite.get_dataset().get_params().get(i).get_key() + ": " + cur_suite.get_dataset().get_params().get(i).get_value());
			}
		}
		
		// Add more Report.Fields
		String Report_Fields = cur_suite.get_dataset().get_string_value("Report.Fields");
		if (Report_Fields != null && Report_Fields.length() > 0) {
			String[] fields = Report_Fields.split("\n");
			for (String field:fields)
				if (field != null && field.length() > 0){
					String field_value = cur_suite.get_dataset().get_string_value(field);
					if (field_value != null && field_value.length() > 0) {
						config_data.append("\n - ");
						config_data.append(field + ": " + field_value);
					}
				}
		}
		
		// START CREATE CONTENT
		StringBuilder content = new StringBuilder (header);

		// --- host name
		if (com.ttv.at.test.testsetting.get_host_name() != null) {
			content.append("\n - Host Name: ");
			content.append(com.ttv.at.test.testsetting.get_host_name());
		}
		if (com.ttv.at.test.testsetting.get_host_ip() != null) {
			content.append("\n - Host IP: ");
			content.append(com.ttv.at.test.testsetting.get_host_ip());
		}
		
		// --- start time
		if (cur_suite.get_startTime() != null) {
			content.append("\n - Start time: ");
			content.append(cur_suite.get_startTime().toString());
		}
		
		
		// --- configuration
		if (config_data != null) {
			content.append("\n\nConfiguration:");
			content.append(config_data);
		}
		
		// get Report emails
		String report_emails = null;
		String tester_emails = null;
		if (cur_suite != null) {
			report_emails = cur_suite.get_dataset().get_string_value("report.email");
			tester_emails = cur_suite.get_dataset().get_string_value("tester.email");
		}
		
		// if (report_emails != null && report_emails.length() > 0)
		// 	send_email (subject, content.toString(), report_emails);
		if (tester_emails != null && tester_emails.length() > 0)
			send_email (subject, content.toString(), tester_emails);
		return content.toString();
	}

	static public String email_summary (com.ttv.at.test.testsuite cur_suite, int total_passed, int total_failed) {

		// ******** ******** ******** send email ******** ******** ********
		// create subject
		String subject = com.ttv.at.test.testsetting.get_host_name() + " - Complete Testsuite ";
		String header = "Complete Testsuite ";
		
		subject = subject + "\"" + cur_suite.get_name() + "\"";
		header = header + "\"" + cur_suite.get_name() + "\"";
		
		subject = subject + " (Passed of Test suite: " + total_passed + ", Failed of Test suite: " + total_failed + ")";
		header = header + 
					"\n - Total of Test suite: " + (total_passed + total_failed) + 
					"\n - Passed of Test suite: " + total_passed + 
					"\n - Failed of Test suite: " + total_failed;
		
		StringBuilder config_data = null;
		for (int i = 0 ; i < cur_suite.get_dataset().get_params().size() && i < 5 ; i ++) {
			if (config_data == null)
				config_data = new StringBuilder("\n - " + cur_suite.get_dataset().get_params().get(i).get_key() + ": " + cur_suite.get_dataset().get_params().get(i).get_value());
			else {
				config_data.append("\n - ");
				config_data.append(cur_suite.get_dataset().get_params().get(i).get_key() + ": " + cur_suite.get_dataset().get_params().get(i).get_value());
			}
		}

		
		// Add more Report.Fields
		String Report_Fields = cur_suite.get_dataset().get_string_value("Report.Fields");
		if (Report_Fields != null && Report_Fields.length() > 0) {
			String[] fields = Report_Fields.split("\n");
			for (String field:fields)
				if (field != null && field.length() > 0){
					String field_value = cur_suite.get_dataset().get_string_value(field);
					if (field_value != null && field_value.length() > 0) {
						config_data.append("\n - ");
						config_data.append(field + ": " + field_value);
					}
				}
		}
		
		StringBuilder passed_tests = null;
		StringBuilder failed_tests = null;
		// create content
		for (com.ttv.at.test.testarea scan_area : cur_suite.get_testareas())
			for (com.ttv.at.test.test scan_test : scan_area.get_tests())
				if (scan_test.get_run_status() == status_run.PASSED)
					if (passed_tests == null)
						passed_tests = new StringBuilder(" - " + scan_test.get_tc_instance().get_name());
					else {
						passed_tests.append("\n - ");
						passed_tests.append(scan_test.get_tc_instance().get_name());
					}
				else if (scan_test.get_run_status() == status_run.FAILED)
					if (failed_tests == null)
						failed_tests = new StringBuilder(" - " + scan_test.get_tc_instance().get_name());
					else {
						failed_tests.append("\n - ");
						failed_tests.append(scan_test.get_tc_instance().get_name());
					}
		
		// START CREATE CONTENT
		StringBuilder content = new StringBuilder (header);

		// --- host name
		if (com.ttv.at.test.testsetting.get_host_name() != null) {
			content.append("\n - Host Name: ");
			content.append(com.ttv.at.test.testsetting.get_host_name());
		}
		if (com.ttv.at.test.testsetting.get_host_ip() != null) {
			content.append("\n - Host IP: ");
			content.append(com.ttv.at.test.testsetting.get_host_ip());
		}
		
		// --- start time
		if (cur_suite.get_startTime() != null) {
			content.append("\n - Start time: ");
			content.append(cur_suite.get_startTime().toString());
			content.append("\n - End time: ");
			content.append((new Date()).toString());
		}
		

		content.append("\n - Log folder path: " + com.ttv.at.test.testsetting.get_default_log_folder());
		
		// --- configuration
		if (config_data != null) {
			content.append("\n\nConfiguration:");
			content.append(config_data);
		}
		
		if (passed_tests != null) {
			content.append("\n\nPASSED TESTS:\n");
			content.append(passed_tests);
		}
		if (failed_tests != null) {
			content.append("\n\nFAILED TESTS:\n");
			content.append(failed_tests);
		}
		
		

		// get Report emails
		String report_emails = null;
		String tester_emails = null;
		if (cur_suite != null) {
			report_emails = cur_suite.get_dataset().get_string_value("report.email");
			tester_emails = cur_suite.get_dataset().get_string_value("tester.email");
		}
		
		// if (report_emails != null && report_emails.length() > 0)
		// 	send_email (subject, content.toString(), report_emails);
		if (tester_emails != null && tester_emails.length() > 0)
			send_email (subject, content.toString(), tester_emails);
		return content.toString();
	}

	static public void send_email (String subject, String content, String receipts) {
		if (receipts != null && receipts.length() > 0) {
			sendMail sendemail = new sendMail(receipts, subject, content);
			sendemail.start();
		}
	}
}

class testsuite_report {
	String name;
	public String get_name() {return name;}
	
	com.ttv.at.test.testsuite cur_testsuite = null;
	
	boolean use_mySQL = false;
	boolean use_MSSQL = false;
	String dbURL = null;
	String ReportDBUsername = null;
	String ReportDBPassword = null;
	String summary = null;
	
	public testsuite_report (com.ttv.at.test.testsuite cur_testsuite, boolean use_mySQL, boolean use_MSSQL, String dbURL, String ReportDBUsername, String ReportDBPassword, String summary) {
		this.cur_testsuite = cur_testsuite;
		name = cur_testsuite.get_name();
		this.use_mySQL = use_mySQL;
		this.use_MSSQL = use_MSSQL;
		this.dbURL = dbURL;
		this.ReportDBUsername = ReportDBUsername;
		this.ReportDBPassword = ReportDBPassword;
		this.summary = summary;
	}
	
	int existing_testsuite_id = -1;
	ArrayList<Integer> existing_test_ids = null;
	
	public boolean update_DB () {
		try {
			if (use_MSSQL)
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Connection conn = (Connection) DriverManager.getConnection(dbURL, ReportDBUsername, ReportDBPassword);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String query_for_testsuite = "INSERT INTO test_suite(startTime, endTime) values (?, ?)";
			if (summary != null && summary.length() > 0)
				query_for_testsuite = "INSERT INTO test_suite(startTime, endTime, summary) values (?, ?, ?)";
			PreparedStatement add_test_suite = conn.prepareStatement(query_for_testsuite, Statement.RETURN_GENERATED_KEYS);
			add_test_suite.setTimestamp(1, new java.sql.Timestamp(cur_testsuite.get_startTime().getTime()));
			add_test_suite.setTimestamp(2, new java.sql.Timestamp(cur_testsuite.get_endTime().getTime()));
			if (summary != null && summary.length() > 0)
				add_test_suite.setString(3, summary);
			int rows = add_test_suite.executeUpdate();
			ResultSet rs = add_test_suite.getGeneratedKeys();
			// ResultSet rs =  stmt.getGeneratedKeys();
			if (rs.next()) {
				existing_testsuite_id = rs.getInt(1);
				
				// Update for test suite properties
				for (com.ttv.at.test.parameter prop:cur_testsuite.get_dataset().get_params()) {
					String query_for_property = "INSERT INTO test_suite_property(test_suite_id, name, value) values (?, ?, ?)";
					PreparedStatement add_test_suite_property = conn.prepareStatement(query_for_property, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_UPDATABLE);
					add_test_suite_property.setInt(1, existing_testsuite_id);
					add_test_suite_property.setString(2, prop.get_key());
					add_test_suite_property.setString(3, prop.get_value());
					add_test_suite_property.executeUpdate();
				}
				if (existing_test_ids == null)
					existing_test_ids = new ArrayList<Integer>();
				else
					existing_test_ids.clear();
				
				// Update for tests
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				for (int i = 0 ; i < testareas.size() ; i++) {
					String category_name = testareas.get(i).get_name();
					ArrayList<com.ttv.at.test.test> tests = testareas.get(i).get_tests();
					for (int j = 0 ; j < tests.size() ; j ++) {
						com.ttv.at.test.test cur_test = tests.get(j);
						if (cur_test.get_run_status() == com.ttv.at.test.status_run.PASSED ||
								cur_test.get_run_status() == com.ttv.at.test.status_run.FAILED) {
							String query_for_failed_test = "INSERT INTO test(test_suite_id, name, result, found_by_manual, severity, description, category) values (?, ?, ?, ?, ?, ?, ?)";
							PreparedStatement add_test = conn.prepareStatement(query_for_failed_test, Statement.RETURN_GENERATED_KEYS);
							add_test.setInt(1, existing_testsuite_id);
							add_test.setString(2, cur_test.get_tc_instance().get_name());
							if (cur_test.get_run_status() == com.ttv.at.test.status_run.PASSED)
								add_test.setString(3, "PASSED");
							else if (cur_test.get_run_status() == com.ttv.at.test.status_run.FAILED)
								add_test.setString(3, "FAILED");
							
							if (cur_test.get_found_by_manual())
								add_test.setInt(4, 1);
							else
								add_test.setInt(4, 0);
							add_test.setString(5, cur_test.get_severity().toString());
							String Description = cur_test.get_message();
							if (Description != null && Description.length() > 0)
								add_test.setString(6, Description);
							else
								add_test.setString(6, "");
							add_test.setString(7, category_name);
							rows = add_test.executeUpdate();
							
							ResultSet add_test_result = add_test.getGeneratedKeys();
							
							
							// Store the test id
							if (add_test_result.next()) {
								int test_id = add_test_result.getInt(1);
								existing_test_ids.add(test_id);
							}
							else
								existing_test_ids.add(-1);
						}
						else
							existing_test_ids.add(-1);
					}
				}
				return true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean update_DB (com.ttv.at.test.testsuite cur_testsuite) {
		this.cur_testsuite = cur_testsuite;
		if (existing_testsuite_id < 0)
			return update_DB ();
		else
			try {
				
				if (use_MSSQL)
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				Connection conn = (Connection) DriverManager.getConnection(dbURL, ReportDBUsername, ReportDBPassword);
				
				// Update for tests
				int test_index = 0;
				ArrayList<com.ttv.at.test.testarea> testareas = cur_testsuite.get_testareas();
				for (int i = 0 ; i < testareas.size() ; i++) {
					String category_name = testareas.get(i).get_name();
					ArrayList<com.ttv.at.test.test> tests = testareas.get(i).get_tests();
					for (int j = 0 ; j < tests.size() ; j ++) {
						com.ttv.at.test.test cur_test = tests.get(j);
						if (cur_test.get_run_status() == com.ttv.at.test.status_run.PASSED ||
								cur_test.get_run_status() == com.ttv.at.test.status_run.FAILED) {
							if (existing_test_ids.get(test_index) >= 0) {
								String query_to_update_test = "UPDATE test Set found_by_manual = ?, severity = ?, result = ? WHERE id = ?";
								PreparedStatement update_test_statement = conn.prepareStatement(query_to_update_test, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_UPDATABLE);
								
								int xxxx= 10;
								// Set found by Manual
								if (cur_test.get_found_by_manual())
									update_test_statement.setInt(1, 1);
								else
									update_test_statement.setInt(1, 0);
								
								// Set severity
								update_test_statement.setString(2, cur_test.get_severity().toString());
								
								// Set result
								if (cur_test.get_run_status() == com.ttv.at.test.status_run.PASSED)
									update_test_statement.setString(3, "PASSED");
								else if (cur_test.get_run_status() == com.ttv.at.test.status_run.FAILED)
									update_test_statement.setString(3, "FAILED");
								
								// Set id
								update_test_statement.setInt(4, existing_test_ids.get(test_index));
								
								// execute
								update_test_statement.executeUpdate();
							}
							else {
								String query_for_failed_test = "INSERT INTO test(test_suite_id, name, result, found_by_manual, severity, description, category) values (?, ?, ?, ?, ?, ?, ?)";
								PreparedStatement add_test = conn.prepareStatement(query_for_failed_test, Statement.RETURN_GENERATED_KEYS);
								add_test.setInt(1, existing_testsuite_id);
								add_test.setString(2, cur_test.get_tc_instance().get_name());
								if (cur_test.get_run_status() == com.ttv.at.test.status_run.PASSED)
									add_test.setString(3, "PASSED");
								else if (cur_test.get_run_status() == com.ttv.at.test.status_run.FAILED)
									add_test.setString(3, "FAILED");
								
								if (cur_test.get_found_by_manual())
									add_test.setInt(4, 1);
								else
									add_test.setInt(4, 0);
								add_test.setString(5, cur_test.get_severity().toString());
								String Description = cur_test.get_message();
								if (Description != null && Description.length() > 0)
									add_test.setString(6, Description);
								else
									add_test.setString(6, "");
								add_test.setString(7, category_name);
								int rows = add_test.executeUpdate();
								
								ResultSet add_test_result = add_test.getGeneratedKeys();
								
	
								// Store the test id
								if (add_test_result.next()) {
									int test_id = add_test_result.getInt(1);
									existing_test_ids.set(test_index, test_id);
								}
							}
						}
	
						test_index ++;
					}
				}
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}
}




