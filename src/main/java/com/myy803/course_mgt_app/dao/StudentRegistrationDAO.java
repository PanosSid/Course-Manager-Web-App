package com.myy803.course_mgt_app.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.myy803.course_mgt_app.model.StudentRegistration;

@Repository
public interface StudentRegistrationDAO extends JpaRepository<StudentRegistration, Integer> {
	
	List<StudentRegistration> findStudentRegistrationsByCourseId(String courseid);
	
	StudentRegistration findStudentRegistrationByStudentId(int id);

}
