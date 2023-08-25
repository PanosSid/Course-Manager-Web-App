package com.myy803.course_mgt_app.unit.controller;

import com.myy803.course_mgt_app.controller.CourseController;

/*
 * Used this, to mock the instructor authentication process because spring security
 * provides only static access to authenticated user so its not possible
 * to mock it with Mockito for unit testing. 
 */
public class FakeCourseController extends CourseController {
	
	@Override
	protected String getAuthenticatedInstuctorLogin() {
		return UnitTestCourseController.INSTRUCTOR_LOGIN;
	}
}
