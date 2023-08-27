package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.Course;

@Service
public class CourseImporter {
	private static final String COURSE_ID = "CourseId";
	private static final String NAME = "Name";
	private static final String INSTRUCTOR_LOGIN = "InstructorLogin";
	private static final String SEMESTER = "Semester";
	private static final String YEAR = "Year";
	private static final String SYLLABUS = "Syllabus";
	private FileLoader fileLoader;
	
	public void setFileLoader(String type) {
		fileLoader = FileLoaderFactory.createFileLoader(type);
	}
	
	public void setFileLoader(FileLoader fileLoader) {
		this.fileLoader = fileLoader;
	}
		
	public List<Course> getCoursesFromFile(MultipartFile file) throws IOException {
		fileLoader = FileLoaderFactory.createFileLoader(file.getOriginalFilename());	
		return convertDataToCoursesList(fileLoader.getDataFromFile(file));
	}
	
	private List<Course> convertDataToCoursesList(List<Map<String, String>> list) {
		List<Course> courses = new ArrayList<Course>();
		for (Map<String, String> map : list) {
			Course course = convertDataToCourse(map);
			courses.add(course);
		}
		return courses;
	}

	private Course convertDataToCourse(Map<String, String> data) {
		if (data.size() != 6) { 	
			throw new RuntimeException();
		}
		String courseId = data.get(COURSE_ID);
		String name = data.get(NAME);
		String instructorLogin = data.get(INSTRUCTOR_LOGIN);
		String semester = data.get(SEMESTER);
		Integer year = Integer.parseInt(data.get(YEAR).trim());
		String syllabus = data.get(SYLLABUS);
		return new Course(courseId, instructorLogin, name, semester, year, syllabus);
	}

}
