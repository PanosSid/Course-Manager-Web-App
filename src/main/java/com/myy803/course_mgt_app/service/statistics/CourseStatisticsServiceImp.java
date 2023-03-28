package com.myy803.course_mgt_app.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.course_mgt_app.model.StudentRegistration;

@Service
public class CourseStatisticServiceImp implements CourseStatisticsService {
	@Autowired
	private List<StatisticStrategy> statCalculationStrategies;
	
	@Override
	public Map<String, List<Double>> getGradeStatisticsOfStudents(List<StudentRegistration> studRegs) {
		Map<String, List<Double>> mapCalcs = new HashMap<String, List<Double>>();
		for (StatisticStrategy statStrat : statCalculationStrategies) {
			String stratName = ((TemplateStatisticStrategy) statStrat).getStatisticName();
			List<Double> gradesStats = statStrat.calculateStatistcs(studRegs);
			mapCalcs.put(stratName, gradesStats);
		}
		return mapCalcs;
	}
}
