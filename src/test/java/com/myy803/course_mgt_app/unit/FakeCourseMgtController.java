package com.myy803.course_mgt_app.unit;

import com.myy803.course_mgt_app.controller.CourseMgtAppController;

/*
 * Used this, to mock the instructor authentication process because spring security
 * provides only static access to authenticated user so its not possible
 * to mock it with Mockito for unit testing. 
 */
public class FakeCourseMgtController extends CourseMgtAppController {
	
	@Override
	protected String getAuthenticatedInstuctorLogin() {
		return UnitTestController.INSTRUCTOR_LOGIN;
	}
}
