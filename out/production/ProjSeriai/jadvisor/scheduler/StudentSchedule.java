/*
 * jadvisor/scheduler/StudentSchedule.java - list of classes and blocks
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

package jadvisor.scheduler;

import java.util.*;
import java.io.*;

public class StudentSchedule implements Serializable {
	
	private final List _classes;
	private final List _blocks;
	transient private Collection _listeners = new ArrayList();
	
public StudentSchedule () {
	_classes = new LinkedList();
	_blocks = new LinkedList();
}

public StudentSchedule (StudentSchedule schedule) {
	this();
	for (int i = 0; i < schedule._classes.size(); i++)
		_classes.add((StudentClass)schedule._classes.get(i));
}

public void add (StudentClass c) throws IllegalArgumentException {
	if (isAConflict(c))
		throw new IllegalArgumentException("Cannot add: " + c.toString());
	if (!_classes.contains(c)) {
		_classes.add(c);
		fireClassAdded(c);
	}
}

public void remove (StudentClass c) {
	if (_classes.contains(c)) {
		_classes.remove(c);
		fireClassRemoved(c);
	}
}

public void addBlock (StudentBlock b) throws IllegalArgumentException {
	if (isAConflict(b))
		throw new IllegalArgumentException("Cannot add: " + b.toString());
	if (!_blocks.contains(b)) {
		_blocks.add(b);
		fireBlockAdded(b);
	}
}

public void removeBlock (StudentBlock b) {
	if (_blocks.contains(b)) {
		_blocks.remove(b);
		fireBlockRemoved(b);
	}
}

public List getClasses () {
	return _classes;
}

public List getBlocks () {
	return _blocks;
}

public void setClasses (List newClasses) {
	while (_classes.size() > 0)
		remove((StudentClass)_classes.get(0));
	for (int i = 0; i < newClasses.size(); i++)
		add((StudentClass)newClasses.get(i));
}

public int getCreditTotal () {
	int result = 0;
	for (int i = 0; i < _classes.size(); i++)
		result += ((StudentClass)_classes.get(i)).getCredit();
	return result;
}

public void addListener(StudentScheduleListener l) {
	_listeners.add(l);
}

private boolean isAConflict (StudentClass c) {
	for (int i = 0; i < _classes.size(); i++)
		if (c.isAConflict((StudentBlock)_classes.get(i)))
			return true;
	return false;
}

private boolean isAConflict (StudentBlock b) {
	for (int i = 0; i < _blocks.size(); i++)
		if (b.isAConflict((StudentBlock)_blocks.get(i)))
			return true;
	return false;
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
	_listeners = new ArrayList();
}

private void fireClassAdded (StudentClass c) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentScheduleListener) i.next()).classAdded(this, c);
}

private void fireClassRemoved (StudentClass c) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentScheduleListener) i.next()).classRemoved(this, c);
}

private void fireBlockAdded (StudentBlock b) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentScheduleListener) i.next()).blockAdded(this, b);
}

private void fireBlockRemoved (StudentBlock b) {
	for (Iterator i = _listeners.iterator(); i.hasNext();)
		 ((StudentScheduleListener) i.next()).blockRemoved(this, b);
}

public String toString () {
	return "Schedule";
}

}
