package com.myy803.course_mgt_app.unit.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.importers.StudentRegistrationImporter;

public class StudentRegistrationImporterUnit {
	
	private StudentRegistrationImporter studImporter = new StudentRegistrationImporter();
	
	@Test 
	public void getStudentRegistrationsFromCSVFile() throws IOException {
		String fileContents = "StudentId,FirstName,LastName,RegistrationYear,YearOfStudies,Semester,CourseId,ProjectGrade,ExamGrade\r\n"
				+ "330,Name3,LName3, 2017, 3,5,tst-007,2.5,3.5\r\n"
				+ "440,Name4,LName4, 1995, 5,10,tst-007,8.5,6.5";
		
		MultipartFile mockFile = new MockMultipartFile("testFile.csv", null, "text/csv", fileContents.getBytes());
				
        List<StudentRegistration> expectedStudRegs = new ArrayList<StudentRegistration>();
        expectedStudRegs.add(new StudentRegistration(330, "Name3", "LName3", 2017, "3", "5", "tst-007", 2.5, 3.5));
        expectedStudRegs.add(new StudentRegistration(440, "Name4", "LName4", 1995, "5", "10", "tst-007", 8.5, 6.5));
		
		Assertions.assertEquals(expectedStudRegs, studImporter.getStudentRegsFromFile(mockFile));
	}
}
