package com.ttv.at.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class testdataset extends AbstractTableModel {
	static private testdataset instance;
	static ImageIcon refresh_icon = new ImageIcon("images\\refresh_16x16.png");

	static public testdataset get_instance () {
		if (instance == null)
			instance = new testdataset();
		return instance;
	}

	Hashtable<Integer, ArrayList<Object>> data = new Hashtable<Integer, ArrayList<Object>>();
	static final int COLUMN_COUNT = 6;
	static final String[] columnNames = {"No", "RUN", ":|:", "Schedule", "Name", "Status"};
	static public final int COLUMN_NO = 0;
	static public final int COLUMN_RUN = 1;
	static public final int COLUMN_REFRESH = 2;
	static public final int COLUMN_SCHEDULE = 3;
	static public final int COLUMN_NAME = 4;
	static public final int COLUMN_STATUS = 5;

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return COLUMN_COUNT;
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}
	@Override
	public Class getColumnClass(int column)
	{
		return data.get(0).get(column).getClass();
	}

	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
	public void clear () {
		data.clear();
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}
	public void setValueAt(Object value, int row, int column)
	{
		if ((value != null) &&
			(row >= 0) && (row < data.size()) &&
			(column >= 0) && (column < COLUMN_COUNT))
		{
			data.get(row).set(column, value);
			fireTableDataChanged();
		}
	}
	public void insertRow(ArrayList<Object> values)
	{
		int iRowCount = getRowCount();
		data.put(iRowCount, values);
		fireTableDataChanged();
	}

	com.ttv.at.test.testsuiteset loaded_testsuiteset;
	public com.ttv.at.test.testsuiteset get_loaded_testsuiteset() { return loaded_testsuiteset;}
	public void set_loaded_testsuiteset (com.ttv.at.test.testsuiteset loaded_testsuiteset) {
		clear();
		this.loaded_testsuiteset = loaded_testsuiteset;
		if (loaded_testsuiteset != null)
			reload_loaded_testsuiteset();
	}

	public testdataset () {
		
	}
	public testdataset (com.ttv.at.test.testsuiteset loaded_testsuiteset) {
		set_loaded_testsuiteset(loaded_testsuiteset);
	}
	private void reload_loaded_testsuiteset() {
		if (loaded_testsuiteset != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.testsuite> testsuites = loaded_testsuiteset.get_testsuites();
			ArrayList<com.ttv.at.test.schedule> schedules = loaded_testsuiteset.get_schedules();
			ArrayList<com.ttv.at.test.status_selection> selections = loaded_testsuiteset.get_selections();
			
			for (int index = 0 ; index < testsuites.size() ; index ++) {
				ArrayList<Object> values = new ArrayList<Object>();
				com.ttv.at.test.testsuite cur_testsuite = testsuites.get(index);
				// Add "No" (column 0)
				values.add(index + 1);
				
				// Add "RUN" (Column 1)
				if (selections.get(index) == com.ttv.at.test.status_selection.SELECTED)
					values.add(true);
				else
					values.add(false);
				
				// Add refresh button (Column 2)
				values.add(refresh_icon);
				
				if (schedules.get(index) == null)
					values.add("Immediately");
				else
					values.add(schedules.get(index).toString());
				
				// Add "Test name" (Column 3)
				values.add(cur_testsuite.get_name());
				
				// Add "Test name" (Column 4)
				values.add("");
				
				insertRow(values);
			}
		}
	}
	
	public void update_detail() {
		if (loaded_testsuiteset != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.testsuite> testsuites = loaded_testsuiteset.get_testsuites();
			ArrayList<com.ttv.at.test.status_selection> selections = loaded_testsuiteset.get_selections();
			if (testsuites.size() == getRowCount()) {
				for (int index = 0 ; index < testsuites.size() ; index ++) {
					// "RUN" (Column 1)
					if (selections.get(index) == com.ttv.at.test.status_selection.SELECTED)
						 setValueAt(true, index, COLUMN_RUN);
					else
						 setValueAt(false, index, COLUMN_RUN);
					
					if (loaded_testsuiteset.get_testsuites().get(index).get_in_running())
						setValueAt("Running", index, COLUMN_STATUS);
					else
						setValueAt("", index, COLUMN_STATUS);
				}
			}
			else {

				for (int index = 0 ; index < getRowCount() ; index ++) {
					// "RUN" (Column 1)
					if (selections.get(index) == com.ttv.at.test.status_selection.SELECTED)
						 setValueAt(true, index, COLUMN_RUN);
					else
						 setValueAt(false, index, COLUMN_RUN);
					
					if (loaded_testsuiteset.get_testsuites().get(index).get_in_running())
						setValueAt("Running", index, COLUMN_STATUS);
					else
						setValueAt("", index, COLUMN_STATUS);
				}
				// Loading data from loaded_testarea
				ArrayList<com.ttv.at.test.schedule> schedules = loaded_testsuiteset.get_schedules();
				
				for (int index = getRowCount() ; index < testsuites.size() ; index ++) {
					ArrayList<Object> values = new ArrayList<Object>();
					com.ttv.at.test.testsuite cur_testsuite = testsuites.get(index);
					// Add "No" (column 0)
					values.add(index + 1);
					
					// Add "RUN" (Column 1)
					if (selections.get(index) == com.ttv.at.test.status_selection.SELECTED)
						values.add(true);
					else
						values.add(false);
					
					// Add refresh button (Column 2)
					values.add(refresh_icon);
					
					// 
					if (schedules.get(index) == null)
						values.add("Immediately");
					else
						values.add(schedules.get(index).toString());
					
					// Add "Test name" (Column 4)
					values.add(cur_testsuite.get_name());
					
					// Add "Test name" (Column 5)
					values.add("");
					
					insertRow(values);
				}
			}
		}
	}
	
	public void toggle_test_selection(int row) {
		if (loaded_testsuiteset != null && row >= 0 && row < loaded_testsuiteset.get_testsuites().size()) {
			loaded_testsuiteset.toggle_selection(row);
			update_detail();
		}
	}
}
