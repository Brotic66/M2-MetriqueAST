/*
 * jadvisor/planner/StudentPlan.java - JAdvisor plan model
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

package jadvisor.planner;

import jadvisor.scheduler.Course;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class StudentPlan implements Serializable {
	private StudentSemesterPlan[][] _plan;
	transient private Collection _listeners = new ArrayList();
	
public StudentPlan (int numYears, int firstYear, String[] semesterNames) {
	_plan = new StudentSemesterPlan[numYears][semesterNames.length];
	for (int i = 0; i < _plan.length; i++)
		for (int j = 0; j < _plan[i].length; j++)
			_plan[i][j] = new StudentSemesterPlan(firstYear + i, semesterNames[j]);
}
	
public StudentPlan (int numYears, int numSemesters) {
	this(numYears, 0, new String[numSemesters]);
}

public void add (Course course, int year, int semester) {
	if (!_plan[year][semester].contains(course)) {
		_plan[year][semester].add(course);
		fireCourseAdded(course, year, semester);
	}
}

public void remove (Course course, int year, int semester) {
	if (_plan[year][semester].contains(course)) {
		_plan[year][semester].remove(course);
		fireCourseRemoved(course, year, semester);
	}
}

public boolean contains (Course course) {
	for (int i = 0; i < _plan.length; i++)
		for (int j = 0; j < _plan[i].length; j++)
			if (_plan[i][j].contains(course))
				return true;
	return false;	
}

public boolean satisfiesPrerequisites (Course course, int year, int semester) {
	int counter;
	List prerequisites = course.getPrerequisites();
	for (int c = 0; c < prerequisites.size(); c++) {
		counter = 0;
		for (int i = 0; i < year; i++)
			for (int j = 0; j < semesters(i); j++)
				if (_plan[i][j].contains((Course)prerequisites.get(c)))
					counter++;
		for (int j = 0; j < semester; j++)
			if (_plan[year][j].contains((Course)prerequisites.get(c)))
				counter++;
		if (counter == 0)
			return false;
	}
	return true;
}

public ListModel getSemester (int year, int semester) {
	return (ListModel)(_plan[year][semester]);
}

public int years () {
	return _plan.length;
}

public int semesters (int year) {
	return _plan[year].length;
}

public void addListener (StudentPlanListener l) {
	_listeners.add(l);
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
	_listeners = new ArrayList();
}

private void fireCourseAdded (Course course, int year, int semester) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentPlanListener) i.next()).courseAdded(this, course, year, semester);
}

private void fireCourseRemoved (Course course, int year, int semester) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentPlanListener) i.next()).courseRemoved(this, course, year, semester);
}

}
