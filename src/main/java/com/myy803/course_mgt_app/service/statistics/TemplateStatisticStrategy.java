package com.myy803.course_mgt_app.service.statistics;

import java.util.ArrayList;
import java.util.List;
import com.myy803.course_mgt_app.model.StudentRegistration;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Component;


@Component
public abstract class TemplateStatisticStrategy implements StatisticStrategy{
	
	private String statisticName; 
	
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
	
	private ArrayList<DescriptiveStatistics> prepareDataSet(List<StudentRegistration> studRegs) {
		ArrayList<DescriptiveStatistics> dsList = new ArrayList<DescriptiveStatistics>();
		
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
		
		dsList.add(projectDs);
		dsList.add(examDs);
		dsList.add(finalDs);
		
		return  dsList;
	}
	
	@Override
	public ArrayList<Double> calculateStatistcs(List<StudentRegistration> studRegs) {

		ArrayList<DescriptiveStatistics> dsList =  prepareDataSet(studRegs);
		
		double projectStat = doActualCalculation(dsList.get(0));	// calculate stat for project grade
		double examStat = doActualCalculation(dsList.get(1));	// calculate stat for exam grade
		double finalStat = doActualCalculation(dsList.get(2));	// calculate stat for final grade
		
		projectStat = Precision.round(projectStat, 3);
		examStat = Precision.round(examStat, 3);
		finalStat = Precision.round(finalStat, 3);
		
		ArrayList<Double> retStats = new ArrayList<Double>();
		retStats.add(projectStat);
		retStats.add(examStat);
		retStats.add(finalStat);
		
		
		return retStats;
	}
	
	abstract public double doActualCalculation(DescriptiveStatistics ds);
	
}
