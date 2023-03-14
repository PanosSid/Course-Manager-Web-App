package com.myy803.course_mgt_app.service.statistics;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Component;

@Component
public class VarianceStatisticStrategy extends TemplateStatisticStrategy{
	
	public VarianceStatisticStrategy() {
		super("Variance");	// name of the statistic
	}

	@Override
	public double doActualCalculation(DescriptiveStatistics ds) {
		return ds.getVariance();
	}

	@Override
	public String getStrategyName() {
		return super.getStatisticName();
	}
}
