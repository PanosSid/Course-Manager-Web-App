package com.myy803.course_mgt_app.service;

import java.util.ArrayList;
import java.util.List;

public enum GradeType {
    Project, Exam, Final;
    
    public static List<GradeType> createFromStrings(List<String> gTypeStr){
    	List<GradeType> gt = new ArrayList<GradeType>();
    	for (String str : gTypeStr) {
			if (str.equals(Project.toString())) {
				gt.add(Project);
			} else if (str.equals(Exam.toString())) {
				gt.add(Exam);
			} else if (str.equals(Final.toString())) {
				gt.add(Final);
			} else {
				throw new RuntimeException("Unknown grade type");
			}
		}
    	return gt;
    }
}
