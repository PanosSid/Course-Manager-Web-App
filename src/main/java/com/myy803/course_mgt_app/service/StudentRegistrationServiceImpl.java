package com.myy803.course_mgt_app.service;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.importers.StudentRegistrationImporter;

@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

	@Autowired
	private StudentRegistrationDAO studentRegDao;
	
	private StudentRegistrationImporter studRegImporter = new StudentRegistrationImporter();

	public StudentRegistrationServiceImpl() {
	}

	@Override
	public StudentRegistration findStudentRegistrationByStudentId(int id) {
		return studentRegDao.findStudentRegistrationByStudentId(id);
	}

	@Override
	public List<StudentRegistration> findStudentRegistrationsByCourseId(String courseId) {
		return studentRegDao.findStudentRegistrationsByCourseId(courseId);
	}

	@Override
	public StudentRegistration save(StudentRegistration studReg) {
		return studentRegDao.save(studReg);
	}

	@Override
	public void delete(StudentRegistration studReg) {
		studentRegDao.delete(studReg);
	}

	@Override
	public void deleteByStudentId(int theId) {
		StudentRegistration sr = studentRegDao.findStudentRegistrationByStudentId(theId);
		studentRegDao.delete(sr);
	}

	@Override
	public void saveStudRegsFromFile(MultipartFile file) throws IOException {
		if ("text/csv".equals(file.getContentType())) {
			studRegImporter.setFileLoader("csv");
			studentRegDao.saveAll(studRegImporter.getStudentRegsFromFile(file));			
		}
	}


}
