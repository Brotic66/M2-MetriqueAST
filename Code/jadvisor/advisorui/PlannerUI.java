/*
 * jadvisor/advisorui/PlannerUI.java - Planner UI pane
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
import java.util.*;
import java.io.*;

/**
 * <code>JPanel</code> of <code>AdvisorUI</code> that manages course planning.
 * Displays a list of courses for each curriculum of a student.  Users can add
 * or remove courses from their <code>StudentPlan</code>s.  Can show or 
 * hide optional semesters.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class PlannerUI extends JPanel {
	private final AdvisorUI _advisorUI;
	private Curriculum[] _curriculums;
	private StudentPlan _studentPlan;
	private SchoolAdapter _schoolAdapter;

	private final JList _curriculumList;
	private final JComboBox _curriculumSelectBox;
	private final JPanel _curriculumPane;
	private final PlanViewPanel _coursesListPane;
	private final JPanel _courseNamePane;
	private final JTextField _coursePreField;
	private final JTextField _courseNumberField;
	private final JPanel _optionPane;
	private boolean _showOptional;

public PlannerUI (AdvisorUI advisorUI, StudentPlan studentPlan, StudentInfo studentInfo, 
		SchoolAdapter schoolAdapter) {
	_advisorUI = advisorUI;
	_studentPlan = studentPlan;
	_schoolAdapter = schoolAdapter;
	_curriculums = curriculums(studentInfo);

	_curriculumList = curriculumList();
	_curriculumSelectBox = curriculumSelectBox();
	_curriculumPane = curriculumPane();
	_coursesListPane = new PlanViewPanel(this, _studentPlan, _showOptional, _schoolAdapter);
	_coursePreField = new JTextField(10);
	_courseNumberField = new JTextField(10);
	_courseNamePane = courseNamePane();
	_optionPane = optionPane();
	
	planner();

	monitorStudentPlan(_studentPlan);
}

private void planner() {
	setLayout(new BorderLayout(3,3));
	final JPanel westPane = new JPanel();
	westPane.setLayout(new BorderLayout(3,3));
	westPane.add(_curriculumPane, BorderLayout.CENTER);
	westPane.add(_courseNamePane, BorderLayout.SOUTH);
	final JPanel eastPane = new JPanel();
	eastPane.setLayout(new BorderLayout(3,3));
	eastPane.add(new JScrollPane(_coursesListPane), BorderLayout.CENTER);
	eastPane.add(_optionPane, BorderLayout.SOUTH);
	add(westPane, BorderLayout.WEST);
	add(eastPane, BorderLayout.CENTER);
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, westPane, 
		eastPane);
	add(splitPane, BorderLayout.CENTER);
}

private JList curriculumList () {
	final JList result = new JList();
	if (_curriculums.length > 0) {
		result.setModel((ListModel)_curriculums[0]);
		if (result.getModel().getSize() > 0)
			result.setSelectedIndex(0);
	}
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setCellRenderer(new ListCellRenderer () {
		public Component getListCellRendererComponent(JList list, 
				Object value, int index, boolean isSelected, 
				boolean cellHasFocus) {
			final JLabel result = new JLabel();
			String v = value.toString();
			String i;
			if (_studentPlan.contains((Course)value))
				i = "* ";
			else
				i = "  ";
			result.setText(i + v);
			result.setOpaque(true);
			result.setEnabled(list.isEnabled());
			result.setFont(list.getFont());
			if (isSelected) {
				result.setBackground(list.getSelectionBackground());
				result.setForeground(list.getSelectionForeground());
			}
			else {
				result.setBackground(list.getBackground());
				result.setForeground(list.getForeground());
			}
			return result;
		}
	});
	result.addListSelectionListener(new ListSelectionListener () {
		public void valueChanged(ListSelectionEvent e) {
			final JList currList = (JList)e.getSource();
			if (!currList.isSelectionEmpty())
				setSelectedCourse((Course)currList.getSelectedValue());

		}
	});
	return result;
}

private JComboBox curriculumSelectBox () {
	final JComboBox result = new JComboBox(_curriculums);
	if (result.getItemCount() > 0)
		result.setSelectedIndex(0);
	result.addActionListener(new ActionListener () {
		public void actionPerformed(ActionEvent e) {
			int index = ((JComboBox)e.getSource()).getSelectedIndex();
			_curriculumList.setModel((ListModel)_curriculums[index]);
		}
	});
	return result;
}

private JPanel curriculumPane () {
	final JPanel result = new JPanel();
	result.setLayout(new BorderLayout());
	result.add(new JScrollPane(_curriculumList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	final JPanel curriculumSelectPane = new JPanel();
	curriculumSelectPane.add(_curriculumSelectBox);
	result.add(curriculumSelectPane, BorderLayout.SOUTH);
	return result;
}

private JPanel courseNamePane () {
	final JPanel result = new JPanel();
	Course selectedCourse = (Course)_curriculumList.getSelectedValue();
	if (selectedCourse != null) {
		_coursePreField.setText(selectedCourse.getCoursePre());
		_courseNumberField.setText(selectedCourse.getCourseNumber());
	}

	final JButton _addCourseButton = new JButton("Add");
	_addCourseButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Course courseAdded = new Course(_coursePreField.getText(), 
				_courseNumberField.getText());
			courseAdded.setPrerequisites(
				_schoolAdapter.getCoursePrerequisites(courseAdded));
			if (_schoolAdapter.isACourse(courseAdded)) 
				_studentPlan.add(courseAdded, _coursesListPane.getSelectedYear(),
					_coursesListPane.getSelectedSemester());
		}
	});
	final JButton _removeCourseButton = new JButton("Remove");
	_removeCourseButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {	
			Course courseRemoved = new Course(_coursePreField.getText(), 
				_courseNumberField.getText());
			if (_schoolAdapter.isACourse(courseRemoved))
				_studentPlan.remove(courseRemoved, _coursesListPane.getSelectedYear(),
					_coursesListPane.getSelectedSemester());
		}
	});
	final JPanel labelpane = new JPanel();
	labelpane.setLayout(new GridLayout(0, 1));
	labelpane.add(new JLabel("Pre:"));
	labelpane.add(new JLabel("Number:"));
	final JPanel fieldpane = new JPanel();
	fieldpane.setLayout(new GridLayout(0, 1));
	fieldpane.add(_coursePreField);
	fieldpane.add(_courseNumberField);
	final JPanel centerpane = new JPanel();
	centerpane.add(labelpane, BorderLayout.CENTER);
	centerpane.add(fieldpane, BorderLayout.EAST);
	final JPanel southpane = new JPanel();
	southpane.add(_addCourseButton);
	southpane.add(_removeCourseButton);
	result.setLayout(new BorderLayout());
	result.add(centerpane, BorderLayout.CENTER);
	result.add(southpane, BorderLayout.SOUTH);
	return result;
}

private JPanel optionPane () {
	final JPanel result = new JPanel();
	final JCheckBox showOptionalBox = 
		new JCheckBox("Show " + _schoolAdapter.getOptionalSemesterText());
	showOptionalBox.setSelected(_showOptional);
	showOptionalBox.addItemListener(new ItemListener() { 
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				_showOptional = true;
			else
				_showOptional = false;
			_coursesListPane.setShowOptional(_showOptional);
			_advisorUI.show();
		}
	});
	result.add(showOptionalBox);
	return result;
}

void setSelectedCourse (Course selectedCourse) {
	_coursePreField.setText(selectedCourse.getCoursePre());
	_courseNumberField.setText(selectedCourse.getCourseNumber());
}

private Curriculum[] curriculums (StudentInfo studentInfo) {
	//loads the curriculums from the list in StudentInfo
	String[] curriculumNames = studentInfo.getCurriculumNames();
	Curriculum[] result = new Curriculum[curriculumNames.length];
	for (int i = 0; i < result.length; i++)
		result[i] = _schoolAdapter.getCurriculum(curriculumNames[i]);
	return result;
}

private void monitorStudentPlan(StudentPlan p) {
	p.addListener(new StudentPlanListener() {
		public void courseAdded (StudentPlan s, Course c, int year, int semester) {
			_curriculumList.repaint();
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					_coursesListPane.draw();
					_advisorUI.show();
					return null;
				}
			};
			worker.start();
		}
		public void courseRemoved (StudentPlan s, Course c, int year, int semester) {
			_curriculumList.repaint();
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					_coursesListPane.draw();
					_advisorUI.show();
					return null;
				}
			};
			worker.start();
		}
	});
}

}
