/*
 * jadvisor/scheduler/StudentBlock.java - JAdvisor StudentBlock model
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

public class StudentBlock implements Serializable {
	
	protected final String _description;
	protected final boolean[] _day;
	protected final TimeOfDay _startTime;
	protected final TimeOfDay _endTime;


//FIX: should throw Exception if startTime is after endTime
public StudentBlock (String description, boolean[] day, 
		TimeOfDay startTime, TimeOfDay endTime) {
//	if (endTime.compareTo(startTime) < 0)
//		throw new IllegalArgumentException("Start Time must precede End Time.");
	_description = description;
	_day = day;
	_startTime = startTime;
	_endTime = endTime;
}

public StudentBlock (StudentBlock other) {
	_description = new String(other._description);
	_day = new boolean[other._day.length];
	System.arraycopy(other._day, 0, _day, 0, _day.length);
	_startTime = new TimeOfDay(other._startTime);
	_endTime = new TimeOfDay(other._endTime);
}

public boolean isAConflict (StudentBlock other) {
	for (int i = 0; i < _day.length; i++) {
		if (this._day[i] && other._day[i]) {
			if (TimeOfDay.isAConflict(this._startTime, this._endTime, 
					other._startTime, other._endTime))
				return true;
		}
	}
	return false;
}

public String getDescription () {
	return _description;
}

public boolean[] getDays () {
	return _day;
}

public TimeOfDay getStartTime () {
	return _startTime;
}

public TimeOfDay getEndTime () {
	return _endTime;
}

public String toString () {
	return _description + " " + _startTime.toString() + "-" +
		_endTime.toString();
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
}

}