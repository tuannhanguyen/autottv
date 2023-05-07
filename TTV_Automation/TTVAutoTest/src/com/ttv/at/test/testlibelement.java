package com.ttv.at.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.ttv.at.test.action.factory;

public class testlibelement {
	public String name;
	public String get_name() { return name; }
	
	ArrayList<parameter> inputs = null;
	public ArrayList<parameter> get_inputs() { return inputs;}
	ArrayList<parameter> returns = null;
	public ArrayList<parameter> get_returns() { return returns;}
	public parameter get_Return() {
		if (returns != null && returns.size() > 0)
			return returns.get(0);
		return null;
	}
	
	testobject action_object;
	public testobject get_action_object() { return action_object;}
	public void set_action_object(testobject action_object) { this.action_object = action_object; }
	
	run_type type;
	public run_type get_type() { return type; }
	
	
	public testlibelement (String name,
			ArrayList<parameter> inputs,
			ArrayList<parameter> returns,
			testobject action_object, 
			run_type type) {
		this.name	= name;
		this.inputs	= inputs;
		this.returns	= returns;
		this.action_object = action_object;
		this.type = type;
	}
	
	public testlibelement (String name,
				ArrayList<parameter> inputs,
				ArrayList<parameter> returns,
				run_type type) {
		this.name	= name;
		this.inputs	= inputs;
		this.returns	= returns;
		this.type = type;
	}
	
	
	
	
	
	
	testlibelementloopgroup parent_group = null;
	public testlibelementloopgroup get_parent_group() { return parent_group; }
	public void set_parent_group (testlibelementloopgroup parent_group) {this.parent_group = parent_group;}
	


	// 1. Clear inputs
	// 2. Clear Return
	// 3. Clear capture
	public void reset() {
		// 1. Clear inputs
		if (inputs != null && inputs.size() > 0)
			for (int i = 0 ; i < inputs.size() ; i ++)
				if (inputs.get(i).get_key() != null && inputs.get(i).get_key().length() > 0)
					inputs.get(i).clear();

		// 2. Clear Return
		if (returns != null && returns.size() > 0)
			for (int i = 0 ; i < returns.size() ; i ++)
				if (returns.get(i).get_key() != null && returns.get(i).get_key().length() > 0)
					returns.get(i).clear();
	}
	
	
	public void clear_action_capture() {
		if (current_instance != null)
			current_instance.clear_action_capture();
	}
	
	public BufferedImage get_before_action_capture () {
		if (current_instance != null)
			return current_instance.get_before_action_capture();
		return null;
	}

	
	public String get_start_action_log_message () {
		String start_message = "Start action - " + name + " -";
		if (action_object != null)
			start_message += " with object '"+action_object.get_key()+"'";
		
		if (inputs != null && inputs.size() > 0) {
			if (inputs.size() == 1) {
				if (inputs.get(0).get_value() != null)
					start_message += " input = | " + inputs.get(0).get_value() + " |";
				else
					start_message += " input = | [null] |";
			}
			else
				for (int i = 0 ; i < inputs.size(); i++)
					if (i == 0) {
						if (inputs.get(i).get_value() != null)
							start_message += " input = | " + inputs.get(i).get_value() + " |";
						else
							start_message += " input = | [null] |";
					}
					else {
						if (inputs.get(i).get_value() != null)
							start_message += inputs.get(i).get_value() + " | ";
						else
							start_message += " [null] | ";
					}
		}
		
		return start_message;
	}
	
	ArrayList<testaction> action_instances = new ArrayList<testaction>(); 
	
	public result check_compatible () {
		if (lib_instance == null) {
			current_instance = null;
			// Check if action instance is exists
			String action_class_name = "com.kms.mkl.test.action." + testsetting.get_runtime().toLowerCase();
			for (testaction scan_instance : action_instances)
				if (scan_instance.getClass().getName().equals(action_class_name)) {
					current_instance = scan_instance;
				}
			
			if (current_instance == null)
				current_instance = factory.create_action(name, inputs, returns, action_object, type);
			
			return current_instance.validate();
		}
		return new result(status_run.PASSED, error_code.NO_ERROR, "");
	}
	
	testaction current_instance = null;
	public result execute () {
		if (lib_instance != null) {
			lib_instance.reset();
		
			// 2. Set input to lib_instance
			// com.kms.mkl.log.log.get_instance().append_testelement(new com.kms.mkl.log.testelement("Starting test library " + lib_instance.get_name()), "");
			lib_instance.set_inputs(inputs);
			
			// 3. Call execute lib_instance
			result run_res = lib_instance.execute();
			if (run_res.get_result() == status_run.PASSED)
				com.ttv.at.log.log.get_instance().update_testelement_result(1, run_res.get_message(), null);
			else
				com.ttv.at.log.log.get_instance().update_testelement_result(0, run_res.get_message(), null);
			
			// 4. Set return
			if (lib_instance.get_lib_returns() != null && 
					lib_instance.get_lib_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int min_index = lib_instance.get_lib_returns().size();
				if (returns.size() < min_index)
					min_index = returns.size();
				for (int index = 0 ; index < min_index ; index ++)
					returns.get(index).copy_from(lib_instance.get_lib_returns().get(index));
			}
			
			// 5. Return run result
			return run_res;
		}
		else {
			current_instance = null;
			// Check if action instance is exists
			String action_class_name = "com.kms.mkl.test.action." + testsetting.get_runtime().toLowerCase();
			for (testaction scan_instance : action_instances)
				if (scan_instance.getClass().getName().equals(action_class_name)) {
					current_instance = scan_instance;
				}
			
			if (current_instance == null)
				current_instance = factory.create_action(name, inputs, returns, action_object, type);
			if (current_instance != null)
				return current_instance.execute();
			else
				return new result(status_run.FAILED, error_code.ACTION_FAILED, "Action '" + name + "' is not supported");
		}
	}
	
	public void stop() {
		if (current_instance != null)
			current_instance.stop();
	}

	public void pause() {
		if (current_instance != null)
			current_instance.pause();
	}
	

	com.ttv.at.test.testlibrary lib_instance = null;
	public com.ttv.at.test.testlibrary get_lib_instance() { return lib_instance; }
	public void set_lib_instance(com.ttv.at.test.testlibrary lib_instance) {this.lib_instance = lib_instance;}
	public result set_lib_instance(ArrayList<com.ttv.at.test.testlibrary> testlibraries) {
		String expected_lib_name = get_name().substring(5, get_name().length()).toLowerCase();
		for (com.ttv.at.test.testlibrary cur_testlibrary: testlibraries) {
			String lib_name = cur_testlibrary.get_name().toLowerCase();
			if (lib_name.equals(expected_lib_name)) {
				lib_instance = cur_testlibrary;
				// Check inputs
				if (lib_instance.get_inputs().size() > get_inputs().size())
					return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Action is not provide enough inputs for \"" + expected_lib_name + "\"");
				
				return new result(status_run.PASSED, error_code.NO_ERROR, "Action OK", "");
			}
		}
		return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Librray \"" + expected_lib_name + "\" not found");
	}
	

	public int get_action_call_library_depth (int call_level) {
		if (lib_instance != null)
			return lib_instance.get_lib_call_lib_depth(call_level + 1);
		return call_level;
	}
}
