/*
 * jadvisor/advisorui/HTMLGenerator.java - Generates HTML for plans and schedules
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

package jadvisor.advisorui;

import jadvisor.planner.*;
import jadvisor.scheduler.*;
import jadvisor.school.*;

import java.util.*;
import javax.swing.ListModel;

/**
 * This class provides static methods for generating
 * Strings of HTML from a <code>StudentSchedule</code> or
 * <code>StudentPlan</code>.
 *
 * @author Curtis Rawls
 * @version 0.4.3
 */
public class HTMLGenerator {

/**
 * Class constructor.
 */
public HTMLGenerator () {
}

/**
 * Generates a String of HTML from a <code>StudentSchedule</code>.
 *
 * @param schedule
 * @param studentInfo
 * @param schoolAdapter
 * @param table
 * @param graph
 * @return String of HTML generated from plan
 */
public static String genHTML (StudentSchedule schedule, StudentInfo studentInfo, 
		SchoolAdapter schoolAdapter, boolean table, boolean graph) {
	StringBuffer result = new StringBuffer();

	if (table)
		result.append(genScheduleTable(schedule, studentInfo, schoolAdapter));
	if (graph)
		result.append(genScheduleGraph(schedule, studentInfo, schoolAdapter));

	return result.toString();
}

private static String genScheduleTable (StudentSchedule schedule, StudentInfo studentInfo, 
		SchoolAdapter schoolAdapter) {
	StringBuffer result = new StringBuffer();

	List classes = schedule.getClasses();
	List blocks = schedule.getBlocks();

	result.append("<TABLE BORDER=1 CELLSPACING=1 WIDTH=100%>");
	result.append("<TH>Description</TH>");
	result.append("<TH>Course</TH>");
	result.append("<TH>" + schoolAdapter.getClassTitles()[2] + "</TH>");
	result.append("<TH>" + schoolAdapter.getClassTitles()[7] + "</TH>");
	result.append("<TH>" + schoolAdapter.getClassTitles()[3] + "</TH>");
	result.append("<TH>Time</TH>");
//	result.append("<TH>Building</TH>");

	for (int i = 0; i < classes.size(); i++)
		result.append(genClassTableLine((StudentClass)classes.get(i), schoolAdapter));
	for (int i = 0; i < blocks.size(); i++)
		result.append(genBlockTableLine((StudentBlock)blocks.get(i), schoolAdapter));
	result.append("</TABLE>");

	result.append("Total Credit: " + schedule.getCreditTotal());

	return result.toString();
}

private static String genClassTableLine (StudentClass c, SchoolAdapter schoolAdapter) {
	StringBuffer result = new StringBuffer();

	result.append("<TR>");
	result.append("<TD>" + c.getDescription() + "</TD>");
	result.append("<TD>" + c.getCourse() + "</TD>");
	result.append("<TD>" + c.getSection() + "</TD>");
	result.append("<TD>" + c.getCredit() + "</TD>");
	result.append("<TD>");
	for (int i = 0; i < c.getDays().length; i++)
		if (c.getDays()[i])
			result.append(schoolAdapter.getDaysAbbreviations()[i]);
		else
			result.append("&nbsp;");
	result.append("</TD>");
	result.append("<TD>" + schoolAdapter.timeToString(c.getStartTime()) + "-" 
		+ schoolAdapter.timeToString(c.getEndTime()) + "</TD>");
//	result.append("<TD></TD>");
	result.append("</TR>");

	return result.toString();
}

private static String genBlockTableLine (StudentBlock b, SchoolAdapter schoolAdapter) {
	StringBuffer result = new StringBuffer();

	result.append("<TR>");
	result.append("<TD>" + b.getDescription() + "</TD>");
	result.append("<TD><BR></TD>");
	result.append("<TD><BR></TD>");
	result.append("<TD><BR></TD>");
	result.append("<TD>");
	for (int i = 0; i < b.getDays().length; i++)
		if (b.getDays()[i])
			result.append(schoolAdapter.getDaysAbbreviations()[i]);
		else
			result.append("&nbsp;");
	result.append("</TD>");
	result.append("<TD>" + schoolAdapter.timeToString(b.getStartTime()) + "-" 
		+ schoolAdapter.timeToString(b.getEndTime()) + "</TD>");
//	result.append("<TD></TD>");
	result.append("</TR>");

	return result.toString();
}

private static String genScheduleGraph (StudentSchedule schedule, StudentInfo studentInfo, 
		SchoolAdapter schoolAdapter) {
	StringBuffer result = new StringBuffer();

	return result.toString();
}

/**
 * Generates a String of HTML from a <code>StudentPlan</code>.
 *
 * @param plan
 * @param studentInfo
 * @param schoolAdapter
 * @param showOptional
 * @return String of HTML generated from plan
 */
public static String genHTML (StudentPlan plan, StudentInfo studentInfo, 
		SchoolAdapter schoolAdapter, boolean showOptional) {
	StringBuffer result = new StringBuffer();

	final String td = "<TD VALIGN=TOP>";

	result.append("<TABLE BORDER=1 CELLSPACING=1 WIDTH=100%>");
	result.append("<TR><TH></TH>");
	for (int i = 0; i < schoolAdapter.getSemesters().length; i++) {
		if (showOptional || schoolAdapter.getRequiredSemesters()[i]) {
			result.append("<TH>");
			result.append(schoolAdapter.getSemesters()[i]);
			result.append("</TH>");
		}
	}
	result.append("<TR>");

	for (int i = 0; i < plan.years(); i++) {
		result.append("<TR>");
		result.append(td + (studentInfo.getFirstYear() + i) + "</TD>");
		for (int j = 0; j < plan.semesters(i); j++) {
			if (showOptional || schoolAdapter.getRequiredSemesters()[j]) {
				result.append(td);
				ListModel model = plan.getSemester(i, j);
				for (int k = 0; k < model.getSize(); k++) {
					result.append(((Course)model.getElementAt(k)).toString() + "<BR>");
				}
				if (model.getSize() == 0)
					result.append("<BR>");
				result.append("</TD>");
			}
		}
		result.append("</TR>");
	}
	result.append("</TABLE>");

	return result.toString();
}

}