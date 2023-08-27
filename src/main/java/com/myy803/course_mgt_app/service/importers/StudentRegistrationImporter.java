package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.StudentRegistration;

@Service
public class StudentRegistrationImporter {
	private static final String STUDENT_ID = "StudentId";
	private static final String FIRST_NAME = "FirstName";
	private static final String LAST_NAME = "LastName";
	private static final String REGISTRATION_YEAR = "RegistrationYear";
	private static final String YEAR_OF_STUDIES = "YearOfStudies";
	private static final String SEMESTER = "Semester";
	private static final String COURSE_ID= "CourseId";
	private static final String PROJECT_GRADE= "ProjectGrade";
	private static final String EXAM_GRADE= "ExamGrade";
	private FileLoader fileLoader;
			
	public List<StudentRegistration> getStudentRegsFromFile(MultipartFile file) throws IOException {
		fileLoader = FileLoaderFactory.createFileLoader(file.getContentType());	
		return convertDataToStudRegsList(fileLoader.getDataFromFile(file));		
	}
	
	private List<StudentRegistration> convertDataToStudRegsList(List<Map<String, String>>  dataList) {
		List<StudentRegistration> studRegs = new ArrayList<StudentRegistration>();
		for (Map<String, String>  innerList : dataList) {
			StudentRegistration studReg = convertDataToStudReg(innerList);
			studRegs.add(studReg);
		}
		return studRegs;
	}

	private StudentRegistration convertDataToStudReg(Map<String, String> data) {
		if (data.size() != 9) { 	
			throw new RuntimeException();
		}
		Integer am = Integer.parseInt(data.get(STUDENT_ID));
		String fName = data.get(FIRST_NAME);
		String lName = data.get(LAST_NAME);
		Integer regYear = Integer.parseInt(data.get(REGISTRATION_YEAR));
		String studYear = data.get(YEAR_OF_STUDIES);
		String semester = data.get(SEMESTER);
		String courseId = data.get(COURSE_ID);
		Double projGrade = Double.parseDouble(data.get(PROJECT_GRADE));
		Double examGrade = Double.parseDouble(data.get(EXAM_GRADE));
		return new StudentRegistration(am, fName, lName, regYear, studYear, semester, courseId, projGrade, examGrade);
	}
}
