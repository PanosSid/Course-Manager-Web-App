package com.myy803.course_mgt_app.acceptance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

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

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;


@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
public class AcceptanceTestStudentRegController {
	
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private CourseDAO courseDao;
	
	@Autowired
	private StudentRegistrationDAO studRegDao;
	
	private Course course = new Course("TTT-000", "instructor_tester", "CourseTest", "2nd", 1, "This is a dummy course used only for testing");
	
	private List<StudentRegistration> listStudRegs;

	@BeforeEach 
    public void setup() {
		mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .build();
		
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
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS6BrowseMyListOfStudentRegs() throws Exception {
		mockMvc.perform(get("/studentRegistrations").
		param("courseId", "TTT-000")).
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
		multiValueMap.add("yearOfStudies", tmpStudent.getYearOfStudies());
		multiValueMap.add("semester", tmpStudent.getSemester());
		multiValueMap.add("courseId", tmpStudent.getCourseId());
		multiValueMap.add("yearOfRegistration", Integer.toString(tmpStudent.getYearOfRegistration()));
		multiValueMap.add("projectGrade", Double.toString(tmpStudent.getProjectGrade()));
		multiValueMap.add("examGrade", Double.toString(tmpStudent.getExamGrade()));
	    
	    String  redirectedPath = "/courses/showStudentRegListOfCourse?courseId="+tmpStudent.getCourseId();
	    mockMvc.perform(
				post("/studentRegistrations/save")
			    .params(multiValueMap))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(redirectedPath));	
	   
	    
	    listStudRegs.add(tmpStudent);
	    
	    List<StudentRegistration> actualStudRegs = studRegDao.findStudentRegistrationsByCourseId("TTT-000");
	    Assertions.assertEquals(listStudRegs, actualStudRegs);
	}
	
	@Test 
	void testUS9US10UpdateStudRegFields() throws Exception {
		// this student is already saved in the db, we use it to check 
		// if the below get request returns the correct student from the db
		StudentRegistration studentFromDb = new StudentRegistration (100, "TestStudent", "SurrrnameA", 2020,"5","4","TTT-000", 7.0, 3.5);
		studRegDao.save(studentFromDb);
		
		MvcResult updtFormResult = mockMvc.perform(get("/studentRegistrations/showFormForUpdateStudentReg?studentRegId=100")).
				andExpect(status().isOk()).
				andExpect(model().attribute("studentReg", studentFromDb)).
				andExpect(view().name("/studentRegistration/studentReg-form")).
				andReturn();
		
		// get the actual returned student from the above request
		StudentRegistration studentToBeUpdated = (StudentRegistration) updtFormResult.getModelAndView().getModel().get("studentReg");
		
		// update some of its fields
		studentToBeUpdated.setStudentId(109);
		studentToBeUpdated.setLastName("SurnameA");
		studentToBeUpdated.setProjectGrade(8.0);
		studentToBeUpdated.setExamGrade(4.0);
		
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("dbKeyStudReg", Integer.toString(studentToBeUpdated.getDbKeyStudReg()));
		multiValueMap.add("studentId", Integer.toString(studentToBeUpdated.getStudentId()));
		multiValueMap.add("firstName", studentToBeUpdated.getFirstName());
		multiValueMap.add("lastName", studentToBeUpdated.getLastName());
		multiValueMap.add("yearOfStudies",studentToBeUpdated.getYearOfStudies());
		multiValueMap.add("semester"	, studentToBeUpdated.getSemester());
		multiValueMap.add("courseId"	, studentToBeUpdated.getCourseId());
		multiValueMap.add("yearOfRegistration"	,Integer.toString(studentToBeUpdated.getYearOfRegistration()));
		multiValueMap.add("projectGrade"	,Double.toString(studentToBeUpdated.getProjectGrade()));
		multiValueMap.add("examGrade"	,Double.toString(studentToBeUpdated.getExamGrade()));
		
		String  redirectPath = "/courses/showStudentRegListOfCourse?courseId="+studentToBeUpdated.getCourseId();
		mockMvc.perform(post("/studentRegistrations/save").
				params(multiValueMap)).
		andExpect(status().isFound()).
		andExpect(redirectedUrl(redirectPath)).
		andReturn();	
		
		StudentRegistration expectedUpdatedStud = studentToBeUpdated;
		StudentRegistration actualUpdatedStud = studRegDao.findStudentRegistrationByStudentId(109);
		Assertions.assertEquals(expectedUpdatedStud, actualUpdatedStud);	
	}
	
	@WithMockUser(value = "instructor_tester")
	@Test 
	void testUS8RemoveStudRegFromList() throws Exception {
		mockMvc.perform(delete("/studentRegistrations/deleteStudentReg?studentRegId=33")).	
		andExpect(status().isFound()).
		andExpect(redirectedUrl("/courses/showStudentRegListOfCourse?courseId=TTT-000"));
	}
	
	@Test
	void testUS12UploadStudentRegsFile() throws Exception {
		courseDao.save(new Course("tst-007", "panos_tester2", "softdev", "1", 3, "Software development basics"));
		String fileContents = "StudentId,FirstName,LastName,RegistrationYear,YearOfStudies,Semester,CourseId,ProjectGrade,ExamGrade\r\n"
				+ "330,Name3,LName3, 2017, 3,5,tst-007,2.5,3.5\r\n"
				+ "440,Name4,LName4, 1995, 5,10,tst-007,8.5,6.5";
		
		MockMultipartFile file =  new MockMultipartFile("file", "tst_myy301_students.csv", "text/csv", fileContents.getBytes());
		       
        mockMvc.perform(MockMvcRequestBuilders.multipart("/studentRegistrations/upload")
                .file(file)
                .param("courseId", "tst-007"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses/showStudentRegListOfCourse?courseId=tst-007"));

        List<StudentRegistration> expectedStudRegs = new ArrayList<StudentRegistration>();
        expectedStudRegs.add(new StudentRegistration(330, "Name3", "LName3", 2017, "3", "5", "tst-007", 2.5, 3.5));
        expectedStudRegs.add(new StudentRegistration(440, "Name4", "LName4", 1995, "5", "10", "tst-007", 8.5, 6.5));

        List<StudentRegistration> actualStudRegs = studRegDao.findStudentRegistrationsByCourseId("tst-007");
        Assertions.assertEquals(expectedStudRegs, actualStudRegs);
	}
}
