package com.myy803.course_mgt_app.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.course_mgt_app.service.GradeType;

@Service
public class CourseStatisticsServiceImp implements CourseStatisticsService {
	
	@Autowired
	private List<StatisticStrategy> statCalculationStrategies;
	
	@Override
	public Map<String, Double> calculateGradeStatistics(GradeType gradeType, List<Double> grades) {
		Map<String, Double> calculatedStatistics = new HashMap<String, Double>();
		DescriptiveStatistics ds = prepareDataset(grades);
		for (StatisticStrategy statStrat : statCalculationStrategies) {
			String stratName = statStrat.getStatisticName();			
			Double stat = statStrat.calculateStatistic(ds);
			String statMapKey = createStatMapKey(gradeType, stratName);
			calculatedStatistics.put(statMapKey, stat);
		}
		return calculatedStatistics;
	}

	private DescriptiveStatistics prepareDataset(List<Double> grades) {
		DescriptiveStatistics ds = new DescriptiveStatistics();
		for (Double grade : grades) {
			ds.addValue(grade);			
		}
		return ds;
	}
	
	private String createStatMapKey(GradeType gradeType, String stratName) {
		return gradeType+stratName;
	}
	
}
