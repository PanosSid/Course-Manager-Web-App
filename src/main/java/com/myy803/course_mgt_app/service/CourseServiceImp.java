package com.myy803.course_mgt_app.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.myy803.course_mgt_app.dao.CourseDAO;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.statistics.StatisticStrategy;

@Service
public class CourseServiceImp implements CourseService {

	@Autowired
	private CourseDAO courseDao;

	@Autowired
	private List<StatisticStrategy> statCalculationStrategies;

	public CourseServiceImp() {
	}

	public void setStatCalculationStrategies(List<StatisticStrategy> statCalculationStrategies) {
		this.statCalculationStrategies = statCalculationStrategies;
	}

	@Override
	public void deleteByCourseId(String courseId) {
		Course c = courseDao.findCourseByCourseId(courseId);
		courseDao.delete(c);
	}

	@Override
	public Course findCourseByCourseId(String theId) {
		return courseDao.findCourseByCourseId(theId);
	}

	@Override
	public List<Course> findCoursesByInstructorLogin(String username) {
		List<Course> list = courseDao.findCoursesByInstructorLogin(username);
		return list;
	}

	@Override
	public Course save(Course course) {
		return courseDao.save(course);
	}

	@Override
	public void delete(Course course) {
		courseDao.delete(course);
	}

	@Override
	public Map<String, ArrayList<Double>> getCourseStatistics(List<StudentRegistration> studReg_list) {
		Map<String, ArrayList<Double>> mapCalcs = new HashMap<String, ArrayList<Double>>();

		for (StatisticStrategy statStrat : statCalculationStrategies) {
			String stratName = statStrat.getStrategyName();
			ArrayList<Double> gradesStats = statStrat.calculateStatistcs(studReg_list);
			mapCalcs.put(stratName, gradesStats);
		}

		return mapCalcs;
	}

	private List<Course> csvToCourses(InputStream is, String instructorLogin) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
			List<Course> coursesList = new ArrayList<Course>();
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();
			for (CSVRecord csvRecord : csvRecords) {
				Course Course = new Course(csvRecord.get("Id"), instructorLogin,
						// csvRecord.get("Instructor Login"),
						csvRecord.get("Name"), csvRecord.get("Semester"), Integer.parseInt(csvRecord.get("Year")),
						csvRecord.get("Syllabus"));

				coursesList.add(Course);
			}
			return coursesList;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse CSV file");
		}
	}

	@Override
	public void saveCoursesFromFile(MultipartFile file, String instructorLogin) {
		List<Course> Courses;
		try {
			Courses = csvToCourses(file.getInputStream(), instructorLogin);
			courseDao.saveAll(Courses);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
