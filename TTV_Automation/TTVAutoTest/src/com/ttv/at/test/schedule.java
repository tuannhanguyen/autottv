package com.ttv.at.test;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class schedule {
	boolean isUseful = false;
	public boolean is_useful () {return isUseful;}
	// 0: run immediately
	// 1: daily recurring
	// 2: weekly recuring
	// 3: monthly recurring
	// 4: schedule 1 time
	int schedule_type = -1;
	
	Date plan_time = null;
	
	@SuppressWarnings("deprecation")
	public schedule(String type, Date specification) {
		if (type == null || type.length() == 0) {
			if (specification == null) {
				isUseful = true;
				this.schedule_type = 0;
				this.in_schedule = true;
			}
			else
				this.schedule_type = 4;
		}
		else if (specification != null && type.toLowerCase().equals("daily"))
			this.schedule_type = 1;
		else if (specification != null && type.toLowerCase().equals("weekly"))
			this.schedule_type = 2;
		else if (specification != null && type.toLowerCase().equals("monthly"))
			this.schedule_type = 3;
		else
			this.schedule_type = -1;

		if (this.schedule_type > -1) {
			if (this.schedule_type == 4) {
				plan_time = specification;
				schedule_time = plan_time;
				in_schedule = true;
				isUseful = true;
			}
			else if (this.schedule_type == 1) {
				try {
					plan_time = new Date ();
					plan_time.setMinutes(specification.getMinutes());
					plan_time.setHours(specification.getHours());
					// Set Second
					plan_time.setSeconds(specification.getSeconds());
					isUseful = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
			else if (this.schedule_type == 2) {
				try {
					int dayOfWeek = specification.getDay();
					plan_time = new Date ();
					int dayOfMonth = plan_time.getDate();
					int now_dayofweek = plan_time.getDay();
					int move_date = 7 + dayOfWeek - now_dayofweek;
					Calendar cal = Calendar.getInstance();
					cal.setTime(plan_time);
					cal.add(Calendar.DATE, move_date);
					plan_time = cal.getTime();

					// set Hour
					plan_time.setHours(specification.getHours());
					// set Minute
					plan_time.setMinutes(specification.getMinutes());
					// Set Second
					plan_time.setSeconds(specification.getSeconds());
					isUseful = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
			else if (this.schedule_type == 3) {
				try {
					int dayOfMonth = specification.getDate();
					plan_time = new Date ();
					plan_time.setMonth(3);
					plan_time.setDate(dayOfMonth);

					// set Hour
					plan_time.setHours(specification.getHours());
					// set Minute
					plan_time.setMinutes(specification.getMinutes());
					// Set Second
					plan_time.setSeconds(specification.getSeconds());
					isUseful = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
		}
		reset_schedule_time ();
	}
	@SuppressWarnings("deprecation")
	public schedule(String type, String specification) {
		if (type == null || type.length() == 0) {
			if (specification == null || specification.length() == 0) {
				isUseful = true;
				this.schedule_type = 0;
				this.in_schedule = true;
			}
			else
				this.schedule_type = 4;
		}
		else if (specification != null && type.toLowerCase().equals("daily"))
			this.schedule_type = 1;
		else if (specification != null && type.toLowerCase().equals("weekly"))
			this.schedule_type = 2;
		else if (specification != null && type.toLowerCase().equals("monthly"))
			this.schedule_type = 3;
		else
			this.schedule_type = -1;
		
		if (this.schedule_type > -1) {
			if (this.schedule_type == 4) {
				try {
					plan_time = new Date (specification);
					schedule_time = plan_time;
					in_schedule = true;
					isUseful = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
			else if (this.schedule_type == 1) {
				try {
					plan_time = new Date ();
					// Analyze
					if (specification.length() <= 4) {
						String[]strs = specification.split(":");
						int hour_value = Integer.parseInt(strs[0]);
						int minute_value = Integer.parseInt(strs[1]);
						plan_time.setMinutes(minute_value);
						plan_time.setHours(hour_value);
						// Set Second
						plan_time.setSeconds(0);
						isUseful = true;
					}
					else {
						Date expected_run = new Date (specification);
						plan_time.setMinutes(expected_run.getMinutes());
						plan_time.setHours(expected_run.getHours());
						// Set Second
						plan_time.setSeconds(0);
						isUseful = true;
						
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
			else if (this.schedule_type == 2) {
				try {
					int dayOfWeek = -1;
					if (specification.toLowerCase().equals("monday") || specification.toLowerCase().equals("mon"))
						dayOfWeek = 1;
					else if (specification.toLowerCase().equals("tuesday") || specification.toLowerCase().equals("tue"))
						dayOfWeek = 2;
					else if (specification.toLowerCase().equals("wednesday") || specification.toLowerCase().equals("wed"))
						dayOfWeek = 3;
					else if (specification.toLowerCase().equals("thursday") || specification.toLowerCase().equals("thu"))
						dayOfWeek = 4;
					else if (specification.toLowerCase().equals("friday") || specification.toLowerCase().equals("fri"))
						dayOfWeek = 5;
					else if (specification.toLowerCase().equals("saturday") || specification.toLowerCase().equals("sat"))
						dayOfWeek = 6;
					else if (specification.toLowerCase().equals("sunday") || specification.toLowerCase().equals("sun"))
						dayOfWeek = 0;
					if (dayOfWeek > -1) {
						plan_time = new Date ();
						@SuppressWarnings("unused")
						int dayOfMonth = plan_time.getDate();
						int now_dayofweek = plan_time.getDay();
						int move_date = 7 + dayOfWeek - now_dayofweek;
						Calendar cal = Calendar.getInstance();
						cal.setTime(plan_time);
						cal.add(Calendar.DATE, move_date);
						plan_time = cal.getTime();

						// set Hour
						plan_time.setHours(0);
						// set Minute
						plan_time.setMinutes(0);
						// Set Second
						plan_time.setSeconds(0);
						isUseful = true;
					}
					else
						plan_time = null;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
			else if (this.schedule_type == 3) {
				try {
					int dayOfMonth = Integer.parseInt(specification);
					plan_time = new Date ();
					plan_time.setMonth(3);
					plan_time.setDate(dayOfMonth);
					
					// set Hour
					plan_time.setHours(0);
					// set Minute
					plan_time.setMinutes(0);
					// Set Second
					plan_time.setSeconds(0);
					isUseful = true;
				}
				catch (Exception e) {
					e.printStackTrace();
					plan_time = null;
				}
			}
		}
		reset_schedule_time ();
	}

	boolean in_schedule = false;
	public boolean is_in_schedule () {
		return in_schedule;
	}
	public void done_schedule () {
		in_schedule = false;
		schedule_time = null;
	}
	
	public boolean is_in_exec_time () {
		if (in_schedule && schedule_type == 0)
			return true;
		if (in_schedule && schedule_time != null && schedule_time.getTime() <= (new Date().getTime()))
			return true;
		return false;
	}
	
	Date schedule_time = null;
	public Date get_schedule_time () {return schedule_time;}
	@SuppressWarnings("deprecation")
	public void reset_schedule_time () {
		schedule_time = null;
		Date now = new Date();
		if (schedule_type == 1 && plan_time != null) {
			plan_time.setYear(now.getYear());
			plan_time.setMonth(now.getMonth());
			plan_time.setDate(now.getDate());
			if (plan_time.getTime() < now.getTime()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(plan_time);
				cal.add(Calendar.DATE, 1);
				plan_time = cal.getTime();
				schedule_time = plan_time;
			}
			else
				schedule_time = plan_time;
			in_schedule = true;
		}
		else if (schedule_type == 2 && plan_time != null) {

			schedule_time = new Date();

			// set Hour
			schedule_time.setHours(plan_time.getHours());
			// set Minute
			schedule_time.setMinutes(plan_time.getMinutes());
			// Set Second
			schedule_time.setSeconds(plan_time.getSeconds());
			
			// if same day, move to next week
			int now_day_of_week = now.getDay();
			int plan_day_of_week = plan_time.getDay();
			int moving_date = now_day_of_week - plan_day_of_week;
			if (moving_date <= 0)
				moving_date = moving_date + 7;
			Calendar cal = Calendar.getInstance();
			cal.setTime(schedule_time);
			cal.add(Calendar.DATE, moving_date);
			schedule_time = cal.getTime();
			in_schedule = true;
		}
		else if (schedule_type == 3 && plan_time != null) {
			schedule_time = new Date();

			in_schedule = true;
			// set to the same day next month
			// set Year
			if (now.getMonth() == 12)
				schedule_time.setYear(now.getYear() + 1);
			else
				schedule_time.setYear(now.getYear());

			// set Month
			if (now.getMonth() == 12)
				schedule_time.setMonth(1);
			else
				schedule_time.setMonth(now.getMonth() + 1);

			// set Date
			int plan_date = plan_time.getDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(schedule_time);
			if (plan_date > cal.getActualMaximum(Calendar.DAY_OF_MONTH))
				schedule_time.setDate(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			else
				schedule_time.setDate(plan_date);

			// set Hour
			schedule_time.setHours(plan_time.getHours());
			// set Minute
			schedule_time.setMinutes(plan_time.getMinutes());
			// Set Second
			schedule_time.setSeconds(plan_time.getSeconds());
			in_schedule = true;
		}
	}

	@SuppressWarnings("deprecation")
	public String toString () {
		String message= "";
		if (schedule_type == -1)
			message = "No Schedule";
		else if (schedule_type == 0)
			message = "Immediately";
		else if (schedule_type == 1)
			message = "Daily at " + plan_time.getHours() + ":" + plan_time.getMinutes();
		else if (schedule_type == 2)
			message = "Weekly on " + new DateFormatSymbols().getWeekdays()[plan_time.getDay()+1] + " at " + plan_time.getHours() + ":" + plan_time.getMinutes();
		else if (schedule_type == 3)
			message = "Monthly on " + plan_time.getDate() + " at " + plan_time.getHours() + ":" + plan_time.getMinutes();
		else if (schedule_type == 4)
			message = "run at " + plan_time.toString();
		return message;
	}
}
