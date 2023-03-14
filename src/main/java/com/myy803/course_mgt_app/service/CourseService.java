package com.myy803.course_mgt_app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.myy803.course_mgt_app.model.*;

@Service
public interface CourseService {

	void delete(Course course);

	void deleteByCourseId(String courseId);

	Course findCourseByCourseId(String theId);

	Map<String, ArrayList<Double>> getCourseStatistics(List<StudentRegistration> studReg_list);

	List<Course> findCoursesByInstructorLogin(String username);

	Course save(Course course);

	void saveCoursesFromFile(MultipartFile file, String instructorLogin);

}
