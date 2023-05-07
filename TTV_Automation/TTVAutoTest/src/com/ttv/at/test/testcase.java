package com.ttv.at.test;

import java.util.ArrayList;

public class testcase {
	String name;
	public String get_name() { return name; }
	
	ArrayList<parameter> inputs;
	public ArrayList<parameter> get_inputs () { return inputs; }
	
	ArrayList<parameter> tc_returns;
	public ArrayList<parameter> get_tc_returns () { return tc_returns; }
	
	ArrayList<parameter> lib_returns;
	public ArrayList<parameter> get_lib_returns () { return lib_returns; }
	
	
	ArrayList<testelement> elements;
	public ArrayList<testelement> get_elements() { return elements; }
	
	ArrayList<testelement> clear_state_elements;
	public ArrayList<testelement> get_clear_state_elements() { return clear_state_elements; }
	
	int startRow;
	public int get_startRow() { return startRow; }
	
	int endRow;
	public int get_endRow() { return endRow; }
	
	public testcase (String name, ArrayList<testelement> elements, ArrayList<parameter> inputs, ArrayList<parameter> tc_returns, ArrayList<parameter> lib_returns, int startRow, int endRow) {
		this.name = name;
		this.elements = elements;
		this.inputs = inputs;
		this.tc_returns = tc_returns;
		this.lib_returns = lib_returns;
		this.startRow = startRow;
		this.endRow = endRow;
	}
	
	public testcase (String name, ArrayList<testelement> elements, ArrayList<testelement> clear_state_elements, ArrayList<parameter> inputs, ArrayList<parameter> tc_returns, ArrayList<parameter> lib_returns, int startRow, int endRow) {
		this.name = name;
		this.elements = elements;
		this.clear_state_elements = clear_state_elements;
		this.inputs = inputs;
		this.tc_returns = tc_returns;
		this.lib_returns = lib_returns;
		this.startRow = startRow;
		this.endRow = endRow;
	}
	
	// 1. Clear inputs
	// 2. Clear returns
	// 3. Clear data for all elements
	// 4. Clear test_running
	// 5. Clear stop/pause
	public void reset() {
		// 1. Clear inputs
		for (int i = 0 ; i < inputs.size() ; i ++)
			if (inputs.get(i).get_key() != null && inputs.get(i).get_key().length() > 0)
				inputs.get(i).clear();
		
		// 2. Clear returns
		if (tc_returns != null)
			for (int i = 0 ; i < tc_returns.size() ; i ++)
				tc_returns.get(i).clear();
		if (lib_returns != null)
			for (int i = 0 ; i < lib_returns.size() ; i ++)
				lib_returns.get(i).clear();
		
		// 3. Clear data for all elements
		for (int index = 0 ; index < elements.size() ; index ++)
			elements.get(index).reset();
		
		// 4. Clear test_running
		running_element = null;
		running_element_index = -1;
		
		// 5. Clear stop/pause
		stop = false;
		pause = false;
		resume_index = -1;
	}
	
	public boolean set_inputs (ArrayList<String> inputs) {
		if (inputs.size() >= this.inputs.size()) {
			for (int index = 0 ; index < this.inputs.size() ; index ++)
				this.inputs.get(index).copy_from(inputs.get(index));
			return true;
		}
		return false;
	}
	
	public boolean set_inputs_param (ArrayList<parameter> inputs) {
		if (inputs.size() >= this.inputs.size()) {
			for (int index = 0 ; index < this.inputs.size() ; index ++)
				this.inputs.get(index).copy_from(inputs.get(index).get_value());
			return true;
		}
		return false;
	}

	String check_dataset_inputs_msg;
	public String get_check_dataset_inputs_msg () { return check_dataset_inputs_msg; }
	public boolean check_dataset_inputs (testsuite_dataset dataset) {
		check_dataset_inputs_msg = name +" - ";
		for (testelement scan_element : elements)
			if (!scan_element.check_dataset_inputs(dataset)) {
				check_dataset_inputs_msg = name + " - " + scan_element.get_check_dataset_inputs_msg ();
				return false;
			}
		return true;
	}
	
	static final public int max_test_call_test_depth = 20;
	public int get_test_call_test_depth(int current_level) {
		if (current_level >= max_test_call_test_depth)
			return current_level;
		if (elements != null && elements.size() > 0) {
			int max_depth = 0;
			for(testelement e:elements) {
				int element_call_depth = e.get_element_call_testcase_depth (current_level);
				if (element_call_depth > max_depth)
					max_depth = element_call_depth;
			}
			return max_depth;
		}
		return current_level;
	}
	
	// 1. Clear stop/pause
	// 2. Scan to execute all elements
	testelement running_element = null;
	int running_element_index = -1;
	int running_element_group_index = -1;
	public result execute() {
		// 1. Clear stop/pause
		stop = false;
		pause = false;
		resume_index = -1;
		resume_group_index = -1;
		running_element = null;
		running_element_index = -1;
		running_element_group_index = -1;
		
		// 2. Scan to execute all elements
		result last_run_res = null;
		for (int index = 0 ; index < elements.size() ; index ++) {
			running_element = elements.get(index);
			running_element_index = index;
			running_element.apply_common_input();
			if (running_element.get_parent_group() == null) {
				boolean pass_step = false;
				if (running_element.get_type() == run_type.DEFAULT) {
					// execute test element
					last_run_res = running_element.execute();
					
					// check result
					if (last_run_res.get_result() != status_run.PASSED) {
						execute_clear_state ();
						return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
					}
					pass_step = true;
				}
				else if (running_element.get_type() == run_type.IGNORE) {
					last_run_res = running_element.execute();
					if (last_run_res.get_result() == status_run.PASSED)
						pass_step = true;
				}
				else if (running_element.get_type() == run_type.INVERSE) {
					last_run_res = running_element.execute(); //_with_capture();
					
					// check result
					if (last_run_res.get_result() == status_run.PASSED) {
						execute_clear_state ();
						return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
					}
					pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.PASSED) {
					// execute test element
					last_run_res = running_element.execute();
					if (last_run_res.get_result() == status_run.PASSED)
						pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.PASSED) {
					// execute test element
					last_run_res = running_element.execute();
					
					// check result
					if (last_run_res.get_result() != status_run.PASSED) {
						execute_clear_state ();
						if (running_element.get_last_testlibelement() != null)
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						else
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
					}
					pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.FAILED) {
					// execute test element
					last_run_res = running_element.execute();
					if (last_run_res.get_result() == status_run.PASSED)
						pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.FAILED) {
					// execute test element
					last_run_res = running_element.execute();
					
					// check result
					if (last_run_res.get_result() != status_run.PASSED) {
						execute_clear_state ();
						if (running_element.get_last_testlibelement() != null)
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						else
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
					}
					pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.NOT_RUN) {
					// execute test element
					last_run_res = running_element.execute();
					if (last_run_res.get_result() == status_run.PASSED)
						pass_step = true;
				}
				else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.NOT_RUN) {
					// execute test element
					last_run_res = running_element.execute();
					
					// check result
					if (last_run_res.get_result() != status_run.PASSED) {
						execute_clear_state ();
						if (running_element.get_last_testlibelement() != null)
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						else
							return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
					}
					pass_step = true;
				}
				else {
					last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test lib " + running_element.get_instance_name() + " is not run because run-type is not matched with last result");
					
					// Setting return from lib instance
					running_element.copy_returns_from_instance ();
					
				}
				
				if (running_element.get_last_testlibelement() != null)
					running_element.get_last_testlibelement().clear_action_capture();
				
				if (pause){
					running_element = null;
					running_element_index = -1;
					if (last_run_res.get_result() == status_run.PASSED || last_run_res.get_result() == status_run.NOT_RUN){
						resume_index = index + 1;
						if (index == (elements.size() - 1)) {
							execute_clear_state ();
							return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
						}
						else
							return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
					}
					else
						return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
				}
				
				if (stop) {
					stop = false;
					running_element = null;
					running_element_index = -1;
					return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
				}
			}
			else {
				testelementloopgroup running_element_group = running_element.get_parent_group();
				// Run in action group
				// Prepare index of input
				running_element_group.prepare_data();
				boolean break_loop = false;
				while (running_element_group.not_at_the_end_loop()) {

					com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("START NEW TEST ELEMENTS LOOP : " + running_element_group.get_name()));
					com.ttv.at.log.log.get_instance().update_testelement_result(1, "ON TEST ELEMENT LOOP", running_element_group.get_name());
					
					for (int element_index = 0 ; element_index < running_element_group.get_testelements().size() ; element_index ++) {
						running_element = elements.get(index + element_index);
						running_element.apply_common_input();
						running_element_index = index + element_index;
						running_element_group_index = element_index;
						boolean pass_step = false;
						
						if (running_element.get_type() == run_type.DEFAULT) {

							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							
							// check result
							if (last_run_res.get_result() != status_run.PASSED) {
								execute_clear_state ();
								if (running_element.get_last_testlibelement() != null)
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								else
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							}
							pass_step = true;
						}
						else if (running_element.get_type() == run_type.IGNORE) {

							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								if (running_element.get_last_testlibelement() != null)
									running_element.get_last_testlibelement().clear_action_capture();
								break;
							}
							
							last_run_res = running_element.execute();
							if (last_run_res.get_result() == status_run.PASSED)
								pass_step = true;
						}
						else if (running_element.get_type() == run_type.INVERSE) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							last_run_res = running_element.execute(); //_with_capture();
							
							// check result
							if (last_run_res.get_result() == status_run.PASSED) {
								execute_clear_state ();
								if (running_element.get_last_testlibelement() != null)
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								else
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							}
							pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.PASSED) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							if (last_run_res.get_result() == status_run.PASSED)
								pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.PASSED) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							
							// check result
							if (last_run_res.get_result() != status_run.PASSED) {
								execute_clear_state ();
								if (running_element.get_last_testlibelement() != null)
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								else
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							}
							pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
								last_run_res != null && 
								last_run_res.get_result() != status_run.PASSED) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							if (last_run_res.get_result() == status_run.PASSED)
								pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
								last_run_res != null && 
								last_run_res.get_result() != status_run.PASSED) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							
							// check result
							if (last_run_res.get_result() != status_run.PASSED) {
								execute_clear_state ();
								if (running_element.get_last_testlibelement() != null)
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								else
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							}
							pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.NOT_RUN) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							if (last_run_res.get_result() == status_run.PASSED)
								pass_step = true;
						}
						else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.NOT_RUN) {
							// Check if break loop
							if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// execute test element
							last_run_res = running_element.execute();
							
							// check result
							if (last_run_res.get_result() != status_run.PASSED) {
								execute_clear_state ();
								if (running_element.get_last_testlibelement() != null)
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								else
									return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							}
							pass_step = true;
						}
						else {
							last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test lib " + running_element.get_instance_name() + " is not run because run-type is not matched with last result");
							
							// Setting return from lib instance
							running_element.copy_returns_from_instance ();
							
						}
						
						if (running_element.get_last_testlibelement() != null)
							running_element.get_last_testlibelement().clear_action_capture();
						
						if (pause){
							running_element = null;
							running_element_index = -1;
							if (last_run_res.get_result() == status_run.PASSED || last_run_res.get_result() == status_run.NOT_RUN){
								resume_index = index + element_index + 1;
								resume_group_index = element_index + 1;
								if (element_index == (elements.size() - 1)) {
									execute_clear_state ();
									return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
								}
								else
									return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
							}
							else
								return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
						}
						
						if (stop) {
							stop = false;
							running_element = null;
							running_element_index = -1;
							return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
						}
					}
					if (break_loop)
						break;
					running_element_group.inputs_move_next();
				}
				
				// reset index to continue to run
				index = index + running_element_group.get_testelements().size() - 1;
			}
		}

		running_element = null;
		running_element_index = -1;
		execute_clear_state ();
		return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed.");
	}
	
	public result resume() {
		stop = false;
		pause = false;
		if (resume_index < 0)
			resume_index = 0;
		if (resume_index < elements.size()) {
			int start_index = resume_index;
			int start_group_index = resume_group_index;
			resume_index = -1;
			resume_group_index = -1;
			// Execute all elements
			result last_run_res = null;
			boolean in_resume = true;
			for (int index = start_index ; index < elements.size() ; index ++) {
				running_element = elements.get(index);
				running_element.apply_common_input();
				running_element_index = index;
				if (running_element.get_parent_group() == null) {
					boolean pass_step = false;
					if (running_element.get_type() == run_type.DEFAULT) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							execute_clear_state ();
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if (running_element.get_type() == run_type.IGNORE) {
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if (running_element.get_type() == run_type.INVERSE) {
						last_run_res = running_element.execute(); //_with_capture();
						
						// check result
						if (last_run_res.get_result() == status_run.PASSED) {
							execute_clear_state ();
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.PASSED) {
						// execute test element
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.PASSED) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							execute_clear_state ();
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// execute test element
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							execute_clear_state ();
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.NOT_RUN) {
						// execute test element
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.NOT_RUN) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							execute_clear_state ();
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else {
						last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test lib " + running_element.get_instance_name() + " is not run because run-type is not matched with last result");
						
						// Setting return from lib instance
						running_element.copy_returns_from_instance ();
						
					}
					
					if (running_element.get_last_testlibelement() != null)
						running_element.get_last_testlibelement().clear_action_capture();
					
					if (pause){
						running_element = null;
						running_element_index = -1;
						if (last_run_res.get_result() == status_run.PASSED || last_run_res.get_result() == status_run.NOT_RUN){
							resume_index = index + 1;
							if (index == (elements.size() - 1)) {
								execute_clear_state ();
								return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
							}
							else
								return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
						}
						else
							return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
					}
					
					if (stop) {
						stop = false;
						running_element = null;
						running_element_index = -1;
						return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
					}
				}
				else {
					testelementloopgroup running_element_group = running_element.get_parent_group();
					// Run in action group
					// Prepare index of input
					running_element_group.prepare_data();
					boolean break_loop = false;
					while (running_element_group.not_at_the_end_loop()) {

						com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("START NEW TEST ELEMENTS LOOP : " + running_element_group.get_name()));
						com.ttv.at.log.log.get_instance().update_testelement_result(1, "ON TEST ELEMENT LOOP", running_element_group.get_name());
						
						for (int element_index = 0 ; element_index < running_element_group.get_testelements().size() ; element_index ++) {
							running_element = elements.get(index + element_index);
							running_element.apply_common_input();
							running_element_index = index + element_index;
							running_element_group_index = element_index;
							boolean pass_step = false;
							
							if (running_element.get_type() == run_type.DEFAULT) {

								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									execute_clear_state ();
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if (running_element.get_type() == run_type.IGNORE) {

								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									if (running_element.get_last_testlibelement() != null)
										running_element.get_last_testlibelement().clear_action_capture();
									break;
								}
								
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if (running_element.get_type() == run_type.INVERSE) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								last_run_res = running_element.execute(); //_with_capture();
								
								// check result
								if (last_run_res.get_result() == status_run.PASSED) {
									execute_clear_state ();
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
										last_run_res != null && 
										last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									execute_clear_state ();
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() != status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
									last_run_res != null && 
									last_run_res.get_result() != status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									execute_clear_state ();
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.NOT_RUN) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.NOT_RUN) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									execute_clear_state ();
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else {
								last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test lib " + running_element.get_instance_name() + " is not run because run-type is not matched with last result");
								
								// Setting return from lib instance
								running_element.copy_returns_from_instance ();
								
							}
							
							if (running_element.get_last_testlibelement() != null)
								running_element.get_last_testlibelement().clear_action_capture();
							
							if (pause){
								running_element = null;
								running_element_index = -1;
								if (last_run_res.get_result() == status_run.PASSED || last_run_res.get_result() == status_run.NOT_RUN){
									resume_index = index + element_index + 1;
									resume_group_index = element_index + 1;
									if (element_index == (elements.size() - 1)) {
										execute_clear_state ();
										return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
									}
									else
										return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
								}
								else
									return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
							}
							
							if (stop) {
								stop = false;
								running_element = null;
								running_element_index = -1;
								return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
							}
						}
						if (break_loop)
							break;
						running_element_group.inputs_move_next();
					}
					
					// reset index to continue to run
					index = index + running_element_group.get_testelements().size() - 1;
				}
			}

			running_element = null;
			running_element_index = -1;
			execute_clear_state ();
			return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed.");
		}
		else {
			resume_index = -1;
			return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " unable to resume.");
		}
	}
	
	testelement element_clear_state = null;
	void execute_clear_state () {
		for (int index = 0 ; index < clear_state_elements.size() ; index ++) {
			element_clear_state = clear_state_elements.get(index);
			element_clear_state.apply_common_input();
			// execute test element
			result last_run_res = element_clear_state.execute();
			
			// check result
			if (last_run_res.get_result() != status_run.PASSED)
				return ;
		}
		element_clear_state = null;
	}
	
	boolean stop = false;
	public void stop() {
		stop = true;
		if (running_element != null)
			running_element.stop();
		if (element_clear_state != null)
			element_clear_state.stop();
	}

	int resume_index = -1;
	int resume_group_index = -1;
	boolean pause = false;
	public void pause() {
		pause = true;
		if (running_element != null) {
			resume_index = running_element_index;
			resume_group_index = running_element_group_index;
			running_element.pause();
		}
		if (element_clear_state != null)
			element_clear_state.stop();
	}
	
	public void clear() {
		
		// Clear list
		if (elements != null) {
			for (int index = 0 ; index < elements.size() ; index ++)
				elements.get(index).clear();
			elements.clear();
		}
		if (inputs != null)
			inputs.clear();
		if (tc_returns != null)
			tc_returns.clear();
		if (lib_returns != null)
			lib_returns.clear();
		
		// clear data
		name = null;
		inputs = null;
		tc_returns = null;
		lib_returns = null;
		elements = null;
		running_element = null;
		element_clear_state = null;
	}

	public result debug_execute() {
		// 1. Clear stop/pause
		stop = false;
		pause = false;
		resume_index = -1;
		resume_group_index = -1;
		running_element = null;
		running_element_index = -1;
		running_element_group_index = -1;
		
		// 2. Scan to execute all elements
		result last_run_res = null;
		for (int index = 0 ; index < elements.size() ; index ++) {
			running_element = elements.get(index);
			running_element_index = index;
			if (running_element.get_debug_run()) {
				running_element.set_debug_run(false);
				if (running_element.get_parent_group() == null) {
					boolean pass_step = false;
					if (running_element.get_type() == run_type.DEFAULT) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if (running_element.get_type() == run_type.IGNORE) {
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if (running_element.get_type() == run_type.INVERSE) {
						last_run_res = running_element.execute(); //_with_capture();
						
						// check result
						if (last_run_res.get_result() == status_run.PASSED) {
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.PASSED) {
						// execute test element
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.PASSED) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// execute test element
						last_run_res = running_element.execute();
						if (last_run_res.get_result() == status_run.PASSED)
							pass_step = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// execute test element
						last_run_res = running_element.execute();
						
						// check result
						if (last_run_res.get_result() != status_run.PASSED) {
							if (running_element.get_last_testlibelement() != null)
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
							else
								return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
						}
						pass_step = true;
					}
					else {
						last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test lib " + running_element.get_instance_name() + " is not run because run-type is not matched with last result");
						
						// Setting return from lib instance
						running_element.copy_returns_from_instance ();
						
					}
					
					if (running_element.get_last_testlibelement() != null)
						running_element.get_last_testlibelement().clear_action_capture();
					
					if (pause){
						running_element = null;
						running_element_index = -1;
						if (pass_step){
							resume_index = index + 1;
							if (index == (elements.size() - 1))
								return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
							else
								return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
						}
						else
							return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
					}
					
					if (stop) {
						stop = false;
						running_element = null;
						running_element_index = -1;
						return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
					}
				}
				else {
					testelementloopgroup running_element_group = running_element.get_parent_group();
					// Run in action group
					// Prepare index of input
					running_element_group.prepare_data();
					boolean break_loop = false;
					while (running_element_group.not_at_the_end_loop()) {
	
						com.ttv.at.log.log.get_instance().append_testelement(new com.ttv.at.log.testelement("START NEW TEST ELEMENTS LOOP : " + running_element_group.get_name()));
						com.ttv.at.log.log.get_instance().update_testelement_result(1, "ON TEST ELEMENT LOOP", running_element_group.get_name());
						
						for (int element_index = 0 ; element_index < running_element_group.get_testelements().size() ; element_index ++) {
							running_element = elements.get(index + element_index);
							running_element_index = index + element_index;
							running_element_group_index = element_index;
							boolean pass_step = false;
							
							if (running_element.get_type() == run_type.DEFAULT) {
	
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if (running_element.get_type() == run_type.IGNORE) {
	
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									if (running_element.get_last_testlibelement() != null)
										running_element.get_last_testlibelement().clear_action_capture();
									break;
								}
								
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if (running_element.get_type() == run_type.INVERSE) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								last_run_res = running_element.execute(); //_with_capture();
								
								// check result
								if (last_run_res.get_result() == status_run.PASSED) {
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
										last_run_res != null && 
										last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() != status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								if (last_run_res.get_result() == status_run.PASSED)
									pass_step = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
									last_run_res != null && 
									last_run_res.get_result() != status_run.PASSED) {
								// Check if break loop
								if (running_element.get_instance_name().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// execute test element
								last_run_res = running_element.execute();
								
								// check result
								if (last_run_res.get_result() != status_run.PASSED) {
									if (running_element.get_last_testlibelement() != null)
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
									else
										return new result(status_run.FAILED, last_run_res.get_error_code(), "Test case " + name + " failed : " + last_run_res.get_message());
								}
								pass_step = true;
							}
							
							if (running_element.get_last_testlibelement() != null)
								running_element.get_last_testlibelement().clear_action_capture();
							
							if (pause){
								running_element = null;
								running_element_index = -1;
								if (pass_step){
									resume_index = index + element_index + 1;
									resume_group_index = element_index + 1;
									if (element_index == (elements.size() - 1))
										return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed before paused.");
									else
										return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user but current lib is run passed.");
								}
								else
									return new result(status_run.PAUSE, error_code.NO_ERROR, "Test case " + name + " paused by user.");
							}
							
							if (stop) {
								stop = false;
								running_element = null;
								running_element_index = -1;
								return new result(status_run.STOP, error_code.NO_ERROR, "Test case " + name + " stop by user.");
							}
						}
						if (break_loop)
							break;
						running_element_group.inputs_move_next();
					}
					
					// reset index to continue to run
					index = index + running_element_group.get_testelements().size() - 1;
				}
			}
		}

		running_element = null;
		running_element_index = -1;
		return new result(status_run.PASSED, error_code.NO_ERROR, "Test case " + name + " passed.");
	}
	
}
