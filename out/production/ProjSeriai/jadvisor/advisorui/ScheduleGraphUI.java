/*
 * jadvisor/advisorui/ScheduleGraphUI.java - Frame UI for ScheduleGraph
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

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ScheduleGraphUI extends JFrame {
	private static final String ICONFILENAME = "/icons/ja.gif";
	private static ScheduleGraphUI _window;

	private final StudentSchedule _schedule;
	private final Properties _properties;
	private final SchoolAdapter _schoolAdapter;
	private final ScheduleGraph _scheduleGraph;

public ScheduleGraphUI (StudentSchedule schedule, Properties properties, SchoolAdapter schoolAdapter) {
	super("Schedule Graph - JAdvisor");
	_schedule = schedule;
	_properties = properties;
	_schoolAdapter = schoolAdapter;
	_scheduleGraph = new ScheduleGraph(_schedule, _schoolAdapter);
	
	frame();
}

public static void initialize (StudentSchedule schedule, Properties properties, SchoolAdapter schoolAdapter) {
	if (_window != null)
		_window.setVisible(false);
	_window = new ScheduleGraphUI(schedule, properties, schoolAdapter);
}

public static void showDialog () {
	if (_window != null) {
		_window.setVisible(true);
	} else {
		System.err.println("ScheduleGraphUI requires you to call initialize "
			+ "before calling showDialog.");
	}
}

public static void hideDialog () {
	if (_window != null) {
		_window.setVisible(false);
	} else {
		System.err.println("ScheduleGraphUI requires you to call initialize "
			+ "before calling hideDialog.");
	}
}

public static void addMenuWindowListener (WindowListener l) {
	if (_window != null) {
		_window.addWindowListener(l);
	} else {
		System.err.println("ScheduleGraphUI requires you to call initialize "
			+ "before calling addMenuWindowListener.");
	}
}

private void frame () {
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	getContentPane().setLayout(new BorderLayout());
	try {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(ICONFILENAME)));
	} catch (NullPointerException e) {System.err.println("Could not open " + ICONFILENAME + ".");}

	getContentPane().add(new JScrollPane(_scheduleGraph), BorderLayout.CENTER);
	getContentPane().add(controlPane(), BorderLayout.SOUTH);
	//pack();
	setSize(Integer.parseInt(_properties.getProperty("sgwidth", "100")), 
		Integer.parseInt(_properties.getProperty("sgheight", "200")));
	setLocation(Integer.parseInt(_properties.getProperty("sgxloc", "0")), 
		Integer.parseInt(_properties.getProperty("sgyloc", "0")));
	setVisible(_properties.getProperty("sgvisible", "false").equals("true"));
}

private JPanel controlPane() {
	final JPanel result = new JPanel();

	final JButton htmlButton = new JButton("Save as HTML");
	htmlButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			FileUtility.writeStringToFile(formatToSaveAsHTML());
		}
	});
	result.add(htmlButton);

/*	final JButton printButton = new JButton("Print");
	printButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//print schedule graph
		}
	});
	result.add(printButton);*/

	final JButton closeButton = new JButton("Close");
	closeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	});
	result.add(closeButton);

	return result;
}

public static void saveProperties (Properties p) {
	p.setProperty("sgxloc", "" + _window.getX());
	p.setProperty("sgyloc", "" + _window.getY());
	p.setProperty("sgwidth", "" + _window.getWidth());
	p.setProperty("sgheight", "" + _window.getHeight());
	p.setProperty("sgvisible", "" + _window.isVisible());
}

private String formatToSaveAsHTML () {
	String output = _scheduleGraph.getText();
	int index = output.indexOf("border=\"0\"");
	StringBuffer outputBuffer = new StringBuffer();
	outputBuffer.append(output.substring(0, index));
	outputBuffer.append("border=\"1\"");
	outputBuffer.append(output.substring(index + 10, output.length()));
	return outputBuffer.toString();
}

}
