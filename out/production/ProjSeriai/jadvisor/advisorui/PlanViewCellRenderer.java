/*
 * jadvisor/advisorui/PlanViewCellRenderer.java - Cell renderer for courses in Planner
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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class PlanViewCellRenderer extends JLabel implements ListCellRenderer {
	private final StudentPlan _studentPlan;
	private final int _year;
	private final int _semester;
	
public PlanViewCellRenderer(StudentPlan studentPlan, int year, int semester) {
	_studentPlan = studentPlan;
	_year = year;
	_semester = semester;
}

public Component getListCellRendererComponent(JList list, 
		Object value, int index, boolean isSelected, 
		boolean cellHasFocus) {

	String star;
	if (_studentPlan.satisfiesPrerequisites((Course)value, _year, _semester))
		star = "  ";
	else
		star = "* ";
	setText(star + value.toString());
	setOpaque(true);
	if (isSelected) {
		setBackground(list.getSelectionBackground());
		setForeground(list.getSelectionForeground());
	}
	else {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
	}

	setEnabled(list.isEnabled());
	setFont(list.getFont());

	String toolTip = "Prerequisites:";
	if (((Course)value).getPrerequisites().size() == 0)
		toolTip += " None";
	else
		for (int i = 0; i < ((Course)value).getPrerequisites().size(); i++)
			toolTip += " " + ((Course)value).getPrerequisites().get(i);
	setToolTipText(toolTip);

	return this;
}

}
