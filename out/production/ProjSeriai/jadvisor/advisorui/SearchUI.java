/*
 * jadvisor/advisorui/SearchUI.java - Search UI pane
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
 * <code>JPanel</code> of <code>AdvisorUI</code> that manages course searches.
 * Can select whether to search by course title and/or description.  Searches 
 * for a String, then outputs the results in a text field.  Uses multi-threading
 * for searching.
 *
 * @author Curtis Rawls
 * @version 0.4.5
 */
public class SearchUI extends JPanel {
	final JTextArea _resultsArea;
	private final SchoolAdapter _schoolAdapter;
	private boolean _searchTitles;
	private boolean _searchDescriptions;
	
public SearchUI (SchoolAdapter schoolAdapter) {
	_schoolAdapter = schoolAdapter;
	_searchTitles = true;
	_searchDescriptions = true;
	
	_resultsArea = new JTextArea(5, 30);
	_resultsArea.setEditable(false);
	
	setLayout(new BorderLayout());
	JPanel northpane = new JPanel();
	northpane.setLayout(new BorderLayout());
	northpane.add(inputPane(), BorderLayout.NORTH);
	northpane.add(checkBoxPane(), BorderLayout.SOUTH);
	
	add(northpane, BorderLayout.NORTH);
	add(new JScrollPane(_resultsArea,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), 
		BorderLayout.CENTER);
}

private JPanel inputPane () {
	JPanel result = new JPanel();
	final JTextField searchField = new JTextField(20);
	
	final JButton searchButton = new JButton("Search");
	searchButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			search(searchField.getText(), _searchTitles, _searchDescriptions, searchButton);
		}
	});
	result.add(searchField);
	result.add(searchButton);
	return result;
}

private JPanel checkBoxPane () {
	JPanel result = new JPanel();
	result.setLayout(new BorderLayout());
	final JCheckBox searchTitleBox = new JCheckBox("Search Course Titles");
	searchTitleBox.setSelected(_searchTitles);
	searchTitleBox.addItemListener(new ItemListener() { 
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				_searchTitles = true;
			else
				_searchTitles = false;
		}
	});
	final JCheckBox searchDescriptionBox = new JCheckBox("Search Course Descriptions");
	searchDescriptionBox.setSelected(_searchDescriptions);
	searchDescriptionBox.addItemListener(new ItemListener() { 
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				_searchDescriptions = true;
			else
				_searchDescriptions = false;
		}
	});
	result.add(searchTitleBox, BorderLayout.NORTH);
	result.add(searchDescriptionBox, BorderLayout.SOUTH);
	return result;
}

private void search (final String searchString, final boolean searchTitles, 
		final boolean searchDescriptions, final JButton searchButton) {
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			_resultsArea.setText("Searching...");
			searchButton.setEnabled(false);
			java.util.List resultList;
			try {
				resultList = _schoolAdapter.searchCourses(searchString,
					searchTitles, searchDescriptions);
			} catch (IOException e) {
				resultList = new ArrayList();
				System.out.println("Search: Catch this exception");
			}
			_resultsArea.setText("");
			for (int i = 0; i < resultList.size(); i++)
				_resultsArea.append((String)resultList.get(i) + "\n");
			searchButton.setEnabled(true);
			return null;
		}
	};
	worker.start();
}

}
