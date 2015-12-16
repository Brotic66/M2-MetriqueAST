/*
 * jadvisor/school/SchoolAdapter.java - Interface for JAdvisor SchoolAdapters
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
import java.io.IOException;

/**
 * This interface defines the methods that JAdvisor components use to customize
 * JAdvisor for a particular school.  To have full support for a school, all
 * methods of SchoolAdapter should be implemented.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public interface SchoolAdapter {

/**
 * Returns an array of semester names.  The names are sorted in chronological
 * order from the beginning of the year.  For example: If a school's year 
 * begins in the fall, the first value in the array should be "Fall".
 *
 * @return an array of semester names
 */
public String[] getSemesters ();

/**
 * Returns an boolean array designating required semesters.  The array should
 * be the same length as getSemesters(), and their values must correspond.
 * A <code>true</code> value means the semester is mandatory, and false 
 * means it is optional (such as summer sessions).
 * 
 * @return an array of <code>booleans</code> designating each semester as 
 * required or not
 */
public boolean[] getRequiredSemesters ();

/**
 * Returns text used to describe optional semesters.  Should return a 
 * <code>String</code> such as "Optional", or "Summers" if only 
 * summer semesters are optional.
 *
 * @return text describing optional semester(s)
 */
public String getOptionalSemesterText ();

/**
 * Returns an array of abbreviations for days of the week.  Only days when 
 * classes are scheduled should be returned.  This array also determines 
 * the number of days in the school week.
 *
 * @return an array of abbreviations for each school day
 */
public String[] getDaysAbbreviations ();

/**
 * Returns the header titles for values of <code>StudentClass</code>es.  The
 * titles will appear in the <code>StudentClass</code> table in the Scheduler.
 *
 * @return an array of titles for <code>StudentClass</code> values
 */
public String[] getClassTitles ();

/**
 * Returns the header titles for info values of <code>StudentClass</code>es.  
 * These are the values returned by the <code>getInfo()</code> method of
 * <code>StudentClass</code>.  These titles will appear after the values
 * returned by <code>getClassTitles()</code> in the Scheduler table.
 *
 * @return an array of titles for <code>StudentClass</code> info values
 */
public String[] getClassInfoTitles ();

/**
 * Returns the names of all class mods for a <code>StudentClass</code>.  A 
 * <code>StudentClass</code> can be assigned a special modifier. This modifier
 * can be used by a <code>SchoolAdapter</code> to designate a class as being
 * audited, credit only, no mod, etc.  The mod values is stored in 
 * <code>StudentClass</code> as an <code>int</code>, and corresponds to the
 * position in the returned array.
 *
 * @return an array of names of all <code>StudentClass</code> modifiers
 */
public String[] getClassMods ();

/**
 * Returns an array of all class pre values in this semester.  A pre for a 
 * <code>Course</code> is the XXX in "XXX YYY", usually designating the 
 * <code>Course</code>'s department abbreviation.
 *
 * @param semester the semester to obtain class pre information for
 * @return an array of class PRE values for semester
 * @throws IOException if the class pre list can not be returned
 */
public String[] getClassPreList (int semester) throws IOException;

/**
 * Returns an array of all class number values in this semester and pre.  A 
 * number for a <code>Course</code> is the YYY in "XXX YYY", usually designating
 * a specific <code>Course</code> in department "pre".
 *
 * @param semester the semester to obtain class number information for
 * @param pre the pre value to obtain class number information for
 * @return an array of class number values for semester and pre
 * @throws IOException if the class number list can not be returned
 */
public String[] getClassNumberList (int semester, String pre) throws IOException;

/**
 * Returns a <code>List</code> of all <code>StudentClass</code>es in semester
 * for a <code>Course</code> with pre and number.
 *
 * @param semester the semester to obtain all classes for
 * @param pre the pre value to obtain all classes for
 * @param pre the pre value to obtain all classes for
 * @return a <code>List</code> of classes in semester with pre and number
 * @throws IOException if the class list can not be returned
 */
public List getClassSectionList (int semester, String pre, String number) 
	throws IOException;

/**
 * Returns a <code>List</code> of class info for <code>StudentClass</code> c.
 * The class info should be in order corresponding to the titles returned by
 * <code>getClassInfoTitles()</code>.
 *
 * @param c the class to obtain class info for
 * @return class info for c
 */
public List getClassInfo (StudentClass c);

/**
 * Tests if course is a valid or existing course.  Has the option to check 
 * either if course is properly formatted, or if course is an actual course.
 *
 * @param course the course to be checked
 * @return <code>true</code> if course is a valid or existing course
 */
public boolean isACourse (Course course);

/**
 * Tests if c is a valid or existing class.  Has the option to check 
 * either if c is properly formatted, or if c is an actual class.
 *
 * @param c the c to be checked
 * @return <code>true</code> if c is a valid or existing class
 */
public boolean isAClass (StudentClass c);

/**
 * Returns a <code>List</code> of <code>Course</code>s representing all 
 * prerequisites for course.
 *
 * @param course the course to get the prerequisites for
 * @return a <code>List</code> of <code>Course</code>s, prerequisites of course
 */
public List getCoursePrerequisites (Course course);

/**
 * Returns the curriculum corresponding to curriculumName.
 *
 * @param curriculumName the curriculum to be retrieved
 * @return the curriculum for curriculumName
 */
public Curriculum getCurriculum (String curriculumName);

/**
 * Returns the description of course.
 *
 * @param course the course to retrieve the description for
 * @return the description for course
 */
public String getCourseDescription (Course course);

/**
 * Returns a <code>List</code> of <code>String</code> for all courses matching 
 * searchString.  Will search titles for searchString if searchTitles is 
 * <code>true</code> and descriptions if searchDescriptions is <code>true</code>.
 * The <code>String</code> for each course can be just the <code>Course</code>'s
 * <code>toString()</code> value, or longer with titles and descriptions.
 *
 * @param searchString string to search courses for
 * @param searchTitles <code>true</code> if titles should be searched
 * @param searchDescriptions <code>true</code> if descriptions should be searched
 * @return a <code>List</code> of all courses matching searchString
 * @throws IOException if the courses can not be correctly searched
 */
public List searchCourses (String searchString, boolean searchTitles, 
	boolean searchDescriptions) throws IOException;

/**
 * Returns a <code>String</code> version of c.  The way 
 * <code>StudentClass</code>es are displayed can be customized using this method.
 * Can display all necessary data on c in any format desired.
 *
 * @param c class object to be formatted
 * @return a <code>String</code> of c in the appropriate format
 */
public String classToString (StudentClass c);

/**
 * Returns time in the appropriate format.  The way <code>TimeOfDay</code> values
 * are displayed can be customized using this method.  Can display times in any
 * format desired: AM/PM, military time, etc.
 *
 * @param time time of day to be formatted
 * @return a <code>String</code> of time in the appropriate format
 */
public String timeToString (TimeOfDay time);

/**
 * Returns the name of this <code>SchoolAdapter</code> as a <code>String</code>.
 *
 * @return the name of this <code>SchoolAdapter</code>
 */
public String toString ();

}
