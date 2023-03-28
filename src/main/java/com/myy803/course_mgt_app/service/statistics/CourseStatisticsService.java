package com.myy803.course_mgt_app.service.statistics;

import java.util.List;
import java.util.Map;

import com.myy803.course_mgt_app.model.StudentRegistration;

public interface CourseStatisticsService {
	
	Map<String, List<Double>> getGradeStatisticsOfStudents(List<StudentRegistration> studRegs);
}
