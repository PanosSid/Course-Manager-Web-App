package com.myy803.course_mgt_app.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.StudentRegistrationServiceImpl;
import com.myy803.course_mgt_app.service.importers.StudentRegistrationImporter;


@ExtendWith(MockitoExtension.class)
public class UnitTestStudentRegistrationService {
		
	@InjectMocks 
	private StudentRegistrationServiceImpl studRegService;
	
	@Mock
	private StudentRegistrationDAO studRegDAO;
	
	@Mock
    private StudentRegistrationImporter studRegImporter;
	
	private	StudentRegistration testStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
	
	@Test
	void testStudentRegistrationServiceImplIsNotNull() {
		Assertions.assertNotNull(studRegService);
	}
	
	@Test
	void testfindStudentRegistrationByStudentIdReturndStudReg() {
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(11)).thenReturn(testStudent);
		StudentRegistration storedStudReg = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(testStudent, storedStudReg);
	}
	
	@Test
	void testfindStudentRegistrationByCourseId() {
		StudentRegistration student1 = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		StudentRegistration student2 = new StudentRegistration (22, "StudTmp2", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		StudentRegistration student3 = new StudentRegistration (33, "StudTmp3", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		
		List<StudentRegistration> mockList = new ArrayList<StudentRegistration>(); 
		mockList.add(student1);
		mockList.add(student2);
		mockList.add(student3);
		
		Mockito.when(studRegDAO.findStudentRegistrationsByCourseId("TTT-000")).thenReturn(mockList);
		
		List<StudentRegistration> storedStudRegs =  studRegService.findStudentRegistrationsByCourseId("TTT-000");
		Assertions.assertNotNull(storedStudRegs);
		Assertions.assertEquals(3, storedStudRegs.size());
		Assertions.assertEquals(student1, storedStudRegs.get(0));
		Assertions.assertEquals(student2, storedStudRegs.get(1));
		Assertions.assertEquals(student3, storedStudRegs.get(2));
	}
	
	@Test
	void testSave() {
		Mockito.when(studRegDAO.save(testStudent)).thenReturn(testStudent);
		StudentRegistration savedStudent = studRegService.save(testStudent);
		Mockito.verify(studRegDAO, Mockito.times(1)).save(savedStudent);
	}
	
	@Test
	void testDelete() {
		studRegService.delete(testStudent);
		Mockito.verify(studRegDAO, Mockito.times(1)).delete(testStudent);
	}
	
	@Test 
	void testDeleteByStudentId() {
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(1111)).thenReturn(testStudent);	
		studRegService.deleteByStudentId(1111);
		Mockito.verify(studRegDAO, Mockito.times(1)).findStudentRegistrationByStudentId(1111);
		Mockito.verify(studRegDAO, Mockito.times(1)).delete(testStudent);;
	}
	
	@Test 
	void testSaveStudentRegsFromFile() throws IOException {
		String fileContents = "AM,First Name,Last Name,Year of Registration,Year of Studies,Semester,Course Id,Project Grade,Exam Grade\r\n"
				+ "3333,Name3,LName3, 2017, 3,5,MYY-301,2.5,3.5\r\n"
				+ "4444,Name4,LName4, 1995, 5,10,MYY-301,8.5,6.5";
		
		MultipartFile file =  new MockMultipartFile("studRegs_upload", "studRegs_upload.csv", "text/csv", fileContents.getBytes());
		List<StudentRegistration> studRegs = new ArrayList<StudentRegistration>();
		studRegs.add(new StudentRegistration(3333, "Name3", "LName3", 2017, "3", "5", "MYY-301", 2.5, 3.5));
		studRegs.add(new StudentRegistration(4444, "Name4", "LName4", 1995, "5", "10", "MYY-301", 8.5, 6.5));
        Mockito.when(studRegImporter.getStudentRegsFromFile(file)).thenReturn(studRegs);
       
        studRegService.saveStudRegsFromFile(file);

        Mockito.verify(studRegImporter).getStudentRegsFromFile(file);        
        Mockito.verify(studRegDAO).saveAll(studRegs);
	}
	
	
}
