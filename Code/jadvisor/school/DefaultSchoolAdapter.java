/*
 * jadvisor/school/DefaultSchoolAdapter.java - A default SchoolAdapter
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

package jadvisor.school;

import jadvisor.scheduler.*;
import jadvisor.planner.*;

import java.util.*;

/**
 * This class is the default implementation of a <code>SchoolAdapter</code>.  
 * For use in testing and when there is no appropriate 
 * <code>SchoolAdapter</code> available.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class DefaultSchoolAdapter implements SchoolAdapter {
	
	public static final int FALL = 0;
	public static final int SPRING = 1;
	public static final int SUMMER1 = 2;
	public static final int SUMMER2 = 3;
	public final String[] getSemesters () {
		return new String[] {"Fall", "Spring", "Summer 1", "Summer 2"};
	}
	
	public final boolean[] getRequiredSemesters () {
		return new boolean[] {true, true, false, false};
	}
	public final String getOptionalSemesterText () {return "Summers";}
	
	public static final int getDaysInWeek () {return 5;}
	public final String[] getDaysAbbreviations () {
		return new String[] {"M", "T", "W", "H", "F"};
	}
	
	public final String[] getClassTitles () {
		return new String[] 
		{"Pre", "Number", "Section", "Days", "Start Time", "End Time", "Mod", "Credit"};
	}
	public final String[] getClassInfoTitles () {
		return new String[] {"Teacher", "Building"};
	}

	public static final int NONE = 0;
	public static final int CREDIT = 1;
	public static final int AUDIT = 2;
	public final String[] getClassMods () {
		return new String[] {"None", "Credit", "Audit"};
	}
	
public DefaultSchoolAdapter () {
	
}

public String[] getClassPreList (int semester) {
	String[] a = new String[3];
	a[0] = "CSC";
	a[1] = "MA";
	a[2] = "PY";
	return a;
}

public String[] getClassNumberList (int semester, String pre) {
	String[] a = new String[2];
	if (pre.equals("CSC")) {
		a[0] = "116";
		a[1] = "216";
	}
	if (pre.equals("MA")) {
		a[0] = "141";
		a[1] = "242";
	}
	if (pre.equals("PY")) {
		a[0] = "101";
		a[1] = "102";
	}
	return a;
}

public List getClassSectionList (int semester, String pre, String number) {
	List a = new ArrayList();
	StudentClass c;
	c = new StudentClass(
		new Course(pre, number), "001",	
		new boolean[] {true, false, false, false, false}, 
		new TimeOfDay(11,0,TimeOfDay.AM), 
		new TimeOfDay(11,30,TimeOfDay.AM), 0, 1);
	c.setInfo(getClassInfo(c));
	a.add(c);
	
	c = new StudentClass(
		new Course(pre, number), "002",	
		new boolean[] {false, true, false, false, false}, 
		new TimeOfDay(11,0,TimeOfDay.AM), 
		new TimeOfDay(11,30,TimeOfDay.AM), 0, 1);
	c.setInfo(getClassInfo(c));
	a.add(c);
	
	c = new StudentClass(
		new Course(pre, number), "003",	
		new boolean[] {false, false, true, false, false}, 
		new TimeOfDay(11,10,TimeOfDay.AM), 
		new TimeOfDay(11,50,TimeOfDay.AM), 0, 1);
	c.setInfo(getClassInfo(c));
	a.add(c);
	
	c = new StudentClass(
		new Course(pre, number), "004",	
		new boolean[] {false, false, false, true, false}, 
		new TimeOfDay(11,0,TimeOfDay.AM), 
		new TimeOfDay(12,30,TimeOfDay.PM), 0, 1);
	c.setInfo(getClassInfo(c));
	a.add(c);

	c = new StudentClass(
		new Course(pre, number), "005",	
		new boolean[] {false, false, false, false, true}, 
		new TimeOfDay(12,0,TimeOfDay.PM), 
		new TimeOfDay(1,30,TimeOfDay.PM), 0, 1);
	c.setInfo(getClassInfo(c));
	a.add(c);
	return a;
}

public List getClassInfo (StudentClass c) {
	List l = new ArrayList();
	l.add("Professor Name");
	l.add("Harrelson");
	return l;
}

public boolean isACourse (Course course) {
	//Substitute all this with call to server to check if course is there
	
	if (course.getCoursePre().length() > 3 || course.getCoursePre().length() < 2)
		return false;

	if (course.getCourseNumber().length() > 4 || course.getCourseNumber().length() < 3)
		return false;

	return true;
}

public boolean isAClass (StudentClass c) {
	//Substitute all this with call to server to check if class is there
	
	return true;
}

public List getCoursePrerequisites (Course course) {
	if (course.equals(new Course("CSC", "216"))) {
		List a = new ArrayList();
		a.add(new Course("CSC", "295H"));
		return a;	
	}
	return new ArrayList();
}

public Curriculum getCurriculum (String curriculumName) {
	final Curriculum result = new Curriculum(curriculumName);
	if (curriculumName.equals("CSC")) {
		result.add(new Course("CSC","295H"));
		result.add(new Course("CSC","216"));
		result.add(new Course("ENG","113"));
	}
	if (curriculumName.equals("Honors")) {
		result.add(new Course("HON","101"));
	}
	return result;
}

public String getCourseDescription (Course course) {
	return "";
}

public List searchCourses (String searchString, boolean searchTitles, 
		boolean searchDescriptions) {
	List a = new ArrayList();
	for (int i = 0; i < 2000; i++) //to test threading of SearchUI
		System.out.println(i);
	if (searchTitles)
		a.add("T - " + searchString);
	if (searchDescriptions) {
		a.add("D - " + searchString);
		a.add("D - " + searchString + "2");
	}
	return a;
}

public String classToString (StudentClass c) {
	return c.toString();
}

public String timeToString (TimeOfDay time) {
	return time.toString();
}

public String toString () {
	return "DefaultSchoolAdapter";
}

}
