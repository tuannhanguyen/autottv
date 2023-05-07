package com.ttv.at.test;

import java.util.ArrayList;

public class testelement {
	String instance_name = null;
	public String get_instance_name() { return instance_name; }

	testcase tc_instance = null;
	public testcase get_tc_instance() { return tc_instance; }
	public void set_tc_instance (testcase tc_instance) {
		this.tc_instance = tc_instance;
	}
	public void set_tc_instance (testcase tc_instance, 
			ArrayList<parameter> inputs, 
			ArrayList<parameter> returns, 
			run_type type) {
		this.tc_instance = tc_instance;
		this.inputs = inputs;
		this.returns = returns;
		this.type = type;
	}
	public result set_tc_instance (ArrayList<testcase> testcases) {
		String expected_testcase_name = instance_name.substring(5, instance_name.length());
		for (int i = 0 ; i < testcases.size() ; i ++) {
			if (testcases.get(i).get_name().equals(expected_testcase_name)) {
				tc_instance = testcases.get(i);
				// Check inputs
				if (tc_instance.get_inputs().size() > inputs.size())
					return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Test Element is not provide enough inputs for \"" + expected_testcase_name + "\"");
				
				return new result(status_run.PASSED, error_code.NO_ERROR, "Test Element OK");
			}
		}
		return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Test case \"" + expected_testcase_name + "\" not found");
	}
	public result set_tc_instance (ArrayList<testcase> testcases, String expected_testcase_name) {
		for (int i = 0 ; i < testcases.size() ; i ++) {
			if (testcases.get(i).get_name().equals(expected_testcase_name)) {
				tc_instance = testcases.get(i);
				// Check inputs
				if (tc_instance.get_inputs().size() > inputs.size())
					return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Test Element is not provide enough inputs for \"" + expected_testcase_name + "\"");
				
				return new result(status_run.PASSED, error_code.NO_ERROR, "Test Element OK");
			}
		}
		return new result(status_run.STOP, error_code.EXPECTED_NOT_MATCH, "Test case \"" + expected_testcase_name + "\" not found");
	}

	testlibrary lib_instance = null;
	public testlibrary get_lib_instance() { return lib_instance;}
	public void set_lib_instance (testlibrary lib_instance) { this.lib_instance = lib_instance; }

	run_type type;
	public run_type get_type() { return type; }

	ArrayList<parameter> inputs;
	public ArrayList<parameter> get_inputs() { return inputs;}
	public void apply_common_input () {
		for (parameter input: inputs) {
			if (input.get_key() != null && input.get_key().startsWith("$")) {
				String dataset_expected_key = input.get_key().substring(1);
				for (com.ttv.at.test.parameter pElement: testsetting.get_common_data().get_params())
					if (pElement.check_key(dataset_expected_key))
						input.copy_from(pElement.get_value());
			}
		}
	}
	
	String check_dataset_inputs_msg;
	public String get_check_dataset_inputs_msg () { return check_dataset_inputs_msg; }
	public boolean check_dataset_inputs (testsuite_dataset dataset) {
		check_dataset_inputs_msg = "";
		
		// Check input parameters
		for (parameter input:inputs) {
			if (input.get_key() != null && input.get_key().startsWith("$")) {
				String dataset_expected_key = input.get_key().substring(1);
				boolean found_input = false;
				for (parameter scan_set_input : dataset.get_params())
					if (scan_set_input.check_key(dataset_expected_key)) {
						found_input = true;
						break;
					}
				
				if (!found_input) {
					check_dataset_inputs_msg = "Parameter '" + input.get_key() + "' is not found";
					return false;
				}
			}
		}
		
		if (tc_instance != null) {
			int i = 13;
			if (!tc_instance.check_dataset_inputs(dataset)) {
				check_dataset_inputs_msg = "Element '" + instance_name + "' - " + tc_instance.get_check_dataset_inputs_msg();
				return false;
			}
		}
		
		return true;
	}
	
	ArrayList<parameter> returns;
	public ArrayList<parameter> get_returns() { return returns;}

	testlibelement last_testlibelement = null;
	public testlibelement get_last_testlibelement () { return last_testlibelement; }

	testelementloopgroup parent_group = null;
	public testelementloopgroup get_parent_group() { return parent_group; }
	public void set_parent_group (testelementloopgroup parent_group) {this.parent_group = parent_group;}

	public testelement (String instance_name, testlibrary lib_instance, 
			ArrayList<parameter> inputs, 
			ArrayList<parameter> returns, 
			run_type type) {
		this.instance_name = instance_name;
		this.lib_instance = lib_instance;
		this.inputs = inputs;
		this.returns = returns;
		this.type = type;
	}

	public testelement (String instance_name,
			ArrayList<parameter> inputs, 
			ArrayList<parameter> returns, 
			run_type type) {
		this.instance_name = instance_name;
		this.inputs = inputs;
		this.returns = returns;
		this.type = type;
	}
	
	public int get_element_call_testcase_depth(int current_level) {
		if (current_level >= testcase.max_test_call_test_depth)
			return current_level;
		if (lib_instance != null)
			return current_level;
		else if (tc_instance != null)
			return tc_instance.get_test_call_test_depth(current_level + 1);
		return current_level;
	}

	// 1. Clear inputs
	// 2. Clear lib_return
	// 3. Clear lib_instance
	public void reset() {

		// 1. Clear inputs
		for (int i = 0 ; i < inputs.size() ; i ++)
			if (inputs.get(i).get_key() != null && inputs.get(i).get_key().length() > 0)
				inputs.get(i).clear();

		// 2. Clear returns
		if (returns != null && returns.size() > 0)
			for (int index = 0 ; index < returns.size() ; index ++)
				returns.get(index).clear();

		// 3. Clear lib_instance
		if (tc_instance != null)
			tc_instance.reset();
		if (lib_instance != null)
			lib_instance.reset();
		
		last_testlibelement = null;
	}

	// Execute step:
	// 1. reset lib_instance
	// 2. Set input to lib_instance
	// 3. Call execute lib_instance
	// 4. Set return
	// 5. Return run result
	public result execute() {
		if (lib_instance != null) {
			last_testlibelement = null;
			
			// 1. reset lib_instance
			lib_instance.reset();
			
			// 2. Set input to lib_instance
			String input_description = null;
			if (inputs != null && inputs.size() > 0)
				for (parameter scan_input : inputs)
					if (input_description == null)
						input_description = "('" + scan_input.get_value() + "'";
					else
						input_description = input_description + "', '" + scan_input.get_value() + "'";
			if (input_description == null)
				com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library " + instance_name));
			else
				com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library " + instance_name + input_description + ")"));
			lib_instance.set_inputs(inputs);
			
			// 3. Call execute lib_instance
			result run_res = lib_instance.execute();
			last_testlibelement = lib_instance.get_running_element();
			if (run_res.get_result() == status_run.PASSED)
				com.ttv.at.log.log.get_instance().update_testelement_result(1, run_res.get_message(), description);
			else if (type == run_type.IGNORE || 
					type == run_type.LAST_FAILED_IGNORE ||
					type == run_type.LAST_PASSED_IGNORE)
				com.ttv.at.log.log.get_instance().update_testelement_result(2, run_res.get_message(), description);
			else
				com.ttv.at.log.log.get_instance().update_testelement_result(0, run_res.get_message(), description);
			
			// 4. Set return
			if (lib_instance.get_lib_returns() != null && 
					lib_instance.get_lib_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int max_index = lib_instance.get_lib_returns().size();
				if (returns.size() < max_index)
					max_index = returns.size();
				for (int index = 0 ; index < max_index ; index ++)
					returns.get(index).copy_from(lib_instance.get_lib_returns().get(index));
			}
			
			// 5. Return run result
			return run_res;
		}
		else if (tc_instance != null) {
			// 1. reset lib_instance
			tc_instance.reset();
			
			// 2. Set input to lib_instance
			com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library call test case " + instance_name));
			tc_instance.set_inputs_param(inputs);
			
			// 3. Call execute lib_instance
			result run_res = tc_instance.execute();
			if (run_res.get_result() == status_run.PASSED)
				com.ttv.at.log.log.get_instance().update_testelement_result(1, run_res.get_message(), description);
			else if (type == run_type.IGNORE || 
					type == run_type.LAST_FAILED_IGNORE ||
					type == run_type.LAST_PASSED_IGNORE)
				com.ttv.at.log.log.get_instance().update_testelement_result(2, run_res.get_message(), description);
			else
				com.ttv.at.log.log.get_instance().update_testelement_result(0, run_res.get_message(), description);
			
			// 4. Set return
			if (tc_instance.get_tc_returns() != null && 
					tc_instance.get_tc_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int min_index = tc_instance.get_tc_returns().size();
				if (returns.size() < min_index)
					min_index = returns.size();
				for (int index = 0 ; index < min_index ; index ++)
					returns.get(index).copy_from(tc_instance.get_tc_returns().get(index));
			}
			
			// 5. Return run result
			return run_res;
		}
		return new result(status_run.NOT_RUN, error_code.UNKNOWN_ERROR, "Nothing to run for test element");
	}

	// Resume step:
	// 1. Set input to lib_instance
	// 2. Call resume lib_instance
	// 3. Set return
	// 4. Return run result
	public result resume() {
		if (lib_instance != null) {
			last_testlibelement = null;
			
			// 1. Set input to lib_instance
			String input_description = null;
			if (inputs != null && inputs.size() > 0)
				for (parameter scan_input : inputs)
					if (input_description == null)
						input_description = "('" + scan_input.get_value() + "'";
					else
						input_description = input_description + "', '" + scan_input.get_value() + "'";
			if (input_description == null)
				com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library " + instance_name));
			else
				com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library " + instance_name + input_description + ")"));
			lib_instance.set_inputs(inputs);
	
			// 2. Call resume lib_instance
			result run_res = lib_instance.resume();
			last_testlibelement = lib_instance.get_running_element();
			if (run_res.get_result() == status_run.PASSED)
				com.ttv.at.log.log.get_instance().update_testelement_result(1, run_res.get_message(), description);
			else if (type == run_type.IGNORE || 
					type == run_type.LAST_FAILED_IGNORE ||
					type == run_type.LAST_PASSED_IGNORE)
				com.ttv.at.log.log.get_instance().update_testelement_result(2, run_res.get_message(), description);
			else
				com.ttv.at.log.log.get_instance().update_testelement_result(0, run_res.get_message(), description);
	
			// 4. Set return
			if (lib_instance.get_lib_returns() != null && 
					lib_instance.get_lib_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int max_index = lib_instance.get_lib_returns().size();
				if (returns.size() < max_index)
					max_index = returns.size();
				for (int index = 0 ; index < max_index ; index ++)
					returns.get(index).copy_from(lib_instance.get_lib_returns().get(index));
			}
	
			// 5. Return run result
			return run_res;
		}
		else if (tc_instance != null) {
			com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("Starting test library " + instance_name));
			
			// 3. Call execute lib_instance
			result run_res = tc_instance.resume();
			if (run_res.get_result() == status_run.PASSED)
				com.ttv.at.log.log.get_instance().update_testelement_result(1, run_res.get_message(), description);
			else if (type == run_type.IGNORE || 
					type == run_type.LAST_FAILED_IGNORE ||
					type == run_type.LAST_PASSED_IGNORE)
				com.ttv.at.log.log.get_instance().update_testelement_result(2, run_res.get_message(), description);
			else
				com.ttv.at.log.log.get_instance().update_testelement_result(0, run_res.get_message(), description);
			
			// 4. Set return
			if (tc_instance.get_tc_returns() != null && 
					tc_instance.get_tc_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int min_index = tc_instance.get_tc_returns().size();
				if (returns.size() < min_index)
					min_index = returns.size();
				for (int index = 0 ; index < min_index ; index ++)
					returns.get(index).copy_from(tc_instance.get_tc_returns().get(index));
			}
			
			// 5. Return run result
			return run_res;
		}
		return new result(status_run.NOT_RUN, error_code.UNKNOWN_ERROR, "Nothing to run for test element");
	}

	public void copy_returns_from_instance () {

		/*
		if (lib_instance != null) {// 4. Set return
			if (lib_instance.get_lib_returns() != null && 
					lib_instance.get_lib_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int max_index = lib_instance.get_lib_returns().size();
				if (returns.size() < max_index)
					max_index = returns.size();
				for (int index = 0 ; index < max_index ; index ++)
					returns.get(index).copy_from(lib_instance.get_lib_returns().get(index));
			}
		}
		else 
		*/
		// Copy return from test case only
		if (tc_instance != null) {// 4. Set return
			if (tc_instance.get_tc_returns() != null && 
					tc_instance.get_tc_returns().size() > 0 &&
					returns != null &&
							returns.size() > 0) {
				int min_index = tc_instance.get_tc_returns().size();
				if (returns.size() < min_index)
					min_index = returns.size();
				for (int index = 0 ; index < min_index ; index ++)
					returns.get(index).copy_from(tc_instance.get_tc_returns().get(index));
			}
		}
	}
	
	public void stop() {
		if (lib_instance != null)
			lib_instance.stop();
		if (tc_instance != null)
			tc_instance.stop();
	}

	public void pause() {
		if (lib_instance != null)
			lib_instance.pause();
		if (tc_instance != null)
			tc_instance.pause();
	}

	public void clear() {
		if (lib_instance != null)
			lib_instance.clear();
		if (tc_instance != null)
			tc_instance.clear();
		if (parent_group != null)
			parent_group.clear();
		
		// clear list
		if (inputs != null)
			inputs.clear();
		
		// clear data
		lib_instance = null;
		inputs = null;
		if (returns != null)
			returns.clear();
		returns = null;
		last_testlibelement = null;
		parent_group = null;
	}

	String description = "";
	public void set_description (String description) {this.description = description;}
	public String get_description () { return description;}

	// ******** FOR DEBUG ******** //
	boolean debug_run = true;
	public boolean get_debug_run () {return debug_run;}
	public void set_debug_run (boolean debug_run) {this.debug_run = debug_run;}
}
