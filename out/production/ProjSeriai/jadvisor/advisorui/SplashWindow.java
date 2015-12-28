/*
 * jadvisor/advisorui/SplashWindow.java - Displays splash window
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashWindow extends JWindow {
	private static final String ICONFILENAME = "/icons/ja_splash.gif";

public SplashWindow (Frame frame) {
	super(frame);
	System.out.println("splash: open");
	try {
		JLabel l = new JLabel(new ImageIcon(
			Toolkit.getDefaultToolkit().getImage(getClass().getResource(ICONFILENAME)), "JAdvisor"));
		getContentPane().add(l, BorderLayout.CENTER);
	} catch (NullPointerException e) {
		System.err.println("Could not open " + ICONFILENAME + ".");
	}
	pack();

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//setSize(100,100);
	setLocation(screenSize.width/2 - (getWidth()/2),
		screenSize.height/2 - (getHeight()/2));
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			setVisible(false);
			dispose();
		}
	});
	setVisible(true);
}

public SplashWindow (Frame frame, int waitTime) {
	this(frame);
	final int pause = waitTime;
	final Runnable closerRunner = new Runnable() {
		public void run() {
			setVisible(false);
			dispose();
		}
	};
	Runnable waitRunner = new Runnable() {
		public void run() {
			try {
				Thread.sleep(pause);
				System.out.println("splash: close");
				SwingUtilities.invokeAndWait(closerRunner);
			} catch(Exception e) {
				e.printStackTrace();
				// can catch InvocationTargetException
				// can catch InterruptedException
			}
		}
	};
	Thread splashThread = new Thread(waitRunner, "SplashThread");
	splashThread.start();
}

}
