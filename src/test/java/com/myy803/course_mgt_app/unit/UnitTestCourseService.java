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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseServiceImp;
import com.myy803.course_mgt_app.service.StudentRegistrationService;
import com.myy803.course_mgt_app.service.importers.CourseImporter;
import com.myy803.course_mgt_app.service.statistics.CourseStatisticsServiceImp;

@ExtendWith(MockitoExtension.class)
public class UnitTestCourseService  {
	
	@InjectMocks 
	private CourseServiceImp courseService;
	
	@Mock
	private CourseDAO courseDAO;
	
	@Mock
    private CourseImporter courseImporter;
	
	@Mock
	private StudentRegistrationService studRegService;
	
	@Mock
	private CourseStatisticsServiceImp statsCalculator;
	
	private Course testCourse = new Course(100,"TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
	
	@Test
	void testCourseCourseServiceImplIsNotNull() {
		Assertions.assertNotNull(courseService);
	}

	@Test
	void testfindCourseByCourseIdReturnsCourse() {
		Mockito.when(courseDAO.findCourseByCourseId("TMP-123")).thenReturn(testCourse);
		Course storedCourse = courseService.findCourseByCourseId("TMP-123");
		Assertions.assertNotNull(storedCourse);
		Assertions.assertEquals(testCourse, storedCourse);
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
		Mockito.when(courseDAO.findCoursesByInstructorLogin("instructor_tester1")).thenReturn(mockedList);
		
		List<Course> storedCourses =  courseService.findCoursesByInstructorLogin("instructor_tester1");
		
		Assertions.assertEquals(3, storedCourses.size());
		Assertions.assertEquals(mockedList.get(0), storedCourses.get(0));
		Assertions.assertEquals(mockedList.get(1), storedCourses.get(1));
		Assertions.assertEquals(mockedList.get(2), storedCourses.get(2));	
	}
	
	@Test
	void testSaveCourse() {
		Mockito.when(courseDAO.save(testCourse)).thenReturn(testCourse);	
		Course savedCourse = courseService.save(testCourse);
		Assertions.assertNotNull(savedCourse);
		Assertions.assertEquals(testCourse, savedCourse);
	}
	
	@Test
	void testDeleteCourse() {
		courseService.delete(testCourse);	
		Mockito.verify(courseDAO, Mockito.times(1)).delete(testCourse);;
	}
	
	
	@Test
	void testDeleteByCourseId() {
		Course course = new Course(100,"TMP-123", "instructor_tester", "TmpCourse", "1st", 1, "...");
		Mockito.when(courseDAO.findCourseByCourseId("TMP-123")).thenReturn(course);	
		courseService.deleteByCourseId("TMP-123");	
		Mockito.verify(courseDAO, Mockito.times(1)).findCourseByCourseId("TMP-123");
		Mockito.verify(courseDAO, Mockito.times(1)).delete(course);;
	}
	
	@Test
	void testGetCourseStatisticsReturnsNotNullMap() {		
		List<StudentRegistration> studRegList = new ArrayList<StudentRegistration>();
		studRegList.add(new StudentRegistration (11, "TopStud", "StudSurnam1e", 2018,"4th","8th","TTT-000", 10, 9.5));
		studRegList.add(new StudentRegistration (22, "AverageStud", "StudSurname2", 2017,"5th","9th","TTT-000", 6, 4));
		studRegList.add(new StudentRegistration (33, "BadStud", "StudSurname3", 2013,"5th","10th","TTT-000", 2, 1.5));

		Map<String, List<Double>> expectedStatsMap = new HashMap<>();
		expectedStatsMap.put("Min", convertToList(2.0, 1.5, 2));
		expectedStatsMap.put("Max", convertToList(10.0, 9.5, 10));
		expectedStatsMap.put("Mean", convertToList(6, 5.0, 5.667));
		expectedStatsMap.put("StandardDeviation", convertToList(4, 4.093, 4.041));
		expectedStatsMap.put("Variance", convertToList(16.0, 16.75, 16.333));
		expectedStatsMap.put("Skewness", convertToList(0.0, 1.034, 0.722));
		expectedStatsMap.put("Percentile", convertToList(6.0, 4.0, 5.0));
		
		Mockito.when(studRegService.findStudentRegistrationsByCourseId("TTT-000")).thenReturn(studRegList);
		Mockito.when(statsCalculator.getGradeStatisticsOfStudents(studRegList)).thenReturn(expectedStatsMap);
		Map<String, List<Double>> actualStatsMap = courseService.getCourseStatistics("TTT-000");
		
		Assertions.assertEquals(expectedStatsMap, actualStatsMap);
		Mockito.verify(studRegService).findStudentRegistrationsByCourseId("TTT-000");
		Mockito.verify(statsCalculator).getGradeStatisticsOfStudents(studRegList);
	}
	
	private List<Double> convertToList(double proj, double exam, double f) {
		List<Double> l = new ArrayList<Double>();
		l.add(proj); l.add(exam); l.add(f);
		return l;
	}
	
	@Test 
	void testSaveCoursesFromFile() throws IOException {
		String fileContents = "Id,Name,InstructorLogin, Semester,Year,Syllabus\n"
				+ "MYY-301,Software Development I, pvassil, 1,3, Software development basics\n"
				+ "PLH-010,Advanced Databases,pvassil,2,4,Advanced DB and more\n";
		
		MultipartFile file =  new MockMultipartFile("courses_upload", "courses_upload.csv", "text/csv", fileContents.getBytes());
		List<Course> courses = new ArrayList<>();
        courses.add(new Course("MYY-301", "pvassil", "Software Development I", "1", 3, "Software development basics"));
        courses.add(new Course("PLH-010", "pvassil", "Advanced Databases", "2", 4, "Advanced DB and more"));
        Mockito.when(courseImporter.getCoursesFromFile(file)).thenReturn(courses);
       
        courseService.saveCoursesFromFile(file);

        Mockito.verify(courseImporter).getCoursesFromFile(file);        
        Mockito.verify(courseDAO).saveAll(courses);
	}
	
}
