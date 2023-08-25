package com.myy803.course_mgt_app.service.importers;

public class FileLoaderFactory {
	
	public static FileLoader createFileLoader(String type) {
		if (type.equals("text/csv") || type.equals("csv") || type.equals("txt")) {
			return new CSVLoader();
		} else if (type.equals("xlsx") || type.equals("xls")) {
			return new ExcelLoader();
		}
		return null; 	// temp
	}
}
