package com.myy803.course_mgt_app.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseService;
import com.myy803.course_mgt_app.service.StudentRegistrationService;
import com.myy803.course_mgt_app.service.statistics.CourseStatisticsServiceImp;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class IntegrationTestCourseService {
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private CourseDAO courseDao;
	
	@Autowired
	private StudentRegistrationService studRegService;
	
	@Autowired
	private CourseStatisticsServiceImp statsService;
	
	
	private Course testCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
	
	@Test
	void testFindCourseByCourseIdReturnsCourse() {
		courseDao.save(testCourse);
		Course storedCourse =  courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNotNull(storedCourse);
		Assertions.assertEquals("TMP-123", storedCourse.getCourseId());
	}
	
	@Test
	void testFindCoursesByInstructorLoginReturnsListOfCourses() {
		courseDao.save(new Course("TMP-123", "instructor_tester2", "TmpCourse", "1st", 1, "..."));
		courseDao.save(new Course("TMP-456", "instructor_tester2", "TmpCourse2", "1st", 1, "..."));
		
		List<Course> storedCourses =  courseService.findCoursesByInstructorLogin("instructor_tester2");
		Assertions.assertNotNull(storedCourses);
		Assertions.assertEquals(2, storedCourses.size());
		for (Course course : storedCourses) {
			Assertions.assertEquals("instructor_tester2",course.getInstructorLogin());
		}
		
	}
	
	@Test
	void testDeleteById() {
		courseService.save(testCourse);
		courseService.deleteByCourseId("TMP-123");	
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	@Test
	void testDeleteCourse() {
		Course savedCourse = courseService.save(testCourse);
		courseService.delete(savedCourse);
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	@Test
	void testSaveCourse() {
		Course testCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		courseService.save(testCourse);
		Course storedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertEquals(testCourse.getCourseId(), storedCourse.getCourseId());
		courseService.delete(storedCourse);	// clean db from tmp course
	}
	
	@Test
	void testGetCourseStatistics() {
		courseService.save(new Course("TTT-000", "instructor_tester", "TmpCourse", "1st", 1, "..."));
		studRegService.save(new StudentRegistration (11, "TopStud", "StudSurnam1e", 2018,"4th","8th","TTT-000", 10, 9.5));
		studRegService.save(new StudentRegistration (22, "AverageStud", "StudSurname2", 2017,"5th","9th","TTT-000", 6, 4));
		studRegService.save(new StudentRegistration (33, "BadStud", "StudSurname3", 2013,"5th","10th","TTT-000", 2, 1.5));

		Map<String, List<Double>> expectedStatsMap = new HashMap<>();
		expectedStatsMap.put("Min", convertToList(2.0, 1.5, 2));
		expectedStatsMap.put("Max", convertToList(10.0, 9.5, 10));
		expectedStatsMap.put("Mean", convertToList(6, 5.0, 5.667));
		expectedStatsMap.put("StandardDeviation", convertToList(4, 4.093, 4.041));
		expectedStatsMap.put("Variance", convertToList(16.0, 16.75, 16.333));
		expectedStatsMap.put("Skewness", convertToList(0.0, 1.034, 0.722));
		expectedStatsMap.put("Percentile", convertToList(6.0, 4.0, 5.0));
		
		Map<String, List<Double>> actualStatsMap = courseService.getCourseStatistics("TTT-000");
		for (String key : expectedStatsMap.keySet()) {
		    Assertions.assertEquals(expectedStatsMap.get(key), actualStatsMap.get(key));
		}
	}
	
	private List<Double> convertToList(double proj, double exam, double f) {
		List<Double> l = new ArrayList<Double>();
		l.add(proj);
		l.add(exam);
		l.add(f);
		return l;
	}
	
	@Test 
	void testSaveCoursesFromFile() throws IOException {
		String fileContents = "Id,Name,InstructorLogin, Semester,Year,Syllabus\n"
				+ "tst-001,Software Development I, panos_tester, 1,3, Software development basics\n"
				+ "tst-002,Advanced Databases,panos_tester,2,4,Advanced DB and more\n";
		
		MockMultipartFile file =  new MockMultipartFile("file", "tst_panos_courses.csv", "text/csv", fileContents.getBytes());
		
		courseService.saveCoursesFromFile(file);
		
		List<Course> expectedCourses = new ArrayList<>();
        expectedCourses.add(new Course("tst-001", "panos_tester", "Software Development I", "1", 3, "Software development basics"));
        expectedCourses.add(new Course("tst-002", "panos_tester", "Advanced Databases", "2", 4, "Advanced DB and more"));

        List<Course> actualCourses = courseDao.findCoursesByInstructorLogin("panos_tester");
        Assertions.assertEquals(expectedCourses, actualCourses);
	}

}
