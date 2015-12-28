/*
 * jadvisor/scheduler/Course.java - JAdvisor Course model
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

public class Course implements Serializable {
	
	private final String _pre;
	private final String _number;
	private List _prerequisites;
	
public Course (String pre, String number) {
	_pre = pre;
	_number = number;
	_prerequisites = new ArrayList();
}

public Course (Course other) {
	this(new String(other._pre), new String(other._number));
	for (int i = 0; i < other._prerequisites.size(); i++)
		_prerequisites.add(other._prerequisites.get(i));
}

public boolean equals (Object other) {
	if (this == other) return true;
	if (other == null) return false;
	if (this.getClass() != other.getClass()) return false;
	return (this._pre.equalsIgnoreCase(((Course)other)._pre)) && 
		(this._number.equalsIgnoreCase(((Course)other)._number));
}

public String getCoursePre () {
	return _pre;
}

public String getCourseNumber () {
	return _number;
}

public List getPrerequisites () {
	return _prerequisites;
}

public void setPrerequisites (List prerequisites) {
	_prerequisites = prerequisites;
}

public String toString () {
	return _pre + " " + _number;
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
}

}
