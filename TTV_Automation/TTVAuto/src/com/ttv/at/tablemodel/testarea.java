package com.ttv.at.tablemodel;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.ttv.at.test.status_run;

public class testarea extends AbstractTableModel {
	
	static private testarea instance;
	static ImageIcon refresh_icon = new ImageIcon("images\\refresh_16x16.png");
	static ImageIcon debug_icon = new ImageIcon("images\\debug_16x16.png");

	static public testarea get_instance() {
		if (instance == null)
			instance = new testarea();
		return instance ;
	}
	static public void set_instance_data(com.ttv.at.test.testarea loaded_testarea) {
		if (instance == null)
			instance = new testarea(loaded_testarea);
		else
			instance.set_loaded_testarea(loaded_testarea);
	}
	static public void update_instance_detail() {
		if (instance != null && instance.get_loaded_testarea() != null)
			instance.update_detail();
	}
	

	Hashtable<Integer, ArrayList<Object>> data = new Hashtable<Integer, ArrayList<Object>>();
	static final int COLUMN_COUNT = 8;
	static final String[] columnNames = {"No", "RUN", ":|:", "Test name", "Result", "Manual", "Severity", "Description"};
	static public final int COLUMN_NO = 0;
	static public final int COLUMN_RUN = 1;
	static public final int COLUMN_DEBUG = 2;
	static public final int COLUMN_TESTNAME = 3;
	static public final int COLUMN_RESULT = 4;
	static public final int COLUMN_FOUNDBYMANUAL = 5;
	static public final int COLUMN_SEVERITY = 6;
	static public final int COLUMN_DESCRIPTION = 7;
	
	com.ttv.at.test.testarea loaded_testarea;
	public com.ttv.at.test.testarea get_loaded_testarea() { return loaded_testarea;}
	public void set_loaded_testarea (com.ttv.at.test.testarea loaded_testarea) {
		clear();
		this.loaded_testarea = loaded_testarea;
		reload_testarea();
	}
	
	public testarea () {
	}
	public testarea (com.ttv.at.test.testarea loaded_testarea) {
		set_loaded_testarea(loaded_testarea);
	}
	
	private void reload_testarea() {
		change_severity = false;
		if (loaded_testarea != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.test> tests = loaded_testarea.get_tests();
			for (int index = 0 ; index < tests.size() ; index ++) {
				ArrayList<Object> values = new ArrayList<Object>();
				com.ttv.at.test.test cur_test = tests.get(index);
				// Add "No" (column 0)
				values.add(index + 1);
				
				// Add "RUN" (Column 1)
				if (cur_test.get_selection_status() == com.ttv.at.test.status_selection.SELECTED ||
						cur_test.get_selection_status() == com.ttv.at.test.status_selection.INIT ||
						(cur_test.get_selection_status() == com.ttv.at.test.status_selection.INIT_1_TIME && 
						cur_test.get_run_status() == status_run.NOT_RUN))
					values.add(true);
				else
					values.add(false);
				
				// Add refresh button (Column 2)
				values.add(debug_icon);
				
				// Add "Test name" (Column 3)
				values.add(cur_test.get_tc_instance().get_name());
				
				// Add "Result" (Column 4)
				values.add(cur_test.get_run_status().toString());
				
				// Add "Manual" (Column 5)
				values.add(cur_test.get_found_by_manual());
				
				// Add "Severity" (Column 6)
				values.add(cur_test.get_severity().toString());
				
				// Add "Description" (Column 7)
				values.add(cur_test.get_description());
				
				insertRow(values);
			}
		}
		change_severity = true;
	}
	public void update_detail() {
		change_severity = false;
		if (loaded_testarea != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.test> tests = loaded_testarea.get_tests();
			int total_PASSED = 0;
			int total_FAILED = 0;;
			for (int index = 0 ; index < tests.size() ; index ++) {
				com.ttv.at.test.test cur_test = tests.get(index);
				// Add "RUN" (Column 1)
				if (cur_test.get_selection_status() == com.ttv.at.test.status_selection.SELECTED ||
						cur_test.get_selection_status() == com.ttv.at.test.status_selection.INIT ||
						(cur_test.get_selection_status() == com.ttv.at.test.status_selection.INIT_1_TIME && 
						cur_test.get_run_status() == status_run.NOT_RUN))
					 setValueAt(true, index, COLUMN_RUN);
				else
					 setValueAt(false, index, COLUMN_RUN);

				// Add "Result" (Column 4)
				setValueAt(cur_test.get_run_status().toString(), index, COLUMN_RESULT);
				
				// Add "Manual" (Column 5)
				setValueAt(cur_test.get_found_by_manual(), index, COLUMN_FOUNDBYMANUAL);
				
				// Add "Severity" (Column 6)
				String Severity_Value = cur_test.get_severity().toString();
				setValueAt(Severity_Value, index, COLUMN_SEVERITY);
				
				// Add "Description" (Column 7)
				setValueAt(cur_test.get_description(), index, COLUMN_DESCRIPTION);
			}
			loaded_testarea.update_test_count();
		}
		change_severity = true;
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}
	

	/******************* modified code *******************/ 
	@Override
	public Class getColumnClass(int column)
	{
		return data.get(0).get(column).getClass();
	}
	
	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == COLUMN_SEVERITY)
			return true;
		return false;// (column == 1 || column == 4 || column == 5);
	}

	boolean change_severity = true;
	public void setValueAt(Object value, int row, int column)
	{
		if ((value != null) &&
			(row >= 0) && (row < data.size()) &&
			(column >= 0) && (column < COLUMN_COUNT))
		{
			data.get(row).set(column, value);
			
			if (column == COLUMN_SEVERITY && value != null && change_severity) {
				String strSeverity = (String)value;
				strSeverity = strSeverity.toLowerCase();
				if (strSeverity.equals("critical")) {
					set_severity_at_row(com.ttv.at.test.test.severity.Critical, row);
				}
				else if (strSeverity.equals("high")) {
					set_severity_at_row(com.ttv.at.test.test.severity.High, row);
				}
				else if (strSeverity.equals("medium")) {
					set_severity_at_row(com.ttv.at.test.test.severity.Medium, row);
				}
				else if (strSeverity.equals("low")) {
					set_severity_at_row(com.ttv.at.test.test.severity.Low, row);
				}
			}
			
			
			fireTableRowsUpdated(row, row);
		}
	}

	public void insertRow(ArrayList<Object> values)
	{
		int iRowCount = getRowCount();
		data.put(iRowCount, values);
		fireTableDataChanged();
	}
	
	public void clear () {
		data.clear();
		fireTableDataChanged();
	}

	public void toggle_test_selection(int row) {
		if (loaded_testarea != null && row >= 0 && row < loaded_testarea.get_tests().size()) {
			loaded_testarea.toggle_selection(row);
			update_detail();
		}
	}
	
	public void toggle_found_by_manual_at_row(int row) {
		if (loaded_testarea != null && row >= 0 && row < loaded_testarea.get_tests().size()) {
			loaded_testarea.toggle_found_by_manual(row);
			update_detail();
		}
	}
	
	public void set_severity_at_row(com.ttv.at.test.test.severity newSeverity, int row) {
		if (loaded_testarea != null && row >= 0 && row < loaded_testarea.get_tests().size()) {
			loaded_testarea.set_severity(newSeverity, row);
		}
	}
	
	public com.ttv.at.test.test get_test_instance_at_row (int row) {
		if (row >= 0 && 
				loaded_testarea != null && 
				loaded_testarea.get_tests().size() > row)
			return loaded_testarea.get_tests().get(row);
		return null;
	}
}
