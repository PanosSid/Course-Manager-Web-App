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

		Map<String, Double> expectedStatsMap = createExpectedStatisticsValues();

		Map<String, Double> actualStatsMap = courseService.getCourseStatistics("TTT-000");
		for (String key : actualStatsMap.keySet()) {
		    Assertions.assertEquals(expectedStatsMap.get(key), actualStatsMap.get(key), 0.001);
		}
	}

	public static Map<String, Double> createExpectedStatisticsValues() {
		Map<String, Double> expectedStatsMap = new HashMap<String, Double>();
		expectedStatsMap.put("ProjectMin", 2.0);
		expectedStatsMap.put("ExamMin", 1.5);
		expectedStatsMap.put("FinalMin", 2.0);
		
		expectedStatsMap.put("ProjectMax", 10.0);
		expectedStatsMap.put("ExamMax", 9.5);
		expectedStatsMap.put("FinalMax", 10.0);
		
		expectedStatsMap.put("ProjectMean", 6.0);
		expectedStatsMap.put("ExamMean", 5.0);
		expectedStatsMap.put("FinalMean", 5.666666666666667);

		expectedStatsMap.put("ProjectStandardDeviation", 4.0);
		expectedStatsMap.put("ExamStandardDeviation", 4.093);
		expectedStatsMap.put("FinalStandardDeviation", 4.041);
		
		expectedStatsMap.put("ProjectVariance", 16.0);
		expectedStatsMap.put("ExamVariance", 16.75);
		expectedStatsMap.put("FinalVariance", 16.333);
		
		expectedStatsMap.put("ProjectSkewness", 0.0);
		expectedStatsMap.put("ExamSkewness", 1.034);
		expectedStatsMap.put("FinalSkewness", 0.722);
		
		expectedStatsMap.put("ProjectPercentile", 6.0);
		expectedStatsMap.put("ExamPercentile", 4.0);
		expectedStatsMap.put("FinalPercentile", 5.0);
		return expectedStatsMap;
	}
	
	@Test 
	void testSaveCoursesFromFile() throws IOException {
		String fileContents = "CourseId,Name,InstructorLogin,Semester,Year,Syllabus\n"
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
