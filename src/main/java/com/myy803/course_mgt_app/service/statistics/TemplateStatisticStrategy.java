package com.myy803.course_mgt_app.service.statistics;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

@Component
public abstract class TemplateStatisticStrategy implements StatisticStrategy {
	
	protected String statisticName; 
	
	public TemplateStatisticStrategy() {}
	
	public TemplateStatisticStrategy(String statisticName) {
		super();
		this.statisticName = statisticName;
	}

	public String getStatisticName() {
		return statisticName;
	}
	
	abstract public double doActualCalculation(DescriptiveStatistics ds);
	
	@Override
	public Double calculateStatistic(List<Double> grades) {
		DescriptiveStatistics ds = prepareDataset(grades);
		return doActualCalculation(ds);
	}

	private DescriptiveStatistics prepareDataset(List<Double> grades) {
		DescriptiveStatistics ds = new DescriptiveStatistics();
		for (Double grade : grades) {
			ds.addValue(grade);			
		}
		return ds;
	}
	
}
