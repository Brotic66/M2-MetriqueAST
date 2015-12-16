/*
 * jadvisor/planner/Curriculum.java - JAdvisor curriculum model
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

package jadvisor.planner;

import jadvisor.scheduler.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class Curriculum implements ListModel {
	private final List _curriculum;
	private final String _name;
	private final Collection _listeners = new ArrayList();
	
public Curriculum (String name) {
	_curriculum = new LinkedList();
	_name = name;
}

public void add (Course course) {
	_curriculum.add(course);
}

public Object getElementAt (int index) {
	if (_curriculum.size() == 0)
		return null;
	return _curriculum.get(index);
}

public int getSize () {
	return _curriculum.size();
}

public void addListDataListener (ListDataListener l) {
	_listeners.add(l);
}

public void removeListDataListener (ListDataListener l) {
	_listeners.remove(l);
}

public String toString () {
	return _name;
}

}