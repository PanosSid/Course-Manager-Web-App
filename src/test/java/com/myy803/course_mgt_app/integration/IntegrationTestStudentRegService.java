package com.myy803.course_mgt_app.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.StudentRegistrationService;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
public class IntegrationTestStudentRegService {
	
	@Autowired 
	StudentRegistrationService studRegService;
	
	@Test
	void testSave() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
		studRegService.save(newStudent);
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(savedStudent);
		Assertions.assertEquals(savedStudent, newStudent);
		studRegService.delete(savedStudent);	// delete tmp row 
	}
	
	@Test
	void testDelete() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
		studRegService.save(newStudent);
		
		studRegService.delete(newStudent);	
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	@Test
	void testDeleteById() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
		studRegService.save(newStudent );
		studRegService.deleteByStudentId(11);	// clean db from tmp course
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	@Test
	void testfindStudentRegistrationById() {
		StudentRegistration storedStudReg =  studRegService.findStudentRegistrationByStudentId(100);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(100, storedStudReg.getStudentId());
		
		StudentRegistration notStoredStudReg =  studRegService.findStudentRegistrationByStudentId(-1);
		Assertions.assertNull(notStoredStudReg);
	}
	
	@Test
	void testfindStudentRegistrationByCourseId() {
		List<StudentRegistration> storedStudRegs =  studRegService.findStudentRegistrationsByCourseId("MCK-000");
		Assertions.assertNotNull(storedStudRegs);
		
		Assertions.assertEquals(100, storedStudRegs.get(0).getStudentId());
		
	}
	
}
