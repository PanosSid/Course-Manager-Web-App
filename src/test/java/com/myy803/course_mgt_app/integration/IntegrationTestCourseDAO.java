package com.myy803.course_mgt_app.integration;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;


@SpringBootTest
@TestPropertySource(
  locations = "classpath:application.properties")
public class IntegrationTestCourseDAO {

	@Autowired 
	CourseDAO courseDao;
		
	@Test
	void testCourseDAOImplIsNotNull() {
		Assertions.assertNotNull(courseDao);	//sanity check
	}

	@Test
	void testfindCoursebyCourseId() {
		Course storedCourse =  courseDao.findCourseByCourseId("MCK-000");
		Assertions.assertEquals("MCK-000", storedCourse.getCourseId());
	}
	
	// needs this INSERT INTO `myy803_course_mgt_db`.`course` (`db_key`, `course_id`, `course_name`, `instructor_login`, `semester`, `year`, `syllabus`) VALUES ('57', 'TTT-000', 'CourseTest', 'instructor_tester2', '2', '1', 'This is a dummy course used only for testing');
	@Test
	void testFindCoursesByInstructorLogin() {
		List<Course> storedCourses = courseDao.findCoursesByInstructorLogin("instructor_tester2");
		Assertions.assertNotNull(storedCourses);
		Assertions.assertEquals(1, storedCourses.size());
		for (Course course : storedCourses) {
			Assertions.assertNotNull(course);
			Assertions.assertEquals("MCK-000", course.getCourseId());
			Assertions.assertEquals("instructor_tester2", course.getInstructorLogin());
		}
		
	}
	
	@Test
	void testSave() {
		Course newCourse = new Course("TMP-123", "instructor_tester2", "TmpCourse", "1st", 1, "...");
		courseDao.save(newCourse);
		Course storedCourse = courseDao.findCourseByCourseId("TMP-123");
		Assertions.assertEquals(newCourse.getCourseId(), storedCourse.getCourseId());
		courseDao.delete(storedCourse);	// clean db from tmp course
	}
	
	void testSaveAll() {
		Course newCourse1 = new Course("TMP-123", "instructor_tester2", "TmpCourse1", "1st", 1, "...");
		Course newCourse2 = new Course("TMP-456", "instructor_tester2", "TmpCourse2", "1st", 1, "...");
		Course newCourse3 = new Course("TMP-789", "instructor_tester2", "TmpCourse3", "1st", 1, "...");
		
		List<Course> newCoursesList = new ArrayList<Course>();
		newCoursesList.add(newCourse1);
		newCoursesList.add(newCourse2);
		newCoursesList.add(newCourse3);
		
		courseDao.saveAll(newCoursesList);
		
		Course storedCourse1 = courseDao.findCourseByCourseId("TMP-123");
		Assertions.assertEquals("TMP-123", storedCourse1.getCourseId());
		courseDao.delete(storedCourse1);
		
		Course storedCourse2 = courseDao.findCourseByCourseId("TMP-456");
		Assertions.assertEquals("TMP-456", storedCourse1.getCourseId());
		courseDao.delete(storedCourse2);
		
		Course storedCourse3 = courseDao.findCourseByCourseId("TMP-789");
		Assertions.assertEquals("TMP-789", storedCourse1.getCourseId());
		courseDao.delete(storedCourse3);

	}
	
	@Test
	void testDelete() {
		Course newCourse = new Course("TMP-123", "instructor_tester2", "TmpCourse", "1st", 1, "...");
		Course savedCourse = courseDao.save(newCourse);
		courseDao.delete(savedCourse);	// clean db from tmp course
		Course searchedCourse = courseDao.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	
}
