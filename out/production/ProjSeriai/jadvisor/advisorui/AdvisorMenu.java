/*
 * jadvisor/advisorui/AdvisorMenu.java - Menu for JAdvisor UI
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * AdvisorMenu is the menu bar for the JAdvisor UI.  It manages the various
 * menus for JAdvisor: File, Edit, View, Help; and their menu items.
 *
 * @author Curtis Rawls
 * @version 0.4.5
 */
public class AdvisorMenu extends JMenuBar 
	implements ActionListener, ItemListener {

	private Advisor _advisor;
	private AdvisorUI _advisorUI;

public AdvisorMenu(Advisor advisor, AdvisorUI advisorUI) {
	_advisor = advisor;
	_advisorUI = advisorUI;
	add(fileMenu());
	//add(editMenu());
	add(viewMenu());
	add(helpMenu());
}

private JMenu fileMenu () {
	JMenu result = new JMenu("File");
	result.setMnemonic(KeyEvent.VK_F);

	JMenuItem menuItem;

	menuItem = new JMenuItem("New");
//		new ImageIcon(getClass().getResource("/icons/New16.gif")));
	menuItem.setMnemonic(KeyEvent.VK_N);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.newFile();
		}
	});
	result.add(menuItem);

	menuItem = new JMenuItem("Open...", KeyEvent.VK_O);
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
		ActionEvent.CTRL_MASK));
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.openFile();
		}
	});
	result.add(menuItem);

	menuItem = new JMenuItem("Save", KeyEvent.VK_S);
	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, 
		ActionEvent.CTRL_MASK));
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.saveFile();
		}
	});
	result.add(menuItem);

	menuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.saveAsFile();
		}
	});
	result.add(menuItem);

	menuItem = new JMenuItem("Save as HTML", KeyEvent.VK_H);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.saveAsHTML();
		}
	});
	result.add(menuItem);
	
	result.addSeparator();

/*	menuItem = new JMenuItem("Print", KeyEvent.VK_P);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.print();
		}
	});
	result.add(menuItem);*/

	menuItem = new JMenuItem("Preferences", KeyEvent.VK_P);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.editPreferences();
		}
	});
	result.add(menuItem);
	
	result.addSeparator();

	menuItem = new JMenuItem("Exit");
	menuItem.setMnemonic(KeyEvent.VK_X);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.exit();
		}
	});
	result.add(menuItem);

	return result;
}

private JMenu editMenu () {
	JMenu result = new JMenu("Edit");
	result.setMnemonic(KeyEvent.VK_E);

	JMenuItem menuItem;

	menuItem = new JMenuItem("Curriculums", KeyEvent.VK_C);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.editCurriculums();
		}
	});
	result.add(menuItem);

	menuItem = new JMenuItem("Student Info", KeyEvent.VK_S);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisor.editStudentInfo();
		}
	});
	result.add(menuItem);

	return result;
}

private JMenu viewMenu () {
	JMenu result = new JMenu("View");
	result.setMnemonic(KeyEvent.VK_V);

//	JMenuItem menuItem;
	JCheckBoxMenuItem cbMenuItem;

	final JCheckBoxMenuItem sgMenuItem = new JCheckBoxMenuItem("Schedule Graph");
	sgMenuItem.setMnemonic(KeyEvent.VK_G);
	sgMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, 
		ActionEvent.CTRL_MASK));
	ScheduleGraphUI.addMenuWindowListener(new WindowAdapter () {
		public void windowOpened(WindowEvent e) {
			sgMenuItem.setSelected(true);
		}
		public void windowClosed(WindowEvent e) {
			sgMenuItem.setSelected(false);
		}
	});
	sgMenuItem.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) 
				ScheduleGraphUI.showDialog();
			else
				ScheduleGraphUI.hideDialog();
		}
	});
	result.add(sgMenuItem);

	return result;
}

private JMenu helpMenu () {
	JMenu result = new JMenu("Help");
	result.setMnemonic(KeyEvent.VK_H);

	JMenuItem menuItem;

	menuItem = new JMenuItem("Splash Screen...", KeyEvent.VK_S);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SplashWindow splash = new SplashWindow(_advisorUI);
		}
	});
	result.add(menuItem);
	
	result.addSeparator();

	menuItem = new JMenuItem("About JAdvisor...", KeyEvent.VK_A);
	menuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			_advisorUI.showAboutDialog();
		}
	});
	result.add(menuItem);

	return result;
}

public void itemStateChanged(ItemEvent e) {
	String newline = "\n";
	JMenuItem source = (JMenuItem)(e.getSource());
	String s = "Item event detected."
		+ newline
		+ "    Event source: " + source.getText()
		+ " (an instance of " + getClassName(source) + ")"
		+ newline
		+ "    New state: " 
		+ ((e.getStateChange() == ItemEvent.SELECTED) ?
			"selected":"unselected");
	System.out.println(s + newline);
}

public void actionPerformed (ActionEvent e) {
	String newline = "\n";
	JMenuItem source = (JMenuItem)(e.getSource());
	String s = "Action event detected."
		+ newline
		+ "    Event source: " + source.getText()
		+ " (an instance of " + getClassName(source) + ")";
	System.out.println(s + newline);
}

protected String getClassName (Object o) {
	String classString = o.getClass().getName();
	int dotIndex = classString.lastIndexOf(".");
	return classString.substring(dotIndex+1);
}

}
