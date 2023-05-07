package com.ttv.at.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class testaction {
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
	
	public testaction (String name,
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
	
	public testaction (String name,
				ArrayList<parameter> inputs,
				ArrayList<parameter> returns,
				run_type type) {
		this.name	= name;
		this.inputs	= inputs;
		this.returns	= returns;
		this.type = type;
	}
	
	public abstract result validate();
	public abstract result execute();
	public abstract result execute(int timeout);
	
	static protected boolean stop = false;
	static protected boolean pause = false;

	static public void stop() {
		stop = true;
	}

	static public void pause() {
		pause = true;
	}
	


	private boolean capture_image = false;
	public boolean get_capture_image() { return capture_image; }
	public void set_capture_image (boolean capture_image) { this.capture_image = capture_image; }
	
	BufferedImage before_action_capture = null;
	public BufferedImage get_before_action_capture () { return before_action_capture; }
	public void set_before_action_capture (BufferedImage before_action_capture) {  this.before_action_capture = before_action_capture; }
	/*
	BufferedImage after_action_capture = null;
	public BufferedImage get_after_action_capture () { return after_action_capture; }
	protected void set_after_action_capture (BufferedImage after_action_capture) {  this.after_action_capture = after_action_capture; }
	*/
	public void clear_before_action_capture () {
		if (before_action_capture != null)
			before_action_capture = null;
		/* Delete file before_action_capture
		if (before_action_capture != null && before_action_capture.length() > 0) {
			File bac = new File(testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + before_action_capture);
			if (bac.exists())
				bac.delete();
		}
		before_action_capture = null;*/
	}
	/*
	public void clear_after_action_capture () {
		if (after_action_capture != null)
			after_action_capture = null;
		/* Delete file after_action_capture
		if (after_action_capture != null && after_action_capture.length() > 0) {
			File aac = new File(testsetting.get_default_log_images_folder() + System.getProperty("file.separator") + after_action_capture);
			if (aac.exists())
				aac.delete();
		}
		after_action_capture = null;*
	}*/
	public void clear_action_capture () {
		if (!name.equals("capturedesktop")) {
			clear_before_action_capture ();
			// clear_after_action_capture ();
		}
	}
}
