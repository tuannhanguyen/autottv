package com.ttv.at.log;

import java.util.EventListener;


public interface log_event_listener extends EventListener {
	public void newTestSuiteSetLogOccur(testsuiteset evt);
	public void endTestSuiteSetLogOccur(testsuiteset evt);
	
	public void newTestSuiteLogOccur(testsuite evt);
	public void endTestSuiteLogOccur(testsuite evt);
	
	public void newTestLogOccur(test evt);
	public void updateTestLogOccur(test evt);
	
	public void newTestElementLogOccur(testelement evt);
	public void updateTestElementLogOccur(testelement evt);
	
	public void newActionLogOccur(action evt);
	public void updateActionLogOccur(action evt);
}
