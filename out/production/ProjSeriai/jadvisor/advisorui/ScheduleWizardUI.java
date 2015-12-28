/*
 * jadvisor/advisorui/ScheduleWizardUI.java - Dialog UI for ScheduleWizard
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
import jadvisor.planner.*;
import jadvisor.school.*;

import java.util.Properties;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ScheduleWizardUI extends JDialog {
	private static final String DIALOG_TITLE = "Schedule Wizard - JAdvisor";
	private static final String ICONFILENAME = "/icons/ja.gif";

	private static ScheduleWizardUI _dialog;
	private static Component _component;

	private ScheduleWizard _scheduleWizard;
	private final SchoolAdapter _schoolAdapter;
	private final StudentSchedule _schedule;
	private final StudentPlan _studentPlan;
	private final Properties _properties;
	private ScheduleGraph _scheduleGraph;
	
	private JComboBox _planComboBox;
	private final JCheckBox _noClassBeforeBox;
	private final JTextField _noClassBeforeText;
	private final JCheckBox _noClassAfterBox;
	private final JTextField _noClassAfterText;
	private final JCheckBox _noClassOnBox;
	private final JCheckBox[] _noClassOnDayBox;
	private final JButton _okButton;
	
	private AbstractTableModel _tableModel;
	private JTable _table;

private ScheduleWizardUI (Frame frame, SchoolAdapter schoolAdapter, 
		StudentSchedule schedule, StudentPlan studentPlan, Properties properties) {
	super(frame, DIALOG_TITLE, true);
	
	_schoolAdapter = schoolAdapter;
	_schedule = schedule;
	_studentPlan = studentPlan;
	_properties = properties;
	_scheduleWizard = new ScheduleWizard(_schoolAdapter);
	_scheduleGraph = new ScheduleGraph(_schoolAdapter);

	_planComboBox = planComboBox();
	_noClassBeforeBox = new JCheckBox("No classes before:");
	_noClassBeforeText = new JTextField("0", 2);
	_noClassAfterBox = new JCheckBox("No classes after:");
	_noClassAfterText = new JTextField("0", 2);
	_noClassOnBox = new JCheckBox("No classes on:");
	_noClassOnDayBox = new JCheckBox[_schoolAdapter.getDaysAbbreviations().length];
	for (int i = 0; i < _noClassOnDayBox.length; i++)
		_noClassOnDayBox[i] = new JCheckBox(_schoolAdapter.getDaysAbbreviations()[i]);
	_okButton = new JButton("OK");
	_table = table();

	setPreferences();

	frame();
}

public static void initialize (Component component, SchoolAdapter schoolAdapter,
		StudentSchedule schedule, StudentPlan studentPlan, Properties properties) {
	_component = component;
	Frame frame = JOptionPane.getFrameForComponent(_component);
	_dialog = new ScheduleWizardUI(frame, schoolAdapter, schedule, studentPlan, properties);
}

public static void showDialog () {
	if (_dialog != null) {
		_dialog.update();
		_dialog.setLocationRelativeTo(_component);
		_dialog.setVisible(true);
	} else {
		System.err.println("ScheduleWizardUI requires you to call initialize "
			+ "before calling showDialog.");
	}
}

//FIX THIS: planComboBox generates errors when items are removed
private void update () {
	try{
		_planComboBox.removeAllItems();
	} catch (Exception exc) {
		System.err.println("unknown combobox error:" + exc.getMessage());
	}
	
	_planComboBox.addItem(_schedule);
	for (int i = 0; i < _studentPlan.years(); i++) {
		for (int j = 0; j < _studentPlan.semesters(i); j++) {
			ListModel semesterPlan = _studentPlan.getSemester(i, j);
			if (semesterPlan.getSize() > 0)
				_planComboBox.addItem(semesterPlan);
		}
	}
	if (_planComboBox.getItemCount() > 0)
		_planComboBox.setSelectedIndex(0);
	else
		System.out.println("ScheduleWizardUI: All plans are empty.");
}

private void frame () {
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
/*	getContentPane().add(optionPane(), BorderLayout.NORTH);
	getContentPane().add(new JScrollPane(_table,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.WEST);
	getContentPane().add(new JScrollPane(_scheduleGraph,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	getContentPane().add(buttonPane(), BorderLayout.SOUTH);*/
	JPanel left = new JPanel(new BorderLayout());
	JPanel center = new JPanel();
	center.setLayout(new GridLayout(0,2));
	left.add(optionPane(), BorderLayout.NORTH);
	left.add(new JScrollPane(_table,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	center.add(left);
	center.add(new JScrollPane(_scheduleGraph,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	getContentPane().add(center, BorderLayout.CENTER);
	getContentPane().add(buttonPane(), BorderLayout.SOUTH);
	pack();
	setSize(Integer.parseInt(_properties.getProperty("swwidth", "400")), 
		Integer.parseInt(_properties.getProperty("swheight", "300")));
}

private JPanel optionPane () {
	final JPanel result = new JPanel();
	result.setBorder(BorderFactory.createTitledBorder("Options"));
	result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

	final JPanel noClassBeforePane = new JPanel();
	noClassBeforePane.add(_noClassBeforeBox);
	noClassBeforePane.add(_noClassBeforeText);
	
	final JPanel noClassAfterPane = new JPanel();
	noClassAfterPane.add(_noClassAfterBox);
	noClassAfterPane.add(_noClassAfterText);
	
	final JPanel noClassOnPane = new JPanel();
	noClassOnPane.setLayout(new BoxLayout(noClassOnPane, BoxLayout.Y_AXIS));
	noClassOnPane.add(_noClassOnBox);
	final JPanel noClassOnDayPane = new JPanel();
	for (int i = 0; i < _noClassOnDayBox.length; i++)
		noClassOnDayPane.add(_noClassOnDayBox[i]);
	noClassOnPane.add(noClassOnDayPane);

	result.add(_planComboBox);	
	result.add(noClassBeforePane);
	result.add(noClassAfterPane);
	result.add(noClassOnPane);
	return result;
}

private JComboBox planComboBox () {
	JComboBox result = new JComboBox();
	result.addActionListener(new ActionListener () {
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox)e.getSource();
			if (cb.getSelectedItem() == null) {
			} else if (cb.getSelectedItem() instanceof StudentSemesterPlan) {
				System.out.println("plan");
				setStudentInfo((StudentSemesterPlan)cb.getSelectedItem());
			} else {
				System.out.println("schedule");
				setStudentInfo((StudentSchedule)cb.getSelectedItem());
			}
		}
	});
	return result;
}

private JTable table () {
	final JTable result = new JTable();
	final ListSelectionModel lsm = result.getSelectionModel();
	lsm.addListSelectionListener(new ListSelectionListener () {
		public void valueChanged(ListSelectionEvent e) {
			final ListSelectionModel l = (ListSelectionModel)e.getSource();
			final int row = l.getMinSelectionIndex();
			if (row >= 0)
				_scheduleGraph.setSchedule((StudentSchedule)
					((java.util.List)_scheduleWizard.getSchedules()).get(row));
			else
				_scheduleGraph.setSchedule(new StudentSchedule());
		}
	});
	result.setSelectionModel(lsm);
	result.setPreferredScrollableViewportSize(new Dimension(300, 70));
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setShowGrid(false);
	result.getTableHeader().setReorderingAllowed(false);
	return result;
}

private JPanel buttonPane () {
	final JPanel result = new JPanel();
	_okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int index = _table.getSelectedRow();
			if (index >= 0) {
				_schedule.setClasses(_scheduleWizard.getSchedule(index).getClasses());
				setVisible(false);
			}
		}
	});
	final JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	});
	final JButton computeButton = new JButton("Compute");
	computeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setPreferences();
			_tableModel.fireTableDataChanged();
			if (_table.getRowCount() > 0)
				_table.addRowSelectionInterval(0, 0);
		}
	});
	result.add(_okButton);
	result.add(cancelButton);
	result.add(computeButton);
	return result;
}

private void setPreferences () {
	boolean noClassBefore = _noClassBeforeBox.isSelected();
	boolean noClassAfter = _noClassAfterBox.isSelected();
	TimeOfDay noClassBeforeTime;
	TimeOfDay noClassAfterTime;
	
	try {
		noClassBeforeTime = new TimeOfDay(
			(Integer.valueOf(_noClassBeforeText.getText())).intValue(), 0);
	} catch (NumberFormatException e) {
		noClassBeforeTime = new TimeOfDay(0, 0);
		noClassBefore = false;
	}
	try {
		noClassAfterTime = new TimeOfDay(
			(Integer.valueOf(_noClassAfterText.getText())).intValue(), 0);
	} catch (NumberFormatException e) {
		noClassAfterTime = new TimeOfDay(0, 0);
		noClassAfter = false;
	}
	
	boolean[] noClassOnDay = new boolean[_noClassOnDayBox.length];
	if (_noClassOnBox.isSelected())
		for (int i = 0; i < noClassOnDay.length; i++)
			noClassOnDay[i] = _noClassOnDayBox[i].isSelected();
	else
		for (int i = 0; i < noClassOnDay.length; i++)
			noClassOnDay[i] = false;
	_scheduleWizard.setPreferences(noClassBefore, noClassBeforeTime, 
		noClassAfter, noClassAfterTime, noClassOnDay);
}

private void setStudentInfo (StudentSemesterPlan semesterplan) {
	_scheduleWizard.setPlan(semesterplan);
	_tableModel = _scheduleWizard.getTableModel();
	_table.setModel(_tableModel);
	if (_table.getRowCount() > 0)
		_table.addRowSelectionInterval(0, 0);
}

private void setStudentInfo (StudentSchedule schedule) {
	_scheduleWizard.setPlan(schedule);
	_tableModel = _scheduleWizard.getTableModel();
	_table.setModel(_tableModel);
	if (_table.getRowCount() > 0)
		_table.addRowSelectionInterval(0, 0);
}

public static void saveProperties (Properties p) {
	p.setProperty("swwidth", "" + _dialog.getWidth());
	p.setProperty("swheight", "" + _dialog.getHeight());
}

}
