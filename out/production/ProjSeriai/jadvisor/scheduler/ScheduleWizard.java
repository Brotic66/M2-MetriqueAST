/*
 * jadvisor/scheduler/ScheduleWizard.java - makes list of possible schedules
 * Copyright (C) 2001-2002 Curtis Rawls
 * jadvisor.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jadvisor.scheduler;

import jadvisor.planner.*;
import jadvisor.school.*;

import java.io.IOException;
import java.util.*;
import javax.swing.table.*;

/**
 * Makes a list of all possible schedules.  Takes a StudentSemesterPlan as 
 * input, finds all sections of the same course in the semester, then outputs 
 * all the possible schedules for those sections.  Rules out schedules with 
 * time conflicts.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class ScheduleWizard {
	private final SchoolAdapter _schoolAdapter;
	private final List _plan = new ArrayList();
	private final List _classes = new ArrayList();
	private final List _schedules = new ArrayList();
	
	private boolean _noClassBefore;
	private TimeOfDay _noClassBeforeTime;
	private boolean _noClassAfter;
	private TimeOfDay _noClassAfterTime;
	private boolean[] _noClassOn;

public ScheduleWizard (SchoolAdapter schoolAdapter) {
	_schoolAdapter = schoolAdapter;
}

public void setPlan (StudentSemesterPlan semesterplan) {
	_plan.clear();
	for (int i = 0; i < semesterplan.getSize(); i++)
		_plan.add(semesterplan.getElementAt(i));
	setSchedules();
}

public void setPlan (StudentSchedule schedule) {
	_plan.clear();
	final List scheduleClasses = schedule.getClasses();
	for (int i = 0; i < scheduleClasses.size(); i++)
		_plan.add(((StudentClass)scheduleClasses.get(i)).getCourse());
	setSchedules();
}

public void setPreferences (boolean noClassBefore, TimeOfDay noClassBeforeTime, 
		boolean noClassAfter, TimeOfDay noClassAfterTime, boolean[] noClassOn) {
	_noClassBefore = noClassBefore;
	_noClassBeforeTime = noClassBeforeTime;
	_noClassAfter = noClassAfter;
	_noClassAfterTime = noClassAfterTime;
	_noClassOn = noClassOn;
	setSchedules();
}

public AbstractTableModel getTableModel () {
	return new AbstractTableModel () {
		public int getColumnCount() {
			return _plan.size();
		}
		public int getRowCount() {
			return _schedules.size();
		}
	    public String getColumnName(int col) {
			return ((Course)(_plan.get(col))).toString();
	    }
		public Object getValueAt(int row, int col) {
			return ((StudentClass)((List)((StudentSchedule)_schedules.get(row))
				.getClasses()).get(col)).getSection();
		}
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	};
}

public StudentSchedule getSchedule (int n) {
	return (StudentSchedule)_schedules.get(n);
}

public List getSchedules () {
	return _schedules;	
}

private void setSchedules () {
	if (_plan.size() > 0) {
		setClasses();
		_schedules.clear();
		recursiveSchedule(0, new StudentSchedule());
	}
}

private void setClasses () {
	_classes.clear();
	for (int i = 0; i < _plan.size(); i++) {
		final Course course = (Course)_plan.get(i);
		try {
			_classes.add(_schoolAdapter.getClassSectionList(
				0, course.getCoursePre(), course.getCourseNumber()));
		} catch (IOException e) {
			System.err.println("ScheduleWizard: " + e.getMessage());
		}
	}
}

private void recursiveSchedule (int course, StudentSchedule schedule) {
	if (_classes.size() < 1)
		return;
	for (int i = 0; i < ((List)_classes.get(course)).size(); i++) {
		final StudentClass myClass = (StudentClass)(((List)_classes.get(course)).get(i));
		if (fitsPreferences(myClass)) {
			boolean j = true;
			final StudentSchedule mySchedule = new StudentSchedule(schedule);
			try {
				mySchedule.add(myClass);
			} catch (IllegalArgumentException e) {j = false;}
			if (j)
				if (_classes.size() == course + 1)
					_schedules.add(mySchedule);
				else
					recursiveSchedule(course + 1, mySchedule);
		}
	}
}

private boolean fitsPreferences (StudentClass c) {
	if(_noClassBefore)
		if(c.getStartTime().compareTo(_noClassBeforeTime) < 0)
			return false;
	if(_noClassAfter)
		if(c.getEndTime().compareTo(_noClassAfterTime) > 0)
			return false;
	for (int i = 0; i < _noClassOn.length; i++)
		if (c.getDays()[i] == true && _noClassOn[i] == true)
			return false;
	return true;
}

}
