package com.myy803.course_mgt_app.integration;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.properties")
public class IntegrationTestCourseDAO {

	@Autowired 
	private CourseDAO courseDao;
	
	@Autowired
	private TestEntityManager entityManager;
	
	private Course testCourse = new Course("TMP-123", "instructor_tester2", "TmpCourse", "1st", 1, "...");
	
	@BeforeEach
	void setUpDBWithTestCourse() {
		entityManager.persist(testCourse);
	}
	
	
	@Test
	void testCourseDAOImplIsNotNull() {
		Assertions.assertNotNull(courseDao);	//sanity check
	}

	@Test
	void testFindCoursebyCourseId() {
		Course storedCourse =  courseDao.findCourseByCourseId(testCourse.getCourseId());
		Assertions.assertEquals(testCourse.getCourseId(), storedCourse.getCourseId());
	}
	
	@Test
	void testFindCoursesByInstructorLogin() {
		Course testCourse2 = new Course("MCK-000", "instructor_tester2", "MckCourse", "1st", 1, "...");
		entityManager.persist(testCourse2);
		
		List<Course> storedCourses = courseDao.findCoursesByInstructorLogin(testCourse.getInstructorLogin());
		Assertions.assertNotNull(storedCourses);
		Assertions.assertEquals(2, storedCourses.size());
		Assertions.assertEquals(testCourse, storedCourses.get(0));
		Assertions.assertEquals(testCourse2, storedCourses.get(1));
		
	}
	
	@Test
	void testSave() {
		Course storedCourse = courseDao.save(testCourse);
		Assertions.assertEquals(testCourse, storedCourse);
	}
	
	void testSaveAll() {
		Course testCourse2 = new Course("MCK-000", "instructor_tester2", "MckCourse", "1st", 1, "...");
		Course testCourse3 = new Course("MCK-333", "instructor_tester2", "MckCourse3", "1st", 1, "...");
		entityManager.persist(testCourse2);
		entityManager.persist(testCourse3);
		
		List<Course> courses = new ArrayList<Course>();
		courses.add(testCourse);
		courses.add(testCourse2);
		courses.add(testCourse3);
		
		List<Course> storedCourses = courseDao.saveAll(courses);
		Assertions.assertEquals(testCourse, storedCourses.get(0));
		Assertions.assertEquals(testCourse2, storedCourses.get(1));
		Assertions.assertEquals(testCourse3, storedCourses.get(2));
	}
	
	@Test
	void testDelete() {
		courseDao.delete(testCourse);
		Course searchedCourse = courseDao.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	
}
