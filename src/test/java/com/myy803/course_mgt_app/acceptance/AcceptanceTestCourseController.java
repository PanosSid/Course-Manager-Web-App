package com.myy803.course_mgt_app.acceptance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FlashMap;

import com.myy803.course_mgt_app.controller.CourseController;
import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
public class AcceptanceTestCourseController {
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CourseDAO courseDao;
	
	@Autowired
	private StudentRegistrationDAO studRegDao;
	
	@Autowired
	private CourseController controller;
	
	private Course course = new Course("TTT-000", "instructor_tester", "CourseTest", "2nd", 1, "This is a dummy course used only for testing");
	
	@BeforeEach 
    public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		courseDao.save(course);
    }
	
	@Test
	void testControllerIsNotNull() {
		Assertions.assertNotNull(controller);
	}
	
	@Test
	void testMockMvcIsNotNull() {
		Assertions.assertNotNull(mockMvc);
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS2BrowseMyListOfCourses() throws Exception {
		List<Course> coursesList = new ArrayList<Course>();	
		coursesList.add(course);
		mockMvc.perform(get("/courses/list?instructorLogin=instructor_tester")).
		andExpect(status().isOk()).
		andExpect(model().attribute("instructorLogin", "instructor_tester")).
		andExpect(model().attribute("coursesList", coursesList)).	// checks if the model attribute "coursesList" matches coursesList  
		andExpect(view().name("courses/list-courses"));
	}
	
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS3AddCourseToList() throws Exception {
		mockMvc.perform(get("/courses/showFormForAddCourse?instructorLogin=instructor_tester")).
		andExpect(status().isOk()).
		andExpect(model().attribute("instructorLogin", "instructor_tester")).
		andExpect(model().attribute("course", new Course())).
		andExpect(view().name("courses/course-form"));	
		
		// simulate instructor_tester courses list 
		List<Course> coursesList = new ArrayList<Course>();
		coursesList.add(course);	
		
		Course newCourse = new Course(58,"TTT-111", "instructor_tester", "CourseTest2", "2nd", 1, "This is a dummy course used only for testing");
		coursesList.add(newCourse);
		
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
	    multiValueMap.add("courseId", newCourse .getCourseId());
	    multiValueMap.add("instructorLogin", newCourse .getInstructorLogin() );
	    multiValueMap.add("name", newCourse .getName());
	    multiValueMap.add("semester", newCourse .getSemester());
	    multiValueMap.add("year", Integer.toString(newCourse .getYear()) );
	    multiValueMap.add("syllabus", newCourse .getSyllabus());
	    mockMvc.perform(
				post("/courses/save")
			    .params(multiValueMap))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/courses/list"));	
	   
	    Assertions.assertEquals(coursesList, courseDao.findCoursesByInstructorLogin("instructor_tester"));   
	}
		
	@WithMockUser(value = "instructor_tester2")
	@Test 
	void testUS5UpdateCourseFields() throws Exception{
		// simulates the course from the db 
		Course courseFromDb = new Course("MCK-000", "instructor_tester2", "Tes5tCo(urse", "4", 2, "Used for testing (fix typos)");
		courseDao.save(courseFromDb);
		MvcResult result  = mockMvc.perform(get("/courses/showFormForUpdateCourse?courseId=MCK-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("course", courseFromDb)).
		andExpect(model().attribute("instructorLogin", "instructor_tester2")).
		andExpect(view().name("courses/course-form")).
		andReturn();
		
		Course courseFromForm = (Course) result.getModelAndView().getModel().get("course");
		
		// make some changes to some fields
		courseFromDb.setCourseId("MCK-111");
		courseFromDb.setName("TestCourse");
		courseFromDb.setYear(5);
		courseFromDb.setSyllabus("Updated test course !!!");
		
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("dbKey", Integer.toString(courseFromForm.getDbKey()));
	    multiValueMap.add("courseId", courseFromDb.getCourseId());
	    multiValueMap.add("instructorLogin", courseFromDb.getInstructorLogin() );
	    multiValueMap.add("name", courseFromDb.getName());
	    multiValueMap.add("semester", courseFromDb.getSemester());
	    multiValueMap.add("year", Integer.toString(courseFromDb.getYear()) );
	    multiValueMap.add("syllabus", courseFromDb.getSyllabus());
		
		mockMvc.perform(post("/courses/save").
		params(multiValueMap)).
		andExpect(status().isFound()).	
		andExpect(redirectedUrl("/courses/list"));	
			
	    Course actualCourse = courseDao.findCourseByCourseId("MCK-111");
	    Assertions.assertEquals(courseFromDb, actualCourse);
		
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS4RemoveCourseFromList() throws Exception{
		// delete the only course of the instructor_tester
		mockMvc.perform(delete("/courses/deleteCourse?courseId=TTT-000")).	
		andExpect(status().isFound()).
		andExpect(redirectedUrl("/courses/list"));	
		
		Assertions.assertEquals(new ArrayList<Course>(), courseDao.findCoursesByInstructorLogin("instructor_tester"));   
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS6BrowseMyListOfStudentRegs() throws Exception {
		MvcResult result = mockMvc.perform(get("/courses/showStudentRegListOfCourse")
		        .param("courseId", "TTT-000"))
		        .andExpect(status().is3xxRedirection())
		        .andExpect(redirectedUrl("/studentRegistrations?courseId=" + "TTT-000"))
		        .andReturn();
		FlashMap flashAttributes =  result.getFlashMap();
	    Assertions.assertEquals("TTT-000 CourseTest", flashAttributes.get("studRegTitle"));
	}
	
	@Test 
	void testUS10CalculateStatsOfStudentGrades() throws Exception {		
		studRegDao.save(new StudentRegistration (11, "TopStud", "StudSurnam1e", 2018,"4th","8th","TTT-000", 10, 9.5));
		studRegDao.save(new StudentRegistration (22, "AverageStud", "StudSurname2", 2017,"5th","9th","TTT-000", 6, 4));
		studRegDao.save(new StudentRegistration (33, "BadStud", "StudSurname3", 2013,"5th","10th","TTT-000", 2, 1.5));
		
		MvcResult result = mockMvc.perform(get("/courses/showStatisticsOfCourse?courseId=TTT-000")).
	    andExpect(status().isOk()).
	    andExpect(view().name("/courses/course-statistics")).
	    andReturn();
		
		@SuppressWarnings("unchecked")
		Map<String, Double> projectMap = (Map<String, Double>) result.getModelAndView().getModel().get("projectMap");
		@SuppressWarnings("unchecked")
		Map<String, Double> examMap = (Map<String, Double>) result.getModelAndView().getModel().get("examMap");
		@SuppressWarnings("unchecked")
		Map<String, Double> finalMap = (Map<String, Double>) result.getModelAndView().getModel().get("finalMap");
		
		String statNames[] = {"Min", "Max", "Mean", "StandardDeviation", "Variance", "Skewness", "Percentile"};
		double statProjectValues[] = {2.0, 10.0, 6.0, 4.0, 16.0, 0.0, 6.0};
		double statExamValues[] = {1.5, 9.5, 5.0, 4.093, 16.75, 1.034, 4.0};
		double statFinalValues[] = {2, 10, 5.667, 4.041, 16.333, 0.722, 5.0};
		Map<String, Double> statProjectMap = setExpectedValuesToStats(statProjectValues, statNames);
		Map<String, Double> statExamMap = setExpectedValuesToStats(statExamValues, statNames);
		Map<String, Double> statFinalMap = setExpectedValuesToStats(statFinalValues, statNames);
		
		for (Entry<String, Double> set :  projectMap.entrySet()) {
			String statName = set.getKey();
			for (int i = 0; i < statNames.length; i++) {
				Assertions.assertEquals(statProjectMap.get(statName), projectMap.get(statName));
				Assertions.assertEquals(statExamMap.get(statName), examMap.get(statName));
				Assertions.assertEquals(statFinalMap.get(statName), finalMap.get(statName));
			}
		}
	}

	private Map<String, Double> setExpectedValuesToStats(double[] statProjectValues, String[] statNames) {
		Map<String, Double> statMap = new HashMap<String, Double>();
		for (int i = 0; i < statNames.length; i++) {
			statMap.put(statNames[i], statProjectValues[i]);			
		}
		return statMap;
	}
	
	@Test
	void testUS11UploadCourseFile() throws Exception {
		String fileContents = "Id,Name,InstructorLogin, Semester,Year,Syllabus\n"
				+ "tst-001,Software Development I, panos_tester, 1,3, Software development basics\n"
				+ "tst-002,Advanced Databases,panos_tester,2,4,Advanced DB and more\n";
		
		MockMultipartFile file =  new MockMultipartFile("file", "tst_panos_courses.csv", "text/csv", fileContents.getBytes());
		       
        mockMvc.perform(MockMvcRequestBuilders.multipart("/courses/upload")
                .file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses/list"));

        List<Course> expectedCourses = new ArrayList<>();
        expectedCourses.add(new Course("tst-001", "panos_tester", "Software Development I", "1", 3, "Software development basics"));
        expectedCourses.add(new Course("tst-002", "panos_tester", "Advanced Databases", "2", 4, "Advanced DB and more"));

        List<Course> actualCourses = courseDao.findCoursesByInstructorLogin("panos_tester");
        Assertions.assertEquals(expectedCourses, actualCourses);
	}
}
