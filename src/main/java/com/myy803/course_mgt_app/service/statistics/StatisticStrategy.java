package com.myy803.course_mgt_app.service.statistics;

import java.util.ArrayList;
import java.util.List;
import com.myy803.course_mgt_app.model.StudentRegistration;

public interface StatisticStrategy {

	String getStrategyName();

	ArrayList<Double> calculateStatistcs(List<StudentRegistration> studReg_list);

}
