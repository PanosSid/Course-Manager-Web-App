package com.myy803.course_mgt_app.acceptance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.controller.CourseMgtAppController;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
@AutoConfigureMockMvc
public class AcceptanceTestController {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CourseDAO courseDao;
	
	@Autowired
	private StudentRegistrationDAO studRegDao;
	
	@Autowired
	private CourseMgtAppController controller;
	
	private Course course ;
	
	private List<StudentRegistration> listStudRegs;

	@BeforeEach 
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .build();
		
		course = new Course("TTT-000", "instructor_tester", "CourseTest", "2nd", 1, "This is a dummy course used only for testing");
		courseDao.save(course);
		
		StudentRegistration student1 = new StudentRegistration (11, "TopStud", "StudSurnam1e", 2018,"4th","8th","TTT-000", 10, 9.5);
		StudentRegistration student2 = new StudentRegistration (22, "AverageStud", "StudSurname2", 2017,"5th","9th","TTT-000", 6, 4);
		StudentRegistration student3 = new StudentRegistration (33, "BadStud", "StudSurname3", 2013,"5th","10th","TTT-000", 2, 1.5);
		
		listStudRegs = new ArrayList<StudentRegistration>(); 
		listStudRegs.add(student1);
		listStudRegs.add(student2);
		listStudRegs.add(student3);
		
		studRegDao.save(student1);
		studRegDao.save(student2);
		studRegDao.save(student3);

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
		mockMvc.perform(get("/courses/list")).
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
	   
	    // check if list of instructor_tester has the 2 courses
	    mockMvc.perform(get("/courses/list")).
		andExpect(status().isOk()).
		andExpect(model().attribute("instructorLogin", "instructor_tester")).
		andExpect(model().attribute("coursesList", coursesList)).
		andExpect(view().name("courses/list-courses"));
	   
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS4RemoveCourseFromList() throws Exception{
		// delete the only course of the instructor_tester
		mockMvc.perform(delete("/courses/deleteCourse?courseId=TTT-000")).	
		andExpect(status().isFound()).
		andExpect(redirectedUrl("/courses/list"));	
		
		// check courses list of instructor_tester is empty
		mockMvc.perform(get("/courses/list")).
		andExpect(status().isOk()).
		andExpect(model().attribute("coursesList", new ArrayList<Course>())).	// model's coursesList must be empty
		andExpect(view().name("courses/list-courses"));
	}
	
	
	@WithMockUser(value = "instructor_tester2")
	@Test 
	void testUS5UpdateCourseFields() throws Exception{
		// simulates the course from the db 
		Course courseFromDb = new Course("MCK-000", "instructor_tester2", "Tes5tCo(urse", "4", 2, "Used for testing (fix typos)");
		
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
		
		// used to get the updated course 
		MvcResult result2  = mockMvc.perform(get("/courses/list")).
		andExpect(status().isOk()).
		andExpect(model().attribute("instructorLogin", "instructor_tester2")).
		andExpect(view().name("courses/list-courses")).
		andReturn();
		
		@SuppressWarnings("unchecked")
		List<Course> coursesList = (List<Course>) result2.getModelAndView().getModel().get("coursesList");
		Assertions.assertEquals(coursesList.get(0), courseFromDb);	// check if the updated course from get request is the expected
		
		//restore course with typos for the next test run
		Course initialCourseFromDb = new Course(courseFromForm.getDbKey(),"MCK-000", "instructor_tester2", "Tes5tCo(urse", "4", 2, "Used for testing (fix typos)");
		courseDao.save(initialCourseFromDb);	
		
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS6BrowseMyListOfStudentRegs() throws Exception {
		mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=TTT-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studRegList", listStudRegs)).
		andExpect(model().attribute("courseId", course.getCourseId())).
		andExpect(view().name("/studentRegistration/list-studentRegistrations"));
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS7AddStudRegToList() throws Exception {
		// student to be saved
		StudentRegistration tmpStudent = new StudentRegistration (44, "Tmp", "TmpStud", 2018,"2nd","8th","TTT-000", 7, 6);
		
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
	    
	    
	    String  redirectedPath = "/courses/showStudentRegListOfCourse?courseId="+tmpStudent.getCourseId();
	    mockMvc.perform(
				post("/studentRegistrations/save")
			    .params(multiValueMap))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(redirectedPath));	
	   
	    listStudRegs.add(tmpStudent);
	    
	    mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=TTT-000")).
	    andExpect(status().isOk()).
	    andExpect(model().attribute("studRegList", listStudRegs)).
	    andExpect(view().name("/studentRegistration/list-studentRegistrations"));
	   
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS8RemoveStudRegFromList() throws Exception {
		String redirectStudsList = "/courses/showStudentRegListOfCourse?courseId=TTT-000";
		
		mockMvc.perform(delete("/studentRegistrations/deleteStudentReg?studentRegId=33")).	
		andExpect(status().isFound()).
		andExpect(redirectedUrl(redirectStudsList));
		
		listStudRegs.remove(2);
	
		mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=TTT-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studRegList", listStudRegs)).
		andExpect(model().attribute("courseId", course.getCourseId())).
		andExpect(view().name("/studentRegistration/list-studentRegistrations"));
	}
	
	@Test 
	void testUS9US10UpdateStudRegFields() throws Exception {
		// this student is already saved in the db, we use it to check 
		// if the below get request returns the correct student from the db
		StudentRegistration studentFromDb = studRegDao.findStudentRegistrationByStudentId(100);
		
		MvcResult updtFormResult = mockMvc.perform(get("/studentRegistrations/showFormForUpdateStudentReg?studentRegId=100")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studentReg", studentFromDb)).
		andExpect(view().name("/studentRegistration/studentReg-form")).
		andReturn();
		
		// get the actual returned student from the above request
		StudentRegistration updtStud = (StudentRegistration) updtFormResult.getModelAndView().getModel().get("studentReg");
		
		// update some of its fields
		updtStud.setStudentId(109);
		updtStud.setLastName("SurnameA");
		updtStud.setProjectGrade(8.0);
		updtStud.setExamGrade(4.0);
		
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("dbKeyStudReg", Integer.toString(updtStud.getDbKeyStudReg()));
	    multiValueMap.add("studentId", Integer.toString(updtStud.getStudentId()));
	    multiValueMap.add("firstName", updtStud.getFirstName());
	    multiValueMap.add("lastName", updtStud.getLastName());
	    multiValueMap.add("yearOfStudies",updtStud.getYearOfStudies());
	    multiValueMap.add("semester"	, updtStud.getSemester());
	    multiValueMap.add("courseId"	, updtStud.getCourseId());
	    multiValueMap.add("yearOfRegistration"	,Integer.toString(updtStud.getYearOfRegistration()));
	    multiValueMap.add("projectGrade"	,Double.toString(updtStud.getProjectGrade()));
	    multiValueMap.add("examGrade"	,Double.toString(updtStud.getExamGrade()));
		
	    String  redirectPath = "/courses/showStudentRegListOfCourse?courseId="+updtStud.getCourseId();
	    mockMvc.perform(post("/studentRegistrations/save").
		params(multiValueMap)).
		andExpect(status().isFound()).
		andExpect(redirectedUrl(redirectPath)).
		andReturn();	
	    
	    // perform a get request and check if the studentReg fields is updated 
	    MvcResult result = mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=MCK-000")).
	    andExpect(status().isOk()).
	    andExpect(view().name("/studentRegistration/list-studentRegistrations")).
	    andReturn();
	    
	    @SuppressWarnings("unchecked")
		List<StudentRegistration> studListFromModel = (List<StudentRegistration>) result.getModelAndView().getModel().get("studRegList");
	    
	    Assertions.assertEquals(1, studListFromModel.size());
	    StudentRegistration updatedStud = studListFromModel.get(0);
	    
	    Assertions.assertEquals(updatedStud, updtStud);
	    
	    // restore db state as it was before running the test
	    studRegDao.delete(updatedStud);
	    StudentRegistration initialStudFromDb= new StudentRegistration (1, 100, "TestStudent", "SurrrnameA", 2020,"5","4","MCK-000", 7.0, 3.5);
	    studRegDao.save(initialStudFromDb);
	}
	
	@Test 
	void testUS11GetOverallStudentGrade() throws Exception {
		
		mockMvc.perform(get("/courses/showStudentRegListOfCourse?courseId=TTT-000")).
		andExpect(status().isOk()).
		andExpect(model().attribute("studRegList", listStudRegs)).
		andExpect(model().attribute("courseId", course.getCourseId())).
		andExpect(view().name("/studentRegistration/list-studentRegistrations")).
		andReturn();
		
		// the resulting model studRegList is the same as our file ListStudRegs
		// so we use that for our Grade checks
		
		Double topStudOverallGrade = listStudRegs.get(0).getFinalGrade();
		Double averageStudOverallGrade = listStudRegs.get(1).getFinalGrade();
		Double badStudOverallGrade = listStudRegs.get(2).getFinalGrade();

		/*
		 * overall grade formula 
		 * finalGrade = 0.5 * projectGrade + 0.5 examGrade
		 * finalGrade = round(finalGrade * 2) / 2.0 
		 */

		Assertions.assertEquals(10.0, topStudOverallGrade);
		Assertions.assertEquals(5.0, averageStudOverallGrade);
		Assertions.assertEquals(2.0, badStudOverallGrade);
		
		
	}
	
	@Test 
	void testUS12CalculateStatsOfStudentGrades() throws Exception {
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
		
		for (Entry<String, Double> set :  projectMap.entrySet()) {
			String statName = set.getKey();
			
			if (statName.equals("Min")) {
				Assertions.assertEquals(projectMap.get(statName), 2.0);
			} else if (statName.equals("Max")) {
				Assertions.assertEquals(projectMap.get(statName), 10.0);
			} else if (statName.equals("Mean")) {
				Assertions.assertEquals(projectMap.get(statName), 6.0);
			} else if (statName.equals("StandardDeviation")) {
				Assertions.assertEquals(projectMap.get(statName), 4.0);
			} else if (statName.equals("Variance")) {
				Assertions.assertEquals(projectMap.get(statName), 16.0 );
			} else if (statName.equals("Skewness")) {
				Assertions.assertEquals(projectMap.get(statName), 0.0);
			} else if (statName.equals("Percentile")) {
				Assertions.assertEquals(projectMap.get(statName), 6.0 );
			} 
		}
		
		for (Entry<String, Double> set :  examMap.entrySet()) {
			String statName = set.getKey();
			
			if (statName.equals("Min")) {
				Assertions.assertEquals(examMap.get(statName), 1.5);
			} else if (statName.equals("Max")) {
				Assertions.assertEquals(examMap.get(statName), 9.5);
			} else if (statName.equals("Mean")) {
				Assertions.assertEquals(examMap.get(statName), 5.0);
			} else if (statName.equals("StandardDeviation")) {
				Assertions.assertEquals(examMap.get(statName), 4.093);
			} else if (statName.equals("Variance")) {
				Assertions.assertEquals(examMap.get(statName), 16.75 );
			} else if (statName.equals("Skewness")) {
				Assertions.assertEquals(examMap.get(statName), 1.034);
			} else if (statName.equals("Percentile")) {
				Assertions.assertEquals(examMap.get(statName), 4.0 );
			} 
		}
		
		for (Entry<String, Double> set :  finalMap.entrySet()) {
			String statName = set.getKey();
			
			if (statName.equals("Min")) {
				Assertions.assertEquals(finalMap.get(statName), 2);
			} else if (statName.equals("Max")) {
				Assertions.assertEquals(finalMap.get(statName), 10);
			} else if (statName.equals("Mean")) {
				Assertions.assertEquals(finalMap.get(statName), 5.667);
			} else if (statName.equals("StandardDeviation")) {
				Assertions.assertEquals(finalMap.get(statName), 4.041);
			} else if (statName.equals("Variance")) {
				Assertions.assertEquals(finalMap.get(statName), 16.333);
			} else if (statName.equals("Skewness")) {
				Assertions.assertEquals(finalMap.get(statName), 0.722);
			} else if (statName.equals("Percentile")) {
				Assertions.assertEquals(finalMap.get(statName), 5.0 );
			} 
		}
		
		
	}
	
	@AfterEach
	void restoreDbFromChanges() {
		List<Course> listCourses = courseDao.findCoursesByInstructorLogin("instructor_tester");
		if (listCourses.size() > 0) {
			for (Course c : listCourses) {
				courseDao.delete(c);
			}			
		}
		
		List<StudentRegistration> listStudReg = studRegDao.findStudentRegistrationByCourseId("TTT-000");
		if (listStudReg.size() > 0) {
			for (StudentRegistration s : listStudReg) {
				studRegDao.delete(s);
			}			
		}
		
	}
	
}
