package com.myy803.course_mgt_app.unit.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.service.importers.CourseImporter;

public class CourseImporterUnit {
	
	private CourseImporter importer = new CourseImporter();
	
	@Test 
	public void getCoursesFromCSVFile() throws IOException {
		String fileContents = ""
				+ "CourseId,Name,InstructorLogin,Semester,Year,Syllabus\r\n"
				+ "MYY-301,Software Development I, panos, Spring,3, Software development basics\r\n"
				+ "PLH-010,Advanced Databases,panos,Fall,4,Advanced DB and more";
		
		MultipartFile mockFile = new MockMultipartFile("testFile.csv", "testFile.csv", "csv", fileContents.getBytes());
				
		List<Course> expected = new ArrayList<Course>();
		expected.add(new Course("MYY-301", "panos", "Software Development I", "Spring", 3, "Software development basics"));
		expected.add(new Course("PLH-010", "panos", "Advanced Databases", "Fall", 4, "Advanced DB and more"));
		
		Assertions.assertEquals(expected, importer.getCoursesFromFile(mockFile));
	}
	
}
