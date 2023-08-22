package com.myy803.course_mgt_app.service.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

@Component
public class PercentileStatisticStrategy extends StatisticStrategy{

	public PercentileStatisticStrategy() {
		super("Percentile");
	}

	@Override
	public double calculateStatistic(DescriptiveStatistics ds) {
		return ds.getPercentile(50);
	}

}

