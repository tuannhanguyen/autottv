package com.ttv.at;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.table.JTableHeader;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

import com.ttv.at.gui.debugTree;
import com.ttv.at.log.testsuiteset;
import com.ttv.at.tablemodel.debug_params;

public class TestDebugger implements ActionListener {
	static TestDebugger instance = null;
	static public TestDebugger get_instance () {
		if (instance == null)
			instance = new TestDebugger();
		return instance;
	}
	
	com.ttv.at.test.test test_instance;
	public com.ttv.at.test.test get_test_instance() { return test_instance; }
	public void set_test_instance (com.ttv.at.test.test test_instance) {
		set_app_state_ready();
		this.test_instance = test_instance;
		// Clean all old information
		tbl_model_test_input.clear();
		tbl_model_test_return.clear();
		// tbl_model_test_content.clear();
		tree_test_content.clear();
		tbl_model_element_input.clear();
		tbl_model_element_return.clear();
		
		// Load new information
		if (mainFrame != null && 
				test_instance != null &&
				test_instance.get_tc_instance() != null) {
			test_instance.set_input_to_tc_instance();
			
			mainFrame.setTitle("Test Debugger - " + test_instance.get_tc_instance().get_name());
			
			// Add test input list
			tbl_model_test_input.set_loaded_params(test_instance.get_tc_instance().get_inputs(), test_instance.get_inputs());
			
			// Add test return list
			tbl_model_test_return.set_loaded_params(test_instance.get_tc_instance().get_tc_returns());
			
			// Add Content
			tree_test_content.set_loaded_test(test_instance);
		}
	}
	public void refresh () {
		set_app_state_ready();
		tbl_model_test_input.clear();
		tbl_model_test_return.clear();
		// tbl_model_test_content.clear();
		tree_test_content.clear();
		tbl_model_element_input.clear();
		tbl_model_element_return.clear();
		
		// Load new information
		if (mainFrame != null && 
				test_instance != null &&
				test_instance.get_tc_instance() != null) {
			test_instance.set_input_to_tc_instance();
			
			mainFrame.setTitle("Test Debugger - " + test_instance.get_tc_instance().get_name());
			
			// Add test input list
			tbl_model_test_input.set_loaded_params(test_instance.get_tc_instance().get_inputs(), test_instance.get_inputs());
			
			// Add test return list
			tbl_model_test_return.set_loaded_params(test_instance.get_tc_instance().get_tc_returns());
			
			// Add Content
			tree_test_content.set_loaded_test(test_instance);
		}
	}
	
	public TestDebugger () {
		CreateGUIComponent ();
	}
	public TestDebugger (com.ttv.at.test.test test_instance) {
		this.test_instance = test_instance;
		CreateGUIComponent ();
	}
	
	public void show () {
		mainFrame.show();
	}
	
	public void hide () {
		mainFrame.hide();
	}
	
	// ******** INIT GUI ********
	JFrame mainFrame;
	Container mainContainer;
	
	void CreateGUIComponent () {
		// Create main display frame
		mainFrame = new JFrame("Test Debugger");
		mainContainer = mainFrame.getContentPane();
		
		CreateGUIComponent_main_panel ();
		
		// *** Display main form *** ///
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		Dimension frameSize = new Dimension(1200, 700);
		mainFrame.setPreferredSize(frameSize);
		mainFrame.setResizable(false);
		mainFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		mainFrame.pack();

		// Init log setting
		com.ttv.at.log.log.get_instance().addlog_event_listener(new com.ttv.at.log.log_event_listener() {

			@Override
			public void newTestLogOccur(com.ttv.at.log.test evt) {
				// TODO Auto-generated method stub
				guiutil.txtOutput_append_12black_3lines(evt.get_full_start_message(), txtOutput, txtOutput_style, txtOutput_doc);
				com.ttv.at.tablemodel.testarea.update_instance_detail();
			}

			@Override
			public void updateTestLogOccur(com.ttv.at.log.test evt) {
				// TODO Auto-generated method stub
				if (evt.get_passed() == 1)
					guiutil.txtOutput_append_12blue(evt.get_full_end_message(), txtOutput, txtOutput_style, txtOutput_doc);
				else {
					String before_absolute_link = evt.get_before_failed_image();
					if (before_absolute_link != null && before_absolute_link.length() > 0)
						before_absolute_link = com.ttv.at.test.testsetting.get_default_log_images_folder() + com.ttv.at.util.os.os_file_separator + before_absolute_link;
					String after_absolute_link = evt.get_after_failed_image();
					if (after_absolute_link != null && after_absolute_link.length() > 0)
						after_absolute_link = com.ttv.at.test.testsetting.get_default_log_images_folder() + com.ttv.at.util.os.os_file_separator + after_absolute_link;
					guiutil.txtOutput_append_12red(evt.get_full_end_message(), before_absolute_link, after_absolute_link, txtOutput, txtOutput_style, txtOutput_doc);
				}
			}

			@Override
			public void newTestElementLogOccur(com.ttv.at.log.testelement evt) {
				// TODO Auto-generated method stub
				guiutil.txtOutput_append_10black_bold(evt.get_full_message_start(), txtOutput, txtOutput_style, txtOutput_doc);
			}

			@Override
			public void updateTestElementLogOccur(com.ttv.at.log.testelement evt) {
				// TODO Auto-generated method stub
				if (evt.get_passed() == 1)
					guiutil.txtOutput_append_10blue_bold(evt.get_full_message_result(), txtOutput, txtOutput_style, txtOutput_doc);
				else
					guiutil.txtOutput_append_10red_bold(evt.get_full_message_result(), txtOutput, txtOutput_style, txtOutput_doc);

			}

			@Override
			public void newActionLogOccur(com.ttv.at.log.action evt) {
				// TODO Auto-generated method stub
				guiutil.txtOutput_append_10black(evt.get_full_message_start(), txtOutput, txtOutput_style, txtOutput_doc);
			}

			@Override
			public void updateActionLogOccur(com.ttv.at.log.action evt) {
				// TODO Auto-generated method stub
				if (evt.get_print_after_action_image()) {

					String after_absolute_link = evt.get_after_action_image();
					if (after_absolute_link != null && after_absolute_link.length() > 0)
						after_absolute_link = com.ttv.at.test.testsetting.get_default_log_images_folder() + com.ttv.at.util.os.os_file_separator + after_absolute_link;
					
					if (evt.get_passed())
						guiutil.txtOutput_append_10blue(evt.get_full_message_result(), after_absolute_link, txtOutput, txtOutput_style, txtOutput_doc);
					else
						guiutil.txtOutput_append_10red(evt.get_full_message_result(), after_absolute_link, txtOutput, txtOutput_style, txtOutput_doc);
				}
				else {
					if (evt.get_passed())
						guiutil.txtOutput_append_10blue(evt.get_full_message_result(), txtOutput, txtOutput_style, txtOutput_doc);
					else
						guiutil.txtOutput_append_10red(evt.get_full_message_result(), txtOutput, txtOutput_style, txtOutput_doc);
				}
			}

			@Override
			public void newTestSuiteLogOccur(com.ttv.at.log.testsuite evt) {
				// TODO Auto-generated method stub
			}

			@Override
			public void endTestSuiteLogOccur(com.ttv.at.log.testsuite evt) {
				
			}

			@Override
			public void newTestSuiteSetLogOccur(testsuiteset evt) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void endTestSuiteSetLogOccur(testsuiteset evt) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	

	JPanel mainPanel;
	void CreateGUIComponent_main_panel () {
		mainPanel = new JPanel(new BorderLayout());

		// Create tool bar
		CreateGUIComponent_main_panel_toolbar ();
		mainPanel.add(mainToolBarPanel, BorderLayout.NORTH);
		mainContainer.add(mainPanel, BorderLayout.CENTER);

		// Create table panel
		CreateGUIComponent_main_panel_table_panel ();
		mainPanel.add(mainTablePanel, BorderLayout.CENTER);
		
		// Create table panel
		// CreateGUIComponent_main_panel_table_panel ();
		// mainPanel.add(mainTablePanel, BorderLayout.CENTER);
	}
	
	JPanel mainToolBarPanel;
	JToolBar mainToolBar;
	JButton btnStart, btnStop, btnLoadScript;
	JProgressBar mainProgress;
	void CreateGUIComponent_main_panel_toolbar () {
		mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);
		mainToolBar.add(new javax.swing.JToolBar.Separator());

		btnStart = new JButton();
		btnStart.setToolTipText("Run");
		// btnStart.setEnabled(false);
		btnStart.addActionListener(this);
		btnStart.setIcon(new javax.swing.ImageIcon("images\\Play_48x48.png"));
		btnStart.setBorder(null);//javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

		mainToolBar.add(btnStart);

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		btnStop = new JButton();
		btnStop.setToolTipText("Stop");
		// btnStop.setEnabled(false);
		btnStop.addActionListener(this);
		btnStop.setIcon(new javax.swing.ImageIcon("images\\Stop_48x48.png")); // NOI18N
		btnStop.setBorder(null);//javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

		mainToolBar.add(btnStop);

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		btnLoadScript = new JButton();
		btnLoadScript.setToolTipText("Reload the test case instance");
		// btnPause.setEnabled(false);
		btnLoadScript.addActionListener(this);
		btnLoadScript.setIcon(new javax.swing.ImageIcon("images\\Load_48x48.png"));
		btnLoadScript.setBorder(null);//javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

		mainToolBar.add(btnLoadScript);

		mainToolBar.add(new javax.swing.JToolBar.Separator());

		// Init Proress Bar
		mainProgress = new JProgressBar();
		mainProgress.setPreferredSize(new Dimension(600, 20));

		mainToolBarPanel = new JPanel( new BorderLayout());
		mainToolBarPanel.add(mainToolBar, BorderLayout.NORTH);
		mainToolBarPanel.add(mainProgress, BorderLayout.SOUTH);
	}

	JPanel mainTablePanel;
	JSplitPane mainSplitPane, mainSplitPane_top, mainSplitPane_left, mainSplitPane_right;
	void CreateGUIComponent_main_panel_table_panel() {
		mainTablePanel = new JPanel(new BorderLayout());
		
		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setDividerLocation(520);

		mainSplitPane_top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane_top.setDividerLocation(250);
		
		mainSplitPane_left = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane_left.setDividerLocation(350);

		// init for test input/return
		CreateGUIComponent_main_panel_table_panel_test_inputs_table ();
		mainSplitPane_left.setTopComponent(test_input_panel);
		CreateGUIComponent_main_panel_table_panel_test_returns_table ();
		mainSplitPane_left.setBottomComponent(test_return_panel);
		

		mainSplitPane_right = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplitPane_right.setDividerLocation(600);
		
		// init for test contain
		CreateGUIComponent_main_panel_table_panel_test_content_table ();
		mainSplitPane_right.setLeftComponent(test_content_panel);


		JSplitPane mainSplitPane_right_rignt = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane_right_rignt.setDividerLocation(300);
		mainSplitPane_right.setRightComponent(mainSplitPane_right_rignt);
		
		// init for element inputs
		CreateGUIComponent_main_panel_table_panel_element_inputs_table ();
		mainSplitPane_right_rignt.setTopComponent(element_input_panel);
		
		// init for element returns
		CreateGUIComponent_main_panel_table_panel_element_returns_table ();
		mainSplitPane_right_rignt.setBottomComponent(element_return_panel);
		
		// init log output
		CreateGUIComponent_main_panel_table_panel_output();
		JPanel noWrapPanel = new JPanel( new BorderLayout() );
		noWrapPanel.add(txtOutput); // anti wrap text
		JScrollPane panelOutput = new JScrollPane(noWrapPanel);
		
		// add tables to up split pane
		mainSplitPane_top.setLeftComponent(mainSplitPane_left);
		mainSplitPane_top.setRightComponent(mainSplitPane_right);
		mainSplitPane.setTopComponent(mainSplitPane_top);
		mainSplitPane.setBottomComponent(panelOutput);
		mainTablePanel.add(mainSplitPane, BorderLayout.CENTER);
	}
	
	JPanel test_input_panel;
	JTable tbl_test_inputs;
	com.ttv.at.tablemodel.debug_params tbl_model_test_input;
	void CreateGUIComponent_main_panel_table_panel_test_inputs_table () {
		test_input_panel = new JPanel(new BorderLayout());
		
		JLabel test_input_label = new JLabel("Test Input List");
		test_input_panel.add(test_input_label, BorderLayout.NORTH);
		
		tbl_model_test_input = new com.ttv.at.tablemodel.debug_params();
		tbl_test_inputs = new JTable (tbl_model_test_input);

		tbl_test_inputs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tbl_test_inputs.getTableHeader().setPreferredSize(new Dimension(300, 22));
		tbl_test_inputs.getTableHeader().setReorderingAllowed(false);
		tbl_test_inputs.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		
		tbl_test_inputs.getColumnModel().getColumn(0).setPreferredWidth(100);
		tbl_test_inputs.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tbl_test_inputs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		test_input_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
	}
	

	JPanel test_return_panel;
	JTable tbl_test_returns;
	com.ttv.at.tablemodel.debug_params tbl_model_test_return;
	void CreateGUIComponent_main_panel_table_panel_test_returns_table () {
		test_return_panel = new JPanel(new BorderLayout());
		
		JLabel test_return_label = new JLabel("Test Return List");
		test_return_panel.add(test_return_label, BorderLayout.NORTH);
		
		tbl_model_test_return = new com.ttv.at.tablemodel.debug_params();
		tbl_test_returns = new JTable (tbl_model_test_return);

		tbl_test_returns.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tbl_test_returns.getTableHeader().setPreferredSize(new Dimension(300, 22));
		tbl_test_returns.getTableHeader().setReorderingAllowed(false);
		tbl_test_returns.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		
		tbl_test_returns.getColumnModel().getColumn(0).setPreferredWidth(100);
		tbl_test_returns.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tbl_test_returns, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		test_return_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
	}
	
	/*
	JPanel test_content_panel;
	JTable tbl_test_content;
	com.kms.mkl.tablemodel.debug_test tbl_model_test_content;
	void CreateGUIComponent_main_panel_table_panel_test_content_table () {
		test_content_panel = new JPanel(new BorderLayout());
		
		JLabel test_content_label = new JLabel("Test Content");
		test_content_panel.add(test_content_label, BorderLayout.NORTH);
		
		tbl_model_test_content = new com.kms.mkl.tablemodel.debug_test();
		tbl_test_content = new JTable(tbl_model_test_content);

		tbl_test_content.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tbl_test_content.getTableHeader().setPreferredSize(new Dimension(600, 22));
		tbl_test_content.getTableHeader().setReorderingAllowed(false);
		tbl_test_content.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		
		tbl_test_content.getColumnModel().getColumn(0).setPreferredWidth(20);
		tbl_test_content.getColumnModel().getColumn(1).setPreferredWidth(30);
		tbl_test_content.getColumnModel().getColumn(2).setPreferredWidth(400);
		tbl_test_content.getColumnModel().getColumn(3).setPreferredWidth(80);
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tbl_test_content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		test_content_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
		
		tbl_test_content.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tbl_model_element_input.clear();
				tbl_model_element_return.clear();
				// TODO Auto-generated method stub
				if (tbl_model_test_content != null && tbl_test_content.isEnabled()) {
					int row = tbl_test_content.getSelectedRow();
					int col = tbl_test_content.getSelectedColumn();
					
					com.kms.mkl.test.testelement cur_element = tbl_model_test_content.get_testelement(row);
					if (cur_element != null) {
						tbl_model_element_input.set_loaded_inputs(cur_element.get_inputs());
						if (cur_element.get_returns() != null)
							tbl_model_element_return.set_loaded_inputs(cur_element.get_returns());
					}
				}
			}
		});
	
		tbl_test_content.getTableHeader().addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				JTableHeader tHeader = (JTableHeader)e.getSource();
				int selectedColumn = 0;  
				selectedColumn = tHeader.columnAtPoint(e.getPoint());
				if (selectedColumn == com.kms.mkl.tablemodel.debug_test.COLUMN_RUN)
					tbl_model_test_content.set_all_run ();
			}
		});
	}
	*/
	
	JPanel test_content_panel;
	debugTree tree_test_content;
	void CreateGUIComponent_main_panel_table_panel_test_content_table () {
		test_content_panel = new JPanel(new BorderLayout());
		
		JLabel test_content_label = new JLabel("Test Content");
		test_content_panel.add(test_content_label, BorderLayout.NORTH);
		
		tree_test_content = new debugTree();
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tree_test_content, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		test_content_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
		
		tree_test_content.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {}
			@Override
			public void mousePressed(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tbl_model_element_input.clear();
				tbl_model_element_return.clear();
				// TODO Auto-generated method stub
				if (tree_test_content != null && tree_test_content.isEnabled()) {
					if (tree_test_content.get_selected_test_object() != null) {
						Object cur_object = tree_test_content.get_selected_test_object();
						if (cur_object instanceof com.ttv.at.test.testelement) {
							tbl_model_element_input.set_loaded_params(((com.ttv.at.test.testelement)cur_object).get_inputs());
							if (((com.ttv.at.test.testelement)cur_object).get_returns() != null)
								tbl_model_element_return.set_loaded_params(((com.ttv.at.test.testelement)cur_object).get_returns());
						}
						else if (cur_object instanceof com.ttv.at.test.testaction) {
							tbl_model_element_input.set_loaded_params(((com.ttv.at.test.testaction)cur_object).get_inputs());
							if (((com.ttv.at.test.testaction)cur_object).get_returns() != null)
								tbl_model_element_return.set_loaded_params(((com.ttv.at.test.testaction)cur_object).get_returns());
						}
					}
				}
			}
		});
	}

	JPanel element_input_panel;
	JTable tbl_element_inputs;
	com.ttv.at.tablemodel.debug_params tbl_model_element_input;
	void CreateGUIComponent_main_panel_table_panel_element_inputs_table () {
		element_input_panel = new JPanel(new BorderLayout());
		
		JLabel element_input_label = new JLabel("Element Input List");
		element_input_panel.add(element_input_label, BorderLayout.NORTH);
		
		tbl_model_element_input = new com.ttv.at.tablemodel.debug_params();
		tbl_element_inputs = new JTable (tbl_model_element_input);

		tbl_element_inputs.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tbl_element_inputs.getTableHeader().setPreferredSize(new Dimension(300, 22));
		tbl_element_inputs.getTableHeader().setReorderingAllowed(false);
		tbl_element_inputs.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		
		tbl_element_inputs.getColumnModel().getColumn(0).setPreferredWidth(100);
		tbl_element_inputs.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tbl_element_inputs, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		element_input_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
	}

	JPanel element_return_panel;
	JTable tbl_element_returns;
	com.ttv.at.tablemodel.debug_params tbl_model_element_return;
	void CreateGUIComponent_main_panel_table_panel_element_returns_table () {
		element_return_panel = new JPanel(new BorderLayout());
		
		JLabel element_return_label = new JLabel("Element Return List");
		element_return_panel.add(element_return_label, BorderLayout.NORTH);
		
		tbl_model_element_return = new com.ttv.at.tablemodel.debug_params();
		tbl_element_returns = new JTable (tbl_model_element_return);

		tbl_element_returns.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tbl_element_returns.getTableHeader().setPreferredSize(new Dimension(300, 22));
		tbl_element_returns.getTableHeader().setReorderingAllowed(false);
		tbl_element_returns.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		
		tbl_element_returns.getColumnModel().getColumn(0).setPreferredWidth(100);
		tbl_element_returns.getColumnModel().getColumn(1).setPreferredWidth(200);
		
		JScrollPane tableTestAreaDetail_pane = new JScrollPane(tbl_element_returns, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tableTestAreaDetail_pane.setMinimumSize(new Dimension(50, 50));
		element_return_panel.add(tableTestAreaDetail_pane, BorderLayout.CENTER);
	}
	
	JTextPane txtOutput; public JTextPane get_txtOutput () { return txtOutput; }
	StyledDocument txtOutput_doc; public StyledDocument get_txtOutput_doc () { return txtOutput_doc; }
	Style txtOutput_style; public Style get_txtOutput_style () { return txtOutput_style; }
	public void CreateGUIComponent_main_panel_table_panel_output () {
		// text for output
		txtOutput = new JTextPane();
		txtOutput.setEditable(false);
		txtOutput.setEnabled(true);
		txtOutput.setBackground(Color.white);
		txtOutput.setForeground(Color.black);
		txtOutput_doc = txtOutput.getStyledDocument();
		txtOutput_style = txtOutput.addStyle("KMS Style", null);

		txtOutput.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent ev) {
				// TODO Auto-generated method stub
				if (! txtOutput.isEditable()) {

					Point pt = new Point(ev.getX(), ev.getY());
					int pos = txtOutput.viewToModel(pt);
					if (pos >= 0) {
						Document doc = txtOutput.getDocument();
						if (doc instanceof DefaultStyledDocument) {
							DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
							Element e = hdoc.getCharacterElement(pos);
							AttributeSet a = e.getAttributes();
							String href = (String) a.getAttribute(HTML.Attribute.HREF);
							if (href != null && href.length() > 0) {
								try {
									String file_image = com.ttv.at.test.testsetting.get_default_log_images_folder() + com.ttv.at.util.os.os_file_separator + href ;
									File image_file = new File(file_image);
									if (image_file.exists()) {
										if (com.ttv.at.util.os.isWindows()) {
											String command_line = "explorer \"" + image_file + "\"";
											Runtime.getRuntime().exec(command_line);
										}
									}
									else
										JOptionPane.showMessageDialog(FormMain.mainFrame,
												"Failed image: \"" + file_image + "\" is not found",
												"image warning",
												JOptionPane.WARNING_MESSAGE);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		});
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == btnStart && test_instance != null) {
			debugTest startRun = new debugTest(test_instance);
			startRun.start();
		}
		else if (arg0.getSource() == btnStop && test_instance != null) {
			test_instance.stop();
		}
		else if (arg0.getSource() == btnLoadScript) {

			if (FormMain.selected_tc_full_path != null && FormMain.selected_tc_full_path.length() > 0) {
				reloadTC reloadTests = new reloadTC(test_instance.get_tc_instance().get_name(), FormMain.selected_tc_full_path);
				reloadTests.start();
			}
		}
	}
	
	
	public void set_app_state_running () {
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
		btnLoadScript.setEnabled(false);
	}
	public void set_app_state_ready () {
		btnStart.setEnabled(true);
		btnStop.setEnabled(false);
		btnLoadScript.setEnabled(true);
	}
	public void set_app_state_disable () {
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);
		btnLoadScript.setEnabled(false);
	}
}

class debugTest extends Thread {
	com.ttv.at.test.test test_instance;
	public debugTest(com.ttv.at.test.test test_instance) {
		this.test_instance = test_instance;
	}
	public void run() {
		TestDebugger.get_instance().set_app_state_running();
		
		// Check to create log folder
		com.ttv.at.test.testsetting.init_default_log_folder();
		
		if (test_instance != null)
			test_instance.debug_execute();
		
		TestDebugger.get_instance().set_app_state_ready();
	}
}

class reloadTC extends Thread {
	private String testcase_name = null;
	private String testcase_file = null;
	public reloadTC(String testcase_name, String testcase_file) {
		this.testcase_name = testcase_name;
		this.testcase_file = testcase_file;
	}

	public void run() {
		FormMain.tableTestSuite.setEnabled(false);
		FormMain.tableTestArea.setEnabled(false);
		
		// Clean up before loading
		TestDebugger.get_instance().set_app_state_disable();

		guiutil.txtOutput_append_12black (FormMain.simple_date_time_format.format(new Date()) + " -- Start reloading test cases '"+testcase_name+"' from file : " + testcase_file, TestDebugger.get_instance().get_txtOutput(), TestDebugger.get_instance().get_txtOutput_style(), TestDebugger.get_instance().get_txtOutput_doc());

		
		
		
		
		/*____________________________________________________
		
		com.kms.mkl.test.testcase loaded_testcase = com.kms.mkl.util.test.loader.reload_test_case(testcase_name, testcase_file, FormMain.loaded_testcases, FormMain.loaded_testlibraries);
		String tcError = com.kms.mkl.util.test.loader.get_load_test_case_warning_message();
		FormMain.mainProgress.setValue(3);


		String errorMessage = "";
		if (loaded_testcase != null && (tcError == null || tcError.length() == 0)) {
			// Remap index all other call the old tc to new tc
			for (int tcIndex = 0 ; (FormMain.loaded_testcases != null) && (tcIndex < FormMain.loaded_testcases.size()) ; tcIndex ++) {
				com.kms.mkl.test.testcase cur_testcase = FormMain.loaded_testcases.get(tcIndex);
				if (cur_testcase != null)
					for (int eIndex = 0 ; (cur_testcase.get_elements() != null) && (eIndex < cur_testcase.get_elements().size()) ; eIndex ++) {
						com.kms.mkl.test.testelement cur_element = cur_testcase.get_elements().get(eIndex);
						if (cur_element != null && cur_element.get_tc_instance() != null && cur_element.get_tc_instance().get_name().equals(testcase_name)) {
							cur_element.set_tc_instance(loaded_testcase);
						}
					}
			}
			
			// Change in testsuite to refer to the test case
			for (int iArea = 0 ; (FormMain.loaded_testsuite != null) && 
								(FormMain.loaded_testsuite.get_testareas() != null) &&
								iArea < FormMain.loaded_testsuite.get_testareas().size() ; iArea++) {
				com.kms.mkl.test.testarea cur_testarea = FormMain.loaded_testsuite.get_testareas().get(iArea);
				for (int iTest = 0 ; (cur_testarea != null)&&(cur_testarea.get_tests() != null)&&(iTest < cur_testarea.get_tests().size()); iTest ++) {
					com.kms.mkl.test.test cur_test = cur_testarea.get_tests().get(iTest);
					if (cur_test.get_tc_instance() != null &&
							cur_test.get_tc_instance().get_name().equals(testcase_name)) {
						cur_test.set_tc_instance(loaded_testcase);
					}
				}
			}
		}
		if (tcError != null && tcError.length() > 0)
		{
			if (errorMessage != null && errorMessage.length() > 0)
				errorMessage += "\n\n---\n";
			errorMessage = errorMessage + "Error on loading test cases : \n" + tcError;
		}

		if (errorMessage != null && errorMessage.length() > 0) {
			guiutil.txtOutput_append_12red(FormMain.simple_date_time_format.format(new Date()) + " -- " + errorMessage, TestDebugger.get_instance().get_txtOutput(), TestDebugger.get_instance().get_txtOutput_style(), TestDebugger.get_instance().get_txtOutput_doc());
			JOptionPane.showMessageDialog(FormMain.mainFrame,
					"Error in loading test case",
					"Test case warning",
					JOptionPane.WARNING_MESSAGE);
		}
		else {
			guiutil.txtOutput_append_12green(FormMain.simple_date_time_format.format(new Date()) + " -- Test case '"+testcase_name+"' are re-loaded successful", TestDebugger.get_instance().get_txtOutput(), TestDebugger.get_instance().get_txtOutput_style(), TestDebugger.get_instance().get_txtOutput_doc());
		}
		_______________________________________________*/
		TestDebugger.get_instance().refresh();
		FormMain.reload_app_state();
		FormMain.tableTestSuite.setEnabled(true);
		FormMain.tableTestArea.setEnabled(true);
	}
}
