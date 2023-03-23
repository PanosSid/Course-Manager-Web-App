package com.myy803.course_mgt_app.service.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;

import com.myy803.course_mgt_app.model.StudentRegistration;


@Component
public abstract class TemplateStatisticStrategy implements StatisticStrategy{
	
	protected String statisticName; 
	
	public TemplateStatisticStrategy() {}
	
	public TemplateStatisticStrategy(String statisticName) {
		super();
		this.statisticName = statisticName;
	}

	public String getStatisticName() {
		return statisticName;
	}

	public void setStatisticName(String statisticName) {
		this.statisticName = statisticName;
	}
	
	abstract public double doActualCalculation(DescriptiveStatistics ds);
	
	private Map<String, DescriptiveStatistics> prepareDataSet(List<StudentRegistration> studRegs) {
//		ArrayList<DescriptiveStatistics> dsList = new ArrayList<DescriptiveStatistics>();
		Map<String, DescriptiveStatistics> dsMap = new HashMap<String, DescriptiveStatistics>();
		
		DescriptiveStatistics projectDs = new DescriptiveStatistics();
		DescriptiveStatistics examDs = new DescriptiveStatistics();
		DescriptiveStatistics finalDs = new DescriptiveStatistics();
		
		for (StudentRegistration student : studRegs) {
			double projectGrade = student.getProjectGrade();
			double examGrade = student.getExamGrade();	/// edo prosoxi analoga me to ti ipologizoume
			double finalGrade = student.getFinalGrade();

			projectDs.addValue(projectGrade);
			examDs.addValue(examGrade);
			finalDs.addValue(finalGrade);
		}
		
		dsMap.put("project", projectDs);
		dsMap.put("exam", examDs);
		dsMap.put("final", finalDs);
		
//		dsList.add(projectDs);
//		dsList.add(examDs);
//		dsList.add(finalDs);
//		
//		return  dsList;
		return dsMap;
	}
	
	@Override
	public List<Double> calculateStatistcs(List<StudentRegistration> studRegs) {
		Map<String, DescriptiveStatistics> dsMap =  prepareDataSet(studRegs);
		double projectStat = doActualCalculation(dsMap.get("project"));	// calculate stat for project grade
		double examStat = doActualCalculation(dsMap.get("exam"));	// calculate stat for exam grade
		double finalStat = doActualCalculation(dsMap.get("final"));	// calculate stat for final grade

		projectStat = Precision.round(projectStat, 3);
		examStat = Precision.round(examStat, 3);
		finalStat = Precision.round(finalStat, 3);
		
		return Arrays.asList(new Double[]{projectStat, examStat, finalStat});
	}
	
	
	
}
