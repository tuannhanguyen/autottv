package com.ttv.at.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

public class testsetting {
	static String runtime = "SELENIUM";
	static public String get_runtime() { return runtime; }
	static public void set_runtime(String runtime) {testsetting.runtime = runtime; }
	
	// FIREFOX -> test FIREFOX
	// IE or INTERNET EXPLORER -> test IE
	// SAFARI -> test Safari
	// ANDROID -> testing for Android
	static String default_browser = "CHROME";
	static public String get_default_browser() { return default_browser; }
	static public void set_default_browser(String default_browser) 
	{
		if (default_browser != null && default_browser.length() > 0) {
			testsetting.default_browser = default_browser;
		}
	}
	static String runtime_browser;
	static public String get_runtime_browser() {
		update_runtime_browser ();
		return runtime_browser;
	}
	static void update_runtime_browser () {
		runtime_browser = null;
		if (default_browser != null && default_browser.length() > 0) {
			if (default_browser.toUpperCase().equals("CHROME")) {
				runtime_browser = "Chrome";
			}
			else if (default_browser.toUpperCase().equals("IE") ||
					default_browser.toUpperCase().equals("INTERNET EXPLORER")) {
				runtime_browser = "IE";
				get_ie_version();
				if (ie_version != null && ie_version.length() > 0)
					runtime_browser += " " +  ie_version.substring(0, ie_version.indexOf('.'));
			}
			else if (default_browser.toUpperCase().equals("FIREFOX")) {
//				get_firefox_version();
				runtime_browser = "Firefox";
//				if (firefox_version != null && firefox_version.length() > 0)
//					runtime_browser += " " +  firefox_version.substring(0, 3);
			}
			else if (default_browser.toUpperCase().equals("EDGE")) {
				runtime_browser = "Edge";
			}
			else if (default_browser.toUpperCase().equals("SAFARI")) {
				runtime_browser = "Safari";
				get_safari_version();
				if (safari_version != null && safari_version.length() > 0)
					runtime_browser += " " +  safari_version.substring(0, 3);
			}
		}
	}
	
	static int timeout = 15;
	static public int get_timeout() { return timeout; }
	static public void set_timeout(int timeout) {testsetting.timeout = timeout; }
	
	static int autoresume = 3;
	static public int get_autoresume() { return autoresume; }
	static public void set_autoresume(int autoresume) {testsetting.autoresume = autoresume;}

	static String host_name = null;
	static public String get_host_name () {
		if (host_name == null)
			try {
				host_name = java.net.InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return host_name;
	}
	
	static String host_ip = null;
	static public String get_host_ip () {
		if (host_ip == null)
			try {
				host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return host_ip;
	}
	
	static String default_script_folder = null;
	static public String get_default_script_folder() {
		if (default_script_folder == null)
			default_script_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "script";
		return default_script_folder;
	}
	
	static String default_schedule_folder = null;
	static public String get_default_schedule_folder() {
		if (default_schedule_folder == null)
			default_schedule_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "schedule";
		return default_schedule_folder;
	}

	static String root_log_folder = null;
	static public String get_root_log_folder () {
		if (root_log_folder == null) {
			root_log_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
			java.io.File default_log_folder_file = new File(root_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		else {
			java.io.File default_log_folder_file = new File(root_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		
		return root_log_folder;
	}
	
	static String default_log_folder = null;
	static public void init_default_log_folder() {
		if (default_log_folder == null) {
			default_log_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
			java.io.File default_log_folder_file = new File(default_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		else {
			java.io.File default_log_folder_file = new File(default_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		if (default_log_images_folder == null) {
			default_log_images_folder = get_default_log_folder() + System.getProperty("file.separator") + "images";

			java.io.File default_log_images_folder_file = new File(default_log_images_folder);
			if ((!default_log_images_folder_file.exists()) || (!default_log_images_folder_file.isDirectory())) {
				// create folder
				default_log_images_folder_file.mkdirs();
			}
		}
		else {
			java.io.File default_log_images_folder_file = new File(default_log_images_folder);
			if ((!default_log_images_folder_file.exists()) || (!default_log_images_folder_file.isDirectory())) {
				// create folder
				default_log_images_folder_file.mkdirs();
			}
		}
	}
	static public void init_default_log_folder(String log_title) {
		if (default_log_folder == null) {
			default_log_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "log" + log_title;
			java.io.File scan_folder_to_init = new File(default_log_folder);

			if ((!scan_folder_to_init.exists()) || (!scan_folder_to_init.isDirectory()))
				scan_folder_to_init.mkdirs();
		}
		else {
			java.io.File default_log_folder_file = new File(default_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		if (default_log_images_folder == null) {
			default_log_images_folder = get_default_log_folder() + System.getProperty("file.separator") + "images";

			java.io.File default_log_images_folder_file = new File(default_log_images_folder);
			if ((!default_log_images_folder_file.exists()) || (!default_log_images_folder_file.isDirectory())) {
				// create folder
				default_log_images_folder_file.mkdirs();
			}
		}
		else {
			java.io.File default_log_images_folder_file = new File(default_log_images_folder);
			if ((!default_log_images_folder_file.exists()) || (!default_log_images_folder_file.isDirectory())) {
				// create folder
				default_log_images_folder_file.mkdirs();
			}
		}
	}
	static public String get_default_log_folder() {
		if (default_log_folder == null) {
			default_log_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
			java.io.File default_log_folder_file = new File(default_log_folder);
			if ((!default_log_folder_file.exists()) || (!default_log_folder_file.isDirectory())) {
				// Create log folder
				default_log_folder_file.mkdirs();
			}
		}
		return default_log_folder;
	}
	static String default_conf_folder = null;
	static public String get_default_conf_folder() {
		if (default_conf_folder == null) {
			default_conf_folder = System.getProperty("user.dir") + System.getProperty("file.separator") + "conf";
			java.io.File default_conf_folder_file = new File(default_conf_folder);
			if ((!default_conf_folder_file.exists()) || (!default_conf_folder_file.isDirectory())) {
				// Create log folder
				default_conf_folder_file.mkdirs();
			}
		}
		return default_conf_folder;
	}
	
	static String default_log_detail_html_file = null;
	static public String get_default_log_detail_html_file() {
		if (default_log_detail_html_file == null)
			default_log_detail_html_file =  get_default_log_folder() + System.getProperty("file.separator") + "detail_log.html";
		
		return default_log_detail_html_file;
	}
	
	static String default_log_simple_html_file = null;
	static public String get_default_log_simple_html_file() {
		if (default_log_simple_html_file == null)
			default_log_simple_html_file =  get_default_log_folder() + System.getProperty("file.separator") + "simple_log.html";
		
		return default_log_simple_html_file;
	}
	
	static String default_test_result_html_file = null;
	static public String get_default_test_result_html_file() {
		if (default_test_result_html_file == null)
			default_test_result_html_file =  get_default_log_folder() + System.getProperty("file.separator") + "test_result.html";
		
		return default_test_result_html_file;
	}
	
	static String default_index_html_file = null;
	static public String get_default_index_html_file() {
		if (default_index_html_file == null)
			default_index_html_file =  get_default_log_folder() + System.getProperty("file.separator") + "index.html";
		
		return default_index_html_file;
	}
	
	static String default_log_images_folder = null;
	static public String get_default_log_images_folder() {
		if (default_log_images_folder == null) {
			default_log_images_folder = get_default_log_folder() + System.getProperty("file.separator") + "images";

			java.io.File default_log_images_folder_file = new File(default_log_images_folder);
			if ((!default_log_images_folder_file.exists()) || (!default_log_images_folder_file.isDirectory())) {
				// create folder
				default_log_images_folder_file.mkdirs();
			}
		}
		return default_log_images_folder;
	}
	
	static String default_log_api_folder = null;
	static public String get_default_log_api_folder() {
		if (default_log_api_folder == null) {
			default_log_api_folder = get_default_log_folder() + System.getProperty("file.separator") + "api";

			java.io.File default_log_api_folder_file = new File(default_log_api_folder);
			if ((!default_log_api_folder_file.exists()) || (!default_log_api_folder_file.isDirectory())) {
				// create folder
				default_log_api_folder_file.mkdirs();
			}
		}
		return default_log_api_folder;
	}

	static public void clear_default_log () {
		default_log_folder = null;
		default_log_detail_html_file = null;
		default_log_simple_html_file = null;
		default_test_result_html_file = null;
		default_index_html_file = null;
		default_log_images_folder = null;
	}
	
	static String firefox_version = null; 
	static final String registry_ff_path = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Mozilla\\Mozilla Firefox";
	static final String registry_ff_path_2 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Mozilla\\Mozilla Firefox";
	static public String get_firefox_version () {
		// HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Mozilla\Mozilla Firefox\
		// HKEY_LOCAL_MACHINE\SOFTWARE\Mozilla\Mozilla Firefox\
		if (firefox_version == null || firefox_version.length() == 0) {
			try {
				Process process = Runtime.getRuntime().exec("reg query \"" + registry_ff_path + "\" /s");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					if(line.contains("REG_SZ") && line.contains("CurrentVersion"))
					{
						int startIndex = line.indexOf("REG_SZ");
						firefox_version = line.substring(startIndex + 6).trim();
						break;
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (firefox_version == null || firefox_version.length() == 0) {
			try {
				Process process = Runtime.getRuntime().exec("reg query \"" + registry_ff_path_2 + "\" /s");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					if(line.contains("REG_SZ") && line.contains("CurrentVersion"))
					{
						int startIndex = line.indexOf("REG_SZ");
						firefox_version = line.substring(startIndex + 6).trim();
						break;
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return firefox_version;
	}

	static String safari_version = null;
	static final String registry_safari_path = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Apple Computer, Inc.\\Safari";
	static final String registry_safari_path_2 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Apple Computer, Inc.\\Safari";
	static public String get_safari_version () {
		if (safari_version == null || safari_version.length() == 0) {
			try {
				Process process = Runtime.getRuntime().exec("reg query \"" + registry_safari_path + "\" /s");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					if(line.contains("REG_SZ") && line.contains("CurrentVersion"))
					{
						int startIndex = line.indexOf("REG_SZ");
						safari_version = line.substring(startIndex + 6).trim();
						break;
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (safari_version == null || safari_version.length() == 0) {
			try {
				Process process = Runtime.getRuntime().exec("reg query \"" + registry_safari_path_2 + "\" /s");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					if(line.contains("REG_SZ") && line.contains("Version"))
					{
						int startIndex = line.indexOf("REG_SZ");
						safari_version = line.substring(startIndex + 6).trim();
						break;
					}
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return safari_version;
	}
	
	static String ie_version = null; 
	static final String registry_ie_path = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Internet Explorer";
	static public String get_ie_version () {
		if (ie_version == null || ie_version.length() == 0) {
			String Version = null;
			String svcVersion = null;
			try {
				Process process = Runtime.getRuntime().exec("reg query \"" + registry_ie_path + "\" /s");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) 
				{
					if(line.contains("REG_SZ") && line.contains("Version") && Version == null)
					{
						int startIndex = line.indexOf("REG_SZ");
						Version = line.substring(startIndex + 6).trim();
					}
					if(line.contains("REG_SZ") && line.contains("svcVersion") && svcVersion == null)
					{
						int startIndex = line.indexOf("REG_SZ");
						svcVersion = line.substring(startIndex + 6).trim();
					}
				}
				reader.close();
				if (svcVersion != null)
					ie_version = svcVersion;
				else if (Version != null)
					ie_version = Version;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ie_version;
	}

	static testsuite_dataset common_data = null;
	static public void set_common_data (testsuite_dataset common_data) {
		testsetting.common_data = common_data;
		if (common_data != null)
			common_data.apply_env();
	}
	static public testsuite_dataset get_common_data () { return common_data;}
	
	static Properties config = null;
	public static Properties getProperties() {
		
	   Properties prop = null;
	   try {

		    String path = "conf" + com.ttv.at.util.os.os_file_separator + "config.properties"; 
		    InputStream input = new FileInputStream(path);
            // load a properties file
		    prop = new Properties();
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	   
		return prop;
	}
	public static void setProperties(Properties config) {
		testsetting.config = config;
	}
}
