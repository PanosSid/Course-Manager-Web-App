package com.myy803.course_mgt_app.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.*;


@ExtendWith(SpringExtension.class)
public class UnitTestStudentRegistrationService {
	
	@TestConfiguration
    static class StudentRegistrationServiceImplTestContextConfiguration {
 
        @Bean
        public StudentRegistrationService CourseService() {
            return new StudentRegistrationServiceImpl();
        }
    }
	
	@Autowired 
	StudentRegistrationService studRegService;
	
	@MockBean
	StudentRegistrationDAO studRegDAO;
	
	@Test
	void testStudentRegistrationServiceImplIsNotNull() {
		Assertions.assertNotNull(studRegService);
	}
	
	@Test
	void testSave() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		Mockito.when(studRegDAO.save(newStudent)).thenReturn(newStudent);
		StudentRegistration savedStudent = studRegService.save(newStudent);
		Assertions.assertNotNull(savedStudent);
		Assertions.assertEquals(savedStudent, newStudent);

	}
	
	@Test
	void testDelete() {
		StudentRegistration newStudent = new StudentRegistration (159, 1111, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1.0, 2.0);
		Mockito.when(studRegDAO.findById(159)).thenReturn(null);	// this method is called inside delete mocks the real behaviour of the db
		studRegService.delete(newStudent);
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(1111);
		Assertions.assertNull(savedStudent);
	}
	
	@Test
	void testfindStudentRegistrationByStudentIdReturndStudReg() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(11)).thenReturn(newStudent);
		StudentRegistration storedStudReg = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(11, storedStudReg.getStudentId());
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
		
		Mockito.when(studRegDAO.findStudentRegistrationByCourseId("TTT-000")).thenReturn(mockList);
		
		List<StudentRegistration> storedStudRegs =  studRegService.findStudentRegistrationsByCourseId("TTT-000");
		Assertions.assertNotNull(storedStudRegs);
		Assertions.assertEquals(3, storedStudRegs.size());
		
		Assertions.assertEquals(11, storedStudRegs.get(0).getStudentId());
		Assertions.assertEquals(22, storedStudRegs.get(1).getStudentId());
		Assertions.assertEquals(33, storedStudRegs.get(2).getStudentId());
	}
	
	@Test 
	void testDeleteByStudentId() {
		//StudentRegistration newStudent = new StudentRegistration (159, 1111, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1.0, 2.0);
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(1111)).thenReturn(null);	
		Mockito.when(studRegDAO.findById(159)).thenReturn(null);	
		
		studRegService.deleteByStudentId(1111);
		StudentRegistration savedStudent = studRegService.findStudentRegistrationByStudentId(1111);
		Assertions.assertNull(savedStudent);
	
	}
}
