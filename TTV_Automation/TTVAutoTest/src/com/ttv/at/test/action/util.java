package com.ttv.at.test.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class util {
	static public boolean reg_compare(String sActual, String regValue) {
		if ((sActual == null && regValue == null) ||
				(sActual != null && sActual.length() == 0 && regValue != null && regValue.length() == 0))
			return true;
		if ((sActual == null && (regValue != null && regValue.length() == 0)) ||
				(sActual != null && sActual.length() == 0 && regValue == null))
			return false;
		try{
			if (sActual.equals(regValue) || 
					java.util.regex.Pattern.matches(regValue, sActual))
				return true;
		}
		catch (Exception e1) {e1.printStackTrace();}
		if (sActual != null && sActual.length() > 0) {
			String sCookActual = sActual.replace('\n', ' ').replace('\r', ' ').replace('	', ' ');
			try{
				if (sCookActual.equals(regValue) || java.util.regex.Pattern.matches(regValue, sCookActual))
					return true;
			}
			catch (Exception e1) {e1.printStackTrace();}
			regValue = regValue.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)").replace(">", "\\>");
			try{
				if (java.util.regex.Pattern.matches(regValue, sCookActual))
					return true;
			}
			catch (Exception e1) {e1.printStackTrace();}
		}
		return false;
	}
	
	static public boolean check_string_letters_only(String String_To_Check) {
		if (String_To_Check != null && String_To_Check.length() > 0 &&
				java.util.regex.Pattern.matches("^[a-zA-Z]+$", String_To_Check))
			return true;
		return false;
	}
	
	static public JSONObject parse_json_object(String path) {
		
		String jsonData = "";
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(path));
			while ((line = br.readLine()) != null) {
				jsonData += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		JSONObject obj = new JSONObject(jsonData);
		return obj;
	}
	
	static public String parse_json_string(String path) {
		
		String jsonData = "";
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(path));
			while ((line = br.readLine()) != null) {
				jsonData += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		JSONObject obj = new JSONObject(jsonData);
		return obj.toString();
	}
	
	static public void write_json_file(String path, JSONObject obj) {
		
		 try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
			 	obj.write(writer);
		        writer.write("\n");
		    } catch (Exception ex) {
		        System.err.println("Couldn't write "+ obj + "\n" + ex.getMessage());
		    }
	}
	
	static public void change_value(JSONObject obj, String target) {
		
		Iterator<String> keysItr = obj.keys();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			
			if(key.equals(target)) {
				obj.put(key, "");
				break;
			}
			
			// if it's jsonobject
			if (obj.optJSONObject(key) != null) {
				change_value(obj.getJSONObject(key), target);
	        }
			
			// if it's jsonarray
			if (obj.optJSONArray(key) != null) {
	            JSONArray jArray = obj.getJSONArray(key);
	            for (int i=0;i<jArray.length();i++) {
	            	change_value(jArray.getJSONObject(i), target);
	            }
	        }
			
		}
	}
}
