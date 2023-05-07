package com.ttv.at.test;

import java.util.ArrayList;

import com.ttv.at.test.action.os_actions;

public class testlibrary {
	String name;
	public String get_name() { return name; }
	
	ArrayList<parameter> inputs;
	public ArrayList<parameter> get_inputs() { return inputs;}
	
	ArrayList<parameter> lib_returns;
	public ArrayList<parameter> get_lib_returns() { return lib_returns;}
	
	ArrayList<testlibelement> testlibelements;
	public ArrayList<testlibelement> get_testlibelements() { return testlibelements;}
	
	ArrayList<parameter> element_returns;
	public ArrayList<parameter> get_element_returns() { return element_returns;}
	
	int startRow;
	public int get_startRow() { return startRow; }
	
	int endRow;
	public int get_endRow() { return endRow; }
	
	public testlibrary (String name,
			ArrayList<parameter> inputs,
			ArrayList<parameter> lib_returns,
			ArrayList<testlibelement> testlibelements, 
			ArrayList<parameter> element_returns) {
		this.name = name;
		this.inputs = inputs;
		this.lib_returns = lib_returns;
		this.testlibelements = testlibelements;
		this.element_returns = element_returns;
	}
	
	public testlibrary (String name,
			ArrayList<parameter> inputs,
			ArrayList<parameter> lib_returns,
			ArrayList<testlibelement> testlibelements, 
			ArrayList<parameter> element_returns,
			int startRow,
			int endRow) {
		this.name = name;
		this.inputs = inputs;
		this.lib_returns = lib_returns;
		this.testlibelements = testlibelements;
		this.element_returns = element_returns;
		this.startRow = startRow;
		this.endRow = endRow;
	}
	
	// 1. Clear inputs
	// 2. Clear lib_return
	// 3. Clear element_returns
	// 4. Clear running_element
	// 5. Clear stop/pause
	// 6. clear testlibelements
	public void reset() {
		// 1. Clear inputs
		for (int i = 0 ; i < inputs.size() ; i ++)
			if (inputs.get(i).get_key() != null && inputs.get(i).get_key().length() > 0)
				inputs.get(i).clear();

		// 2. Clear lib_return
		if (lib_returns != null && lib_returns.size() > 0)
			for (int index = 0 ; index < lib_returns.size() ; index ++)
				if (lib_returns.get(index) != null)
				lib_returns.get(index).clear();
		
		// 3. Clear element_returns
		if (element_returns != null)
			for (int i = 0 ; i < element_returns.size() ; i ++)
				if(element_returns.get(i) != null)
					element_returns.get(i).clear();

		// 4. Clear running_element
		running_element = null;
		running_element_index = -1;

		// 5. Clear stop/pause
		stop = false;
		pause = false;
		resume_index = -1;
		
		// 6. clear testlibelements
		for (int i = 0 ; i < testlibelements.size() ; i ++)
			if (testlibelements.get(i) != null)
				testlibelements.get(i).reset();
	}
	
	public boolean set_inputs (ArrayList<parameter> inputs) {
		if (inputs.size() >= this.inputs.size()){
			for (int index = 0 ; index < this.inputs.size() ; index ++)
				this.inputs.get(index).copy_from(inputs.get(index));
			return true;
		}
		return false;
	}
	
	// Execute step
	// Scan all action group to run, each action group as follow
	testlibelement running_element = null;
	public testlibelement get_running_element() { return running_element; }
	int running_element_index = -1;
	int running_element_group_index = -1;
	static result execute_all_null (testlibelement act) {
			com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(act.get_start_action_log_message ()));
			com.ttv.at.log.log.get_instance().update_action_result(true, "Action not run because all inputs are null", null, null);
			return new result(status_run.NOT_RUN, error_code.NO_ERROR, "Action not run because all inputs are null");
	}
	public result check_compatible () {
		for (testlibelement scan_element : testlibelements) {
			result check_result = scan_element.check_compatible ();
			if (check_result.get_result() != status_run.PASSED)
				return check_result;
		}
		return new result(status_run.PASSED, error_code.NO_ERROR, "");
	}
	public result execute() {
		stop = false;
		pause = false;
		resume_index = -1;
		running_element = null;
		running_element_index = -1;
		running_element_group_index = -1;
		// run all testlibelements

		result last_run_res = null;
		
		for (int index = 0 ; index < testlibelements.size() ; index ++) {
			running_element = testlibelements.get(index);
			running_element_index = index;

			
			
			if (running_element.get_parent_group() == null) {
				// Check if all null that ignore action
				boolean all_null = true;
				if (running_element.get_inputs() != null && running_element.get_inputs().size() > 0) {
					for (int input_index = 0 ; input_index < running_element.get_inputs().size() ; input_index ++)
						if (running_element.get_inputs().get(input_index).get_value() != null) {
							all_null = false;
							break;
						}
				}
				else
					all_null = false;
				
				if (running_element.get_type() == run_type.DEFAULT) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						// Check the result
						if (running_element.get_name().equals("capturedesktop"))
							com.ttv.at.log.log.get_instance().update_action_result_print_image(true, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
						else {
							if (last_run_res.get_result() == status_run.PASSED) {
								// Update log
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
							}
							else {
								com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
								return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
							}
						}
					}
				}
				else if (running_element.get_type() == run_type.IGNORE) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED)
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						else
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
					}
				}
				else if (running_element.get_type() == run_type.INVERSE) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.FAILED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.PASSED) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED)
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						else
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.PASSED) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.FAILED) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED)
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						else
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.FAILED) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.NOT_RUN) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED)
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						else
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
					}
				}
				else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
						last_run_res != null && 
						last_run_res.get_result() == status_run.NOT_RUN) {
					// Prepare log
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
		
					// Execute action
					if (all_null)
						last_run_res = execute_all_null (running_element);
					else {
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
					}
				}
				else {
					last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test action " + running_element.get_name() + " is not run because run-type is not matched with last result");
				}
				
				// Clear action_capture before running next action
				if (index < (testlibelements.size() - 1))
					running_element.clear_action_capture();
				
				
				if (pause) {
					running_element = null;
					resume_index = index;
					if (resume_index < running_element_index)
						resume_index = running_element_index;
					running_element_index = -1;
					if (last_run_res != null && (last_run_res.get_result() == status_run.PASSED || last_run_res.get_result() == status_run.NOT_RUN)){
						resume_index ++ ;
						if (index == (testlibelements.size() - 1))
							return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed before paused");
						else
							return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user at the pass action (resume_index = '"+resume_index+"')");
					}
					else
						return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user (resume_index = '"+resume_index+"')");
				}
				
				if (stop) {
					stop = false;
					running_element = null;
					running_element_index = -1;
					return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' stop by user");
				}
			}
			else {
				testlibelementloopgroup running_element_group = running_element.get_parent_group();
				// Run in action group
				// Prepare index of input
				boolean break_loop = false;
				while (true) {
					
					com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action("START NEW TEST ACTIONS LOOP ROUND : " + running_element_group.get_name()));
					com.ttv.at.log.log.get_instance().update_action_result(true, "ON LOOP ROUND", null, null);
					
					for (int action_index = 0 ; action_index < running_element_group.get_testlibelements().size() ; action_index ++) {
						running_element = testlibelements.get(index + action_index);
						running_element_index = index + action_index;
						running_element_group_index = action_index;
						boolean pass_step = false;
						
						// Check if all null that ignore action
						boolean all_null = true;
						if (running_element.get_inputs() != null && running_element.get_inputs().size() > 0) {
							for (int input_index = 0 ; input_index < running_element.get_inputs().size() ; input_index ++)
								if (running_element.get_inputs().get(input_index).get_value() != null) {
									all_null = false;
									break;
								}
						}
						else
							all_null = false;

						if (running_element.get_type() == run_type.DEFAULT) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
							}
						}
						else if (running_element.get_type() == run_type.IGNORE) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
							last_run_res = running_element.execute();
							
							// Update log
							if (last_run_res.get_result() == status_run.PASSED)
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
							else
								com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
							}
						}
						else if (running_element.get_type() == run_type.INVERSE) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.FAILED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.PASSED) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								
								// Update log
								if (last_run_res.get_result() == status_run.PASSED)
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								else
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.PASSED) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.FAILED) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								
								// Update log
								if (last_run_res.get_result() == status_run.PASSED)
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								else
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.FAILED) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.NOT_RUN) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								
								// Update log
								if (last_run_res.get_result() == status_run.PASSED)
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								else
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
							}
						}
						else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.NOT_RUN) {
							// Check if break loop
							if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
								last_run_res = running_element.execute();
								break_loop = true;
								break;
							}
							// Prepare log
							com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
				
							// Execute action
							if (all_null)
								last_run_res = execute_all_null (running_element);
							else {
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
							}
						}
						else {
							last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test action " + running_element.get_name() + " is not run because run-type is not matched with last result");
						}
						
						// Clear action_capture before running next action
						if (index < (testlibelements.size() - 1))
							running_element.clear_action_capture();
						
						
						if (pause) {
							running_element = null;
							resume_index = index;
							if (resume_index < running_element_index)
								resume_index = running_element_index;
							running_element_index = -1;
							if (last_run_res != null && last_run_res.get_result() == status_run.PASSED){
								resume_index ++ ;
								if (index == (testlibelements.size() - 1))
									return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed before paused");
								else
									return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user at the pass action (resume_index = '"+resume_index+"')");
							}
							else
								return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user (resume_index = '"+resume_index+"')");
						}
						
						if (stop) {
							stop = false;
							running_element = null;
							running_element_index = -1;
							return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' stop by user");
						}
					}
					if (break_loop)
						break;
				}
				
				// reset index to continue to run
				index = index + running_element_group.get_testlibelements().size() - 1;
			}
		}
			/*
		running_element = null;
		running_element_index = -1;*/
		return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed");
	}
	
	// Resume step
	// Scan all action group to run, each action group as follow
	public result resume() {

		stop = false;
		pause = false;
		resume_index = -1;
		
		if (resume_index < 0)
			resume_index = 0;
		if (resume_index < testlibelements.size()) {
			int start_index = resume_index;
			resume_index = -1;
			// run all testlibelements

			result last_run_res = null;
			
			for (int index = start_index ; index < testlibelements.size() ; index ++) {
				running_element = testlibelements.get(index);
				running_element_index = index;
				
				if (running_element.get_parent_group() == null) {
					boolean pass_action = false;
					if (running_element.get_type() == run_type.DEFAULT) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
						pass_action = true;
					}
					else if (running_element.get_type() == run_type.IGNORE) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
						}
						pass_action = true;
					}
					else if (running_element.get_type() == run_type.INVERSE) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.FAILED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
								last_run_res != null && 
								last_run_res.get_result() == status_run.PASSED) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.PASSED) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.FAILED) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.NOT_RUN) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						
						// Update log
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
						}
						pass_action = true;
					}
					else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
							last_run_res != null && 
							last_run_res.get_result() == status_run.NOT_RUN) {
						// Prepare log
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
			
						// Execute action
						last_run_res = running_element.execute();
						// Check the result
						if (last_run_res.get_result() == status_run.PASSED) {
							// Update log
							com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message());
						}
						else {
							com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
									running_element.get_before_action_capture(), os_actions.capture_screen());
							return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
						}
						pass_action = true;
					}
					else {
						last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test action " + running_element.get_name() + " is not run because run-type is not matched with last result");
					}
					
					// Clear action_capture before running next action
					if (index < (testlibelements.size() - 1))
						running_element.clear_action_capture();
					
					
					if (pause) {
						running_element = null;
						resume_index = index;
						if (resume_index < running_element_index)
							resume_index = running_element_index;
						running_element_index = -1;
						if (last_run_res != null && last_run_res.get_result() == status_run.PASSED && pass_action){
							resume_index ++ ;
							if (index == (testlibelements.size() - 1))
								return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed before paused");
							else
								return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user at the pass action (resume_index = '"+resume_index+"')");
						}
						else
							return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user (resume_index = '"+resume_index+"')");
					}
					
					if (stop) {
						stop = false;
						running_element = null;
						running_element_index = -1;
						return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' stop by user");
					}
				}
				else {
					testlibelementloopgroup running_element_group = running_element.get_parent_group();
					// Run in action group
					// Prepare index of input
					boolean break_loop = false;
					while (true) {
						
						com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action("START NEW TEST ACTIONS LOOP ROUND : " + running_element_group.get_name()));
						com.ttv.at.log.log.get_instance().update_action_result(true, "ON LOOP ROUND", null, null);
						
						for (int action_index = 0 ; action_index < running_element_group.get_testlibelements().size() ; action_index ++) {
							running_element = testlibelements.get(index + action_index);
							running_element_index = index + action_index;
							running_element_group_index = action_index;
							boolean pass_step = false;
							boolean pass_action = false;
							if (running_element.get_type() == run_type.DEFAULT) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
								pass_action = true;
							}
							else if (running_element.get_type() == run_type.IGNORE) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								
								// Update log
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
								pass_action = true;
							}
							else if (running_element.get_type() == run_type.INVERSE) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.FAILED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED_IGNORE) &&
										last_run_res != null && 
										last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								
								// Update log
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_PASSED) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.PASSED) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.FAILED) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								
								// Update log
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_FAILED) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.FAILED) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_NOT_RUN_IGNORE) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.NOT_RUN) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								
								// Update log
								com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
										running_element.get_before_action_capture(), os_actions.capture_screen());
								pass_action = true;
							}
							else if ((	running_element.get_type() == run_type.LAST_NOT_RUN) &&
									last_run_res != null && 
									last_run_res.get_result() == status_run.NOT_RUN) {
								// Check if break loop
								if (running_element.get_name().toLowerCase().indexOf("breakloop") >= 0) {
									last_run_res = running_element.execute();
									break_loop = true;
									break;
								}
								// Prepare log
								com.ttv.at.log.log.get_instance().append_action(new com.ttv.at.log.action(running_element.get_start_action_log_message ()));
					
								// Execute action
								last_run_res = running_element.execute();
								// Check the result
								if (last_run_res.get_result() == status_run.PASSED) {
									// Update log
									com.ttv.at.log.log.get_instance().update_action_result(true, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
								}
								else {
									com.ttv.at.log.log.get_instance().update_action_result(false, last_run_res.get_message(), 
											running_element.get_before_action_capture(), os_actions.capture_screen());
									return new result(status_run.FAILED, error_code.ACTION_FAILED, "Test Library  '"+name+"' failed at Action '" + running_element.get_name() + "'");
								}
								pass_action = true;
							}
							else {
								last_run_res = new result(status_run.NOT_RUN, error_code.NO_ERROR, "Test action " + running_element.get_name() + " is not run because run-type is not matched with last result");
							}
							
							// Clear action_capture before running next action
							if (index < (testlibelements.size() - 1))
								running_element.clear_action_capture();
							
							
							if (pause) {
								running_element = null;
								resume_index = index;
								if (resume_index < running_element_index)
									resume_index = running_element_index;
								running_element_index = -1;
								if (last_run_res != null && last_run_res.get_result() == status_run.PASSED && pass_action){
									resume_index ++ ;
									if (index == (testlibelements.size() - 1))
										return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed before paused");
									else
										return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user at the pass action (resume_index = '"+resume_index+"')");
								}
								else
									return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' paused by user (resume_index = '"+resume_index+"')");
							}
							
							if (stop) {
								stop = false;
								running_element = null;
								running_element_index = -1;
								return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' stop by user");
							}
						}
						if (break_loop)
							break;
					}
					
					// reset index to continue to run
					index = index + running_element_group.get_testlibelements().size() - 1;
				}
			}
			running_element = null;
			running_element_index = -1;
			return new result(status_run.PASSED, error_code.NO_ERROR, "Test Library '"+name+"' run passed");
		}
		else {
			resume_index = -1;
			return new result(status_run.STOP, error_code.NO_ERROR, "Test Library '"+name+"' unable to resume (resume_index = '"+resume_index+"')");
		}
	}
	
	boolean stop = false;
	public void stop() {
		stop = true;
		if (running_element != null)
			running_element.stop();
	}

	int resume_index = -1;
	boolean pause = false;
	public void pause() {
		pause = true;
		if (running_element != null) {
			resume_index = running_element_index;
			running_element.pause();
		}
	}
	
	public void clear() {
		// Clear list
		if (testlibelements != null) {
			for (int index = 0 ; index < testlibelements.size() ; index ++)
				testlibelements.get(index).clear_action_capture();
			testlibelements.clear();
		}
		if (inputs != null)
			inputs.clear();
		if (element_returns != null)
			element_returns.clear();
		
		// Clear data
		name = null;
		inputs = null;
		if (lib_returns != null)
			lib_returns.clear();
		lib_returns = null;
		testlibelements = null;
		element_returns = null;
		running_element = null;
	}

	public int get_lib_call_lib_depth (int call_level) {
		if (call_level >= testcase.max_test_call_test_depth)
			return call_level;
		if (testlibelements != null && testlibelements.size() > 0) {
			int max_depth = 0;
			for (testlibelement cur_act : testlibelements) {
				int element_call_depth = cur_act.get_action_call_library_depth (call_level);
				if (element_call_depth > max_depth)
					max_depth = element_call_depth;
			}
			return max_depth;
		}
		return call_level;
	}
}
