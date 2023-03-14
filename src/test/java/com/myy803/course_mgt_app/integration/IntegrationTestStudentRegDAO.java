package com.myy803.course_mgt_app.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
public class IntegrationTestStudentRegDAO {

	@Autowired 
	StudentRegistrationDAO studRegDao;
	
	
	@Test
	void testfindStudentRegistrationById() {
		StudentRegistration storedStudReg =  studRegDao.findStudentRegistrationByStudentId(100);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(100, storedStudReg.getStudentId());
	}
	
	@Test
	void testfindStudentRegistrationByCourseId() {
		List<StudentRegistration> storedStudRegs =  studRegDao.findStudentRegistrationByCourseId("MCK-000");
		Assertions.assertNotNull(storedStudRegs);
		Assertions.assertEquals(1, storedStudRegs.size());
		Assertions.assertEquals(100, storedStudRegs.get(0).getStudentId());
	}
	
	
	@Test
	void testSave() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
		studRegDao.save(newStudent);
		StudentRegistration savedStudent =  studRegDao.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(savedStudent);
		Assertions.assertEquals(savedStudent, newStudent);
		studRegDao.delete(savedStudent);	// delete tmp row 
	}
	
	@Test
	void testDelete() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
		studRegDao.save(newStudent );
		studRegDao.delete(newStudent);	// clean db from tmp course
		StudentRegistration savedStudent = studRegDao.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	
	
}
