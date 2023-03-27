package com.myy803.course_mgt_app.integration;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
public class IntegrationTestStudentRegDAO {

	@Autowired 
	private StudentRegistrationDAO studRegDao;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private StudentRegistration testStudReg = new StudentRegistration(11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-111", 1, 2);
	
	
	@BeforeAll
	static void setUpDBwithTestCourseForForeignKeyConstrain(@Autowired CourseDAO courseDao) {
		if (courseDao.findCourseByCourseId("MCK-111") == null) {
			courseDao.save(new Course("MCK-111", "instructor_tester2", "TmpCourse", "1st", 1, "..."));			
		}
	}
	
	@BeforeEach
	void setUpDBwithTestStudReg() {
		entityManager.persist(testStudReg);
	}
	
	@Test
	void testfindStudentRegistrationById() {
		entityManager.persist(testStudReg);
		StudentRegistration storedStudReg = studRegDao.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(testStudReg, storedStudReg);
	}
	
	@Test
	void testfindStudentRegistrationByCourseId() {
		StudentRegistration testStudReg2 = new StudentRegistration(22, "StudTmp2", "StudSurname2", 2000,"1","1","MCK-111", 1, 2);
		entityManager.persist(testStudReg);
		entityManager.persist(testStudReg2);
		List<StudentRegistration> storedStudRegs = studRegDao.findStudentRegistrationsByCourseId("MCK-111");
		Assertions.assertEquals(testStudReg, storedStudRegs.get(0));
		Assertions.assertEquals(testStudReg2, storedStudRegs.get(1));
	}
	
	@Test
	void testSave() {
		StudentRegistration savedStudent = studRegDao.save(testStudReg);
		Assertions.assertNotNull(savedStudent);
		Assertions.assertEquals(testStudReg, savedStudent);
	}
	
	@Test
	void testDelete() {
		entityManager.persist(testStudReg);
		studRegDao.delete(testStudReg);	// clean db from tmp course
		StudentRegistration savedStudent = studRegDao.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	@AfterAll
	static void teadDownDBwithTestCourse(@Autowired CourseDAO courseDao) {
		courseDao.delete(courseDao.findCourseByCourseId("MCK-111"));
	}

}
