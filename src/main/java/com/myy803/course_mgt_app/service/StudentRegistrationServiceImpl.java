package com.myy803.course_mgt_app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.myy803.course_mgt_app.dao.StudentRegistrationDAO;
import com.myy803.course_mgt_app.model.StudentRegistration;

@Service
public class StudentRegistrationServiceImpl implements StudentRegistrationService {

	@Autowired
	private StudentRegistrationDAO studentRegDao;

	public StudentRegistrationServiceImpl() {
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
	public StudentRegistration findStudentRegistrationByStudentId(int id) {
		return studentRegDao.findStudentRegistrationByStudentId(id);
	}

	@Override
	public void deleteByStudentId(int theId) {
		StudentRegistration sr = studentRegDao.findStudentRegistrationByStudentId(theId);
		studentRegDao.delete(sr);
	}

	@Override
	public List<StudentRegistration> findStudentRegistrationsByCourseId(String courseId) {
		return studentRegDao.findStudentRegistrationByCourseId(courseId);
	}

	private List<StudentRegistration> csvToStudentRegistration(InputStream is, String courseId) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
			List<StudentRegistration> listStudReg = new ArrayList<StudentRegistration>();
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				StudentRegistration studeReg = new StudentRegistration(Integer.parseInt(csvRecord.get("AM")),
						csvRecord.get("First Name"), csvRecord.get("Last Name"),
						Integer.parseInt(csvRecord.get("Year of Registration")), csvRecord.get("Year of Studies"),
						csvRecord.get("Semester"), courseId, Double.parseDouble(csvRecord.get("Project Grade")),
						Double.parseDouble(csvRecord.get("Exam Grade")));

				listStudReg.add(studeReg);
			}
			return listStudReg;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		}
	}

	@Override
	public void saveStudRegFile(MultipartFile file, String courseId) {
		List<StudentRegistration> studRegs;
		try {
			studRegs = csvToStudentRegistration(file.getInputStream(), courseId);
			studentRegDao.saveAll(studRegs);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
