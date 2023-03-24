package com.myy803.course_mgt_app.unit;

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
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import com.myy803.course_mgt_app.controller.CourseMgtAppController;
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

	@InjectMocks
	private CourseMgtAppController controller;

	private Model model = new ConcurrentModel();

	@Test
	void testControllerIsNotNull() {
		Assertions.assertNotNull(controller);
	}

	@Test
	void testListCouresesReturnsPageWithCoursesList() throws Exception {
		Course newCourse1 = new Course(1, "TMP-123", "panos_tester", "TmpCourse1", "1st", 1, "...");
		Course newCourse2 = new Course(2, "TMP-456", "panos_tester", "TmpCourse2", "1st", 1, "...");
		Course newCourse3 = new Course(3, "TMP-789", "panos_tester", "TmpCourse3", "1st", 1, "...");

		List<Course> mockedList = new ArrayList<Course>();
		mockedList.add(newCourse1);
		mockedList.add(newCourse2);
		mockedList.add(newCourse3);
		Mockito.when(courseService.findCoursesByInstructorLogin("panos_tester")).thenReturn(mockedList);

		String actualFilePath = controller.showCoursesList(model, "panos_tester");
		Assertions.assertEquals("courses/list-courses", actualFilePath);
		Assertions.assertEquals("panos_tester", model.getAttribute("instructorLogin"));
		Assertions.assertEquals(mockedList, model.getAttribute("coursesList"));

	}

	@Test
	void testDeleteCourseRedirectsToCoursesList() throws Exception {
		String actualRedirectFile = controller.deleteCourse("MCK-000");
		Assertions.assertEquals("redirect:/courses/list", actualRedirectFile);
	}

	@Test
	void testShowFormForAddCourseReturnsPage() throws Exception {
		String actualCourseFormFile = controller.showFormForAddCourse(model, "panos_tester");
		Assertions.assertEquals("courses/course-form", actualCourseFormFile);
	}

	@Test
	void testShowFormForUpdateCourseReturnsPage() throws Exception {
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);
		String actualCoursesFormFile = controller.showFormForUpdateCourse(course.getCourseId(), model);
		Assertions.assertEquals("courses/course-form", actualCoursesFormFile);
	}

	@Test
	void testSaveCourseHappyPath() throws Exception {
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
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
		Course course = new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "...");
		Mockito.when(courseService.findCourseByCourseId("MCK-000")).thenReturn(course);

		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
		Map<String, List<Double>> statsMap = new HashMap<String, List<Double>>();
		ArrayList<Double> minGradeStats = new ArrayList<Double>();
		minGradeStats.add(1.0);
		minGradeStats.add(2.0);
		minGradeStats.add(3.0);
		statsMap.put("Min", minGradeStats);

		Mockito.when(courseService.getCourseStatistics(studRegList)).thenReturn(statsMap);

		String actualCourseStatsPath = controller.showStatisticsOfCourse("MCK-000", model);
		Assertions.assertEquals("/courses/course-statistics", actualCourseStatsPath);
		Assertions.assertEquals(1.0, model.getAttribute("ProjectMin"));
		Assertions.assertEquals(2.0, model.getAttribute("ExamMin"));
		Assertions.assertEquals(3.0, model.getAttribute("FinalMin"));

	}

	@Test
	void testShowStudentRegListOfCourseReturnsPage() throws Exception {
		List<StudentRegistration> studReg_list = new ArrayList<StudentRegistration>();
		StudentRegistration student1 = new StudentRegistration(1999, "StudMock1", "", 1, "1", "1", "MCK-000", 1, 2);
		StudentRegistration student2 = new StudentRegistration(1998, "StudMock2", "", 1, "1", "1", "MCK-000", 1, 2);
		StudentRegistration student3 = new StudentRegistration(1997, "StudMock3", "", 1, "1", "1", "MCK-000", 1, 2);
		studReg_list.add(student1);
		studReg_list.add(student2);
		studReg_list.add(student3);

		Mockito.when(studRegService.findStudentRegistrationsByCourseId("MCK-000")).thenReturn(studReg_list);
		Mockito.when(courseService.findCourseByCourseId("MCK-000"))
				.thenReturn(new Course("MCK-000", "panos_tester", "MockCourse", "1st", 1, "..."));

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
		Assertions.assertEquals(modelStudReg, model.getAttribute("studentReg"));
		Assertions.assertEquals("MCK-000", model.getAttribute("courseId"));

	}

	@Test
	void testShowFormForUpdateStudRegReturnsPage() throws Exception {
		StudentRegistration mckStud = new StudentRegistration();
		mckStud.setCourseId("MCK-000");
		Mockito.when(studRegService.findStudentRegistrationByStudentId(1111)).thenReturn(mckStud);
		String actualTemplate = controller.showFormForUpdateStudentReg(model, 1111);
		Assertions.assertEquals("/studentRegistration/studentReg-form", actualTemplate);
		Assertions.assertEquals(mckStud, model.getAttribute("studentReg"));
		Assertions.assertEquals("MCK-000", model.getAttribute("courseId"));
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

}
