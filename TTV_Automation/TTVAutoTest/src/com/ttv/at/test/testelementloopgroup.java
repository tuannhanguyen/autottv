package com.ttv.at.test;

import java.util.ArrayList;

public class testelementloopgroup {
	String name = null;
	public String get_name() { return name; }
	
	ArrayList<testelement> testelements = null;
	public ArrayList<testelement> get_testelements() { return testelements; }
	
	public testelementloopgroup (testelement element) {
		testelements = new ArrayList<testelement>();
		testelements.add(element);
	}
	
	public testelementloopgroup (String name, ArrayList<testelement> testelements) {
		this.name = name;
		this.testelements = testelements;
		
		for (int i = 0 ; i < testelements.size() ; i ++)
			testelements.get(i).set_parent_group(this);
	}
	
	public void prepare_data() {
		
	}
	
	public void inputs_move_next() {
		
	}
	
	public int get_loop_count() {
		return -1;
	}
	
	public boolean not_at_the_end_loop() {
		return true;
	}

	public void clear() {
		// Clear list
		if (testelements != null)
			testelements.clear();
		
		// Clear data
		name = null;
		testelements = null;
	}
}
