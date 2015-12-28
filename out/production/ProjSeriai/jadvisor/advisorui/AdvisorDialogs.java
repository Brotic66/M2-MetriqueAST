/*
 * jadvisor/advisorui/AdvisorDialogs.java - Collection of dialogs for JAdvisor
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

import jadvisor.school.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.IOException;

/**
 * AdvisorDialogs is a collection of dialogs used by JAdvisor.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
class AdvisorDialogs {

static boolean showPreferencesDialog (Frame parent, String title, Properties properties) {
	JPanel prefPanel = new JPanel();
	prefPanel.setLayout(new GridLayout(0,1));
	JCheckBox showSplash = new JCheckBox("Show splash on startup");
	showSplash.setSelected(properties.getProperty("showsplash", "true").equals("true"));
	prefPanel.add(showSplash);
	JCheckBox showTextBox = new JCheckBox("Show text box");
	showTextBox.setSelected(properties.getProperty("showtextbox", "true").equals("true"));
	prefPanel.add(showTextBox);

	int optionValue = JOptionPane.showConfirmDialog(parent, prefPanel, title,
		JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

	if (optionValue != JOptionPane.OK_OPTION)
		return false;

	properties.setProperty("showsplash", "" + showSplash.isSelected());
	properties.setProperty("showtextbox", "" + showTextBox.isSelected());
	return true;
}

static SchoolAdapter showLoadSchoolAdapterDialog (Frame parent, String title, 
		SchoolAdapter[] schoolAdapters) {
	JList list = new JList(schoolAdapters);
	list.setSelectedIndex(0);
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	JPanel aboutPanel = new JPanel();
	aboutPanel.add(list);
	JOptionPane optionPane = new JOptionPane(list, 
		JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
	JDialog dialog = optionPane.createDialog(parent, title);
	dialog.show();
	
	return schoolAdapters[list.getSelectedIndex()];
}

static boolean[] showSaveAsHTMLDialog (Frame parent, String title, 
		SchoolAdapter schoolAdapter) {
	final JPanel panel = new JPanel();
	final JPanel schedulePanel = new JPanel();
	schedulePanel.setBorder(BorderFactory.createEtchedBorder());
	final JPanel planPanel = new JPanel();
	planPanel.setBorder(BorderFactory.createEtchedBorder());
	panel.setLayout(new GridLayout(1,0));
	schedulePanel.setLayout(new BoxLayout(schedulePanel, BoxLayout.Y_AXIS));
	planPanel.setLayout(new BoxLayout(planPanel, BoxLayout.Y_AXIS));

	final JRadioButton scheduleButton = new JRadioButton("Schedule");
	scheduleButton.setSelected(true);
	final JRadioButton planButton = new JRadioButton("Plan");
	final ButtonGroup saveGroup = new ButtonGroup();
	saveGroup.add(scheduleButton);
	saveGroup.add(planButton);

	final JCheckBox scheduleTableButton = new JCheckBox("Schedule Table");
	scheduleTableButton.setSelected(true);
	final JCheckBox scheduleGraphButton = new JCheckBox("Schedule Graph");
	scheduleGraphButton.setSelected(true);
	final JCheckBox planOptionalSemester = new JCheckBox(
		schoolAdapter.getOptionalSemesterText());
	planOptionalSemester.setSelected(true);

	schedulePanel.add(scheduleButton);
	schedulePanel.add(scheduleTableButton);
	//schedulePanel.add(scheduleGraphButton);
	planPanel.add(planButton);
	planPanel.add(planOptionalSemester);
	panel.add(schedulePanel);
	panel.add(planPanel);

	final JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, 
		JOptionPane.OK_CANCEL_OPTION);
	final JDialog dialog = pane.createDialog(parent, title);
	dialog.show();

	Object selectedValue = pane.getValue();
	if (selectedValue != null && 
			selectedValue instanceof Integer && 
			((Integer)selectedValue).intValue() == JOptionPane.YES_OPTION)
		return new boolean[] {planButton.isSelected(), 
			planOptionalSemester.isSelected(),
			scheduleTableButton.isSelected(),
			scheduleGraphButton.isSelected()};
	return null;
}

static int[] showStudentInfoDialog (Frame parent, String title, 
		SchoolAdapter schoolAdapter) {
	//Year info
	final JTextField firstYearField = new JTextField("2002", 4);
	final JTextField numYearsField = new JTextField("4", 4);
	final JPanel labelpane = new JPanel();
	labelpane.setLayout(new GridLayout(0, 1));
	labelpane.add(new JLabel("First Year:"));
	labelpane.add(new JLabel("Num of Years:"));
	final JPanel fieldpane = new JPanel();
	fieldpane.setLayout(new GridLayout(0, 1));
	fieldpane.add(firstYearField);
	fieldpane.add(numYearsField);
	final JPanel yearpane = new JPanel();
	yearpane.add(labelpane);
	yearpane.add(fieldpane);

	//Current Semester Info
	final JComboBox semesterBox = new JComboBox(schoolAdapter.getSemesters());
	final JPanel semesterPane = new JPanel();
	semesterPane.setLayout(new BorderLayout());
	semesterPane.add(new JLabel("Semester:"), BorderLayout.NORTH);
	semesterPane.add(semesterBox, BorderLayout.SOUTH);

	final JPanel panel = new JPanel();
	panel.add(yearpane);
	panel.add(semesterPane);
	final JOptionPane optionPane = new JOptionPane(panel, 
		JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
	final JDialog dialog = optionPane.createDialog(parent, title);
	dialog.show();

	int[] result = new int[3];
	try {
		result[0] = Integer.parseInt(firstYearField.getText());
	} catch (NumberFormatException e) {
		result[0] = 2002;//should show error message, reshow dialog
	}
	try {
		result[1] = Integer.parseInt(numYearsField.getText());
	} catch (NumberFormatException e) {
		result[1] = 4;//should show error message, reshow dialog
	}
	result[2] = semesterBox.getSelectedIndex();
	return result;
}

static String[] showCurriculumsDialog (Frame parent, String title, String[] curriculums,
		SchoolAdapter schoolAdapter) {
	final Vector curriculumsVector = new Vector(curriculums.length);
	for (int i = 0; i < curriculums.length; i++)
		curriculumsVector.add(curriculums[i]);
	final JList schoolCurriculumsList = new JList();
	try {
		schoolCurriculumsList.setListData(schoolAdapter.getClassPreList(0));
	} catch (IOException e) {System.out.println("AdvisorDialogs: Catch this exception");}
	schoolCurriculumsList.setSelectedIndex(0);
	schoolCurriculumsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	schoolCurriculumsList.setPrototypeCellValue("AAAAA");//sets prefered size
	final JList studentCurriculumsList = new JList(curriculumsVector);
	studentCurriculumsList.setSelectedIndex(-1);
	studentCurriculumsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	studentCurriculumsList.setPrototypeCellValue("AAAAA");//sets prefered size
	final JButton addButton = new JButton("Add");
	addButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//add to studentlist
			if (schoolCurriculumsList.getSelectedIndex() >= 0 && 
					!curriculumsVector.contains(
						(String)schoolCurriculumsList.getSelectedValue())) {
				curriculumsVector.add((String)schoolCurriculumsList.getSelectedValue());
				studentCurriculumsList.setListData(curriculumsVector);
			}
		}
	});
	final JButton removeButton = new JButton("Remove");
	removeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//remove from studentlist
			if (studentCurriculumsList.getSelectedIndex() >= 0) {
				curriculumsVector.remove((String)studentCurriculumsList.getSelectedValue());
				studentCurriculumsList.setListData(curriculumsVector);
			}
		}
	});
	final JPanel curriculumListPane = new JPanel();
	curriculumListPane.add(new JScrollPane(schoolCurriculumsList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	curriculumListPane.add(new JScrollPane(studentCurriculumsList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	final JPanel curriculumButtonPane = new JPanel();
	curriculumButtonPane.add(addButton);
	curriculumButtonPane.add(removeButton);
	final JPanel curriculumPane = new JPanel();
	curriculumPane.setLayout(new BorderLayout());
	curriculumPane.add(new JLabel("Select your Curriculum(s):"), BorderLayout.NORTH);
	curriculumPane.add(curriculumListPane, BorderLayout.CENTER);
	curriculumPane.add(curriculumButtonPane, BorderLayout.SOUTH);

	final JOptionPane optionPane = new JOptionPane(curriculumPane, 
		JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
	final JDialog dialog = optionPane.createDialog(parent, title);
	dialog.show();

	String[] result = new String[curriculumsVector.size()];
	for (int i = 0; i < result.length; i++)
		result[i] = (String)curriculumsVector.get(i);
	return result;
}

}
