package com.myy803.course_mgt_app.service.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

@Component
public class StandardDeviationStatisticStrategy extends StatisticStrategy {

	public StandardDeviationStatisticStrategy() {
		super("StandardDeviation");	// name of the statistic
	}

	@Override
	public double calculateStatistic(DescriptiveStatistics ds) {
		return ds.getStandardDeviation();
	}
	
}
