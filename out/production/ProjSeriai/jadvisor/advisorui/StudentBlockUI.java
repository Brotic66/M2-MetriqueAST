/*
 * jadvisor/advisorui/StudentBlockUI.java - Dialog to add new StudentBlock to schedule
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
//import jadvisor.planner.*;
import jadvisor.school.*;

import javax.swing.*;
//import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class StudentBlockUI extends JDialog {
	private static final String DIALOG_TITLE = "Block Time - JAdvisor";

	private static StudentBlockUI _dialog;
	private static Component _component;

	private final SchoolAdapter _schoolAdapter;
	private final StudentSchedule _schedule;

	private final JTextField _titleText;
	private final JTextField _startTimeText;
	private final JTextField _endTimeText;
	private final JCheckBox _startTimeCheckBox;
	private final JCheckBox _endTimeCheckBox;
	private final JCheckBox[] _dayBox;

private StudentBlockUI (Frame frame, SchoolAdapter schoolAdapter, 
		StudentSchedule schedule) {
	super(frame, DIALOG_TITLE, true);
	_schoolAdapter = schoolAdapter;
	_schedule = schedule;

	_titleText = new JTextField(10);
	_startTimeText = new JTextField(4);
	_endTimeText = new JTextField(4);
	_startTimeCheckBox = new JCheckBox("PM", false);
	_endTimeCheckBox = new JCheckBox("PM", false);
	_dayBox = new JCheckBox[_schoolAdapter.getDaysAbbreviations().length];
	for (int i = 0; i < _dayBox.length; i++)
		_dayBox[i] = new JCheckBox(_schoolAdapter.getDaysAbbreviations()[i]);
	
	frame();
}

public static void initialize (Component component, SchoolAdapter schoolAdapter,
		StudentSchedule schedule) {
	_component = component;
	Frame frame = JOptionPane.getFrameForComponent(_component);
	_dialog = new StudentBlockUI(frame, schoolAdapter, schedule);
}

public static void showDialog (StudentSchedule schedule) {
	if (_dialog != null) {
		//dialog.setStudentInfo(schedule);
		_dialog.setLocationRelativeTo(_component);
		_dialog.setVisible(true);
	} else {
		System.err.println("StudentBlockUI requires you to call initialize "
			+ "before calling showDialog.");
	}
}

private void frame () {
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	final JPanel north = new JPanel();
	north.setLayout(new BorderLayout());
	north.add(titlePane(), BorderLayout.NORTH);
	north.add(timePane(), BorderLayout.SOUTH);
	getContentPane().add(north, BorderLayout.NORTH);
	getContentPane().add(daysPane(), BorderLayout.CENTER);
	getContentPane().add(buttonPane(), BorderLayout.SOUTH);
	pack();
}

private JPanel titlePane () {
	final JPanel result = new JPanel();
	result.add(new JLabel("Title:"));
	result.add(_titleText);
	return result;
}

private JPanel timePane () {
	final JPanel result = new JPanel();
	result.setLayout(new GridLayout(2,0));
	final JPanel startPane = new JPanel();
	startPane.add(new JLabel("Start Time:"));
	startPane.add(_startTimeText);
	startPane.add(_startTimeCheckBox);
	final JPanel endPane = new JPanel();
	endPane.add(new JLabel("End Time:"));
	endPane.add(_endTimeText);
	endPane.add(_endTimeCheckBox);
	result.add(startPane);
	result.add(endPane);
	return result;
}

private JPanel daysPane () {
	final JPanel result = new JPanel();
	for (int i = 0; i < _schoolAdapter.getDaysAbbreviations().length; i++)
		result.add(_dayBox[i]);
	return result;
}

private JPanel buttonPane () {
	final JPanel result = new JPanel();
	final JButton okButton = new JButton("OK");
	okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			addBlock();
			setVisible(false);
		}
	});
	final JButton cancelButton = new JButton("Cancel");
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	});
	result.add(okButton);
	result.add(cancelButton);
	return result;
}

private void addBlock () throws NumberFormatException {
	final boolean[] days = new boolean[_dayBox.length];
	for (int i = 0; i < days.length; i++)
		days[i] = _dayBox[i].isSelected();

	StudentBlock block = new StudentBlock(_titleText.getText(), days, 
		getTime(_startTimeText.getText(), _startTimeCheckBox.isSelected()),
		getTime(_endTimeText.getText(), _endTimeCheckBox.isSelected()));
	try {
		_schedule.addBlock(block);
	} catch (IllegalArgumentException e) {System.out.println(e.getMessage());}
}

private TimeOfDay getTime (String timeText, boolean PM) {
	int hour = Integer.valueOf(timeText.substring(0, timeText.length() - 2)).intValue();
	int minute = Integer.valueOf(timeText.substring(timeText.length() - 2)).intValue();

	int AMPM;
	if (PM)
		AMPM = TimeOfDay.PM;
	else
		AMPM = TimeOfDay.AM;

	TimeOfDay result = new TimeOfDay(hour, minute, AMPM);
	return result;
}

}
