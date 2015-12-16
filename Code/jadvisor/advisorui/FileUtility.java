/*
 * jadvisor/advisorui/FileUtility.java - Set of useful file utilities
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

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.JFileChooser;

public class FileUtility {

public FileUtility () {

}

public static void saveFile (Object o, File file) {
	try {
		FileOutputStream ostream = new FileOutputStream(file);
		BufferedOutputStream bostream = new BufferedOutputStream(ostream);
		ObjectOutputStream p = new ObjectOutputStream(bostream);
		
		p.writeObject(o);
		p.flush();
		ostream.close();
	} catch (IOException e) {
		System.err.println("File Utility (save): " + e.getMessage());
		//e.printStackTrace();
	}
}

public static Object openFile (File file) throws IOException, ClassNotFoundException {
	Object result;
//	try {
		FileInputStream istream = new FileInputStream (file);
		BufferedInputStream bistream = new BufferedInputStream(istream);
		ObjectInputStream in = new ObjectInputStream (bistream);

		result = in.readObject();
		istream.close();
	return result;
}

public static String getExtension(File f) {
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf('.');

	if (i > 0 &&  i < s.length() - 1) {
		ext = s.substring(i+1).toLowerCase();
	}
	return ext;
}

public static void writeStringToFile (String output) {
	final JFileChooser fc = new JFileChooser();
	int returnVal = fc.showSaveDialog(null);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
		try {
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(
				fc.getSelectedFile()));
			bwriter.write(output, 0, output.length());
			bwriter.close();
		} catch (IOException e) {
			System.err.println("FileUtility (write): " + e.getMessage());
			//e.printStackTrace();
		}
	} else {
		System.out.println("Write command cancelled by user.");
	}
}

}
