package com.ttv.at.tablemodel;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

public class debug_params extends AbstractTableModel {
	
	ArrayList<com.ttv.at.test.parameter> loaded_params;
	ArrayList<String> loaded_values;
	public void set_loaded_params (ArrayList<com.ttv.at.test.parameter> loaded_params, ArrayList<String> loaded_values) {
		this.loaded_params = loaded_params;
		this.loaded_values = loaded_values;
		data.clear();
		reload_inputs();
	}
	public void set_loaded_params (ArrayList<com.ttv.at.test.parameter> loaded_params) {
		this.loaded_params = loaded_params;
		this.loaded_values = null;
		data.clear();
		reload_inputs();
	}
	void reload_inputs() {
		if (loaded_params != null) {
			// Loading data from loaded_testarea
			for (int index = 0 ; index < loaded_params.size() ; index ++) {
				ArrayList<Object> values = new ArrayList<Object>();
				// Add "Name" (column 0)
				values.add(loaded_params.get(index).get_key());
				
				// Add "Value" (column 0)
				if (loaded_values != null && loaded_values.size() > index)
					values.add(loaded_values.get(index));
				else
					values.add(loaded_params.get(index).get_value());
				
				insertRow(values);
			}
		}
	}
	
	
	
	
	Hashtable<Integer, ArrayList<Object>> data = new Hashtable<Integer, ArrayList<Object>>();
	static final String[] columnNames = {"Name", "Value"};
	static final int COLUMN_COUNT = 2;
	static public final int COLUMN_NAME = 0;
	static public final int COLUMN_VALUE = 1;
	
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
		if (column == COLUMN_VALUE)
			return true;
		return false;// (column == 1 || column == 4 || column == 5);
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
			if (column == COLUMN_VALUE)
				if (loaded_values != null && loaded_values.size() > row)
					loaded_values.set(row, value.toString());
				else
					loaded_params.get(row).copy_from(value.toString());
		}
	}
}
