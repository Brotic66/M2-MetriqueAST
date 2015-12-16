/*
 * jadvisor/scheduler/StudentFile.java - holds a student's info, schedule, and plan
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

import java.io.*;

public class StudentFile implements Serializable {
	private StudentInfo _studentInfo;
	private StudentSchedule _studentSchedule;
	private StudentPlan _studentPlan;

public StudentFile (int years, int semesters) {
	_studentInfo = new StudentInfo();
	_studentSchedule = new StudentSchedule();
	_studentPlan = new StudentPlan(years, semesters);
}
		
public StudentFile (StudentInfo studentInfo, StudentSchedule studentSchedule, 
		StudentPlan studentPlan) {
	_studentInfo = studentInfo;
	_studentSchedule = studentSchedule;
	_studentPlan = studentPlan;
}

public StudentInfo getStudentInfo () {
	return _studentInfo;
}

public StudentSchedule getStudentSchedule () {
	return _studentSchedule;
}

public StudentPlan getStudentPlan () {
	return _studentPlan;
}
/*
public void load (InputStream inStream) throws IOException {
	
}

public void store (OutputStream out, String header) throws IOException {
	
}*/

}
