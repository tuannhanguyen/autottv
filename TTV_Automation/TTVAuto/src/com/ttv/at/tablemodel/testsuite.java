package com.ttv.at.tablemodel;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.table.AbstractTableModel;

public class testsuite extends AbstractTableModel {
	
	static private testsuite instance;
	static public testsuite get_instance() {
		if (instance == null)
			instance = new testsuite(null);
		return instance;
	}
	static public void set_instance_data(com.ttv.at.test.testsuite loaded_testsuite) {
		if (instance == null)
			instance = new testsuite(loaded_testsuite);
		else
			instance.set_loaded_testsuite(loaded_testsuite);
	}
	static public void update_instance_detail() {
		if (instance != null)
			instance.update_detail();
	}
	
	Hashtable<Integer, ArrayList<Object>> data = new Hashtable<Integer, ArrayList<Object>>();
	static final int COLUMN_COUNT = 7;
	static final String[] columnNames = {"No", "RUN", "Test Area", "Total", "Passed", "Failed", "Selected"};
	static public final int COLUMN_NO = 0;
	static public final int COLUMN_RUN = 1;
	static public final int COLUMN_TESTAREA = 2;
	static public final int COLUMN_TOTAL = 3;
	static public final int COLUMN_PASSED = 4;
	static public final int COLUMN_FAILED = 5;
	static public final int COLUMN_SELECTED = 6;

	com.ttv.at.test.testsuite loaded_testsuite;
	public com.ttv.at.test.testsuite get_loaded_testsuite() { return loaded_testsuite;}
	public void set_loaded_testsuite(com.ttv.at.test.testsuite loaded_testsuite) {
		clear();
		this.loaded_testsuite = loaded_testsuite;
		
		if (loaded_testsuite != null) {
			// Loading data from loaded_testsuite
			ArrayList<com.ttv.at.test.testarea> testareas = loaded_testsuite.get_testareas();
			if (testareas != null)
				for (int index = 0 ; index < testareas.size() ; index ++) {
					ArrayList<Object> values = new ArrayList<Object>();
					com.ttv.at.test.testarea cur_ta = testareas.get(index);
					values.add(index + 1);
					if (cur_ta.get_total_selected() > 0)
						values.add(true);
					else
						values.add(false);
					values.add(cur_ta.get_name());
					values.add(cur_ta.get_total());
					values.add(cur_ta.get_total_passed());
					values.add(cur_ta.get_total_failed());
					values.add(cur_ta.get_total_selected());
					insertRow(values);
				}
		}
	}
	
	public void update_detail() {
		if (loaded_testsuite != null) {
			ArrayList<com.ttv.at.test.testarea> testareas = loaded_testsuite.get_testareas();
			for (int index = 0 ; index < testareas.size() ; index ++) {
				com.ttv.at.test.testarea cur_ta = testareas.get(index);
				// Column 0: No
				// Column 1: Selection
				if (cur_ta.get_total_selected() > 0)
					setValueAt(true, index, COLUMN_RUN);
				else
					setValueAt(false, index, COLUMN_RUN);
				
				// Column 2: Name
				// Column 3: Total
				// Column 4: Passed
				setValueAt(cur_ta.get_total_passed(), index, COLUMN_PASSED);
				// Column 5: Failed
				setValueAt(cur_ta.get_total_failed(), index, COLUMN_FAILED);
				// Column 6: Not run
				setValueAt(cur_ta.get_total_selected(), index, COLUMN_SELECTED);
			}
		}
	}
	
	public testsuite(com.ttv.at.test.testsuite loaded_testsuite){
		set_loaded_testsuite(loaded_testsuite);
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

	
	public void clear () {
		data.clear();
		this.loaded_testsuite = null;
		fireTableDataChanged();
	}
	
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
		return column == 1;
	}

	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if ((value != null) &&
			(row >= 0) && (row < data.size()) &&
			(column >= 0) && (column < 8))
		{
			data.get(row).set(column, value);
			fireTableRowsUpdated(row, row);
		}
	}

	public void insertRow(ArrayList<Object> values)
	{
		int iRowCount = getRowCount();
		data.put(iRowCount, values);
		fireTableDataChanged();
	}

	public void uncheckedAtRow(int row) {
		if (loaded_testsuite != null && 
				row >= 0 && 
				row < loaded_testsuite.get_testareas().size()) {
			com.ttv.at.test.testarea cur_ta = loaded_testsuite.get_testareas().get(row);
			cur_ta.deselect_all_tests();
		}
	}
	public void checkedAtRow(int row) {
		if (loaded_testsuite != null && 
				row >= 0 && 
				row < loaded_testsuite.get_testareas().size()) {
			com.ttv.at.test.testarea cur_ta = loaded_testsuite.get_testareas().get(row);
			cur_ta.select_all_tests();
			update_detail();
		}
	}
}
