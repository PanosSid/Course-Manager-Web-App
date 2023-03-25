package com.myy803.course_mgt_app.controller;

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
	private StudentRegistrationService studentRegService;
	
	@RequestMapping("/courses/list")
	public String showCoursesList(Model theModel) {
		String insturctorLoginField = getAuthenticatedInstuctorLogin();
		List<Course> coursesList = courseService.findCoursesByInstructorLogin(insturctorLoginField);
		theModel.addAttribute("coursesList", coursesList);
		theModel.addAttribute("instructorLogin", insturctorLoginField);
		return "courses/list-courses";
	}
	
	protected String getAuthenticatedInstuctorLogin() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
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
	public String showStatisticsOfCourse(@RequestParam("courseId") String courseId, Model model) {
		setStatistcsPageTile(courseId, model);
		Map<String, List<Double>> statsMap = courseService
				.getCourseStatistics(studentRegService.findStudentRegistrationsByCourseId(courseId));

		setStatsToModel(model, statsMap);
		return "/courses/course-statistics";
	}

	private void setStatsToModel(Model model, Map<String, List<Double>> statsMap) {
		// ex. stasMap = {"Min" : [projectGrade, examGrade, finalGrade],
		//		"Max" : [projectGrade,examGrade, finalGrade] ...}
		
		Map<String, Double> projectMap = new HashMap<String, Double>();
		Map<String, Double> examMap = new HashMap<String, Double>();
		Map<String, Double> finalMap = new HashMap<String, Double>();

		for (Map.Entry<String, List<Double>> set : statsMap.entrySet()) {
			String statisticName = set.getKey();
			projectMap.put(set.getKey(), set.getValue().get(0));
			examMap.put(set.getKey(), set.getValue().get(1));
			finalMap.put(set.getKey(), set.getValue().get(2));
			model.addAttribute("Project" + statisticName, projectMap.get(statisticName));
			model.addAttribute("Exam" + statisticName, examMap.get(statisticName));
			model.addAttribute("Final" + statisticName, finalMap.get(statisticName));
		}
		
		//TODO remove these
		// these attributes are used only for acceptance testing to make our life easier
		model.addAttribute("projectMap", projectMap);
		model.addAttribute("examMap", examMap);
		model.addAttribute("finalMap", finalMap);
	}

	public void setStatistcsPageTile(String courseId, Model model) {
		Course theCourse = courseService.findCourseByCourseId(courseId);
		model.addAttribute("studRegTitle", getStudentRegTitle(theCourse));
		model.addAttribute("courseId", courseId);
	}

	private String getStudentRegTitle(Course theCourse) {
		return theCourse.getCourseId() + " " + theCourse.getName();
	}

	@RequestMapping("/courses/showStudentRegListOfCourse")
	public String showStudentRegListOfCourse(@RequestParam("courseId") String courseId, Model theModel) {
		Course theCourse = courseService.findCourseByCourseId(courseId);
		theModel.addAttribute("studRegTitle", getStudentRegTitle(theCourse));
		theModel.addAttribute("courseId", courseId);
		theModel.addAttribute("studRegList", studentRegService
				.findStudentRegistrationsByCourseId(courseId));
		return "/studentRegistration/list-studentRegistrations";
	}

	@RequestMapping("/studentRegistrations/deleteStudentReg")
	public String deleteStudentRegistration(@RequestParam("studentRegId") int studRegId) {
		String cId = studentRegService.findStudentRegistrationByStudentId(studRegId).getCourseId();
		studentRegService.deleteByStudentId(studRegId);
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
		StudentRegistration savedStudReg = studentRegService.save(theStudentReg);
		theModel.addAttribute("savedStudReg", savedStudReg);
		String redirectToCourse = "redirect:/courses/showStudentRegListOfCourse?courseId="
				+ theStudentReg.getCourseId();
		return redirectToCourse;

	}

	@RequestMapping("/studentRegistrations/showFormForUpdateStudentReg")
	public String showFormForUpdateStudentReg(Model theModel, @RequestParam("studentRegId") int studRegId) {
		StudentRegistration theStudentReg = studentRegService.findStudentRegistrationByStudentId(studRegId);
		theModel.addAttribute("studentReg", theStudentReg);
		// used for link back to Course MYY-xxx in the update page
		String courseId = theStudentReg.getCourseId();
		theModel.addAttribute("courseId", courseId);

		return "/studentRegistration/studentReg-form";
	}

	@PostMapping("/upload")
	public String uploadCourseFile(@RequestParam("file") MultipartFile file) {
		String instructorLogin = getAuthenticatedInstuctorLogin();
		if ("text/csv".equals(file.getContentType())) { // check if file has csv format
			courseService.saveCoursesFromFile(file, instructorLogin);
		}
		return "redirect:/courses/list";
	}

	@PostMapping("/studentReg/upload")
	public String uploadStudentRegFile(@RequestParam("file") MultipartFile file,
			@RequestParam("courseId") String courseId) {
		if ("text/csv".equals(file.getContentType())) {
			studentRegService.saveStudRegFile(file, courseId);
		}
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + courseId;
	}

}
