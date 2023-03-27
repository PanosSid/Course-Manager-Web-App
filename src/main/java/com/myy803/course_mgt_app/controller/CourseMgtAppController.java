package com.myy803.course_mgt_app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
	public String showCoursesList(Model model) {
		String insturctorLoginField = getAuthenticatedInstuctorLogin();
		List<Course> coursesList = courseService.findCoursesByInstructorLogin(insturctorLoginField);
		model.addAttribute("coursesList", coursesList);
		model.addAttribute("instructorLogin", insturctorLoginField);
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
	public String showFormForAddCourse(Model model, @RequestParam("instructorLogin") String instructorLogin) {
		Course course = new Course();
		model.addAttribute("course", course);
		model.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}

	@RequestMapping("/courses/showFormForUpdateCourse")
	public String showFormForUpdateCourse(@RequestParam("courseId") String courseId, Model model) {
		Course course = courseService.findCourseByCourseId(courseId);
		model.addAttribute("course", course);
		String instructorLogin = course.getInstructorLogin();
		model.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}

	@RequestMapping("/courses/save")
	public String saveCourse(@Valid @ModelAttribute(value = "course") Course course, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			model.addAttribute("course", course);
			String instructorLogin = course.getInstructorLogin();
			model.addAttribute("instructorLogin", instructorLogin);
			return "courses/course-form";
		}
		courseService.save(course);
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
		model.addAttribute("studRegTitle", getStudentRegPageTitle(theCourse));
		model.addAttribute("courseId", courseId);
	}

	private String getStudentRegPageTitle(Course course) {
		return course.getCourseId() + " " + course.getName();
	}

	@RequestMapping("/courses/showStudentRegListOfCourse")
	public String showStudentRegListOfCourse(@RequestParam("courseId") String courseId, Model model) {
		Course theCourse = courseService.findCourseByCourseId(courseId);
		model.addAttribute("studRegTitle", getStudentRegPageTitle(theCourse));
		model.addAttribute("courseId", courseId);
		model.addAttribute("studRegList", studentRegService
				.findStudentRegistrationsByCourseId(courseId));
		return "/studentRegistration/list-studentRegistrations";
	}

	@RequestMapping("/studentRegistrations/deleteStudentReg")
	public String deleteStudentRegistration(@RequestParam("studentRegId") int studRegId) {
		String cId = studentRegService.findStudentRegistrationByStudentId(studRegId).getCourseId();
		studentRegService.deleteByStudentId(studRegId);
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + cId;
	}

	@RequestMapping("/studentRegistrations/showFormForAddStudentReg")
	public String showFormForAddStudentRegistration(Model model, @RequestParam("courseId") String courseId) {
		StudentRegistration theStudentReg = new StudentRegistration();
		theStudentReg.setCourseId(courseId);
		model.addAttribute("studentReg", theStudentReg);
		model.addAttribute("courseId", courseId);
		return "/studentRegistration/studentReg-form";
	}

	@RequestMapping("/studentRegistrations/save")
	public String saveStudentRegistration(@Valid @ModelAttribute("studentReg") StudentRegistration studentReg,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("studentReg", studentReg);
			String courseId = studentReg.getCourseId();
			model.addAttribute("courseId", courseId);
			return "/studentRegistration/studentReg-form";
		}
		// used for testing
		StudentRegistration savedStudReg = studentRegService.save(studentReg);
		model.addAttribute("savedStudReg", savedStudReg);
		return "redirect:/courses/showStudentRegListOfCourse?courseId="
				+ studentReg.getCourseId();

	}

	@RequestMapping("/studentRegistrations/showFormForUpdateStudentReg")
	public String showFormForUpdateStudentReg(Model model, @RequestParam("studentRegId") int studRegId) {
		StudentRegistration theStudentReg = studentRegService.findStudentRegistrationByStudentId(studRegId);
		model.addAttribute("studentReg", theStudentReg);
		// used for link back to Course MYY-xxx in the update page
		String courseId = theStudentReg.getCourseId();
		model.addAttribute("courseId", courseId);
		return "/studentRegistration/studentReg-form";
	}

	@PostMapping("/courses/upload")
	public String uploadCourseFile(@RequestParam("file") MultipartFile file) throws IOException {
		courseService.saveCoursesFromFile(file);
		return "redirect:/courses/list";
	}

	@PostMapping("/studentReg/upload")
	public String uploadStudentRegFile(@RequestParam("file") MultipartFile file,
			@RequestParam("courseId") String courseId) throws IOException {
		studentRegService.saveStudRegsFromFile(file);
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + courseId;
	}

}
