package com.myy803.course_mgt_app.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.importers.CourseImporter;
import com.myy803.course_mgt_app.service.statistics.StatisticStrategy;
import com.myy803.course_mgt_app.service.statistics.TemplateStatisticStrategy;

@Service
public class CourseServiceImp implements CourseService {

	@Autowired
	private CourseDAO courseDao;
	
	private CourseImporter courseImporter;
	
	@Autowired
	private List<StatisticStrategy> statCalculationStrategies;

	public CourseServiceImp() {
		courseImporter = new CourseImporter();
	}
	
	public void setStatCalculationStrategies(List<StatisticStrategy> statCalculationStrategies) {
		this.statCalculationStrategies = statCalculationStrategies;
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
	public Map<String, List<Double>> getCourseStatistics(List<StudentRegistration> studRegs) {
		Map<String, List<Double>> mapCalcs = new HashMap<String, List<Double>>();
		for (StatisticStrategy statStrat : statCalculationStrategies) {
			String stratName = ((TemplateStatisticStrategy) statStrat).getStatisticName();
			List<Double> gradesStats = statStrat.calculateStatistcs(studRegs);
			mapCalcs.put(stratName, gradesStats);
		}
		return mapCalcs;
	}
	
	@Override
	public void saveCoursesFromFile(MultipartFile file) throws IOException {
		if ("text/csv".equals(file.getContentType())) {
			courseImporter.setFileLoader("csv");
			courseDao.saveAll(courseImporter.getCoursesFromFile(file));				
		}
	}

}
