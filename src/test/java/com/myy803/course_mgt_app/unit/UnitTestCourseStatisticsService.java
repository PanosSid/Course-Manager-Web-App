package com.myy803.course_mgt_app.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.myy803.course_mgt_app.service.statistics.CourseStatisticsService;
import com.myy803.course_mgt_app.service.statistics.CourseStatisticsServiceImp;

public class UnitTestCourseStatisticsService {
	
	private CourseStatisticsService statsService = new CourseStatisticsServiceImp();
	
//	@Mock
//	private List<StatisticStrategy> statCalculationStrategies;
	
	@Test
	void testCalculateDistribution() {
		List<Double> grades = new ArrayList<Double>();
		grades.add(0.0);
		grades.add(2.0); grades.add(2.0);
		grades.add(3.5); grades.add(3.5); grades.add(3.5);
		grades.add(5.0); grades.add(5.0); grades.add(5.0); grades.add(5.0); grades.add(5.0);
		grades.add(10.0);
		
				
		Map<Double, Integer> expDistr = new HashMap<>();
		expDistr.put(0.0, 1); expDistr.put(0.5, 0); expDistr.put(1.0, 0); expDistr.put(1.5, 0); expDistr.put(2.0, 2);
		expDistr.put(2.5, 0); expDistr.put(3.0, 0); expDistr.put(3.5, 3); expDistr.put(4.0, 0); expDistr.put(4.5, 0);
		expDistr.put(5.0, 5); expDistr.put(5.5, 0); expDistr.put(6.0, 0); expDistr.put(6.5, 0); expDistr.put(7.0, 0);
		expDistr.put(7.5, 0); expDistr.put(8.0, 0); expDistr.put(8.5, 0); expDistr.put(9.0, 0); expDistr.put(9.5, 0);
		expDistr.put(10.0, 1); 
		Assertions.assertEquals(expDistr, statsService.calculateDistribution(grades));
	}
}
