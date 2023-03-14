package com.myy803.course_mgt_app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.*;

@Service
public interface StudentRegistrationService {

	StudentRegistration save(StudentRegistration studReg);

	void delete(StudentRegistration studReg);

	StudentRegistration findStudentRegistrationByStudentId(int i);

	List<StudentRegistration> findStudentRegistrationsByCourseId(String courseId);

	void deleteByStudentId(int id);

	void saveStudRegFile(MultipartFile file, String courseId);

}
