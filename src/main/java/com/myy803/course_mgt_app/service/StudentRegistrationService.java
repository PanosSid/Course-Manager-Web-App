package com.myy803.course_mgt_app.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.*;

@Service
public interface StudentRegistrationService {

	StudentRegistration findStudentRegistrationByStudentId(int id);
	
	List<StudentRegistration> findStudentRegistrationsByCourseId(String courseId);

	StudentRegistration save(StudentRegistration studReg);

	void delete(StudentRegistration studReg);

	void deleteByStudentId(int id);

	void saveStudRegFile(MultipartFile file) throws IOException;

}
