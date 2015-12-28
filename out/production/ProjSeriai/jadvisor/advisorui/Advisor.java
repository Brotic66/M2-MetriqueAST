/*
 * jadvisor/advisorui/Advisor.java - College scheduler and course planner
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * JAdvisor is a class scheduler, course planner, and course 
 * search program. It also allows college students to view 
 * their schedules graphically and create an optimal schedule. 
 * Adapters are used to customize JAdvisor for your particular 
 * school.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class Advisor {
	private static final String SETTINGS_DIRECTORY = System.getProperty("user.home") +
		File.separator + ".jadvisor" + File.separator;
	private static final File PROPERTIESFILE = new File(SETTINGS_DIRECTORY + ".japrop");
	
	private final AdvisorUI _advisorUI;
	private StudentInfo _studentInfo;
	private StudentFile _studentFile;
	private StudentSchedule _schedule;
	private StudentPlan _studentPlan;
	private SchoolAdapter _schoolAdapter;
	private File _file;
	private Properties _properties;

public Advisor (File file) {
	_properties = new Properties();
	loadProperties(_properties, PROPERTIESFILE);

	_advisorUI = new AdvisorUI(this, _properties);
	if (file == null)
		newStudentFile();
	else
		openStudentFile(file);
	
	_advisorUI.open(_studentFile, _file, _schoolAdapter);
	
	//tests
//	scheduleTest();
//	studentPlanTest();
}

public Advisor () {
	this(null);
}

private void newStudentFile () {
	newStudentWizard();
	_file = null;
//	_schoolAdapter = loadSchoolAdapter(null);
//	_studentInfo = new StudentInfo();
	_schedule = new StudentSchedule();
//	_studentPlan = new StudentPlan(4, _schoolAdapter.getSemesters().length);
	_studentFile = new StudentFile(_studentInfo, _schedule, _studentPlan);
	System.out.println("New File");
}

private void openStudentFile (File file) {
	_file = file;
	try {
		_studentFile = (StudentFile)FileUtility.openFile(_file);
	} catch (Exception e) {
		System.err.println("Open File: " + e.getMessage());
		JOptionPane.showMessageDialog(_advisorUI, "Could not open file " + _file, 
		"Open",	JOptionPane.WARNING_MESSAGE);
		newStudentFile();
		return;
	}
	if (_studentFile != null) {
		_schoolAdapter = loadSchoolAdapter(null);
		_studentInfo = _studentFile.getStudentInfo();
		_schedule = _studentFile.getStudentSchedule();
		_studentPlan = _studentFile.getStudentPlan();
		System.out.println("Opening: " + _file.getName());
	} else {
		//do something for errors opening files here
		newStudentFile();
	}
}

private void saveStudentFile (File file) {
	_file = file;
	System.out.println("Saving: " + _file.getName());
	FileUtility.saveFile(_studentFile, _file);
	_advisorUI.setTitle(_file);
}

void newFile () {
	newStudentFile();
	_advisorUI.open(_studentFile, _file, _schoolAdapter);
}

void openFile () {
	//Create a file chooser
	final JFileChooser fc = new JFileChooser();
	fc.addChoosableFileFilter(getJAFileFilter());
	int returnVal = fc.showOpenDialog(_advisorUI);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		openStudentFile(fc.getSelectedFile());
		_advisorUI.open(_studentFile, _file, _schoolAdapter);
	} else {
		System.out.println("Open command cancelled by user.");
	}
}

void saveFile () {
	if (_file == null)//if file has not been saved yet
		saveAsFile();
	else {
		saveStudentFile(_file);
	}
}

void saveAsFile () {
	//Create a file chooser
	final JFileChooser fc = new JFileChooser();
	if (_file != null)
		fc.setSelectedFile(_file);
	fc.addChoosableFileFilter(getJAFileFilter());
	int returnVal = fc.showSaveDialog(_advisorUI);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
		saveStudentFile(fc.getSelectedFile());
	} else {
		System.out.println("Save command cancelled by user.");
	}
}

void saveAsHTML () {
	boolean[] dialogValues = AdvisorDialogs.showSaveAsHTMLDialog(
		_advisorUI, "Save as HTML - JAdvisor", _schoolAdapter);
	if (dialogValues != null) {
		if (dialogValues[0])
			FileUtility.writeStringToFile(HTMLGenerator.genHTML(_studentPlan, 
				_studentInfo, _schoolAdapter, dialogValues[1]));
		else
			FileUtility.writeStringToFile(HTMLGenerator.genHTML(_schedule, 
				_studentInfo, _schoolAdapter, dialogValues[2], dialogValues[3]));
	}
}

void print () {
	System.out.println("Print");
}

void editPreferences () {
	if(AdvisorDialogs.showPreferencesDialog(
			_advisorUI, "Preferences - JAdvisor", _properties))
		_advisorUI.setTextAreaVisible(
			_properties.getProperty("showtextbox", "true").equals("true"));
}

void editCurriculums () {
	String[] curriculums = AdvisorDialogs.showCurriculumsDialog(_advisorUI, 
		"Curriculums - JAdvisor", _studentInfo.getCurriculumNames(), _schoolAdapter);
}

void editStudentInfo () {
	int[] dialogValues = AdvisorDialogs.showStudentInfoDialog(_advisorUI, 
		"Student Info - JAdvisor", _schoolAdapter);

//	_studentInfo = new StudentInfo(curriculums, 
//		dialogValues[0], dialogValues[2]);
//	_studentPlan = new StudentPlan(dialogValues[1], 
//		_schoolAdapter.getSemesters().length);
}

void exit () {
	int n = JOptionPane.showConfirmDialog(_advisorUI,
		"Do you want to save your changes?", "JAdvisor",
		JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
	if (n == JOptionPane.CANCEL_OPTION || n == JOptionPane.CLOSED_OPTION)
		return;
	if (n == JOptionPane.YES_OPTION)
		saveFile();

	saveProperties();
	storeProperties(_properties, PROPERTIESFILE);
	System.out.println("Exit");
	System.exit(0);
}

private void loadProperties (Properties p, File f) {
	File parentDir = f.getParentFile();
	if (!parentDir.exists())
		parentDir.mkdir();

	try {
		p.load(new FileInputStream(f));
	} catch (IOException e) {
		System.err.println(e.getMessage());
	}
}

private void storeProperties (Properties p, File f) {
	try {
		p.store(new FileOutputStream(f), "JAdvisor properties");
	} catch (IOException e) {
		System.err.println(e.getMessage());
	}
}

private void saveProperties () {
	_advisorUI.saveProperties(_properties);
}

private SchoolAdapter loadSchoolAdapter (String name) {
	SchoolAdapter[] adapters = 
		new SchoolAdapter[] {new NCSU(), new UNC(), new DefaultSchoolAdapter()};
	return AdvisorDialogs.showLoadSchoolAdapterDialog(_advisorUI, 
		"Choose Your SchoolAdapter", adapters);
}

private void newStudentWizard () {
	_schoolAdapter = loadSchoolAdapter(null);

	String[] curriculums = AdvisorDialogs.showCurriculumsDialog(_advisorUI, 
		"Curriculums - JAdvisor", new String[0], _schoolAdapter);

	int[] dialogValues = AdvisorDialogs.showStudentInfoDialog(_advisorUI, 
		"Student Info - JAdvisor", _schoolAdapter);

	_studentInfo = new StudentInfo(curriculums, 
		dialogValues[0], dialogValues[2]);
	_studentPlan = new StudentPlan(dialogValues[1], dialogValues[0], 
		_schoolAdapter.getSemesters());
}

private FileFilter getJAFileFilter () {
	return new FileFilter () {
		// Accept all directories and all jadvisor files.
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
	
			String extension = FileUtility.getExtension(f);
			if (extension != null && extension.equals("jaf"))
				return true;
			return false;
		}
	
		// The description of this filter
		public String getDescription() {
			return "JAdvisor Files (.jaf)";
		}
	};
}

private void scheduleTest () {
	StudentClass a = new StudentClass(new Course("CSC","295H"), "001",
		new boolean[] {true, false, true, false, true}, new TimeOfDay(8,0,TimeOfDay.AM), 
		new TimeOfDay(9,0,TimeOfDay.AM), 0, 1);
	a.setInfo(_schoolAdapter.getClassInfo(a));
	StudentClass b = new StudentClass(new Course("CSC","216"), "001",
		new boolean[] {true, false, true, false, true}, new TimeOfDay(8,30,TimeOfDay.AM), 
		new TimeOfDay(9,0,TimeOfDay.AM), 0, 1);
	StudentClass c = new StudentClass(new Course("CSC","222"), "002",
		new boolean[] {true, true, false, true, false}, new TimeOfDay(8,0,TimeOfDay.AM), 
		new TimeOfDay(9,0,TimeOfDay.AM), 0, 1);
	try {
		_schedule.add(a);
	} catch (IllegalArgumentException e) {System.err.println(e.getMessage());}
	try {
		_schedule.add(b);
	} catch (IllegalArgumentException e) {System.err.println(e.getMessage());}
	try {
		_schedule.add(c);
	} catch (IllegalArgumentException e) {System.err.println(e.getMessage());}
		
}

private void studentPlanTest () { 
	Course bob = new Course("CSC","295H");
	_studentPlan.add(bob,0,0);
}

void show () {
	_advisorUI.show();
}

public static void main (String[] args) {
	final Advisor jadvisor;
	if (args.length > 0)//if there is a filename argument, then open file
		jadvisor = new Advisor(new File(args[0]));
	else
		jadvisor = new Advisor();
	jadvisor.show();
}

}
