package com.ttv.at.test.action;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class os_actions {
	static GraphicsEnvironment ge = null; // GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice gd = null; // ge.getDefaultScreenDevice();
	static DisplayMode mode = null;
	static Rectangle bounds = null; // new Rectangle(0, 0, mode.getWidth(), mode.getHeight());
	static Robot rb = null; // new Robot();
	
	
	static void init_graphic_for_capture() {
		if (ge == null) {
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gd = ge.getDefaultScreenDevice();
			mode = gd.getDisplayMode();
			bounds = new Rectangle(0, 0, mode.getWidth(), mode.getHeight());
			try {
				rb = new Robot(gd);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ge = null;
				gd = null;
				mode = null;
				bounds = null;
				rb = null;
			}
		}
	}
	
	static public void mouse_click(int X, int Y)
	{
		try
		{
			init_graphic_for_capture();
			rb.mouseMove(X, Y);
			rb.mousePress(InputEvent.BUTTON1_MASK);
			rb.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	static public String get_file_separator () {
		return System.getProperty("file.separator");
	}
	
	static public BufferedImage capture_screen () {
		init_graphic_for_capture();
		if (rb != null)
		try{
			return rb.createScreenCapture(bounds);
		}catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	/*
	static public boolean capture_screen_png (String PNGImageFullPath) {
		init_graphic_for_capture();
		if (rb != null)
			try {
				File file = new File(PNGImageFullPath);
				rb.createScreenCapture(bounds);
				return ImageIO.write(rb.createScreenCapture(bounds), "PNG", file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			//	e1.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}*/
}
