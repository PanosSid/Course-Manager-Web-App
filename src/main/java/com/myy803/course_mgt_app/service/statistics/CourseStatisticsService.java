package com.myy803.course_mgt_app.service.statistics;

import java.util.List;
import java.util.Map;

public interface CourseStatisticsService {
	
	Map<String, Double> calculateGradeStatistics(String gradeType, List<Double> grades);
	
}
