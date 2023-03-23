package com.myy803.course_mgt_app.unit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseService;
import com.myy803.course_mgt_app.service.StudentRegistrationService;
import com.myy803.course_mgt_app.controller.CourseMgtAppController;




@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class UnitTestController {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	CourseService courseService;
	
	@MockBean
	StudentRegistrationService  studRegService;
	
	@Autowired
	CourseMgtAppController controller;

	@BeforeEach
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .build();
    }
	
	@Test
	void testControllerIsNotNull() {
		Assertions.assertNotNull(controller);
	}
	
	@Test
	void testMockMvcIsNotNull() {
		Assertions.assertNotNull(mockMvc);
	}
	
	
	@BeforeEach
	void setupDB() {
		
	}
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testListCouresesReturnsPageWithCoursesList() throws Exception {
		
		Course newCourse1 = new Course(1,"TMP-123", "panos_tester", "TmpCourse1", "1st", 1, "...");
		Course newCourse2 = new Course(2,"TMP-456", "panos_tester", "TmpCourse2", "1st", 1, "...");
		Course newCourse3 = new Course(3,"TMP-789", "panos_tester", "TmpCourse3", "1st", 1, "...");
		
		List<Course> mockedList = new ArrayList<Course>();
		mockedList.add(newCourse1);
		mockedList.add(newCourse2);
		mockedList.add(newCourse3);
		
		Mockito.when(courseService.findCoursesByInstructorLogin("panos_tester")).thenReturn(mockedList);
		
		mockMvc.perform(get("/courses/list")).
		andExpect(status().isOk()).
		andExpect(view().name("courses/list-courses")).
		andExpect(model().attribute("instructorLogin", "panos_tester")).
		andExpect(model().attribute("coursesList", mockedList));
	}

	@WithMockUser(value = "panos_tester")
	@Test 
	void testDeleteCourseRedirectsToCoursesList() throws Exception {
		mockMvc.perform(delete("/courses/deleteCourse?courseId=MCK-000")).
		andExpect(status().is(302)).	// 302 is status code for redirection
		andExpect(redirectedUrl("/courses/list"));		
	}
	
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testShowFormForAddCourseReturnsPage() throws Exception {
		
		mockMvc.perform(get("/courses/showFormForAddCourse?instructorLogin=panos_tester")).
		andExpect(status().isOk()).
		andExpect(model().attribute("instructorLogin", "panos_tester")).
		andExpect(model().attribute("course", new Course())).
		andExpect(view().name("courses/course-form"));	
	}
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testShowFormForUpdateCourseReturnsPage() throws Exception {
		
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);
		
		mockMvc.perform(get("/courses/showFormForUpdateCourse?courseId=MCK-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("course", course)).
		andExpect(model().attribute("instructorLogin", "panos_tester")).
		andExpect(view().name("courses/course-form"));		
	}
	
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testShowStudentRegListOfCourseReturnsPage() throws Exception {
		List<StudentRegistration> studReg_list = new ArrayList<StudentRegistration>();
		StudentRegistration student1 = new StudentRegistration (1999, "StudMock1", "",1,"1","1","MCK-000", 1, 2);
		StudentRegistration student2 = new StudentRegistration (1998, "StudMock2", "",1,"1","1","MCK-000", 1, 2);
		StudentRegistration student3 = new StudentRegistration (1997, "StudMock3", "",1,"1","1","MCK-000", 1, 2);
		studReg_list.add(student1);
		studReg_list.add(student2);
		studReg_list.add(student3);
		
		Mockito.when(studRegService.findStudentRegistrationsByCourseId("MCK-000")).thenReturn(studReg_list);
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "..."));
		
		mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=MCK-000")).
		andExpect(status().isOk()).
		andExpect(view().name("/studentRegistration/list-studentRegistrations"));
		
	}
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testSaveCourse() throws Exception {	
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
	    
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
	    multiValueMap.add("courseId", course.getCourseId());
	    multiValueMap.add("instructorLogin", course.getInstructorLogin() );
	    multiValueMap.add("name", course.getName());
	    multiValueMap.add("semester", course.getSemester());
	    multiValueMap.add("year", Integer.toString(course.getYear()) );
	    multiValueMap.add("syllabus", course.getSyllabus());
	    
	    Mockito.when(courseService.save(course)).thenReturn(course);
	    
	    mockMvc.perform(
				post("/courses/save")
			    .params(multiValueMap))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/courses/list"));	
	    
	}
	
	@WithMockUser(value = "panos_tester")
	@Test 
	void testShowStatisticsOfCourse() throws Exception {
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);
		
		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
		Map<String, List<Double>> map = new HashMap<String, List<Double>>();
		Mockito.when(courseService.getCourseStatistics(studRegList)).thenReturn(map);
		
		mockMvc.perform(get("/courses/showStatisticsOfCourse?courseId=MCK-000")).
		andExpect(status().isOk()).
		andExpect(view().name("/courses/course-statistics"));		
	}
	
	@Test 
	void testDeleteStudRegRedirectsToStudRegList() throws Exception {
		StudentRegistration mckStud = new StudentRegistration();
		mckStud.setCourseId("MCK-000");
		Mockito.when(studRegService.findStudentRegistrationByStudentId(1111)).thenReturn(mckStud);
		
		mockMvc.perform(delete("/studentRegistrations/deleteStudentReg?studentRegId=1111")).
		andExpect(status().is(302)).	// 302 is status code for redirection
		andExpect(redirectedUrl("/courses/showStudentRegListOfCourse?courseId=MCK-000"));	
		
	}
	
	@Test 
	void testShowFormForAddStudRegReturnsPage() throws Exception {
		StudentRegistration modelStudReg = new StudentRegistration();
		modelStudReg.setCourseId("MCK-000");
		mockMvc.perform(get("/studentRegistrations/showFormForAddStudentReg?courseId=MCK-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studentReg", modelStudReg)).
		andExpect(view().name("/studentRegistration/studentReg-form"));	
	}
	
	@Test 
	void testShowFormForUpdateStudRegReturnsPage() throws Exception {
		
		StudentRegistration mckStud = new StudentRegistration();
		mckStud.setCourseId("MCK-000");
		Mockito.when(studRegService.findStudentRegistrationByStudentId(1111)).thenReturn(mckStud);
		
		mockMvc.perform(get("/studentRegistrations/showFormForUpdateStudentReg?studentRegId=1111")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studentReg", mckStud)).
		andExpect(model().attribute("courseId", "MCK-000")).
		andExpect(view().name("/studentRegistration/studentReg-form"));		
	}
	
	@Test 
	void testSaveStudentRegistration() throws Exception {	
		
		StudentRegistration tmpStudent = new StudentRegistration (44, "Tmp", "TmpStud", 2018,"2nd","8th","MCK-000", 7.0, 6.0);
	    
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
	    multiValueMap.add("studentId", Integer.toString(tmpStudent.getStudentId()));
	    multiValueMap.add("firstName", tmpStudent.getFirstName());
	    multiValueMap.add("LastName", tmpStudent.getLastName());
	    multiValueMap.add("yearOfStudies",tmpStudent.getYearOfStudies());
	    multiValueMap.add("semester"	, tmpStudent.getSemester());
	    multiValueMap.add("courseId"	, tmpStudent.getCourseId());
	    multiValueMap.add("yearOfRegistration"	,Integer.toString(tmpStudent.getYearOfRegistration()));
	    multiValueMap.add("projectGrade"	,Double.toString(tmpStudent.getProjectGrade()));
	    multiValueMap.add("examGrade"	,Double.toString(tmpStudent.getExamGrade()));
	    
	    Mockito.when(studRegService.save(tmpStudent)).thenReturn(tmpStudent);
	    
	    mockMvc.perform(
				post("/studentRegistrations/save")
			    .params(multiValueMap))
	    		.andExpect(status().isFound())
				.andExpect(redirectedUrl("/courses/showStudentRegListOfCourse?courseId=MCK-000"));	
	    
	}
	
}
