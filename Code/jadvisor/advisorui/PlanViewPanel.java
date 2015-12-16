/*
 * jadvisor/advisorui/PlanViewPanel.java - Plan viewer UI pane
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

/**
 * PlanViewPanel is a <code>JPanel</code> that displays a StudentPlan.  Each 
 * semester is displayed in its own cell of the table, using a 
 * <code>PlanViewSemesterPanel</code>.  Adding or removing a course from the
 * StudentPlan will refresh the view to show the changes.  Selecting a semester
 * will cause the other semesters to be de-selected.  Only one semester and one
 * course can be in focus at a time.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
class PlanViewPanel extends JPanel {
	private final PlannerUI _plannerUI;
	private final StudentPlan _plan;
	private final SchoolAdapter _schoolAdapter;
	private boolean _showOptional;

	private final PlanViewSemesterPanel[][] _semesterPanels;

	private int _selectedYear = 0;
	private int _selectedSemester = 0;

public PlanViewPanel(PlannerUI plannerUI, StudentPlan plan, boolean showOptional,
		SchoolAdapter schoolAdapter) {
	_plannerUI = plannerUI;
	_plan = plan;
	_schoolAdapter = schoolAdapter;
	_showOptional = showOptional;

	_semesterPanels = new PlanViewSemesterPanel[_plan.years()][_plan.semesters(0)];
	for (int i = 0; i < _semesterPanels.length; i++)
		for (int j = 0; j < _semesterPanels[i].length; j++)
			_semesterPanels[i][j] = 
				new PlanViewSemesterPanel(_plan, this, i, j);
	_semesterPanels[_selectedYear][_selectedSemester].setSelected(true);

	//monitorStudentPlan(_plan);
	
	draw();
}

public void draw () {
	removeAll();
	int numSemesters;
	if (_showOptional)
		numSemesters = _schoolAdapter.getSemesters().length;
	else {
		numSemesters = 0;
		for (int i = 0; i < _schoolAdapter.getSemesters().length; i++)
			if (_schoolAdapter.getRequiredSemesters()[i])
				numSemesters++;
	}
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	for (int year = 0; year < _plan.years(); year++) {
		final JPanel yearPanel = new JPanel();
		yearPanel.setLayout(new GridLayout(1, numSemesters));
		//yearPanel.setPreferredSize(new Dimension(0, 200));
		for (int semester = 0; semester < numSemesters; semester++) {
			_semesterPanels[year][semester] = 
				new PlanViewSemesterPanel(_plan, this, year, semester);
			yearPanel.add(_semesterPanels[year][semester]);
		}
		add(yearPanel);
	}
	_semesterPanels[_selectedYear][_selectedSemester].setSelected(true);
}

public int getSelectedYear () {
	return _selectedYear;	
}

public int getSelectedSemester () {
	return _selectedSemester;
}

public void setSelectedSemester (int year, int semester) {
	_selectedYear = year;
	_selectedSemester = semester;
	for (int i = 0; i < _semesterPanels.length; i++)
		for (int j = 0; j < _semesterPanels[i].length; j++)
			_semesterPanels[i][j].setSelected(i == year && j == semester);
}

public void setShowOptional (boolean showOptional) {
	_showOptional = showOptional;
	draw();
}

void courseSelected (Course course, int year, int semester) {
	_plannerUI.setSelectedCourse(course);
	setSelectedSemester(year, semester);
}

/*
private void monitorStudentPlan(StudentPlan p) {
	p.addListener(new StudentPlanListener() {
		public void courseAdded (StudentPlan s, Course c, int year, int semester) {
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					repaint(true);
					return null;
				}
			};
			worker.start();
		}
		public void courseRemoved (StudentPlan s, Course c, int year, int semester) {
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					repaint(true);
					return null;
				}
			};
			worker.start();
		}
	});
}*/

}

/**
 * PlanViewSemesterPanel is a <code>JPanel</code> that displays a semester 
 * for a StudentPlan.  Each PlanViewSemesterPanel consists of a 
 * <code>JButton</code> and a <code>JList</code>.  Selecting the button will
 * make the button inactive, and bring this PlanViewSemesterPanel into focus.
 * Selecting a course from the list will highlight it, and bring the panel
 * into focus if not already.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
class PlanViewSemesterPanel extends JPanel {
	private final StudentPlan _plan;
	private final PlanViewPanel _planViewPanel;
	private final int _year;
	private final int _semester;
	private SchoolAdapter _schoolAdapter;
	
	private final PlanViewCellRenderer _planViewCellRenderer;

	private final JButton _button;
	private final JList _courseList;

public PlanViewSemesterPanel (StudentPlan plan, PlanViewPanel planViewPanel, 
		int year, int semester) {
	_plan = plan;
	_planViewPanel = planViewPanel;
	_year = year;
	_semester = semester;
	
	_planViewCellRenderer = new PlanViewCellRenderer(_plan, _year, _semester);
	
	_button = button();
	_courseList = courseList();

	pane();
}

private void pane () {
	setLayout(new BorderLayout(1,1));
	add(_button, BorderLayout.NORTH);
	add(_courseList, BorderLayout.CENTER);	
}

private JButton button () {
	JButton result = new JButton((
		(StudentSemesterPlan)_plan.getSemester(_year, _semester)).getTitle());
	result.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_planViewPanel.setSelectedSemester(_year, _semester);
		}
	});
	return result;
}

private JList courseList () {
	JList result = new JList((ListModel)_plan.getSemester(_year, _semester));
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setCellRenderer(_planViewCellRenderer);
	result.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
			if (_courseList.getSelectedIndex() > -1) {
				_planViewPanel.courseSelected((Course)_courseList.getModel().getElementAt(
					_courseList.getSelectedIndex()), _year, _semester);
//				_planViewPanel.setSelectedSemester(_year, _semester);
			}
		}
	});
	return result;
}

public void setSelected (boolean isSelected) {
	_button.setEnabled(!isSelected);
	if (!isSelected)
		_courseList.clearSelection();
}

public boolean getSelected () {
	return !_button.isEnabled();
}

}
