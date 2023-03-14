package com.myy803.course_mgt_app.unit;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.service.statistics.StatisticStrategy;
import com.myy803.course_mgt_app.service.*;

@ExtendWith(SpringExtension.class)
public class UnitTestCourseService  {
	
	@TestConfiguration
    static class CourseServiceImplTestContextConfiguration {
 
        @Bean
        public CourseService CourseService() {
            return new CourseServiceImp();
        }
    }
	
	@Autowired 
	CourseService courseService;
	
	@MockBean
	CourseDAO courseDAO;
	
	@MockBean
	List<StatisticStrategy> statCalculationStrategies;
	
	@Test
	void testCourseCourseServiceImplIsNotNull() {
		Assertions.assertNotNull(courseService);
	}

	@Test
	void testfindCourseByCourseIdReturnsCourse() {
		Mockito.when(courseDAO.findCourseByCourseId("MCK-000")).thenReturn(new Course("MCK-000", "instructor_tester", "MockCourse", "1st", 1, "..."));
		Course storedCourse = courseService.findCourseByCourseId("MCK-000");
		Assertions.assertNotNull(storedCourse);
		Assertions.assertEquals("MCK-000", storedCourse.getCourseId());
	}
	
	@Test
	void testFindCoursesByInstructorLoginReturnsListOfCourses() {
		Course newCourse1 = new Course("TMP-123", "instructor_tester1", "TmpCourse1", "1st", 1, "...");
		Course newCourse2 = new Course("TMP-456", "instructor_tester1", "TmpCourse2", "1st", 1, "...");
		Course newCourse3 = new Course("TMP-789", "instructor_tester1", "TmpCourse3", "1st", 1, "...");
		
		List<Course> mockedList = new ArrayList<Course>();
		mockedList.add(newCourse1);
		mockedList.add(newCourse2);
		mockedList.add(newCourse3);
		
		System.out.println(mockedList.size());
		
		Mockito.when(courseDAO.findCoursesByInstructorLogin("instructor_tester1")).thenReturn(mockedList);
		List<Course> storedCourses =  courseService.findCoursesByInstructorLogin("instructor_tester1");
		Assertions.assertNotNull(storedCourses);
		Assertions.assertEquals(3, storedCourses.size());
		for (Course course : storedCourses) {
			Assertions.assertEquals("instructor_tester1", course.getInstructorLogin());
		}
	
	}
	
	@Test	//thelei allagi
	void testDeleteCourse() {
		
		Course course = new Course(100,"TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		
		Mockito.when(courseDAO.findById(100)).thenReturn(null);		//this method is called inside delete mocks the real behaviour of the db
		courseService.delete(course);	
		
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);	
	}
	
	@Test
	void testSaveCourse() {
		Course newCourse = new Course("TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		Mockito.when(courseDAO.save(newCourse)).thenReturn(newCourse);	
		Course savedCourse = courseService.save(newCourse);
		Assertions.assertNotNull(savedCourse);
		Assertions.assertEquals(newCourse, savedCourse);
	}
	
	
	@Test
	void testDeleteByCourseId() {

		Course course = new Course(100,"TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		Mockito.when(courseDAO.findCourseByCourseId("TMP-123")).thenReturn(null);		
		Mockito.when(courseDAO.findById(100)).thenReturn(null);		//this method is called inside delete mocks the real behaviour of the db
		courseService.delete(course);	
		
		Course searchedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNull(searchedCourse);
	}
	
	@Test
	void testGetCourseStatisticsReturnsNotNullMap() {
//		StudentRegistration student1 = new StudentRegistration (11, "TopStud", "StudSurnam1e", 2018,"4th","8th","TTT-000", 10, 9.5);
//		StudentRegistration student2 = new StudentRegistration (22, "AverageStud", "StudSurname2", 2017,"5th","9th","TTT-000", 6, 4);
//		StudentRegistration student3 = new StudentRegistration (33, "BadStud", "StudSurname3", 2013,"5th","10th","TTT-000", 2, 1.5);
//		
//		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
//		studRegList.add(student1);
//		studRegList.add(student2);
//		studRegList.add(student3);
//
//		
//		for (int i = 0; i < 7; i++) {
//			Mockito.when(statCalculationStrategies.get(i).getStrategyName()).thenReturn("Strat name");
//			Mockito.when(statCalculationStrategies.get(i).calculateStatistcs(studRegList)).thenReturn(new ArrayList<Double>());	
//		}
//		
//		Map<String, ArrayList<Double>> resultMap = courseService.getCourseStatistics(studRegList);
//		Assertions.assertNotNull(resultMap);
		
	}
	
}
