package com.myy803.course_mgt_app.unit.service.importers;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.service.importers.CSVLoader;

public class CSVLoaderUnit {
	
	private CSVLoader csvLoader = new CSVLoader();
	
	@Test
	void testGetDataFromFile_CSV() throws IOException {
		String fileContents = ""
				+ "CourseId,Name,InstructorLogin,Semester,Year,Syllabus\r\n"
				+ "MYY-301,Software Development I, panos, Spring,3, Software development basics\r\n"
				+ "PLH-010,Advanced Databases,panos,Fall,4,Advanced DB and more";
		MultipartFile file =  new MockMultipartFile("courses_upload", "courses_upload.json", "json", fileContents.getBytes());
		Assertions.assertEquals(FileLoaderUnitHelper.getExpectedData(), csvLoader.getDataFromFile(file));
	}	
}
