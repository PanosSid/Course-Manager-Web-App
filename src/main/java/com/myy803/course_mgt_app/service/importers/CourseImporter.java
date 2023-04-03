package com.myy803.course_mgt_app.service.importers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.Course;

public class CourseImporter {

	private FileLoader fileLoader;
	
	public void setFileLoader(String type) {
		fileLoader = FileLoaderFactory.createFileLoader(type);
	}
	
	public void setFileLoader(FileLoader fileLoader) {
		this.fileLoader = fileLoader;
	}
		
	public List<Course> getCoursesFromFile(MultipartFile file) throws IOException {
		return convertDataToCoursesList(fileLoader.getDataFromFile(file));
	}
	
	private List<Course> convertDataToCoursesList(List<List<String>> dataList) {
		List<Course> courses = new ArrayList<Course>();
		for (List<String> innerlist : dataList) {
			Course course = convertDataToCourse(innerlist);
			courses.add(course);
		}
		return courses;
	}

	private Course convertDataToCourse(List<String> data) {
		if (data.size() != 6) { 	// magic num
			throw new RuntimeException();
		}
		String courseId = data.get(0);
		String name = data.get(1);
		String instructorLogin = data.get(2);
		String semester = data.get(3);
		Integer year = Integer.parseInt(data.get(4).trim());
		String syllabus = data.get(5);
		return new Course(courseId, instructorLogin, name, semester, year, syllabus);
	}

}
