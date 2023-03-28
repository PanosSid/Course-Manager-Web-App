package com.myy803.course_mgt_app.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseService;
import com.myy803.course_mgt_app.service.StudentRegistrationService;

@ExtendWith(MockitoExtension.class)
public class UnitTestController {

	@Mock
	private CourseService courseService;

	@Mock
	private StudentRegistrationService studRegService;
	
	@Mock
	private Model model;
	
	@InjectMocks
	private FakeCourseMgtController controller;

	protected static String INSTRUCTOR_LOGIN = "panos_tester";

	@Test
	void testControllerIsNotNull() {
		Assertions.assertNotNull(controller); 	// sanity check
	}
	
	@Test
	void testListCouresesReturnsPageWithCoursesList() throws Exception {
		List<Course> mockedCourses = new ArrayList<Course>();
		mockedCourses.add(new Course(1, "TMP-123", INSTRUCTOR_LOGIN, "TmpCourse1", "1st", 1, "..."));
		mockedCourses.add(new Course(2, "TMP-456", INSTRUCTOR_LOGIN, "TmpCourse2", "1st", 1, "..."));
		mockedCourses.add(new Course(3, "TMP-789", INSTRUCTOR_LOGIN, "TmpCourse3", "1st", 1, "..."));

		Mockito.when(courseService.findCoursesByInstructorLogin(INSTRUCTOR_LOGIN)).thenReturn(mockedCourses);
		String actualViewName = controller.showCoursesList(model);
		
		Assertions.assertEquals("courses/list-courses", actualViewName);
		Mockito.verify(model).addAttribute("coursesList", mockedCourses);
		Mockito.verify(model).addAttribute("instructorLogin", INSTRUCTOR_LOGIN);
	}

	@Test
	void testDeleteCourseRedirectsToCoursesList() throws Exception {
		String actualRedirectFile = controller.deleteCourse("MCK-000");
		Assertions.assertEquals("redirect:/courses/list", actualRedirectFile);
	}

	@Test
	void testShowFormForAddCourseReturnsPage() throws Exception {
		String actualCourseFormFile = controller.showFormForAddCourse(model, INSTRUCTOR_LOGIN);
		Assertions.assertEquals("courses/course-form", actualCourseFormFile);
	}

	@Test
	void testShowFormForUpdateCourseReturnsPage() throws Exception {
		Course course = new Course("MCK-000", INSTRUCTOR_LOGIN, "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);
		String actualCoursesFormFile = controller.showFormForUpdateCourse(course.getCourseId(), model);
		Assertions.assertEquals("courses/course-form", actualCoursesFormFile);
	}

	@Test
	void testSaveCourseHappyPath() throws Exception {
		Course course = new Course("MCK-000", INSTRUCTOR_LOGIN, "MockCourse", "1st", 1, "...");
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("courseId", course.getCourseId());
		multiValueMap.add("instructorLogin", course.getInstructorLogin());
		multiValueMap.add("name", course.getName());
		multiValueMap.add("semester", course.getSemester());
		multiValueMap.add("year", Integer.toString(course.getYear()));
		multiValueMap.add("syllabus", course.getSyllabus());

		Mockito.when(courseService.save(course)).thenReturn(course);
		BindingResult result = new BeanPropertyBindingResult(course, "course");

		String actualRedirectFile = controller.saveCourse(course, result, model);
		Assertions.assertEquals("redirect:/courses/list", actualRedirectFile);

	}

	@Test
	void testShowStatisticsOfCourse() throws Exception {
		Course course = new Course("MCK-000", INSTRUCTOR_LOGIN, "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);

		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
		Map<String, List<Double>> statsMap = new HashMap<String, List<Double>>();
		ArrayList<Double> minGradeStats = new ArrayList<Double>();
		minGradeStats.add(1.0);
		minGradeStats.add(2.0);
		minGradeStats.add(3.0);
		statsMap.put("Min", minGradeStats);

		Mockito.when(courseService.getCourseStatistics("MCK-000")).thenReturn(statsMap);

		String actualCourseStatsPath = controller.showStatisticsOfCourse("MCK-000", model);
		Assertions.assertEquals("/courses/course-statistics", actualCourseStatsPath);
		Mockito.verify(model).addAttribute("ProjectMin", 1.0);
		Mockito.verify(model).addAttribute("ExamMin", 2.0);
		Mockito.verify(model).addAttribute("FinalMin", 3.0);
	}

	@Test
	void testShowStudentRegListOfCourseReturnsPage() throws Exception {
		List<StudentRegistration> studReg_list = new ArrayList<StudentRegistration>();
		studReg_list.add(new StudentRegistration(1999, "StudMock1", "", 1, "1", "1", "MCK-000", 1, 2));
		studReg_list.add(new StudentRegistration(1998, "StudMock2", "", 1, "1", "1", "MCK-000", 1, 2));
		studReg_list.add(new StudentRegistration(1997, "StudMock3", "", 1, "1", "1", "MCK-000", 1, 2));

		Mockito.when(studRegService.findStudentRegistrationsByCourseId("MCK-000")).thenReturn(studReg_list);
		Mockito.when(courseService.findCourseByCourseId("MCK-000"))
				.thenReturn(new Course("MCK-000", INSTRUCTOR_LOGIN, "MockCourse", "1st", 1, "..."));

		String actualStudRegListFile = controller.showStudentRegListOfCourse("MCK-000", model);
		Assertions.assertEquals("/studentRegistration/list-studentRegistrations", actualStudRegListFile);
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
	void testUploadCourseFile() throws IOException {
		MultipartFile fileMock = Mockito.mock(MultipartFile.class);
		String redirectedView = controller.uploadCourseFile(fileMock);
		Assertions.assertEquals("redirect:/courses/list", redirectedView);
		Mockito.verify(courseService, Mockito.times(1)).saveCoursesFromFile(fileMock);
	}
	
	@Test 
	void testUploadStudentRegFile() throws IOException {
		MultipartFile fileMock = Mockito.mock(MultipartFile.class);
		String redirectedView = controller.uploadStudentRegFile(fileMock, "MCK-000");
		Assertions.assertEquals("redirect:/courses/showStudentRegListOfCourse?courseId=MCK-000", redirectedView);
		Mockito.verify(studRegService, Mockito.times(1)).saveStudRegsFromFile(fileMock);
	}

}
