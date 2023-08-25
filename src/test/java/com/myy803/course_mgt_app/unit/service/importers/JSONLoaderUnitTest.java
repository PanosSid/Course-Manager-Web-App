package com.myy803.course_mgt_app.unit.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.service.importers.JSONLoader;

public class JSONLoaderUnitTest {
	
	private JSONLoader jsonLoader = new JSONLoader();
	
	@Test
	void getDataFromFile_JSON() throws IOException {
		String fileContents = ""
				+ "[\r\n"
				+ "    {\r\n"
				+ "        \"CourseId\": \"MYY-301\",\r\n"
				+ "        \"Name\": \"Software Development I\",\r\n"
				+ "        \"InstructorLogin\": \"panos\",\r\n"
				+ "        \"Semester\": \"Spring\",\r\n"
				+ "        \"Year\": 3,\r\n"
				+ "        \"Syllabus\": \"Software development basics\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "        \"CourseId\": \"PLH-010\",\r\n"
				+ "        \"Name\": \"Advanced Databases\",\r\n"
				+ "        \"InstructorLogin\": \"panos\",\r\n"
				+ "        \"Semester\": \"Fall\",\r\n"
				+ "        \"Year\": 4,\r\n"
				+ "        \"Syllabus\": \"Advanced DB and more\"\r\n"
				+ "    }\r\n"
				+ "]\r\n"
				+ "";
		
		MultipartFile file =  new MockMultipartFile("courses_upload", "courses_upload.json", "json", fileContents.getBytes());
		Assertions.assertEquals(FileLoaderUnitHelper.getExpectedData(), jsonLoader.getDataFromFile(file));
	}
}
