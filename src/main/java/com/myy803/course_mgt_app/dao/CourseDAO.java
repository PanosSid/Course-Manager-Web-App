package com.myy803.course_mgt_app.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myy803.course_mgt_app.model.Course;

@Repository
public interface CourseDAO extends JpaRepository<Course, Integer> {
	
	List<Course> findCoursesByInstructorLogin(String username);
	
	Course findCourseByCourseId(String theId);

}
