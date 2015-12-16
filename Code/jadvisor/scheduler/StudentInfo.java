/*
 * jadvisor/scheduler/StudentInfo.java - holds a student's information
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

import java.io.*;

public class StudentInfo implements Serializable {
	private String[] _curriculumNames;
	private int _firstYear;
	private int _currentSemester;

public StudentInfo (String[] curriculumNames, int firstYear, int currentSemester) {
	_curriculumNames = curriculumNames;
	_firstYear = firstYear;
	_currentSemester = currentSemester;
}

public StudentInfo (String[] curriculumNames) {
	this(curriculumNames, 0, 0);
}

public StudentInfo (int firstYear, int currentSemester) {
	this(new String[0], firstYear, currentSemester);
}

public StudentInfo () {
	this(new String[0], 0, 0);
}

public String[] getCurriculumNames () {
	return	_curriculumNames;
}

public void setCurriculumNames (String[] curriculumNames) {
	_curriculumNames = curriculumNames;
}

public int getFirstYear () {
	return	_firstYear;
}

public int getCurrentSemester () {
	return	_currentSemester;
}

public String toString () {
	return "StudentInfo";	
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
}

}
