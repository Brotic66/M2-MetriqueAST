/*
 * jadvisor/planner/StudentSemesterPlan.java - JAdvisor semester plan model
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

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.Serializable;

public class StudentSemesterPlan implements ListModel, Serializable {
	private final List _semesterPlan;
	transient private Collection _listeners = new ArrayList();
	private int _year;
	private String _semester;

public StudentSemesterPlan (int year, String semester) {
	_semesterPlan = new LinkedList();
	_year = year;
	_semester = semester;
}

public StudentSemesterPlan () {
	this(0, "");
}

public void add (Course course) {
	_semesterPlan.add(course);
}

public void remove (Course course) {
	_semesterPlan.remove(course);
}

public boolean contains (Course course) {
	for (int i = 0; i < _semesterPlan.size(); i++)
		if (course.equals(_semesterPlan.get(i)))
			return true;
	return false;
}

public Object getElementAt (int index) {
	return _semesterPlan.get(index);
}

public int getSize () {
	return _semesterPlan.size();
}

public String getTitle() {
	return "" + _year + " " + _semester;
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
	_listeners = new ArrayList();
}

public void addListDataListener (ListDataListener l) {
	_listeners.add(l);
}

public void removeListDataListener (ListDataListener l) {
	_listeners.remove(l);
}

public String toString () {
	return "Plan: " + _year + " " + _semester;
}

}