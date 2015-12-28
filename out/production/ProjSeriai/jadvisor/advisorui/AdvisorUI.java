/*
 * jadvisor/advisorui/AdvisorUI.java - JAdvisor UI
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

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.net.URL;

/**
 * AdvisorUI extends the <code>JFrame</code> class.  It manages all the UI 
 * elements of JAdvisor: menu, toolbar, scheduler, planner, search, and message
 * field.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class AdvisorUI extends JFrame {
	private static final String ICONFILENAME = "/icons/ja.gif";
	private static final int SPLASHWAIT = 5000;//5000 == 5 seconds
	
	private final Advisor _advisor;
	private final AdvisorMenu _menu;
	private final JToolBar _toolBar;
	private SchedulerUI _scheduler;
	private PlannerUI _planner;
	private SearchUI _search;
	private final JTextArea _textArea;
	private final Component _textAreaComponent;
	private SchoolAdapter _schoolAdapter;
	private Properties _properties;

public AdvisorUI(Advisor advisor, Properties properties) {
	super("JAdvisor");
    
    _advisor = advisor;
	_properties = properties;

	SplashWindow splash;
	if (_properties.getProperty("showsplash", "true").equals("true"))
		splash = new SplashWindow(this, SPLASHWAIT);//draws splash window

	//sets Look and Feel
	try {
        UIManager.setLookAndFeel(
	    	UIManager.getSystemLookAndFeelClassName());
	    //UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) { }
    
    load();

	//OptionsUI.initialize(this, this, _schoolAdapter, (Properties)_properties.clone());
	
	_menu = new AdvisorMenu(advisor, this);
	_toolBar = toolBar();
	_textArea = textArea();
	_textAreaComponent = new JScrollPane(_textArea,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	frame();
}

private void load () {
}

private void frame () {
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	
	setJMenuBar(_menu);
	getContentPane().setLayout(new BorderLayout(0, 3));
	
	try {
		//setIconImage(Toolkit.getDefaultToolkit().getImage(ICONFILENAME));
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(ICONFILENAME)));
		//setIconImage(FileUtility.getImageFromJAR(ICONFILENAME, getClass()));
	} catch (NullPointerException e) {System.err.println("Could not open " + ICONFILENAME + ".");}
	
	addWindowListener(exitAction());
	setSize(Integer.parseInt(_properties.getProperty("width", "600")), 
		Integer.parseInt(_properties.getProperty("height", "450")));
	setLocation(Integer.parseInt(_properties.getProperty("xloc", "0")), 
		Integer.parseInt(_properties.getProperty("yloc", "0")));
}

private WindowListener exitAction () {
	return new WindowAdapter () {
		public void windowClosing(WindowEvent e) {
			_advisor.exit();}
	};
}

private JToolBar toolBar () {
	final JToolBar result = new JToolBar();
	final JButton scheduleButton = new JButton("Schedule");
	final JButton scheduleGraphButton = new JButton("Schedule Graph");
	scheduleGraphButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ScheduleGraphUI.showDialog();
		}
	});
	final JButton planButton = new JButton("Plan");
	planButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		}
	});
	//result.add(scheduleButton);
	result.add(scheduleGraphButton);
	//result.add(planButton);
	result.setFloatable(false);
	return result;
}

private JTextArea textArea () {
	JTextArea result = new JTextArea(3, 30);
	result.setEditable(false);
	return result;
}

void open (StudentFile studentFile, File file, SchoolAdapter schoolAdapter) {
	_schoolAdapter = schoolAdapter;
	_scheduler = new SchedulerUI(studentFile.getStudentSchedule(),
		studentFile.getStudentPlan(), studentFile.getStudentInfo(), 
		schoolAdapter, _properties);

	_planner = new PlannerUI(this, studentFile.getStudentPlan(), studentFile.getStudentInfo(), schoolAdapter);
	_search = new SearchUI(schoolAdapter);
	final JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.addTab("Scheduler", _scheduler);
	tabbedPane.addTab("Planner", _planner);
	tabbedPane.addTab("Search", _search);
	tabbedPane.setSelectedIndex(0);
	getContentPane().removeAll();
	getContentPane().add(_toolBar, BorderLayout.NORTH);
	getContentPane().add(tabbedPane, BorderLayout.CENTER);
	if (_properties.getProperty("showtextbox", "true").equals("true"))
		getContentPane().add(_textAreaComponent, BorderLayout.SOUTH);
	show();
	setTitle(file);
	
	ScheduleGraphUI.initialize(studentFile.getStudentSchedule(), _properties, schoolAdapter);

	monitorStudentSchedule(studentFile.getStudentSchedule());
	monitorStudentPlan(studentFile.getStudentPlan());
}

void setTitle (File file) {
	if (file == null)
		setTitle("JAdvisor - [untitled]");
	else
		setTitle("JAdvisor - [" + file + "]");
}

void setTextAreaVisible (boolean visible) {
	if (visible)
		getContentPane().add(_textAreaComponent, BorderLayout.SOUTH);
	else
		getContentPane().remove(_textAreaComponent);
	show();
}

void saveProperties (Properties p) {
	p.setProperty("xloc", "" + getX());
	p.setProperty("yloc", "" + getY());
	p.setProperty("width", "" + getWidth());
	p.setProperty("height", "" + getHeight());
	_scheduler.saveProperties(p);
	ScheduleGraphUI.saveProperties(p);
}

void showAboutDialog () {
	JPanel aboutPanel = new JPanel();
	aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
	aboutPanel.add(new JLabel("JAdvisor - a Java class scheduler and course planner for students."));
	aboutPanel.add(new JLabel("Copyright 2001-2002 Curtis Rawls"));
	aboutPanel.add(new JLabel("All rights reserved."));
	aboutPanel.add(new JLabel("jadvisor.sourceforge.net"));
	JOptionPane optionPane = new JOptionPane(aboutPanel, 
		JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, 
		new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(ICONFILENAME))));
	JDialog dialog = optionPane.createDialog(this, "About JAdvisor v0.4.6");
	dialog.show();
}

private void monitorStudentSchedule(StudentSchedule p) {
	p.addListener(new StudentScheduleListener() {
		public void classAdded (StudentSchedule s, StudentClass c) {
			_textArea.append("Class added: " + _schoolAdapter.classToString(c) + "\n");
		}
		public void classRemoved (StudentSchedule s, StudentClass c) {
			_textArea.append("Class removed: " + _schoolAdapter.classToString(c) + "\n");
		}
		public void blockAdded (StudentSchedule s, StudentBlock b) {
			_textArea.append("Block added: " + b.toString() + "\n");
		}
		public void blockRemoved (StudentSchedule s, StudentBlock b) {
			_textArea.append("Block removed: " + b.toString() + "\n");
		}
	});
}

private void monitorStudentPlan(StudentPlan p) {
	p.addListener(new StudentPlanListener() {
		public void courseAdded (StudentPlan s, Course c, int year, int semester) {
			_textArea.append("Course added: " + c.toString() + " to " 
				+ year + " " + _schoolAdapter.getSemesters()[semester] + "\n");
		}
		public void courseRemoved (StudentPlan s, Course c, int year, int semester) {
			_textArea.append("Course removed: " + c.toString() + " from " 
				+ year + " " + _schoolAdapter.getSemesters()[semester] + "\n");
		}
	});
}

}
