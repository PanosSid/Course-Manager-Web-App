package com.myy803.course_mgt_app.service.statistics;

import java.util.List;
import com.myy803.course_mgt_app.model.StudentRegistration;

public interface StatisticStrategy {

	List<Double> calculateStatistcs(List<StudentRegistration> studReg_list);

}
