package com.myy803.course_mgt_app.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.GradeType;
import com.myy803.course_mgt_app.service.StudentRegistrationService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class IntegrationTestStudentRegService {
	
	@Autowired 
	private StudentRegistrationService studRegService;
	
	@Autowired 
	private StudentRegistrationDAO studRegDao;
	
	@Autowired
	private CourseDAO courseDao;
	
	private StudentRegistration testStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","MCK-000", 1, 2);
	
	@Test
	void testSave() {
		studRegService.save(testStudent);
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(savedStudent);
		Assertions.assertEquals(savedStudent, testStudent);
		studRegService.delete(savedStudent);	// delete tmp row 
	}
	
	@Test
	void testDelete() {
		studRegService.save(testStudent);
		studRegService.delete(testStudent);	
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	@Test
	void testDeleteById() {
		studRegService.save(testStudent);
		studRegService.deleteByStudentId(11);	// clean db from tmp course
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNull(savedStudent);
	}
	
	@Test
	void testfindStudentRegistrationById() {
		studRegService.save(testStudent);
		StudentRegistration storedStudReg =  studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(testStudent, storedStudReg);
		
		StudentRegistration notStoredStudReg =  studRegService.findStudentRegistrationByStudentId(789);
		Assertions.assertNull(notStoredStudReg);
	}
	
	@Test
	void testFindStudentRegistrationByCourseId() {
		StudentRegistration testStudent2 = new StudentRegistration (441, "StudTmp2", "StudSurname2", 2000,"1","1","MCK-000", 1, 2);
		studRegDao.save(testStudent);
		studRegDao.save(testStudent2);
		List<StudentRegistration> storedStudRegs =  studRegService.findStudentRegistrationsByCourseId("MCK-000");
		Assertions.assertNotNull(storedStudRegs);
		Assertions.assertEquals(testStudent, storedStudRegs.get(0));
		Assertions.assertEquals(testStudent2, storedStudRegs.get(1));
	}
	
	@Test
	void testSaveStudentRegsFile() throws Exception {
		Course courseOfStudents = new Course("tst-007", "panos_tester2", "softdev", "Spring", 3, "Software development basics");
		courseDao.save(courseOfStudents);
		
		String fileContents = "StudentId,FirstName,LastName,RegistrationYear,YearOfStudies,Semester,CourseId,ProjectGrade,ExamGrade\r\n"
				+ "330,Name3,LName3, 2017, 3,5,tst-007,2.5,3.5\r\n"
				+ "440,Name4,LName4, 1995, 5,10,tst-007,8.5,6.5";
		
		MockMultipartFile file =  new MockMultipartFile("file", "tst_myy301_students.csv", "text/csv", fileContents.getBytes());
		       
        studRegService.saveStudRegsFromFile(file);

        List<StudentRegistration> expectedStudRegs = new ArrayList<StudentRegistration>();
        expectedStudRegs.add(new StudentRegistration(330, "Name3", "LName3", 2017, "3", "5", "tst-007", 2.5, 3.5));
        expectedStudRegs.add(new StudentRegistration(440, "Name4", "LName4", 1995, "5", "10", "tst-007", 8.5, 6.5));

        List<StudentRegistration> actualStudRegs = studRegDao.findStudentRegistrationsByCourseId("tst-007");
        Assertions.assertEquals(expectedStudRegs, actualStudRegs);
	}
	
	@Test
	void testfindGradesByTypeAndCourse() {
		StudentRegistration testStudent2 = new StudentRegistration(22, "StudTmp2", "StudSurname2", 2000,"1","1","MCK-111", 4, 5);
		StudentRegistration testStudent3 = new StudentRegistration(33, "StudTmp3", "StudSurname3", 2000,"1","1","MCK-111", 9, 10);
		studRegDao.save(testStudent2);
		studRegDao.save(testStudent3);
		
		List<Double> expPrjGrades = new ArrayList<Double>();
		expPrjGrades.add(4.0); expPrjGrades.add(9.0);
		
		List<Double> expExamGrades = new ArrayList<Double>();
		expExamGrades.add(5.0); expExamGrades.add(10.0);
		
		List<Double> expFinalGrades = new ArrayList<Double>();
		expFinalGrades.add(4.5); expFinalGrades.add(9.5);
		
		Assertions.assertEquals(expPrjGrades, studRegService.findGradesByTypeAndCourse(GradeType.Project, "MCK-111") );
		Assertions.assertEquals(expExamGrades, studRegService.findGradesByTypeAndCourse(GradeType.Exam, "MCK-111") );
		Assertions.assertEquals(expFinalGrades, studRegService.findGradesByTypeAndCourse(GradeType.Final, "MCK-111") );
		
		studRegDao.delete(testStudent2);
		studRegDao.delete(testStudent3);

	}
}
