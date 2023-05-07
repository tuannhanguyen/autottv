package com.ttv.at.office;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class appPOI {

	String file_full_path = null;
	POIFSFileSystem file_instance = null;
	HSSFWorkbook xls_instance = null;
	XSSFWorkbook xlsx_instance = null;
	
	static appPOI instance = null;
	static public appPOI get_instance() {return instance;}
	
	static public boolean OpenFile (String inputFile)
	{
		instance = new appPOI (inputFile);
		if (instance != null)
			return true;
		else
			return false;
	}
	static public void SaveAsExcelAndExit (String excel_file_full_path) {
		if (instance != null) {
			instance.SaveAsExcelAndClose(excel_file_full_path);
		}
	}
	
	
	private ArrayList<sheetPOI> sheets = new ArrayList<sheetPOI>();
	
	public appPOI(String fullFilePath) {
		try {
			File input_file = new File(fullFilePath);
			if (input_file.exists()) {
				int dot = fullFilePath.lastIndexOf('.');
				String file_ext = fullFilePath.substring(dot + 1);
				if (file_ext.toLowerCase().equals("xls")){
					InputStream myxls = new FileInputStream(input_file);
					POIFSFileSystem fs = new POIFSFileSystem(myxls);
					xls_instance = new HSSFWorkbook (fs);
					file_full_path = fullFilePath;
					load_sheet ();
				}
				else {
					InputStream myxls = new FileInputStream(input_file);
					xlsx_instance = new XSSFWorkbook(myxls);
					file_full_path = fullFilePath;
					load_sheet ();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean Open(String inputFile) {
		file_instance = null; 
		xls_instance = null;
		xlsx_instance = null;
		file_full_path = null;

		try {
			File input_file = new File(inputFile);
			if (input_file.exists()) {
				int dot = inputFile.lastIndexOf('.');
				String file_ext = inputFile.substring(dot + 1);
				if (file_ext.toLowerCase().equals("xls")){
					InputStream myxls = new FileInputStream(input_file);
					xls_instance = new HSSFWorkbook (myxls);
					file_full_path = inputFile;
					load_sheet ();
				}
				else {
					InputStream myxls = new FileInputStream(input_file);
					xlsx_instance = new XSSFWorkbook(myxls);
					file_full_path = inputFile;
					load_sheet ();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void SaveAsExcelAndClose(String file_full_path) {
		// create the dirctory
		if (file_full_path != null && file_full_path.length() > 0) {
			// delete old file
			File cur_File = new File(file_full_path);
			if (cur_File.exists())
				cur_File.delete();
			else {
			
				int last_slash = file_full_path.lastIndexOf('\\');
				if (last_slash < file_full_path.lastIndexOf('/'))
					last_slash = file_full_path.lastIndexOf('/');
				if (last_slash > 0) {
					String parent_dir = file_full_path.substring(0, last_slash);
					// create parentDir
					File dir = new File (parent_dir);
					dir.mkdirs();
				}
				else
					return;
			}
		}
		else
			return;
		
		// delete old file
		
		
		if (xls_instance != null) {
			FileOutputStream output_file;
			try {
				output_file = new FileOutputStream(file_full_path);
				xls_instance.write(output_file);
				output_file.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public sheetPOI RenameSheet(int sheet_index, String new_sheet_name) {
		if (sheets != null && sheets.size() > 0 && sheet_index < sheets.size()){
			sheets.get(sheet_index).rename(new_sheet_name);
			return sheets.get(sheet_index);
		}
		return null;
	}
	public sheetPOI RenameSheet(String old_sheet_name, String new_sheet_name) {
		if (sheets != null && sheets.size() > 0)
			for (int index = 0; index < sheets.size(); index++)
				if (sheets.get(index).get_Name() == old_sheet_name) {
					sheets.get(index).rename(new_sheet_name);
					return sheets.get(index);
				}
		return null;
	}

	public void Close() {
		// TODO Auto-generated method stub
		
	}

	public void Reload() {
		if (xls_instance != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				xls_instance.write(baos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				xls_instance = new HSSFWorkbook(bais);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				xls_instance = null;
			}
		}
	}
	
	public void SaveAndClose() {
		if (xls_instance != null) {
			FileOutputStream output_file;
			try {
				output_file = new FileOutputStream(file_full_path);
				xls_instance.write(output_file);
				output_file.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (xlsx_instance != null) {
			
		}
	}

	public ArrayList<sheetPOI> get_sheets() {return sheets;}

	boolean create_first_sheet = false;
	public sheetPOI create_sheet(String sheet_name) {
		return null;
	}

	public void remove_all_sheet() {
		// TODO Auto-generated method stub
		
	}

	public sheetPOI duplicate_sheet(String name_parent, String name_sheet_copy) {
		if (xls_instance != null) {
			HSSFSheet new_sheet = null;
			HSSFSheet src_sheet = xls_instance.getSheet(name_parent);
			if (src_sheet != null) {
				new_sheet = xls_instance.cloneSheet(xls_instance.getSheetIndex(name_parent));
				xls_instance.setSheetName(xls_instance.getSheetIndex(new_sheet), name_sheet_copy);
			}
			else
				new_sheet = xls_instance.createSheet(name_sheet_copy);
			sheetPOI new_wsheet = new sheetPOI(xls_instance, new_sheet, name_sheet_copy);
			sheets.add(new_wsheet);
			return new_wsheet;
		}
		if (xlsx_instance != null) {
			XSSFSheet new_sheet = xlsx_instance.createSheet(name_sheet_copy);
			XSSFSheet src_sheet = xlsx_instance.getSheet(name_parent);
			if (src_sheet != null) {
				new_sheet = xlsx_instance.cloneSheet(xlsx_instance.getSheetIndex(name_parent));
			}
			sheetPOI new_wsheet = new sheetPOI(xlsx_instance, new_sheet, name_sheet_copy);
			sheets.add(new_wsheet);
			return new_wsheet;
		}
		return null;
	}
	
	private void load_sheet () {
		sheets.clear();
		if (xls_instance != null) {
			for (int i = 0 ; i < xls_instance.getNumberOfSheets() ; i ++)
				sheets.add(new sheetPOI(xls_instance, xls_instance.getSheetAt(i), xls_instance.getSheetName(i)));
		}
		else if (xlsx_instance != null) {
			for (int i = 0 ; i < xlsx_instance.getNumberOfSheets() ; i ++)
				sheets.add(new sheetPOI(xlsx_instance, xlsx_instance.getSheetAt(i), xlsx_instance.getSheetName(i)));
		}
	}


	public Object create_cell_style (Color charColor, Color backColor, boolean Bold, boolean Italic, boolean enableBorder) {
		if (xls_instance != null) {
			HSSFCellStyle myStyle = xls_instance.createCellStyle();
			HSSFFont font = xls_instance.createFont();
			
			// Set Color
			if (charColor != null) {
				HSSFColor textColor = xls_instance.getCustomPalette().findSimilarColor((byte)charColor.getRed(), (byte)charColor.getGreen(), (byte)charColor.getBlue());
				font.setColor(textColor.getIndex());
			}
			
			// Back Color
			if (backColor != null) {
				HSSFColor backgColor = xls_instance.getCustomPalette().findSimilarColor((byte)backColor.getRed(), (byte)backColor.getGreen(), (byte)backColor.getBlue());
				myStyle.setFillBackgroundColor(backgColor.getIndex());
			}
			
			if (Bold) {
				font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			}
			
			if (Italic) {
				font.setItalic(true);
			}
			
			if (enableBorder) {
				myStyle.setBorderLeft(CellStyle.BORDER_THIN);
				myStyle.setBorderRight(CellStyle.BORDER_THIN);
				myStyle.setBorderTop(CellStyle.BORDER_THIN);
				myStyle.setBorderBottom(CellStyle.BORDER_THIN);
			}
			myStyle.setFont(font);
			return myStyle;
		}
		else if (xlsx_instance != null) {
			XSSFCellStyle myStyle = xlsx_instance.createCellStyle();
			XSSFFont font = xlsx_instance.createFont();

			// Set Color
			if (charColor != null)
				font.setColor(new XSSFColor(charColor));
			
			// Back Color
			if (backColor != null)
				myStyle.setFillBackgroundColor(new XSSFColor(backColor));

			if (Bold)
				font.setBold(true);
			
			if (Italic)
				font.setItalic(true);

			
			if (enableBorder) {
				myStyle.setBorderLeft(CellStyle.BORDER_THIN);
				myStyle.setBorderRight(CellStyle.BORDER_THIN);
				myStyle.setBorderTop(CellStyle.BORDER_THIN);
				myStyle.setBorderBottom(CellStyle.BORDER_THIN);
			}
			myStyle.setFont(font);
			return myStyle;
		}
		return null;
	}
}
