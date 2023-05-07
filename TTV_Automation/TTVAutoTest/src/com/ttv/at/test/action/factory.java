package com.ttv.at.test.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.ttv.at.test.parameter;
import com.ttv.at.test.run_type;
import com.ttv.at.test.testaction;
import com.ttv.at.test.testobject;
import com.ttv.at.test.testsetting;

public class factory {
	static String create_action_warning_message;
	static public String get_create_action_warning_message() { return create_action_warning_message; }
	static public testaction create_action(String name,
			ArrayList<parameter> inputs,
			ArrayList<parameter> returns,
			testobject action_object,
			run_type type) {
		create_action_warning_message = "";
		String action_name = name.toLowerCase();
		/******** SPECIAL ACTIONS ********/
		if (action_name.equals("sleep") ||
			action_name.equals("getfloatnumber") ||
			action_name.equals("getlastfloatnumber") ||
			action_name.equals("breakloop") ||
			action_name.equals("movefirstitem") ||
			action_name.equals("movelastitem") ||
			action_name.equals("movenextitem") ||
			action_name.equals("movepreviousitem") ||
			action_name.equals("moverandomitem") ||
			action_name.equals("moveitemindex") ||
			action_name.equals("checklastitem") ||
			action_name.equals("checkvaluelistcount") ||
			action_name.equals("checkvaluelistcountlessthan") ||
			action_name.equals("checkvaluelistcountmorethan") ||
			action_name.equals("postmethod") ||
			action_name.equals("putmethod") ||
			action_name.equals("getvaluescount") ||
			action_name.equals("substring") ||
			action_name.equals("plus") ||
			action_name.equals("subtract") ||
			action_name.equals("multiply") ||
			action_name.equals("compare") ||
			action_name.equals("compareignorecase") ||
			action_name.equals("comparesubstring") ||
			action_name.equals("comparesubstringignorecase") ||
			action_name.equals("comparefloat") ||
			action_name.equals("getnextsiblingfromxpath") ||
			action_name.equals("getparentfromxpath") ||
			action_name.equals("makeuniqueemail")||
			action_name.equals("countchars")||
			action_name.equals("strcat")||
			action_name.equals("strsplit")||
			action_name.equals("strreplace")||
			action_name.equals("makeuniquestring")||
			action_name.equals("strgreaterequal")||
			action_name.equals("floatgreaterequal")||
			action_name.equals("capturedesktop")||
			action_name.equals("setenv")||
			action_name.equals("querydbselect")||
			action_name.equals("querydbupdate")||
			action_name.equals("getnow")){
			return new action_core(name, inputs, returns, type);
		}
		
		/******** NORMAL ACTIONS ********/
		// Check the object runtime type
		String action_class_name = "com.ttv.at.test.action." + testsetting.get_runtime().toLowerCase();
		ClassLoader classLoader = factory.class.getClassLoader();
		
		try {
			// Get the class of action
			Class<?> action_class = classLoader.loadClass(action_class_name);
			
			// get the constructor
			Constructor<?> init_action_object = action_class.getDeclaredConstructor(String.class, ArrayList.class, ArrayList.class, testobject.class, run_type.class); 
			
			// Create object of the action class
			Object act_object = init_action_object.newInstance(name, inputs, returns, action_object, type);
			return (testaction)act_object;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check if runtime '" + testsetting.get_runtime().toLowerCase() + "' is support, exception: " + e;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create_action_warning_message = "Class '" + action_class_name + "' is not found ,Please check the constructor of the class, exception: " + e;
		}
		return null;
	}
}
