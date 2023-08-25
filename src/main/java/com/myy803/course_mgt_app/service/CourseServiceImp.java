package com.myy803.course_mgt_app.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.service.importers.CourseImporter;
import com.myy803.course_mgt_app.service.statistics.CourseStatisticsService;

@Service
public class CourseServiceImp implements CourseService {

	@Autowired
	private CourseDAO courseDao;
	
	@Autowired
	private StudentRegistrationService studRegService;
	
	@Autowired
	private CourseStatisticsService statsService;
	
	private CourseImporter courseImporter;
		
	public CourseServiceImp() {
		courseImporter = new CourseImporter();
	}
	
	@Override
	public Course findCourseByCourseId(String theId) {
		return courseDao.findCourseByCourseId(theId);
	}
	
	@Override
	public List<Course> findCoursesByInstructorLogin(String username) {
		return courseDao.findCoursesByInstructorLogin(username);
	}
	
	@Override
	public Course save(Course course) {
		return courseDao.save(course);
	}
	
	@Override
	public void delete(Course course) {
		courseDao.delete(course);
	}
	
	@Override
	public void deleteByCourseId(String courseId) {
		Course c = courseDao.findCourseByCourseId(courseId);
		courseDao.delete(c);
	}

	@Override	
	public Map<String, Double> getCourseStatistics(String courseId) {
		Map<String, Double> stats = new HashMap<String, Double>();
		for (GradeType gradeType : GradeType.values()) {
			List<Double> grades = studRegService.findGradesByTypeAndCourse(gradeType, courseId);
			stats.putAll(statsService.calculateGradeStatistics(gradeType, grades));			
		}
		return stats;
	}
	
	@Override
	public void saveCoursesFromFile(MultipartFile file) throws IOException {
		courseDao.saveAll(courseImporter.getCoursesFromFile(file));
	}


}
