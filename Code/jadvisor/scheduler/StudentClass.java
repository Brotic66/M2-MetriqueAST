/*
 * jadvisor/scheduler/StudentClass.java - JAdvisor StudentClass model
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

import java.util.*;
import java.io.*;

public class StudentClass extends StudentBlock implements Serializable {

	private final Course _course;
	private final String _section;
	private int _mod;
	private int _credit;
	private List _infoList;
	
public StudentClass (String description, Course course, String section, boolean[] day, 
		TimeOfDay startTime, TimeOfDay endTime, int mod, int credit) {
	super(description, day, startTime, endTime);
	_course = course;
	_section = section;
	_mod = mod;
	_credit = credit;
	_infoList = new ArrayList();
}
	
public StudentClass (Course course, String section, boolean[] day, 
		TimeOfDay startTime, TimeOfDay endTime, int mod, int credit) {
	this("", course, section, day, startTime, endTime, mod, credit);
}

public StudentClass (StudentClass other) {
	super(other);
	_course = new Course(other._course);
	_section = new String(other._section);
	_mod = other._mod;
	_credit = other._credit;
	_infoList = new ArrayList();
	for (int i = 0; i < other._infoList.size(); i++)
		_infoList.add(other._infoList.get(i));
}

public void setInfo (List infoList) {
	_infoList = infoList;	
}

public boolean equals (Object other) {
	if (this == other) return true;
	if (other == null) return false;
	if (this.getClass() != other.getClass()) return false;
	return (this._course.equals(((StudentClass)other)._course)) &&
		(this._section.equalsIgnoreCase(((StudentClass)other)._section));
}

public Course getCourse () {
	return _course;
}

public String getSection () {
	return _section;
}

public int getMod () {
	return _mod;
}

public int getCredit () {
	return _credit;
}

public List getInfo () {
	return _infoList;
}

public String toString () {
	return _course.toString() + " " + _section + " " + _startTime.toString() + "-" +
		_endTime.toString() + " " + _mod + " " + _credit;
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
}

}