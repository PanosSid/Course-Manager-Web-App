package com.myy803.course_mgt_app.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseStatisticsServiceImp implements CourseStatisticsService {
	
	@Autowired
	private List<StatisticStrategy> statCalculationStrategies;
	
	@Override
	public Map<String, Double> calculateGradeStatistics(String gradeType, List<Double> grades) {
		Map<String, Double> mapCalcs = new HashMap<String, Double>();
		for (StatisticStrategy statStrat : statCalculationStrategies) {
			String stratName = ((TemplateStatisticStrategy) statStrat).getStatisticName();
			Double stat = statStrat.calculateStatistic(grades);
			mapCalcs.put(gradeType+stratName, stat);
		}
		return mapCalcs;
	}
	
}
