package com.myy803.course_mgt_app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.CourseService;
import com.myy803.course_mgt_app.service.StudentRegistrationService;

@Controller
public class CourseMgtAppController {

	@Autowired
	private CourseService courseService;

	@Autowired
	private StudentRegistrationService studentRegistrationService;

	@RequestMapping("/courses/list")
	public String listCourses(Model theModel) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String instructorLogin = auth.getName();
		List<Course> coursesList = courseService.findCoursesByInstructorLogin(instructorLogin);
		theModel.addAttribute("coursesList", coursesList);
		theModel.addAttribute("instructorLogin", instructorLogin);
		return "courses/list-courses";
	}

	@RequestMapping("/courses/deleteCourse")
	public String deleteCourse(@RequestParam("courseId") String courseId) {
		courseService.deleteByCourseId(courseId);
		return "redirect:/courses/list";
	}

	@RequestMapping("/courses/showFormForAddCourse")
	public String showFormForAddCourse(Model theModel, @RequestParam("instructorLogin") String instructorLogin) {
		Course theCourse = new Course();
		theModel.addAttribute("course", theCourse);
		theModel.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}

	@RequestMapping("/courses/showFormForUpdateCourse")
	public String showFormForUpdateCourse(@RequestParam("courseId") String courseId, Model theModel) {
		Course theCourse = courseService.findCourseByCourseId(courseId);
		theModel.addAttribute("course", theCourse);
		String instructorLogin = theCourse.getInstructorLogin();
		theModel.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}

	@RequestMapping("/courses/save")
	public String saveCourse(@Valid @ModelAttribute(value = "course") Course theCourse, BindingResult result,
			Model theModel) {
		if (result.hasErrors()) {
			theModel.addAttribute("course", theCourse);
			String instructorLogin = theCourse.getInstructorLogin();
			theModel.addAttribute("instructorLogin", instructorLogin);
			return "courses/course-form";
		}
		courseService.save(theCourse);
		return "redirect:/courses/list";
	}

	@RequestMapping("/courses/showStatisticsOfCourse")
	public String showStatisticsOfCourse(@RequestParam("courseId") String courseId, Model theModel) {

		List<StudentRegistration> studReg_list = studentRegistrationService
				.findStudentRegistrationsByCourseId(courseId);
		// used for setting the title dynamically
		Course theCourse = courseService.findCourseByCourseId(courseId);
		String studRegTitle = theCourse.getCourseId() + " " + theCourse.getName();
		theModel.addAttribute("studRegTitle", studRegTitle);

		theModel.addAttribute("courseId", courseId);

		Map<String, ArrayList<Double>> statsMap = courseService.getCourseStatistics(studReg_list);
		// ex {"Min" : [projectGrade, examGrade, finalGrad], "Max" : [projectGrade,
		// examGrade, finalGrade] ...}

		Map<String, Double> projectMap = new HashMap<String, Double>();
		Map<String, Double> examMap = new HashMap<String, Double>();
		Map<String, Double> finalMap = new HashMap<String, Double>();

		for (Map.Entry<String, ArrayList<Double>> set : statsMap.entrySet()) {
			String statisticName = set.getKey();
			projectMap.put(set.getKey(), set.getValue().get(0));
			theModel.addAttribute("Project" + statisticName, projectMap.get(statisticName));
			examMap.put(set.getKey(), set.getValue().get(1));
			theModel.addAttribute("Exam" + statisticName, examMap.get(statisticName));
			finalMap.put(set.getKey(), set.getValue().get(2));
			theModel.addAttribute("Final" + statisticName, finalMap.get(statisticName));

		}

		// these attributes are used only for acceptance testing to make our life easier
		theModel.addAttribute("projectMap", projectMap);
		theModel.addAttribute("examMap", examMap);
		theModel.addAttribute("finalMap", finalMap);

		return "/courses/course-statistics";
	}

	@RequestMapping("/courses/showStudentRegListOfCourse")
	public String showStudentRegListOfCourse(@RequestParam("courseId") String courseId, Model theModel) {
		List<StudentRegistration> studReg_list = studentRegistrationService
				.findStudentRegistrationsByCourseId(courseId);
		theModel.addAttribute("studRegList", studReg_list);
		// used for setting the title dynamically
		Course theCourse = courseService.findCourseByCourseId(courseId);
		String studRegTitle = theCourse.getCourseId() + " " + theCourse.getName();
		theModel.addAttribute("studRegTitle", studRegTitle);
		theModel.addAttribute("courseId", courseId);
		return "/studentRegistration/list-studentRegistrations";
	}

	@RequestMapping("/studentRegistrations/deleteStudentReg")
	public String deleteStudentRegistration(@RequestParam("studentRegId") int studRegId) {
		String cId = studentRegistrationService.findStudentRegistrationByStudentId(studRegId).getCourseId();
		studentRegistrationService.deleteByStudentId(studRegId);
		String redirectToCourse = "redirect:/courses/showStudentRegListOfCourse?courseId=" + cId;
		return redirectToCourse;

	}

	@RequestMapping("/studentRegistrations/showFormForAddStudentReg")
	public String showFormForAddStudentRegistration(Model theModel, @RequestParam("courseId") String courseId) {
		StudentRegistration theStudentReg = new StudentRegistration();
		theStudentReg.setCourseId(courseId);
		theModel.addAttribute("studentReg", theStudentReg);
		theModel.addAttribute("courseId", courseId);
		return "/studentRegistration/studentReg-form";
	}

	@RequestMapping("/studentRegistrations/save")
	public String saveStudentRegistration(@Valid @ModelAttribute("studentReg") StudentRegistration theStudentReg,
			BindingResult result, Model theModel) {
		if (result.hasErrors()) {
			theModel.addAttribute("studentReg", theStudentReg);
			String courseId = theStudentReg.getCourseId();
			theModel.addAttribute("courseId", courseId);
			return "/studentRegistration/studentReg-form";
		}
		// used for testing
		StudentRegistration savedStudReg = studentRegistrationService.save(theStudentReg);
		theModel.addAttribute("savedStudReg", savedStudReg);
		String redirectToCourse = "redirect:/courses/showStudentRegListOfCourse?courseId="
				+ theStudentReg.getCourseId();
		return redirectToCourse;

	}

	@RequestMapping("/studentRegistrations/showFormForUpdateStudentReg")
	public String showFormForUpdateStudentReg(Model theModel, @RequestParam("studentRegId") int studRegId) {
		StudentRegistration theStudentReg = studentRegistrationService.findStudentRegistrationByStudentId(studRegId);
		theModel.addAttribute("studentReg", theStudentReg);
		// used for link back to Course MYY-xxx in the update page
		String courseId = theStudentReg.getCourseId();
		theModel.addAttribute("courseId", courseId);

		return "/studentRegistration/studentReg-form";
	}

	@PostMapping("/upload")
	public String uploadCourseFile(@RequestParam("file") MultipartFile file) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String instructorLogin = auth.getName();
		if ("text/csv".equals(file.getContentType())) { // check if file has csv format
			courseService.saveCoursesFromFile(file, instructorLogin);
		}
		return "redirect:/courses/list";
	}

	@PostMapping("/studentReg/upload")
	public String uploadStudentRegFile(@RequestParam("file") MultipartFile file,
			@RequestParam("courseId") String courseId) {
		if ("text/csv".equals(file.getContentType())) {
			studentRegistrationService.saveStudRegFile(file, courseId);
		}
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + courseId;
	}

}
