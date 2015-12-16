/*
 * jadvisor/scheduler/TimeOfDay.java - JAdvisor TimeOfDay model
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

public class TimeOfDay implements Comparable, Serializable {
	
	public static final int AM = 0;
	public static final int PM = 1;
	
	private int _hour;
	private int _minute;

public TimeOfDay (int hour, int minute) {
	if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
		throw new IllegalArgumentException("Illegal Time Argument: " + hour + ":" + minute);
	_hour = hour;
	_minute = minute;
}

public TimeOfDay (int hour, int minute, int ampm) {
	if (hour < 1 || hour > 12 || minute < 0 || minute > 59 || ampm < 0 || ampm > 1)
		throw new IllegalArgumentException("Illegal Time Argument: " + hour + ":" + minute + " " + ampm);
	_hour = hour;
	_minute = minute;
	if (ampm == 0 && hour == 12)
		_hour = 0;
	if (ampm == 1 && hour != 12)
		_hour += 12;
}

public TimeOfDay (TimeOfDay other) {
	this(other._hour, other._minute);
}

public void add (int hour, int minute) {
	int h = _hour;
	int m = _minute;

	m += minute;
	while (m >= 60) {
		m -= 60;
		h++;
	}
	while (m < 0) {
		m += 60;
		h--;
	}
	if (h < 0 || h > 23)
		throw new IllegalArgumentException("Illegal Time Argument: " + hour + ":" + minute);

	_hour = h;
	_minute = m;
}

public static boolean isAConflict (TimeOfDay start1, TimeOfDay end1, 
		TimeOfDay start2, TimeOfDay end2) {
	if ((start1.compareTo(start2) >= 0 && start1.compareTo(end2) < 0) ||
		(end1.compareTo(start2) > 0 && end1.compareTo(end2) <= 0))
		return true;
	return false;
}

public boolean equals (Object other) {
	if (this == other) return true;
	if (other == null) return false;
	if (this.getClass() != other.getClass()) return false;
	return (this._hour == ((TimeOfDay)other)._hour) && 
		(this._minute == ((TimeOfDay)other)._minute);
}

//Returns positive if this comes after other
public int compareTo (Object other) {
	return (60 * this._hour + this._minute)
		- (60 * ((TimeOfDay)other)._hour + ((TimeOfDay)other)._minute);
}

public int getHour () {
	return _hour;
}

public int getMinute () {
	return _minute;
}

public String toString () {
	String minute = Integer.toString(_minute);
	if (_minute < 10)
		minute = "0" + minute;
	return _hour + ":" + minute;
}

public String toStringAMPM () {
	StringBuffer result = new StringBuffer();
	boolean pm = false;
	if (_hour == 0)
		result.append("12");
	else if (_hour == 12) {
		result.append("12");
		pm = true;
	}
	else if (_hour > 12) {
		result.append(_hour - 12);
		pm = true;
	}
	else
		result.append(_hour);

	result.append(":");
	if (_minute < 10)
		result.append("0");
	result.append(_minute);
	if (pm)
		result.append("P");
	return result.toString();
}

private void writeObject (ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
}

private void readObject (ObjectInputStream s) throws IOException, ClassNotFoundException  {
	s.defaultReadObject();
}

}
