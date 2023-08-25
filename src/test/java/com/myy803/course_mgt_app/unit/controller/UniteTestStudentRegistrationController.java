package com.myy803.course_mgt_app.unit.controller;

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
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.controller.StudentRegistrationController;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseService;
import com.myy803.course_mgt_app.service.StudentRegistrationService;

@ExtendWith(MockitoExtension.class)
public class UniteTestStudentRegistrationController {
	
	@Mock
	private CourseService courseService;

	@Mock
	private StudentRegistrationService studRegService;
	
	@Mock
	private Model model;
	
	@InjectMocks
	private StudentRegistrationController controller;

	protected static String INSTRUCTOR_LOGIN = "panos_tester";
	
	@Test
	void testControllerIsNotNull() {
		Assertions.assertNotNull(controller); 	// sanity check
	}
	
	@Test
	void testDeleteStudRegRedirectsToStudRegList() throws Exception {
		StudentRegistration mckStud = new StudentRegistration();
		mckStud.setCourseId("MCK-000");
		Mockito.when(studRegService.findStudentRegistrationByStudentId(1111)).thenReturn(mckStud);
		String actualRedirectUrl = controller.deleteStudentRegistration(1111);
		Assertions.assertEquals("redirect:/courses/showStudentRegListOfCourse?courseId=MCK-000", actualRedirectUrl);
	}

	@Test
	void testShowFormForAddStudRegReturnsPage() throws Exception {
		StudentRegistration modelStudReg = new StudentRegistration();
		modelStudReg.setCourseId("MCK-000");
		controller.showFormForAddStudentRegistration(model, "MCK-000");
		Mockito.verify(model).addAttribute("studentReg", modelStudReg);
		Mockito.verify(model).addAttribute("courseId", "MCK-000");
	}

	@Test
	void testShowFormForUpdateStudRegReturnsPage() throws Exception {
		StudentRegistration mckStud = new StudentRegistration();
		mckStud.setCourseId("MCK-000");
		Mockito.when(studRegService.findStudentRegistrationByStudentId(1111)).thenReturn(mckStud);
		String actualTemplate = controller.showFormForUpdateStudentReg(model, 1111);
		Assertions.assertEquals("/studentRegistration/studentReg-form", actualTemplate);	
		Mockito.verify(model).addAttribute("studentReg", mckStud);
		Mockito.verify(model).addAttribute("courseId", "MCK-000");
	}

	@Test
	void testSaveStudentRegistration() throws Exception {
		StudentRegistration tmpStudent = new StudentRegistration(44, "Tmp", "TmpStud", 2018, "2nd", "8th", "MCK-000",
				7.0, 6.0);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("studentId", Integer.toString(tmpStudent.getStudentId()));
		multiValueMap.add("firstName", tmpStudent.getFirstName());
		multiValueMap.add("LastName", tmpStudent.getLastName());
		multiValueMap.add("yearOfStudies", tmpStudent.getYearOfStudies());
		multiValueMap.add("semester", tmpStudent.getSemester());
		multiValueMap.add("courseId", tmpStudent.getCourseId());
		multiValueMap.add("yearOfRegistration", Integer.toString(tmpStudent.getYearOfRegistration()));
		multiValueMap.add("projectGrade", Double.toString(tmpStudent.getProjectGrade()));
		multiValueMap.add("examGrade", Double.toString(tmpStudent.getExamGrade()));

		Mockito.when(studRegService.save(tmpStudent)).thenReturn(tmpStudent);
		BindingResult result = new BeanPropertyBindingResult(tmpStudent, "studReg");

		String actualRedirectFile = controller.saveStudentRegistration(tmpStudent, result, model);
		Assertions.assertEquals("redirect:/courses/showStudentRegListOfCourse?courseId=MCK-000", actualRedirectFile);
	}
	
	@Test
	void testShowStudentRegListOfCourseReturnsPage() throws Exception {
		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
		studRegList.add(new StudentRegistration(1999, "StudMock1", "", 1, "1", "1", "MCK-000", 1, 2));
		studRegList.add(new StudentRegistration(1998, "StudMock2", "", 1, "1", "1", "MCK-000", 1, 2));
		studRegList.add(new StudentRegistration(1997, "StudMock3", "", 1, "1", "1", "MCK-000", 1, 2));
		
		Mockito.when(studRegService.findStudentRegistrationsByCourseId("MCK-000")).thenReturn(studRegList);

		String actualStudRegListFile = controller.showStudentRegListOfCourse("MCK-000", model);
		Assertions.assertEquals("/studentRegistration/list-studentRegistrations", actualStudRegListFile);
		Mockito.verify(model).addAttribute("courseId", "MCK-000");
		Mockito.verify(model).addAttribute("studRegList", studRegList);
	}
	
	@Test 
	void testUploadStudentRegFile() throws IOException {
		MultipartFile fileMock = Mockito.mock(MultipartFile.class);
		String redirectedView = controller.uploadStudentRegFile(fileMock, "MCK-000");
		Assertions.assertEquals("redirect:/courses/showStudentRegListOfCourse?courseId=MCK-000", redirectedView);
		Mockito.verify(studRegService, Mockito.times(1)).saveStudRegsFromFile(fileMock);
	}
}
