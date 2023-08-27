package com.myy803.course_mgt_app.service.importers;

public class FileLoaderFactory {
	
	public static FileLoader createFileLoader(String filename) {
		String type = getFileExtension(filename);
		if (type.equals("csv") || type.equals("txt")) {
			return new CSVLoader();
		} else if (type.equals("xlsx") || type.equals("xls")) {
			return new ExcelLoader();
		} else if (type.equals("json")) {
			return new JSONLoader();
		}
		throw new RuntimeException("Unknown file type for file: '"+filename+"'");
	}
	
	private static String getFileExtension(String filename) {
	    int lastDotIndex = filename.lastIndexOf('.');
	    if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
	        return filename.substring(lastDotIndex + 1).toLowerCase();
	    } else {
	    	throw new IllegalArgumentException("Invalid filename: '" + filename + "'");
	    }
	}
}
