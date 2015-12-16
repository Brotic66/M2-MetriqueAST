/*
 * jadvisor/school/UNC.java - SchoolAdapter for Univ. of North Carolina
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

package jadvisor.school;

import jadvisor.advisorui.SwingWorker;
import jadvisor.scheduler.*;
import jadvisor.planner.*;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * This class implements <code>SchoolAdapter</code> for the
 * University of North Carolina - Chapel Hill.
 *
 * @author Curtis Rawls
 * @version 0.4.6
 */
public class UNC implements SchoolAdapter {
	private static String CLASSES_URL = "http://www.ais.unc.edu/sis/clsched/";
	private static String[] SEMESTER_NAMES_URL = 
		new String[] {"fall", "sprg", "sum1", "sum2"};

	private static int DAYS_OLD = 5;
	
	private String[][] _departments;
	private String[][] _courses;
	private List[][] _classes;
	
	public static final int FALL = 0;
	public static final int SPRING = 1;
	public static final int SUMMER1 = 2;
	public static final int SUMMER2 = 3;
	public final String[] getSemesters () {
		return new String[] {"Fall", "Spring", "Summer 1", "Summer 2"};
	}
	
	public final boolean[] getRequiredSemesters () {
		return new boolean[] {true, true, false, false};
	}
	public final String getOptionalSemesterText () {return "Summers";}
	
	public static final int getDaysInWeek () {return 5;}
	public final String[] getDaysAbbreviations () {
		return new String[] {"M", "T", "W", "R", "F"};
	}
	
	public final String[] getClassTitles () {
		return new String[] 
		{"Pre", "Number", "Section", "Days", "Start Time", "End Time", "Mod", "Credit"};
	}
	public final String[] getClassInfoTitles () {
		return new String[] {"Instructor", "Building"};
	}

	public static final int NONE = 0;
	public static final int CREDIT = 1;
	public static final int AUDIT = 2;
	public final String[] getClassMods () {
		return new String[] {"None", "Credit", "Audit"};
	}
	
public UNC () {
	_departments = new String[][] {
		{"AERO", "AFAM", "AFRI", "AHSC", "AMST", "ANTH", "APPL", "ARAB", "ARMY",
			"ART", "ASIA", "ASTR", "BENG", "BIOC", "BIOL", "BIOS", "BMME", "BUSI", "CBIO", "CDFS", "CHEM", 
			"CHIN", "CLAR", "CLAS", "CLSC", "CMPL", "COMM", "COMP", "CZCH", "DECO", "DENG", "DHED", "DHYG", 
			"DRAM", "DTCH", "ECOL", "ECON", "EDUC", "ENDO", "ENGL", "ENST", "ENVR", "EPID", "EXSS", "FOLK", 
			"FREN", "GEOG", "GEOL", "GERM", "GNET", "GRAD", "GREK", "HBHE", "HIST", "HMSC", "HNRS", "HNUR", 
			"HPAA", "IBMS", "IDST", "IHMS", "INLS", "INTS", "ISP", "ITAL", "JAPN", "JOMC", "LATN", "LING", 
			"LTAM", "MACD", "MASC", "MATH", "MCRO", "MEDC", "MHCH", "MNGT", "MTSC", "MUSC", "NAVS", "NBIO", 
			"NURS", "NUTR", "OBIO", "OCCT", "OMSU", "OPER", "OR", "ORAD", "ORPA", "ORTH", "PATH", "PEDO", 
			"PERI", "PHCO", "PHCY", "PHIL", "PHNU", "PHPR", "PHYA", "PHYI", "PHYS", "PHYT", "PLAN", "PLCY", 
			"PLSH", "POLI", "PORT", "PROS", "PRSN", "PSYC", "PUBA", "PUBH", "PWAD", "RADI", "RECR", "RELI", 
			"ROML", "RPSY", "RUES", "RUSS", "SERB", "SEVI", "SLAV", "SOCI", "SOWO", "SPAN", "SPCL", "SPHS", 
			"STAT", "SWAH", "TAML", "TOXC", "VIET", "WMST", "YAP"},
		{}	//full names for each department go here
	};
	_courses = new String[_departments[0].length][];
	_classes = new List[_departments[0].length][];
}

public String[] getClassPreList (int semester) {
	return _departments[0];
}

public String[] getClassNumberList (int semester, String pre) throws IOException {
	int i;
	for (i = 0; i < _departments[0].length && !_departments[0][i].equals(pre); i++) {}
	if (i >= _departments[0].length)
		return new String[0];

	if (_courses[i] == null) {
		try {
			getData(pre, SEMESTER_NAMES_URL[semester], _courses[i], _classes[i], i);
		} catch (Exception e) {
			System.out.println("Get Data:" + e.getMessage());
			throw new IOException(e.getMessage());
		}
	}

	return _courses[i];
}

public List getClassSectionList (int semester, String pre, String number) throws IOException {
	int i,j;
	getClassNumberList(semester, pre);

	for (i = 0; i < _departments[0].length && !_departments[0][i].equals(pre); i++) {}
	if (i >= _departments[0].length)
		throw new IOException("UNC: Could not find department \"" + pre + "\"");

	for (j = 0; j < _courses[i].length && !_courses[i][j].equals(number); j++) {}
	if (j >= _courses[i].length)
		throw new IOException("UNC: Could not find course \"" + pre + " " + number + "\"");
	return _classes[i][j];
}

public List getClassInfo (StudentClass c) {
	List l = new ArrayList();
	l.add("Professor Name");
	l.add("Building");
	return l;
}

public boolean isACourse (Course course) {
	//Substitute all this with call to server to check if course is there
	
//	if (course.getCoursePre().length() > 3 || course.getCoursePre().length() < 2)
//		return false;

//	if (course.getCourseNumber().length() > 4 || course.getCourseNumber().length() < 3)
//		return false;

	return true;
}

public boolean isAClass (StudentClass c) {
	//Substitute all this with call to server to check if class is there
	
	return true;
}

public List getCoursePrerequisites (Course course) {
	return new ArrayList();
}

public Curriculum getCurriculum (String curriculumName) {
	final Curriculum result = new Curriculum(curriculumName);

	String[] courses;
	try {
		courses = getClassNumberList(0, curriculumName);
	} catch (IOException e) {
		courses = new String[0];
	}
	for (int i = 0; i < courses.length; i++)
		result.add(new Course(curriculumName, courses[i]));

	return result;
}

public String getCourseDescription (Course course) {
	return "";
}

public List searchCourses (String searchString, boolean searchTitles, 
		boolean searchDescriptions) {
	List a = new ArrayList();
	a.add("Search Courses not yet implemented for UNC.");
	return a;
}

public String classToString (StudentClass c) {
	StringBuffer days = new StringBuffer();
		for (int i = 0; i < getDaysAbbreviations().length; i++)
			if(c.getDays()[i])
				days.append(getDaysAbbreviations()[i]);
			else
				days.append(" ");
	return c.getCourse() + " " + c.getSection() + " " + " "
		+ days + " " + timeToString(c.getStartTime()) + "-"
		+ timeToString(c.getEndTime()) + " " + c.getCredit();
}

public String timeToString (TimeOfDay time) {
	return time.toStringAMPM();
}

public String toString () {
	return "UNC SchoolAdapter";
}

private StudentClass parseClassData (String s1, String s3, String s4) {
	int index = s3.indexOf("</A>");
	if (index >= 0)
		s3 = new String(s3.substring(0, index) + s3.substring(index + 4, s3.length()));
	
	String[] tokens = new String[14];
	tokens[0] = s1.substring(6, 15);	//0 - DEPT
	tokens[1] = s1.substring(16, 27);	//1 - NUM
	tokens[2] = s1.substring(26, 53);	//2 - NAME
	tokens[3] = s1.substring(54, 57);	//3 - LEC/REC/LAB
	tokens[4] = s1.substring(61, 66);	//4 - CREDIT

	tokens[5] = s3.substring(0, 8);		//5 - NOTE
	tokens[6] = s3.substring(9, 14);	//6 - CALL NO
	tokens[7] = s3.substring(15, 19);	//7 - SEC
	tokens[8] = s3.substring(20, 28);	//8 - DAYS
	tokens[9] = s3.substring(29, 44);	//9 - TIME
	tokens[10] = s3.substring(45, 60);	//10 - INSTRUCTOR	
	tokens[11] = s3.substring(61, 63);	//11 - BUILDING
	tokens[12] = s3.substring(66, 70);	//12 - ROOM NO.
	tokens[13] = s3.substring(71, 78);	//13 - STATUS

	for (int i = 0; i < tokens.length; i++)
		tokens[i] = tokens[i].trim();

	boolean [] days = new boolean[getDaysAbbreviations().length];
	Arrays.fill(days, false);
	for (int i = 0; i < days.length; i++)
		if (tokens[9].indexOf(getDaysAbbreviations()[i]) != -1)
			days[i] = true;

	TimeOfDay[] times = parseTime(tokens[9]);

	int credit;
	if (tokens[4].indexOf("*VAR*") >= 0)
		credit = 0;
	else
		credit = (int)Double.parseDouble(tokens[4]);

	StudentClass result = new StudentClass(
		new Course(tokens[0], tokens[1]), tokens[7],	
		days, times[0], times[1], 0, credit);

	List classInfo = new ArrayList();
	classInfo.add(tokens[10]);		//10 - INSTRUCTOR
	classInfo.add(tokens[11]);		//11 - BUILDING
	result.setInfo(classInfo);
	return result;
}

private TimeOfDay[] parseTime (String s) {
	if (s.equals("*TBA*"))
		return new TimeOfDay[] {new TimeOfDay(12,00), new TimeOfDay(12,00)};
	TimeOfDay[] result = new TimeOfDay[2];
	String[] s1 = new String[2];
	StringTokenizer t = new StringTokenizer(s, "-");
	for (int i = 0; t.hasMoreTokens(); i++)
		s1[i] = t.nextToken();
	
	int starth = Integer.parseInt(s1[0].substring(0,2));
	int startm = Integer.parseInt(s1[0].substring(3,5));
	int startAMPM;
	if (s1[0].substring(5,6).equals("A"))
		startAMPM = TimeOfDay.AM;
	else
		startAMPM = TimeOfDay.PM;
	int endh = Integer.parseInt(s1[1].substring(0,2));
	int endm = Integer.parseInt(s1[1].substring(3,5));
	int endAMPM;
	if (s1[1].substring(5,6).equals("A"))
		endAMPM = TimeOfDay.AM;
	else
		endAMPM = TimeOfDay.PM;

	result[0] = new TimeOfDay(starth, startm, startAMPM);
	result[1] = new TimeOfDay(endh, endm, endAMPM);
	return result;
}

private void makeDataCache (InputStream i, File f) throws IOException {
	BufferedWriter out = new BufferedWriter(new FileWriter(f));
	BufferedInputStream bin = new BufferedInputStream(i);
	int c;
	while ((c = bin.read()) != -1)
		out.write(c);

	bin.close();
	out.close();
}

//adapted from JCarnegie (carnegie.handlers.ASU) by Mark Edgar
private void getData (String pre, String semester, String[] courses, List[] classes, int k) 
		throws IOException {
	File tmpDir = getTmpDir();
	File cacheFile = new File(tmpDir, "jadvisor-UNC-" + semester + "-" + pre + ".cache");
	if (tmpDir != null && !cleanCache(cacheFile)) {
		if (cacheFile.exists()) {
			System.err.println("DEBUG: Got cacheFile == " + cacheFile);
			try {
				System.err.println("DEBUG: Parsing Class Data");
				readData(cacheFile, courses, classes, k);
				System.err.println("DEBUG: Parsing Done");
				makeDataCacheThreaded(pre, semester, cacheFile);
				return;
			} catch (IOException e) {
				System.err.println("Could not read cache file: " + e.getMessage());
				removeCacheFile(cacheFile);
			}
		} else {
			System.err.println("DEBUG: " + cacheFile + ": Cache file does not exist");
		}
	}

	String preURL = new String(pre);
	while (preURL.length() < 4)
		preURL += "_";
		
	URL u = new URL(CLASSES_URL + semester + "/" + preURL + ".html");
	URLConnection c = u.openConnection();
	if (cacheFile == null)
		c.setUseCaches(true);
	
	System.err.println("DEBUG: Fetching URL: " + u);
	try {
		makeDataCache(c.getInputStream(), cacheFile);
	} catch (IOException e) {
		removeCacheFile(cacheFile);
		System.err.println("Could not make cache file: " + e.getMessage());
		throw new IOException("Could not make cache file: " + e.getMessage());
	}
	System.err.println("DEBUG: Parsing Class Data");
	try {
		readData(cacheFile, courses, classes, k);
	} catch (IOException e) {
		removeCacheFile(cacheFile);
		System.err.println("Could not read cache file: " + e.getMessage());
		throw new IOException("Could not read cache file: " + e.getMessage());
	}
	System.err.println("DEBUG: Parsing Done");
}

//returns true if cache file is more than DAYS_OLD days old.
private boolean cleanCache(File cacheFile) {
	long numdaysold = System.currentTimeMillis() - 24*60*60*1000*DAYS_OLD;
	return cacheFile.lastModified() < numdaysold;
}

private File getTmpDir() {
	File f;
	try {
		f = File.createTempFile("foo", null);
	} catch (IOException e) {
		System.err.println("DEBUG: Could not create temporary file: " + e.getMessage());
		return null;
	}
	f.delete();
	return f.getParentFile();
}

private void removeCacheFile (File cacheFile) {
	cacheFile.delete();
}

private void readData(File f, String[] courses, List[] classes, int k) throws IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
	String line, line1 = "", line2, line3;
	StudentClass c;
	List courseList = new LinkedList();
	List classList = new LinkedList();
	try {
	while ((line = reader.readLine()) != null && line.indexOf("<pre>") < 0) {
		//System.out.println("DEBUG: got " + line.length() + " bytes");
	}
	} catch (Exception e) {System.out.println(e.getMessage());}

	for (int i = 0; i < 4; i++)
		line = reader.readLine();

	try {
	while ((line = reader.readLine()) != null) {
		//System.out.println(line);
		if (line.indexOf("</pre>") >= 0)
			break;
		if (line.charAt(0) != ' ') {
		} else if (line.charAt(6) != ' ') {
			line1 = line;
		} else if (line.charAt(9) != ' ') {
			line2 = line;
			line3 = "";//reader.readLine();
			if (line2.indexOf("C A N C E L L E D") < 0) {
				c = parseClassData(line1, line2, line3);
				if (!courseList.contains(c.getCourse().getCourseNumber()))
					courseList.add(new String(c.getCourse().getCourseNumber()));
				classList.add(c);
			}
		}
	}
	} catch (Exception e) {System.out.println(e);}
	reader.close();

	Object[] c2 = courseList.toArray();
	_courses[k] = new String[c2.length];
	for (int i = 0; i < c2.length; i++) {
		_courses[k][i] = (String)c2[i];
	}
	Arrays.sort(_courses[k]);

	_classes[k] = new List[_courses[k].length];
	for (int i = 0; i < _classes[k].length; i++)
		_classes[k][i] = new ArrayList();

	for (int i = 0; i < classList.size(); i++) {
		c = (StudentClass)classList.get(i);
		String s = c.getCourse().getCourseNumber();
		int n = Arrays.binarySearch(_courses[k], s);
		_classes[k][n].add(c);
	}
}

private void makeDataCacheThreaded (final String pre, final String semester, 
		final File cacheFile) {
	final SwingWorker worker = new SwingWorker() {
		public Object construct() {
			long numdaysold = System.currentTimeMillis() - 24*60*60*1000;
			if (cacheFile.lastModified() < numdaysold) {
				try {
					String preURL = new String(pre);
					while (preURL.length() < 4)
						preURL += "_";
						
					URL u = new URL(CLASSES_URL + semester + "/" + preURL + ".html");
					URLConnection c = u.openConnection();
					if (cacheFile == null)
						c.setUseCaches(true);
	
					System.err.println("DEBUG: Thread Fetching URL: " + u);
					makeDataCache(c.getInputStream(), cacheFile);
					System.err.println("DEBUG: Thread Done");
				} catch (IOException e) {}
			}
			return null;
		}
	};
	worker.start();
}

}
