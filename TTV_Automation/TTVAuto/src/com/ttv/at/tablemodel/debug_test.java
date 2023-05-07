package com.ttv.at.tablemodel;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

import com.ttv.at.log.action;
import com.ttv.at.log.test;
import com.ttv.at.log.testelement;
import com.ttv.at.log.testsuite;
import com.ttv.at.log.testsuiteset;

public class debug_test extends AbstractTableModel {
	
	public debug_test () {
		super();
		com.ttv.at.log.log.get_instance().addlog_event_listener(new com.ttv.at.log.log_event_listener() {
			@Override
			public void newTestSuiteLogOccur(testsuite evt) {
			}
			@Override
			public void endTestSuiteLogOccur(testsuite evt) {
			}
			@Override
			public void newTestLogOccur(test evt) {
			}

			@Override
			public void updateTestLogOccur(test evt) {
			}

			@Override
			public void newTestElementLogOccur(testelement evt) {
			}

			@Override
			public void updateTestElementLogOccur(testelement evt) {
				reload_test();
			}

			@Override
			public void newActionLogOccur(action evt) {
			}

			@Override
			public void updateActionLogOccur(action evt) {
			}
			@Override
			public void newTestSuiteSetLogOccur(testsuiteset evt) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void endTestSuiteSetLogOccur(testsuiteset evt) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	com.ttv.at.test.test loaded_test;
	

	public void set_loaded_test (com.ttv.at.test.test loaded_test) {
		this.loaded_test = loaded_test;
		data.clear();
		reload_test();
	}
	void reload_test() {
		clear();
		if (loaded_test != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.testelement> cur_elements = loaded_test.get_tc_instance().get_elements();
			for (int index = 0 ; (cur_elements != null) && index < cur_elements.size() ; index ++) {
				ArrayList<Object> values = new ArrayList<Object>();
				// Add number
				values.add(index);
				
				// Add RUN
				values.add(cur_elements.get(index).get_debug_run());
				
				// Add "Name"
				values.add(cur_elements.get(index).get_instance_name());
				
				// Add "RunType"
				values.add(cur_elements.get(index).get_type());
				
				insertRow(values);
			}
		}
	}
	
	public com.ttv.at.test.testelement get_testelement (int index) {

		if (index >= 0 &&
				loaded_test != null &&
				loaded_test.get_tc_instance() != null &&
				loaded_test.get_tc_instance().get_elements() != null &&
				loaded_test.get_tc_instance().get_elements().size() > index)
			return loaded_test.get_tc_instance().get_elements().get(index);
		return null;
	}
	
	public void set_all_run () {
		clear();
		if (loaded_test != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.testelement> cur_elements = loaded_test.get_tc_instance().get_elements();
			for (int index = 0 ; (cur_elements != null) && index < cur_elements.size() ; index ++) {
				ArrayList<Object> values = new ArrayList<Object>();
				// Add number
				values.add(index);
				
				// Add RUN
				cur_elements.get(index).set_debug_run(true);
				values.add(cur_elements.get(index).get_debug_run());
				
				// Add "Name"
				values.add(cur_elements.get(index).get_instance_name());
				
				// Add "RunType"
				values.add(cur_elements.get(index).get_type());
				
				insertRow(values);
			}
		}
	}
	
	
	
	Hashtable<Integer, ArrayList<Object>> data = new Hashtable<Integer, ArrayList<Object>>();
	static final String[] columnNames = {"No", "RUN", "Name", "Run Type"};
	static final int COLUMN_COUNT = 4;
	static public final int COLUMN_NO = 0;
	static public final int COLUMN_RUN = 1;
	static public final int COLUMN_NAME = 2;
	static public final int COLUMN_RUNTYPE = 3;
	
	public void clear () {
		data.clear();
		fireTableDataChanged();
	}
	
	public void insertRow(ArrayList<Object> values) {
		int iRowCount = getRowCount();
		data.put(iRowCount, values);
		fireTableDataChanged();
	}
	
	
	
	// ******** OVERIDE METHODS ********
	@Override
	public Class getColumnClass(int column)
	{
		return data.get(0).get(column).getClass();
	}
	
	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_COUNT;
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == COLUMN_RUN)
			return true;
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}

	public void setValueAt(Object value, int row, int column) {
		if ((value != null) &&
				(row >= 0) && (row < data.size()) &&
				(column >= 0) && (column < COLUMN_COUNT))
		{
			data.get(row).set(column, value);
			if (column == COLUMN_RUN) {
				// change the debug_run setting of element
				Boolean debug_run = (Boolean)value;
				com.ttv.at.test.testelement cur_element = get_testelement(row);
				if (cur_element != null) {
					cur_element.set_debug_run(debug_run);
				}
			}
		}
	}

}
