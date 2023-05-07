package com.ttv.at.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class os {
	// File separator character
	static public final String os_file_separator = System.getProperty("file.separator");
	
	// OS def value
	static public final int WIN = 10;
	static public final int WIN_XP_X32 = 11;
	static public final int WIN_XP_X64 = 12;
	static public final int WIN_Vista_X32 = 13;
	static public final int WIN_Vista_X64 = 14;
	static public final int WIN_7_X32 = 15;
	static public final int WIN_7_X64 = 16;
	
	static public final int LINUX = 20;
	static public final int UBUNTU_10_10_X32 = 21;
	
	static public final int MAC = 40;
	
	static public String os_def_string()
	{
		return System.getProperty("os.name");
	}
	
	static public boolean isWindows()
	{
		if ((os_def_string().toLowerCase().indexOf( "win" ) >= 0))
			return true;
		return false;
	}
	static public boolean isLinux()
	{
		if ((os_def_string().toLowerCase().indexOf( "nux" ) >= 0))
			return true;
		return false;
	}
	static public boolean isMac()
	{
		if ((os_def_string().toLowerCase().indexOf( "mac" ) >= 0))
			return true;
		return false;
	}
	
	// Get Application path
	static private String openOfficePath = "";
	static public String getOpenOfficePath()
	{
		if (openOfficePath.length() < 3)
		{
			if (isWindows())
			{
				Process process;
				try {
					process = Runtime.getRuntime().exec("reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\" /s");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) 
					{
						if(line.toLowerCase().contains("soffice.exe"))
						{
							if(line.toLowerCase().contains("<no name>") || line.toLowerCase().contains("(default)"))	//on XP: <NO NAME>	on SEVEN: (Default)
							{
								String startIndex = "REG_SZ";
								openOfficePath = line.substring(line.indexOf(startIndex) + startIndex.length()).trim();
								break;
							}
						}
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (isLinux())
			{
				String ooCheckFolder = "/opt/openoffice.org2/program";
				File checkFile = new File(ooCheckFolder);
				if (!checkFile.exists())
				{
					ooCheckFolder = "/opt/openoffice.org/program";
					checkFile = new File(ooCheckFolder);
					if (!checkFile.exists()) {
						ooCheckFolder = "/usr/lib/openoffice/program";
						checkFile = new File(ooCheckFolder);
						if (!checkFile.exists()) {
							ooCheckFolder = "/opt/openoffice.org3/program";
							checkFile = new File(ooCheckFolder);
							if (checkFile.exists())
								openOfficePath = ooCheckFolder;
						}
						else
							openOfficePath = ooCheckFolder;
					}
					else
						openOfficePath = ooCheckFolder;
					
				}
				else
					openOfficePath = ooCheckFolder;
			}
		}
		return openOfficePath;
	}

	static private String libreOfficePath = "";
	static public String getLibreOfficePath()
	{
		if (libreOfficePath.length() < 3)
		{
			if (isWindows())
			{
				Process process;
				try {
					process = Runtime.getRuntime().exec("reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\" /s");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) 
					{
						if(line.toLowerCase().contains("soffice.exe"))
						{
							if(line.toLowerCase().contains("<no name>") || line.toLowerCase().contains("(default)"))	//on XP: <NO NAME>	on SEVEN: (Default)
							{
								String startIndex = "REG_SZ";
								libreOfficePath = line.substring(line.indexOf(startIndex) + startIndex.length()).trim();
								break;
							}
						}
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (isLinux())
			{
				String ooCheckFolder = "/opt/openoffice.org2/program";
				File checkFile = new File(ooCheckFolder);
				if (!checkFile.exists())
				{
					ooCheckFolder = "/opt/openoffice.org/program";
					checkFile = new File(ooCheckFolder);
					if (!checkFile.exists()) {
						ooCheckFolder = "/usr/lib/openoffice/program";
						checkFile = new File(ooCheckFolder);
						if (!checkFile.exists()) {
							ooCheckFolder = "/opt/openoffice.org3/program";
							checkFile = new File(ooCheckFolder);
							if (checkFile.exists())
								libreOfficePath = ooCheckFolder;
						}
						else
							libreOfficePath = ooCheckFolder;
					}
					else
						libreOfficePath = ooCheckFolder;
					
				}
				else
					libreOfficePath = ooCheckFolder;
			}
		}
		return libreOfficePath;
	}
	
	static private String iePath = "";
	static public String get_iePath ()
	{
		if (isWindows())
		{
			if (iePath == null || iePath.length() == 0)
				try {
					Process process = Runtime.getRuntime().exec("reg query \"HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\" /s");
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line;
					while ((line = reader.readLine()) != null) 
					{
						if(line.toLowerCase().contains("iexplore.exe"))
						{
							if(line.toLowerCase().contains("<no name>") || line.toLowerCase().contains("(default)"))	//on XP: <NO NAME>	on SEVEN: (Default)
							{
								String startIndex = "REG_SZ";
								iePath = line.substring(line.indexOf(startIndex) + startIndex.length()).trim();
								return iePath;
							}
						}
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else
				return iePath;
		}
		return null;
	}

	static public void kill_process_by_name (String processname) {

		if (isWindows())
		{
			
			String command_line = "taskkill /F /IM " + processname;
			Runtime rt = Runtime.getRuntime();
			try {
				rt.exec(command_line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static public boolean is_process_running_by_name (String processname) {
		if (isWindows())
			try {
				Process p = Runtime.getRuntime().exec("tasklist");
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
				 
					// System.out.println(line);
					if (line.contains(processname)) {
						return true;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return false;
	}
}
