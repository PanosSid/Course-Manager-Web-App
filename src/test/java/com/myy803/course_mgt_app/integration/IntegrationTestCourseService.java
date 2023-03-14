package com.myy803.course_mgt_app.integration;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.service.CourseService;

@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
public class IntegrationTestCourseService {
	
	@Autowired
	CourseService courseService;
	
	@Test
	void testfindCourseByCourseIdReturnsCourse() {
		Course storedCourse =  courseService.findCourseByCourseId("MCK-000");
		Assertions.assertNotNull(storedCourse);
		Assertions.assertEquals("MCK-000", storedCourse.getCourseId());
	}
	
	@Test
	void testFindCoursesByInstructorLoginReturnsListOfCourses() {
		List<Course> storedCourses =  courseService.findCoursesByInstructorLogin("instructor_tester2");
		Assertions.assertNotNull(storedCourses);
		Assertions.assertEquals(1, storedCourses.size());
		for (Course course : storedCourses) {
			Assertions.assertEquals("instructor_tester2",course.getInstructorLogin());
		}
		
	}
	
	@Test
	void testDeleteById() {
		Course newCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		courseService.save(newCourse);
		courseService.deleteByCourseId("TMP-123");	
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	@Test
	void testDeleteCourse() {
		Course newCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		Course savedCourse = courseService.save(newCourse);
		courseService.delete(savedCourse);
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	@Test
	void testSaveCourse() {
		Course newCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		courseService.save(newCourse);
		Course storedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertEquals(newCourse.getCourseId(), storedCourse.getCourseId());
		courseService.delete(storedCourse);	// clean db from tmp course
	}

}
