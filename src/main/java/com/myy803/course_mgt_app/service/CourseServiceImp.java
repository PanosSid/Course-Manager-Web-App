package com.myy803.course_mgt_app.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.importers.CourseImporter;
import com.myy803.course_mgt_app.service.importers.FileLoaderFactory;
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
	public Map<String, List<Double>> getCourseStatistics(String courseId) {
		List<StudentRegistration> studRegs = studRegService.findStudentRegistrationsByCourseId(courseId);
		return statsService.getGradeStatisticsOfStudents(studRegs);
	}
	
	@Override
	public void saveCoursesFromFile(MultipartFile file) throws IOException {
		System.out.println(getFileExtention(file));
		courseImporter.setFileLoader(getFileExtention(file));
		courseDao.saveAll(courseImporter.getCoursesFromFile(file));
	}

	private String getFileExtention(MultipartFile file) {
		System.out.println(file.getOriginalFilename());
		String ending[] = file.getOriginalFilename().split("\\.");
		return ending[ending.length-1];
	}

}
