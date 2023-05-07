package com.ttv.at.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

public class debugTreeNode extends DefaultMutableTreeNode {
	public final static int SINGLE_SELECTION = 0;
	public final static int DIG_IN_SELECTION = 4;
	
	protected int selectionMode = DIG_IN_SELECTION;
	public int selectionMode() { return selectionMode; }
	public void set_selectionMode(int selectionMode) { this.selectionMode = selectionMode; }
	
	protected boolean isSelected;
	public boolean isSelected() { return isSelected; }
	
	public debugTreeNode() {
		this(null);
	}
	public debugTreeNode(Object userObject) {
		this(userObject, true, false);
	}
	public debugTreeNode(Object userObject, boolean allowsChildren, boolean isSelected) {
		super(userObject, allowsChildren);
		this.isSelected = isSelected;
		set_selectionMode(DIG_IN_SELECTION);
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
			Enumeration e = children.elements();
			while (e.hasMoreElements()) {
				debugTreeNode node = (debugTreeNode) e.nextElement();
				node.setSelected(isSelected);
			}
		}
	}
}