package com.myy803.course_mgt_app.service.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public abstract class StatisticStrategy {
	
	protected String statisticName; 
	
	public StatisticStrategy() {}
	
	public StatisticStrategy(String statisticName) {
		super();
		this.statisticName = statisticName;
	}

	public String getStatisticName() {
		return statisticName;
	}
	
	abstract public double calculateStatistic(DescriptiveStatistics ds);

}
