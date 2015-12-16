/*
 * jadvisor/advisorui/SchedulerUI.java - Scheduler UI pane
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

import java.io.IOException;
import java.util.Properties;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <code>JPanel</code> of <code>AdvisorUI</code> that manages class scheduling.
 * Reads the class pre, number and section lists from the 
 * <code>SchoolAdapter</code> and displays them.  Users can select classes to 
 * add to or remove from their schedule, as well as add and remove
 * <code>StudentBlock</code>s.  Can create a schedule automatically using the
 * <code>ScheduleWizard</code>.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class SchedulerUI extends JPanel {
	private StudentSchedule _schedule;
	private StudentPlan _studentPlan;
	private StudentInfo _studentInfo;
	private final SchoolAdapter _schoolAdapter;
	
	private final JList _classPreList;
	private final JList _classNumberList;
	private final JList _classSectionList;
	private final JTable _classScheduleTable;
	private final JPanel _controlPane;
	private final JLabel _creditLabel;
	private final ScheduleTableModel _scheduleTableModel;

	private String _classPreSelection;
	private String _classNumberSelection;
	private String _classSectionSelection;

public SchedulerUI (StudentSchedule schedule, StudentPlan studentPlan, 
		StudentInfo studentInfo, SchoolAdapter schoolAdapter, Properties properties) {
	_schedule = schedule;
	_studentPlan = studentPlan;
	_studentInfo = studentInfo;
	_schoolAdapter = schoolAdapter;

	_classPreList = classPreList();
	_classNumberList = classNumberList();
	_classSectionList = classSectionList();
	_scheduleTableModel = new ScheduleTableModel();
	_classScheduleTable = classScheduleTable();
	_creditLabel = new JLabel(new Integer(_schedule.getCreditTotal()).toString());
	_controlPane = controlPane();
	pane();
	
	ScheduleWizardUI.initialize(this, _schoolAdapter, _schedule, _studentPlan, properties);
	StudentBlockUI.initialize(this, _schoolAdapter, _schedule);

	monitorStudentSchedule(_schedule);
}

private void pane() {
	setLayout(new BorderLayout());
	final JPanel listsPane = new JPanel();
	listsPane.setLayout(new GridLayout(0,3));
	listsPane.add(new JScrollPane(_classPreList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	listsPane.add(new JScrollPane(_classNumberList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	listsPane.add(new JScrollPane(_classSectionList,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	final JPanel northPane = new JPanel();
	northPane.setLayout(new BorderLayout());
	northPane.add(listsPane, BorderLayout.CENTER);
	northPane.add(_controlPane, BorderLayout.SOUTH);
	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, northPane, 
		new JScrollPane(_classScheduleTable));
	add(splitPane, BorderLayout.CENTER);
}

private JList classPreList () {
	final JList result = new JList();
	try {
		result.setListData(_schoolAdapter.getClassPreList(
			_studentInfo.getCurrentSemester()));
	} catch (IOException e) {
		result.setListData(new Object[0]);
		JOptionPane.showMessageDialog(this.getRootPane(), e.getMessage(), "Error", 
			JOptionPane.ERROR_MESSAGE); 
	}
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setSelectedIndex(-1);
	result.addListSelectionListener(new ListSelectionListener () {
		public void valueChanged(ListSelectionEvent e) {
			setClassNumberList((JList)e.getSource(), _classNumberList, _classSectionList);
		}
	});
	return result;
}

private JList classNumberList () {
	final JList result = new JList();
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setSelectedIndex(-1);
	result.addListSelectionListener(new ListSelectionListener () {
		public void valueChanged(ListSelectionEvent e) {
			final JList selectedList = (JList)e.getSource();
			setClassSectionList((JList)e.getSource(), _classSectionList);
		}
	});
	return result;
}

private JList classSectionList () {
	final JList result = new JList();
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setSelectedIndex(-1);
	result.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				final JList selectedList = (JList)e.getSource();
				int index = selectedList.locationToIndex(e.getPoint());
				if (index >= 0) {
					addClass(new StudentClass(
						(StudentClass)(selectedList.getSelectedValue())));
				}
			}
		}
	});
	return result;
}

private JTable classScheduleTable () {
	JTable result = new JTable(_scheduleTableModel);
	result.setPreferredScrollableViewportSize(new Dimension(300, 70));
	result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	result.setShowGrid(false);
	result.getTableHeader().setReorderingAllowed(false);
	return result;
}

class ScheduleTableModel extends AbstractTableModel {
	public ScheduleTableModel () {
	}
	public int getColumnCount() {
		return _schoolAdapter.getClassTitles().length + 
			_schoolAdapter.getClassInfoTitles().length;
	}
	public int getRowCount() {
		return _schedule.getClasses().size() + _schedule.getBlocks().size();
	}
    public String getColumnName(int col) {
		if (col < 8) return _schoolAdapter.getClassTitles()[col];
		return _schoolAdapter.getClassInfoTitles()[col - 8];
    }
	public Object getValueAt(int row, int col) {
		if (row < _schedule.getClasses().size()) {//classes
			StudentClass c = (StudentClass)(_schedule.getClasses().get(row));
			if (col == 0) return c.getCourse().getCoursePre();
			if (col == 1) return c.getCourse().getCourseNumber();
			if (col == 2) return c.getSection();
			if (col == 3) {
				StringBuffer s = new StringBuffer();
				for (int i = 0; i < c.getDays().length; i++)
					if (c.getDays()[i])
						s.append(_schoolAdapter.getDaysAbbreviations()[i]);
					else
						s.append(" ");
				return s;
			}
			if (col == 4) return _schoolAdapter.timeToString(c.getStartTime());
			if (col == 5) return _schoolAdapter.timeToString(c.getEndTime());
			if (col == 6) return _schoolAdapter.getClassMods()[c.getMod()];
			if (col == 7) return new Integer(c.getCredit());
			if (col >= 8 && c.getInfo().size() > 0 ) 
				return c.getInfo().get(col - 8);
		} else {//blocks
			StudentBlock b = (StudentBlock)(_schedule.getBlocks().get(
				row - _schedule.getClasses().size()));
			if (col == 0) return b.getDescription();
			if (col == 3) {
				StringBuffer s = new StringBuffer();
				for (int i = 0; i < b.getDays().length; i++)
					if (b.getDays()[i])
						s.append(_schoolAdapter.getDaysAbbreviations()[i]);
					else
						s.append(" ");
				return s;
			}
			if (col == 4) return _schoolAdapter.timeToString(b.getStartTime());
			if (col == 5) return _schoolAdapter.timeToString(b.getEndTime());
			return new String();
		}
		return new Integer(0);
	}
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
}

private JPanel controlPane () {
	JPanel result = new JPanel();
	final JButton addClassButton = new JButton("Add");
	addClassButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (_classSectionList.getSelectedIndex() >= 0) {
				addClass((StudentClass)(_classSectionList.getSelectedValue()));
			}
		}
	});
	final JButton removeClassButton = new JButton("Remove");
	removeClassButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			final int index = _classScheduleTable.getSelectedRow();
			if (index > -1 && index < _classScheduleTable.getRowCount()) {
				if (index < _schedule.getClasses().size())
					removeClass((StudentClass)(_schedule.getClasses().get(index)));
				else
					_schedule.removeBlock((StudentBlock)(_schedule.getBlocks().get(
						index - _schedule.getClasses().size())));
			}
		}
	});
	final JButton blockTimeButton = new JButton("Add Block Time");
	blockTimeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			StudentBlockUI.showDialog(_schedule);
		}
	});
	final JButton scheduleWizardButton = new JButton("Schedule Wizard");
	scheduleWizardButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ScheduleWizardUI.showDialog();
		}
	});
	result.add(addClassButton);
	result.add(removeClassButton);
	result.add(new JLabel("Credits:"));
	result.add(_creditLabel);
	result.add(blockTimeButton);
	result.add(scheduleWizardButton);
	return result;
}

void saveProperties (Properties p) {
	ScheduleWizardUI.saveProperties(p);
}

private void monitorStudentSchedule (StudentSchedule p) {
	p.addListener(new StudentScheduleListener() {
		public void classAdded (StudentSchedule s, StudentClass c) {
			_scheduleTableModel.fireTableDataChanged();
			_creditLabel.setText(new Integer(_schedule.getCreditTotal()).toString());
		}
		public void classRemoved (StudentSchedule s, StudentClass c) {
			_classScheduleTable.clearSelection();
			_scheduleTableModel.fireTableDataChanged();
			_creditLabel.setText(new Integer(_schedule.getCreditTotal()).toString());
		}
		public void blockAdded (StudentSchedule s, StudentBlock b) {
			_scheduleTableModel.fireTableDataChanged();
		}
		public void blockRemoved (StudentSchedule s, StudentBlock b) {
			_classScheduleTable.clearSelection();
			_scheduleTableModel.fireTableDataChanged();
		}
	});
}

private void addClass (final StudentClass c) {
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			try {
				if (_schoolAdapter.isAClass(c))
					_schedule.add(c);
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());}
			return null;
		}
	};
	worker.start();
}

private void removeClass (final StudentClass c) {
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			_schedule.remove(c);
			return null;
		}
	};
	worker.start();
}

private void setClassNumberList (final JList preList, final JList numberList, 
		final JList sectionList) {
	final Component parent = this.getRootPane();
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			preList.setEnabled(false);
			numberList.setEnabled(false);
			if (preList.getSelectedIndex() >= 0) {
				_classPreSelection = (String)(preList.getSelectedValue());
				numberList.setListData(new String[] {"Loading..."});
				sectionList.setListData(new Object[0]);
				try {
					numberList.setListData(_schoolAdapter.getClassNumberList(
						_studentInfo.getCurrentSemester(), _classPreSelection));
				} catch (IOException e) {
					numberList.setListData(new Object[0]);
					System.err.println(e.getMessage());
					JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", 
						JOptionPane.ERROR_MESSAGE); 
				}
			}
			preList.setEnabled(true);
			numberList.setEnabled(true);
			return null;
		}
	};
	worker.start();
}

private void setClassSectionList (final JList numberList, final JList sectionList) {
	final Component parent = this.getRootPane();
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			numberList.setEnabled(false);
			sectionList.setEnabled(false);
			if (numberList.getSelectedIndex() >= 0) {
				_classNumberSelection = (String)(numberList.getSelectedValue());
				sectionList.setListData(new String[] {"Loading..."});
				try {
					sectionList.setListData(_schoolAdapter.getClassSectionList(
						_studentInfo.getCurrentSemester(), _classPreSelection, 
						_classNumberSelection).toArray());
				} catch (IOException e) {
					sectionList.setListData(new Object[0]);
					System.err.println(e.getMessage());
					JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", 
						JOptionPane.ERROR_MESSAGE); 
				}
			}
			numberList.setEnabled(true);
			sectionList.setEnabled(true);
			return null;
		}
	};
	worker.start();
}

}
