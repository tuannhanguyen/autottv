package com.ttv.at.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class debugTree extends JTree {
	// DefaultMutableTreeNode
	static debugTreeNode rootNode = new debugTreeNode("Start debug", true, true);
	public debugTree () {
		super(rootNode);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		putClientProperty("JTree.lineStyle", "Angled");
		addMouseListener(new NodeSelectionListener(this));
	}
	
	com.ttv.at.test.test loaded_test;
	ArrayList<debugTreeNode> node_list = new ArrayList<debugTreeNode>();
	ArrayList<Object> test_object_list = new ArrayList<Object>();
	
	public void set_loaded_test (com.ttv.at.test.test loaded_test) {
		this.loaded_test = loaded_test;
		reload_test();
		repaint();
	}
	
	void reload_test() {
		clear();
		if (loaded_test != null) {
			// Loading data from loaded_testarea
			ArrayList<com.ttv.at.test.testelement> cur_elements = loaded_test.get_tc_instance().get_elements();
			for (int index = 0 ; (cur_elements != null) && index < cur_elements.size() ; index ++) {
				// ADD TO TREE
				debugTreeNode cur_node = new debugTreeNode(cur_elements.get(index).get_instance_name(), true, true);
				rootNode.add(cur_node);
				
				// ADD TO STORED ARRAY
				node_list.add(cur_node);
				test_object_list.add(cur_elements.get(index));
				
				// PROCESS WITH CHILDREN
				if (cur_elements.get(index).get_lib_instance() != null)
					reload_test_lib_instance_children (cur_elements.get(index).get_lib_instance(), cur_node);
				else if (cur_elements.get(index).get_tc_instance() != null)
					reload_test_tc_instance_children (cur_elements.get(index).get_tc_instance(), cur_node);
			}
			
			// Expand all
			for(int i=0;i<getRowCount();i++)
				expandRow(i);
		}
	}
	
	void reload_test_tc_instance_children (com.ttv.at.test.testcase tc_element, debugTreeNode parent) {
		if (tc_element != null) {
			ArrayList<com.ttv.at.test.testelement> element_list = tc_element.get_elements();
			for (int i = 0 ; element_list != null && i < element_list.size() ; i ++) {
				com.ttv.at.test.testelement cur_element = element_list.get(i);

				// ADD TO TREE
				debugTreeNode cur_node = new debugTreeNode(cur_element.get_instance_name(), true, true);
				parent.add(cur_node);
				
				// ADD TO STORED ARRAY
				node_list.add(cur_node);
				test_object_list.add(cur_element);
				
				// PROCESS WITH CHILDREN
				if (cur_element.get_lib_instance() != null)
					reload_test_lib_instance_children (cur_element.get_lib_instance(), cur_node);
				else if (cur_element.get_tc_instance() != null)
					reload_test_tc_instance_children (cur_element.get_tc_instance(), cur_node);
			}
		}
	}
	
	void reload_test_lib_instance_children (com.ttv.at.test.testlibrary lib_element, debugTreeNode parent) {
		if (lib_element != null) {
			ArrayList<com.ttv.at.test.testlibelement> action_list = lib_element.get_testlibelements();
			for (int i = 0 ; action_list != null && i < action_list.size() ; i ++) {
				com.ttv.at.test.testlibelement cur_act = action_list.get(i);

				// ADD TO TREE
				debugTreeNode cur_node = new debugTreeNode(cur_act.get_name(), true, true);
				parent.add(cur_node);
				
				// ADD TO STORED ARRAY
				node_list.add(cur_node);
				test_object_list.add(cur_act);
				
				// process with children
				//if (cur_act instanceof com.ttv.at.test.action.action_lib)
				//	reload_test_lib_instance_children (((com.ttv.at.test.action.action_lib)cur_act).get_lib_instance(), cur_node);
			}
		}
	}
	
	public void clear () {
		for (int i = (node_list.size() - 1) ; i >= 0 ; i --) {
			node_list.get(i).removeFromParent();
			node_list.remove(i);
			test_object_list.remove(i);
		}
	}

	public Object get_selected_test_object () {
		TreePath path = getSelectionPath();
		if (path != null) {
			debugTreeNode selectedNode = (debugTreeNode) path.getLastPathComponent();
			if (selectedNode != null) {
				for (int i = 0 ; i < node_list.size() ; i ++)
					if (node_list.get(i) == selectedNode) {
						Object selected_obj = test_object_list.get(i);
						return selected_obj;
					}
			}
		}
		return null;
	}

}


class NodeSelectionListener extends MouseAdapter implements TreeSelectionListener {
	private debugTreeSelectionModel selectionModel;
	private JTree tree;
	int hotspot = new JCheckBox().getPreferredSize().width;
	
	public NodeSelectionListener(JTree tree){
		this.tree = tree;
		selectionModel = new debugTreeSelectionModel(tree.getModel());
		tree.setCellRenderer(new debugNodeRenderer(tree.getCellRenderer(), selectionModel));
		selectionModel.addTreeSelectionListener(this);
	}
	 
	public void mouseClicked(MouseEvent me){
		TreePath path = tree.getPathForLocation(me.getX(), me.getY());
		if(path==null)
			return;
		if(me.getX()>tree.getPathBounds(path).x+hotspot)
			return;
		
		boolean selected = selectionModel.isPathSelected(path, true);
		selectionModel.removeTreeSelectionListener(this);
		
		try{
			if(selected)
				selectionModel.removeSelectionPath(path);
			else
				selectionModel.addSelectionPath(path);
		} finally{
			selectionModel.addTreeSelectionListener(this);
			tree.treeDidChange();
		}
	}
	
	public debugTreeSelectionModel getSelectionModel(){
		return selectionModel;
	}
	 
	public void valueChanged(TreeSelectionEvent e){
		tree.treeDidChange();
	}
}


class debugTreeSelectionModel extends DefaultTreeSelectionModel{
	private TreeModel model;
	
	public debugTreeSelectionModel(TreeModel model){
		this.model = model;
		setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	// tests whether there is any unselected node in the subtree of given path
	public boolean isPartiallySelected(TreePath path){
		if(isPathSelected(path, true))
			return false;
		TreePath[] selectionPaths = getSelectionPaths();
		if(selectionPaths==null)
			return false;
		for(int j = 0; j<selectionPaths.length; j++){
			if(isDescendant(selectionPaths[j], path))
				return true;
		}
		return false;
	}
	 
	// tells whether given path is selected. if dig is true, then a path is assumed to be selected, if one of its ancestor is selected.
	public boolean isPathSelected(TreePath path, boolean dig){
		if(!dig)
			return super.isPathSelected(path);
		while(path!=null && !super.isPathSelected(path))
			path = path.getParentPath();
		return path!=null;
	}
	
	// is path1 descendant of path2
	private boolean isDescendant(TreePath path1, TreePath path2){
		Object obj1[] = path1.getPath();
		Object obj2[] = path2.getPath();
		for(int i = 0; i<obj2.length; i++){
			if(obj1[i]!=obj2[i])
			return false;
		}
		return true;
	}
	
	public void setSelectionPaths(TreePath[] pPaths){
		throw new UnsupportedOperationException("not implemented yet!!!");
	}
	
	public void addSelectionPaths(TreePath[] paths){
		// unselect all descendants of paths[]
		for(int i = 0; i<paths.length; i++){
			TreePath path = paths[i];
			TreePath[] selectionPaths = getSelectionPaths();
			if(selectionPaths==null)
				break;
			ArrayList toBeRemoved = new ArrayList();
			for(int j = 0; j<selectionPaths.length; j++){
				if(isDescendant(selectionPaths[j], path))
					toBeRemoved.add(selectionPaths[j]);
			}
			super.removeSelectionPaths((TreePath[])toBeRemoved.toArray(new TreePath[0]));
		}
		 
		// if all siblings are selected then unselect them and select parent recursively otherwize just select that path. 
		for(int i = 0; i<paths.length; i++){
			TreePath path = paths[i];
			TreePath temp = null;
			while(areSiblingsSelected(path)){
				temp = path;
				if(path.getParentPath()==null)
				break;
				path = path.getParentPath();
			}
			if(temp!=null){
				if(temp.getParentPath()!=null)
					addSelectionPath(temp.getParentPath());
				else{
					if(!isSelectionEmpty())
					removeSelectionPaths(getSelectionPaths());
					super.addSelectionPaths(new TreePath[]{temp});
				}
			}
			else
				super.addSelectionPaths(new TreePath[]{ path});
		}
	}
	 
	// tells whether all siblings of given path are selected.
	private boolean areSiblingsSelected(TreePath path){
		TreePath parent = path.getParentPath();
		if(parent==null)
			return true;
		Object node = path.getLastPathComponent();
		Object parentNode = parent.getLastPathComponent();
		
		int childCount = model.getChildCount(parentNode);
		for(int i = 0; i<childCount; i++){
			Object childNode = model.getChild(parentNode, i);
			if(childNode==node)
				continue;
			if(!isPathSelected(parent.pathByAddingChild(childNode)))
				return false;
		}
		return true;
	}
	
	public void removeSelectionPaths(TreePath[] paths){
		for(int i = 0; i<paths.length; i++){
			TreePath path = paths[i];
			if(path.getPathCount()==1)
				super.removeSelectionPaths(new TreePath[]{ path});
			else
				toggleRemoveSelection(path);
		}
	}
	 
	// if any ancestor node of given path is selected then unselect it and selection all its descendants except given path and descendants. otherwise just unselect the given path 
	private void toggleRemoveSelection(TreePath path){
		Stack stack = new Stack();
		TreePath parent = path.getParentPath();
		while(parent!=null && !isPathSelected(parent)){
			stack.push(parent);
			parent = parent.getParentPath();
		}
		if(parent!=null)
			stack.push(parent);
		else{
			super.removeSelectionPaths(new TreePath[]{path});
			return;
		}
		
		while(!stack.isEmpty()){
			TreePath temp = (TreePath)stack.pop();
			TreePath peekPath = stack.isEmpty() ? path : (TreePath)stack.peek();
			Object node = temp.getLastPathComponent();
			Object peekNode = peekPath.getLastPathComponent();
			int childCount = model.getChildCount(node);
			for(int i = 0; i<childCount; i++){
				Object childNode = model.getChild(node, i);
				if(childNode!=peekNode)
					super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)});
			}
		}
		super.removeSelectionPaths(new TreePath[]{parent});
	}
}



class debugNodeRenderer extends JPanel implements TreeCellRenderer {
	private debugTreeSelectionModel selectionModel;
	private TreeCellRenderer delegate;
	private debugCheckBox checkBox = new debugCheckBox();
	
	public debugNodeRenderer(TreeCellRenderer delegate, debugTreeSelectionModel selectionModel){
		this.delegate = delegate;
		this.selectionModel = selectionModel;
		setLayout(new BorderLayout());
		setOpaque(false);
		checkBox.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){
		Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		TreePath path = tree.getPathForRow(row);
		if(path!=null){
			if(selectionModel.isPathSelected(path, true))
				checkBox.setState(Boolean.TRUE);
			else
				checkBox.setState(Boolean.FALSE);
		}
		removeAll();
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
class debugCheckBox extends JCheckBox {
	/** This is a type-safe enumerated type */
	public static class State { private State() { } }
	public static final State NOT_SELECTED = new State();
	public static final State SELECTED = new State();
	public static final State DONT_CARE = new State();

	private final debugDecorator model;

	public debugCheckBox(String text, Icon icon, State initial){
		super(text, icon);
		// Add a listener for when the mouse is pressed
		super.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				grabFocus();
				model.nextState();
			}
		});
		// Reset the keyboard action map
		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				grabFocus();
				model.nextState();
			}
		});
		map.put("released", null);
		SwingUtilities.replaceUIActionMap(this, map);
		// set the model to the adapted model
		model = new debugDecorator(getModel());
		setModel(model);
		setState(initial);
	}
	
	public debugCheckBox(String text, State initial) {
		this(text, null, initial);
	}
	public debugCheckBox(String text) {
		this(text, DONT_CARE);
	}
	public debugCheckBox() {
		this(null);
	}

	/** No one may add mouse listeners, not even Swing! */
	public void addMouseListener(MouseListener l) { }
	/**
	 * Set the new state to either SELECTED, NOT_SELECTED or
	 * DONT_CARE.If state == null, it is treated as DONT_CARE.
	 */
	public void setState(State state) { model.setState(state); }
	public void setState(boolean b) {
		if (b) {
			setState(SELECTED);
		} else {
			setState(NOT_SELECTED);
		}
	}
	/** Return the current state, which is determined by the
	 * selection status of the model. */
	public State getState() { return model.getState(); }
	public void setSelected(boolean b) {
		if (b) {
			setState(SELECTED);
		} else {
			setState(NOT_SELECTED);
		}
	}
	/**
	 * Exactly which Design Pattern is this?Is it an Adapter,
	 * a Proxy or a Decorator?In this case, my vote lies with the
	 * Decorator, because we are extending functionality and
	 * "decorating" the original model with a more powerful model.
	 */
	private class debugDecorator implements ButtonModel {
		private final ButtonModel other;
		private debugDecorator(ButtonModel other) {
			this.other = other;
		}
		private void setState(State state) {
			if (state == NOT_SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else { // either "null" or DONT_CARE
				other.setArmed(true);
				setPressed(true);
				setSelected(true);
			}
		}
		/**
		 * The current state is embedded in the selection / armed
		 * state of the model.
		 * 
		 * We return the SELECTED state when the checkbox is selected
		 * but not armed, DONT_CARE state when the checkbox is
		 * selected and armed (grey) and NOT_SELECTED when the
		 * checkbox is deselected.
		 */
		private State getState() {
			if (isSelected() && !isArmed()) {
				// normal black tick
				return SELECTED;
			} else if (isSelected() && isArmed()) {
				// don't care grey tick
				return DONT_CARE;
			} else {
				// normal deselected
				return NOT_SELECTED;
			}
		}
		/** We rotate between NOT_SELECTED, SELECTED and DONT_CARE.*/
		private void nextState() {
			State current = getState();
			if (current == NOT_SELECTED) {
				setState(SELECTED);
			} else if (current == SELECTED) {
				setState(DONT_CARE);
			} else if (current == DONT_CARE) {
				setState(NOT_SELECTED);
			}
		}
		/** Filter: No one may change the armed status except us. */
		public void setArmed(boolean b) {
		}
		/** We disable focusing on the component when it is not
		 * enabled. */
		public void setEnabled(boolean b) {
			setFocusable(b);
			other.setEnabled(b);
		}
		/** All these methods simply delegate to the "other" model
		 * that is being decorated. */
		public boolean isArmed() { return other.isArmed(); }
		public boolean isSelected() { return other.isSelected(); }
		public boolean isEnabled() { return other.isEnabled(); }
		public boolean isPressed() { return other.isPressed(); }
		public boolean isRollover() { return other.isRollover(); }
		public void setSelected(boolean b) { other.setSelected(b); }
		public void setPressed(boolean b) { other.setPressed(b); }
		public void setRollover(boolean b) { other.setRollover(b); }
		public void setMnemonic(int key) { other.setMnemonic(key); }
		public int getMnemonic() { return other.getMnemonic(); }
		public void setActionCommand(String s) {
			other.setActionCommand(s);
		}
		public String getActionCommand() {
			return other.getActionCommand();
		}
		public void setGroup(ButtonGroup group) {
			other.setGroup(group);
		}
		public void addActionListener(ActionListener l) {
			other.addActionListener(l);
		}
		public void removeActionListener(ActionListener l) {
			other.removeActionListener(l);
		}
		public void addItemListener(ItemListener l) {
			other.addItemListener(l);
		}
		public void removeItemListener(ItemListener l) {
			other.removeItemListener(l);
		}
		public void addChangeListener(ChangeListener l) {
			other.addChangeListener(l);
		}
		public void removeChangeListener(ChangeListener l) {
			other.removeChangeListener(l);
		}
		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}
	}
}