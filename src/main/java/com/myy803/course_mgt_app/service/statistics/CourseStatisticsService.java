package com.myy803.course_mgt_app.service.statistics;

import java.util.List;
import java.util.Map;

import com.myy803.course_mgt_app.service.GradeType;

public interface CourseStatisticsService {
	
	Map<String, Double> calculateGradeStatistics(GradeType gradeType, List<Double> grades);

	Map<Double, Integer> calculateDistribution(List<Double> grades);
	
}
