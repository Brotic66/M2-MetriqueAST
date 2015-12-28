/*
 * jadvisor/advisorui/ScheduleGraph.java - Graphs a schedule in an HTML pane
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

import jadvisor.scheduler.*;
import jadvisor.school.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLEditorKit;
import java.util.*;

public class ScheduleGraph extends JTextPane {
	private static final TimeOfDay STARTTIME = new TimeOfDay(7, 0);
	private static final TimeOfDay ENDTIME = new TimeOfDay(23, 0);
	private static final int INCTIME = 30;

	private StudentSchedule _schedule;
	private final SchoolAdapter _schoolAdapter;

	private final String _htmlHeader;
	private final String _htmlFooter;
	private final String[] _htmlTimes;
	private final TimeOfDay[] _times;

public ScheduleGraph (StudentSchedule schedule, SchoolAdapter schoolAdapter) {
	super();
	_schedule = schedule;
	_schoolAdapter = schoolAdapter;
	_htmlHeader = htmlHeader();
	_htmlFooter = htmlFooter();
	_times = times();
	_htmlTimes = htmlTimes();
	
	pane();

	monitorStudentSchedule(_schedule);
}

public ScheduleGraph (SchoolAdapter schoolAdapter) {
	this(new StudentSchedule(), schoolAdapter);	
}

public void setSchedule (StudentSchedule schedule) {
	_schedule = schedule;
	updateGraph();
	monitorStudentSchedule(_schedule);
}

private void pane () {
	setEditable(false);
	setEditorKit(new HTMLEditorKit());
	setText(html());
}

private String html () {
	final StringBuffer result = new StringBuffer();
	result.append(_htmlHeader);
	result.append(htmlClasses());
	result.append(_htmlFooter);
	
/*	final List classesList = _schedule.getClasses();
	for (int i = 0; i < classesList.size(); i++) {
		final StudentClass c = (StudentClass)classesList.get(i);
		result.append(c.getCourse().toString() + " " + c.getSection() + "<br>");
	}
	final List blocksList = _schedule.getBlocks();
	for (int i = 0; i < blocksList.size(); i++) {
		final StudentBlock c = (StudentBlock)blocksList.get(i);
		result.append(c.toString() + "<br>");
	}*/
	//System.out.println(result);
	return result.toString();
}

private String htmlHeader () {
	final StringBuffer result = new StringBuffer();
	result.append("<HTML><BODY BGCOLOR='WHITE'><TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0 VALIGN='top' WIDTH=100%>"); 
	result.append("<TR><TH></TH>");
	for (int i = 0; i < _schoolAdapter.getDaysAbbreviations().length; i++)
		result.append("<TH BORDER=1>" + _schoolAdapter.getDaysAbbreviations()[i] + "</TH>");
	result.append("</TR>");
	return result.toString();
}

private String htmlClasses () {
	final StringBuffer result = new StringBuffer();

	//set up array
	final List classes = _schedule.getClasses();
	final List blocks = _schedule.getBlocks();
	
//	int numTimeIncs = ENDTIME.compareTo(STARTTIME) / INCTIME;
	//System.out.println(numTimeIncs);
	final String[][] days = new String[_schoolAdapter.getDaysAbbreviations().length][_times.length];
	for (int i = 0; i < days.length; i++)
		Arrays.fill(days[i], "<TD></TD>");

	final String cellStart = "<TD BORDER=1 BGCOLOR=#cccccc ROWSPAN=";

	for (int i = 0; i < classes.size(); i++) {
		StudentClass c = (StudentClass)classes.get(i);
		int start = getStartTime(c);
		int end = getEndTime(c);
		int length = end - start;
		
		for (int j = 0; j < days.length; j++) {
			if (c.getDays()[j]) {
				if (start >= 0 && end < days[j].length) {//prevents array out of bounds 
					days[j][start] = cellStart + length + ">" + getDescription(c) + "</TD>";
					for (int k = 1; k < length; k++)
						days[j][start + k] = "";
				}
			}
		}
	}

	for (int i = 0; i < blocks.size(); i++) {
		StudentBlock b = (StudentBlock)blocks.get(i);
		int start = getStartTime(b);
		int end = getEndTime(b);
		int length = end - start;
		
		for (int j = 0; j < days.length; j++) {
			if (b.getDays()[j]) {
				if (start >= 0 && end < days[j].length) {//prevents array out of bounds 
					days[j][start] = cellStart + length + ">" + getDescription(b) + "</TD>";
					for (int k = 1; k < length; k++)
						days[j][start + k] = "";
				}
			}
		}
	}

	//output html
	for (int i = 0; i < days[0].length; i++) {
		result.append("<TR>");
		result.append(_htmlTimes[i]);
		for (int j = 0; j < days.length; j++)
			result.append(days[j][i]);
		result.append("</TR>");
	}

	return result.toString();
}

private int getStartTime (StudentBlock b) {
	TimeOfDay start = b.getStartTime();
	int hour = (start.getHour() - STARTTIME.getHour()) * 2;
	int minute = start.getMinute();
//	if (minute < 15)
//		hour += 0;
	if (minute >= 15 && minute < 45)
		hour += 1;
	if (minute >= 45)
		hour += 2;
	return hour;
}

private int getEndTime (StudentBlock b) {
	TimeOfDay end = b.getEndTime();
	int hour = (end.getHour() - STARTTIME.getHour()) * 2;
	int minute = end.getMinute();
//	if (minute <= 15)
//		hour += 0;
	if (minute > 15 && minute <= 45)
		hour += 1;
	if (minute > 45)
		hour += 2;
	return hour;
}

private String getDescription (StudentClass c) {
	final StringBuffer result = new StringBuffer();
	result.append(c.getCourse().toString() + " ");
	result.append(c.getSection());
	result.append("<BR>");
	result.append(_schoolAdapter.timeToString(c.getStartTime()) + "-");
	result.append(_schoolAdapter.timeToString(c.getEndTime()));
	return result.toString();
}

private String getDescription (StudentBlock b) {
	final StringBuffer result = new StringBuffer();
	result.append(b.getDescription());
	result.append("<BR>");
	result.append(_schoolAdapter.timeToString(b.getStartTime()) + "-");
	result.append(_schoolAdapter.timeToString(b.getEndTime()));
	return result.toString();
}

private String htmlFooter () {
	final StringBuffer result = new StringBuffer();
	result.append("</TABLE></BODY></HTML>");
	return result.toString();
}

private TimeOfDay[] times () {
	TimeOfDay[] result = new TimeOfDay[ENDTIME.compareTo(STARTTIME) / INCTIME];
	TimeOfDay t = new TimeOfDay(STARTTIME);
	try {
		for (int i = 0; i < result.length; i++) {
			result[i] = new TimeOfDay(t);
			t.add(0, INCTIME);
		}
	} catch (IllegalArgumentException e) {}
	return result;
}

private String[] htmlTimes () {
	String[] result = new String[_times.length];
	for (int i = 0; i < result.length; i++) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<TD BORDER=1>");
		buffer.append(_schoolAdapter.timeToString(_times[i]));
		buffer.append("</TD>");
		result[i] = buffer.toString();
	}
	return result;
}

private void updateGraph () {
	setText(html());
}

private void monitorStudentSchedule (StudentSchedule p) {
	p.addListener(new StudentScheduleListener() {
		public void classAdded (StudentSchedule s, StudentClass c) {
			updateGraph();
		}
		public void classRemoved (StudentSchedule s, StudentClass c) {
			updateGraph();
		}
		public void blockAdded (StudentSchedule s, StudentBlock b) {
			updateGraph();
		}
		public void blockRemoved (StudentSchedule s, StudentBlock b) {
			updateGraph();
		}
	});
}

}
