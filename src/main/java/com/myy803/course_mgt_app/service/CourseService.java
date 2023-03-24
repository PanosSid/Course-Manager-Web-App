package com.myy803.course_mgt_app.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.myy803.course_mgt_app.model.*;

@Service
public interface CourseService {

	Course findCourseByCourseId(String theId);
	
	List<Course> findCoursesByInstructorLogin(String username);
	
	Course save(Course course);
	
	void delete(Course course);

	void deleteByCourseId(String courseId);

	Map<String, List<Double>> getCourseStatistics(List<StudentRegistration> studReg_list);

	void saveCoursesFromFile(MultipartFile file, String instructorLogin);

}
