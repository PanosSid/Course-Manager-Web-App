package com.myy803.course_mgt_app.unit;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.StudentRegistrationServiceImpl;


@ExtendWith(MockitoExtension.class)
public class UnitTestStudentRegistrationService {
		
	@InjectMocks 
	private StudentRegistrationServiceImpl studRegService;
	
	@Mock
	private StudentRegistrationDAO studRegDAO;
	
	@Test
	void testStudentRegistrationServiceImplIsNotNull() {
		Assertions.assertNotNull(studRegService);
	}
	
	@Test
	void testfindStudentRegistrationByStudentIdReturndStudReg() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(11)).thenReturn(newStudent);
		StudentRegistration storedStudReg = studRegService.findStudentRegistrationByStudentId(11);
		Assertions.assertNotNull(storedStudReg);
		Assertions.assertEquals(newStudent, storedStudReg);
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
		Assertions.assertEquals(student1, storedStudRegs.get(0));
		Assertions.assertEquals(student2, storedStudRegs.get(1));
		Assertions.assertEquals(student3, storedStudRegs.get(2));
	}
	
	@Test
	void testSave() {
		StudentRegistration newStudent = new StudentRegistration (11, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1, 2);
		Mockito.when(studRegDAO.save(newStudent)).thenReturn(newStudent);
		StudentRegistration savedStudent = studRegService.save(newStudent);
		Mockito.verify(studRegDAO, Mockito.times(1)).save(savedStudent);
	}
	
	@Test
	void testDelete() {
		StudentRegistration newStudent = new StudentRegistration (159, 1111, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1.0, 2.0);
		studRegService.delete(newStudent);
		Mockito.verify(studRegDAO, Mockito.times(1)).delete(newStudent);
	}
	
	@Test 
	void testDeleteByStudentId() {
		StudentRegistration newStudent = new StudentRegistration (159, 1111, "StudTmp1", "StudSurname", 2000,"1","1","TTT-000", 1.0, 2.0);
		Mockito.when(studRegDAO.findStudentRegistrationByStudentId(1111)).thenReturn(newStudent);	
		studRegService.deleteByStudentId(1111);
		Mockito.verify(studRegDAO, Mockito.times(1)).findStudentRegistrationByStudentId(1111);
		Mockito.verify(studRegDAO, Mockito.times(1)).delete(newStudent);;
	}
	
	//TODO add test for saveStudentRegsFromFile()
	
}
