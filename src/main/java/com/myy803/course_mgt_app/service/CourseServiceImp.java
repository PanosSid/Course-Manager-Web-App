package com.myy803.course_mgt_app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
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
	
	@Autowired
	private CourseImporter courseImporter;
			
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


	@Override
	public String getCourseGradeDistribution(String courseId, List<GradeType> gradeTypes) {
		JSONArray arr = new JSONArray();
		for (GradeType gradeType : gradeTypes) {
			List<Double> grades = studRegService.findGradesByTypeAndCourse(gradeType, courseId);
			Map<Double, Integer> distribution = statsService.calculateDistribution(grades);
			List<JSONObject> distrObjs = createJsonObjsFromDistribution(distribution, gradeType);
			loadJsonObjsToArray(distrObjs, arr);
		}
		return arr.toString();
	}

	private List<JSONObject> createJsonObjsFromDistribution(Map<Double, Integer> distribution, GradeType gradeType) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (Double key : distribution.keySet()) {
			JSONObject obj = new JSONObject();
			obj.put("gradeType", gradeType);
			obj.put("gradeValue", key);
			obj.put("count", distribution.get(key));
			list.add(obj);
		}
		return list;
	}
	
	private void loadJsonObjsToArray(List<JSONObject> distrObjs, JSONArray arr) {
		for (JSONObject obj : distrObjs) {
			arr.put(obj);
		}
	}

}
