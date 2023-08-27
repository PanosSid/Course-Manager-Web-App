package com.myy803.course_mgt_app.unit.service.importers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileLoaderUnitHelper {
	
	public static  List<Map<String, String>> getExpectedData(){
		List<Map<String, String>> expected = new ArrayList<>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("CourseId", "MYY-301");
		map1.put("Name", "Software Development I");
		map1.put("InstructorLogin", "panos");
		map1.put("Semester", "Spring");
		map1.put("Year", "3");
		map1.put("Syllabus", "Software development basics");
		expected.add(map1);
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("CourseId", "PLH-010");
		map2.put("Name", "Advanced Databases");
		map2.put("InstructorLogin", "panos");
		map2.put("Semester", "Fall");
		map2.put("Year", "4");
		map2.put("Syllabus", "Advanced DB and more");
		expected.add(map2);
		return expected;
	}
}
