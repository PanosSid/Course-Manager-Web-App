package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.StudentRegistration;

public class StudentRegistrationImporter {
	
	private FileLoader fileLoader;
	
	public void setFileLoader(String type) {
		fileLoader = FileLoaderFactory.createFileLoader(type);
	}
		
	public List<StudentRegistration> getStudentRegsFromFile(MultipartFile file) throws IOException {
		return convertDataToStudRegsList(fileLoader.getDataFromFile(file));		
	}
	
	private List<StudentRegistration> convertDataToStudRegsList(List<List<String>> dataList) {
		List<StudentRegistration> studRegs = new ArrayList<StudentRegistration>();
		for (List<String> innerList : dataList) {
			StudentRegistration studReg = convertDataToStudReg(innerList);
			studRegs.add(studReg);
		}
		return studRegs;
	}

	private StudentRegistration convertDataToStudReg(List<String> data) {
		if (data.size() != 9) { 	// magic num
			throw new RuntimeException();
		}
		Integer am = Integer.parseInt(data.get(0));
		String fName = data.get(1);
		String lName = data.get(2);
		Integer regYear = Integer.parseInt(data.get(3));
		String studYear = data.get(4);
		String semester = data.get(5);
		String courseId = data.get(6);
		Double projGrade = Double.parseDouble(data.get(7));
		Double examGrade = Double.parseDouble(data.get(8));
		return new StudentRegistration(am, fName, lName, regYear, studYear, semester, courseId, projGrade, examGrade);
	}
}
