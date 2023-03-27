package com.myy803.course_mgt_app.service.importers;

public class FileLoaderFactory {
	
	public static FileLoader createFileLoader(String type) {
		if (type.equals("csv") || type.equals("txt")) {
			return new CSVLoader();
		}
		return null; 	// temp
	}
}
