package com.ttv.at.test;

import java.util.HashMap;

public class result {
	status_run result;
	public status_run get_result() { return result;}
	
	error_code ecode;
	public error_code get_error_code() { return ecode;}
	
	String message;
	public String get_message() { return message;}
	
	String return_text;
	public String get_return_text() { return return_text; }
	
	public result(HashMap<String, String> init_hash_map) {
		boolean init_result = false;
		boolean init_ecode = false;
		boolean init_message = false;
		for (int i = 0 ; i < init_hash_map.size() ; i ++) {
			String key = init_hash_map.keySet().toArray()[i].toString().toLowerCase();
			String value = init_hash_map.values().toArray()[i].toString();
			if (key.equals("result")) {
				String result = value.toUpperCase().trim();
				if (result.equals("PASSED"))
					this.result = status_run.PASSED;
				else if (result.equals("FAILED"))
					this.result = status_run.FAILED;
				else if (result.equals("STOP"))
					this.result = status_run.STOP;
				else if (result.equals("PAUSE"))
					this.result = status_run.PAUSE;
				else if (result.equals("NOT_RUN"))
					this.result = status_run.NOT_RUN;
				else
					this.result = status_run.FAILED;
				init_result = true;
			}
			else if (key.equals("ecode")) {
				String e_code = value.toUpperCase().trim();
				if (e_code.equals("NO_ERROR"))
					this.ecode = error_code.NO_ERROR;
				else if (e_code.equals("UNKNOWN_ERROR"))
					this.ecode = error_code.UNKNOWN_ERROR;
				else if (e_code.equals("NOT_SUPPORT"))
					this.ecode = error_code.NOT_SUPPORT;
				else if (e_code.equals("REQUIRED_OBJECT"))
					this.ecode = error_code.REQUIRED_OBJECT;
				else if (e_code.equals("REQUIRE_INPUT"))
					this.ecode = error_code.REQUIRE_INPUT;
				else if (e_code.equals("REQUIRE_RETURN"))
					this.ecode = error_code.REQUIRE_RETURN;
				else if (e_code.equals("OBJECT_NOT_FOUND"))
					this.ecode = error_code.OBJECT_NOT_FOUND;
				else if (e_code.equals("ACTION_FAILED"))
					this.ecode = error_code.ACTION_FAILED;
				else if (e_code.equals("TEST_FAILED"))
					this.ecode = error_code.TEST_FAILED;
				else if (e_code.equals("EXPECTED_NOT_MATCH"))
					this.ecode = error_code.EXPECTED_NOT_MATCH;
				else
					this.ecode = error_code.UNKNOWN_ERROR;
				init_ecode = true;
			}
			else if (key.equals("message")) {
				this.message = value;
				init_message = true;
			}
		}
	}
	
	public result(status_run result,  error_code e_code, String message)
	{
		this.result = result;
		this.ecode = e_code;
		this.message = message;
	}
	
	public result(status_run result,  error_code e_code, String message, String return_text)
	{
		this.result = result;
		this.ecode = e_code;
		this.message = message;
		this.return_text = return_text;
	}

	public result(String result,  String e_code, String message)
	{
		result = result.toUpperCase().trim();
		if (result.equals("PASSED"))
			this.result = status_run.PASSED;
		else if (result.equals("FAILED"))
			this.result = status_run.FAILED;
		else if (result.equals("STOP"))
			this.result = status_run.STOP;
		else if (result.equals("PAUSE"))
			this.result = status_run.PAUSE;
		else if (result.equals("NOT_RUN"))
			this.result = status_run.NOT_RUN;
		else
			this.result = status_run.FAILED;
		
		e_code = e_code.toUpperCase().trim();
		if (e_code.equals("NO_ERROR"))
			this.ecode = error_code.NO_ERROR;
		else if (e_code.equals("UNKNOWN_ERROR"))
			this.ecode = error_code.UNKNOWN_ERROR;
		else if (e_code.equals("NOT_SUPPORT"))
			this.ecode = error_code.NOT_SUPPORT;
		else if (e_code.equals("REQUIRED_OBJECT"))
			this.ecode = error_code.REQUIRED_OBJECT;
		else if (e_code.equals("REQUIRE_INPUT"))
			this.ecode = error_code.REQUIRE_INPUT;
		else if (e_code.equals("REQUIRE_RETURN"))
			this.ecode = error_code.REQUIRE_RETURN;
		else if (e_code.equals("OBJECT_NOT_FOUND"))
			this.ecode = error_code.OBJECT_NOT_FOUND;
		else if (e_code.equals("ACTION_FAILED"))
			this.ecode = error_code.ACTION_FAILED;
		else if (e_code.equals("TEST_FAILED"))
			this.ecode = error_code.TEST_FAILED;
		else if (e_code.equals("EXPECTED_NOT_MATCH"))
			this.ecode = error_code.EXPECTED_NOT_MATCH;
		else
			this.ecode = error_code.UNKNOWN_ERROR;
		
		this.message = message;
	}
}
