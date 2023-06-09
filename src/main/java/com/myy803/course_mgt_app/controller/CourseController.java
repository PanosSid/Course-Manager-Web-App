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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myy803.course_mgt_app.model.Course;
import com.myy803.course_mgt_app.service.CourseService;

@Controller
@RequestMapping("/courses")
public class CourseController {

	@Autowired
	private CourseService courseService;
	
	@GetMapping("/list")
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

	@GetMapping("/showFormForAddCourse")
	public String showFormForAddCourse(Model model, @RequestParam("instructorLogin") String instructorLogin) {
		Course course = new Course();
		model.addAttribute("course", course);
		model.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}
	
	@GetMapping("/showFormForUpdateCourse")
	public String showFormForUpdateCourse(@RequestParam("courseId") String courseId, Model model) {
		Course course = courseService.findCourseByCourseId(courseId);
		model.addAttribute("course", course);
		String instructorLogin = course.getInstructorLogin();
		model.addAttribute("instructorLogin", instructorLogin);
		return "courses/course-form";
	}
	
	@PostMapping("/save")
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
	
	@RequestMapping("/deleteCourse")
	public String deleteCourse(@RequestParam("courseId") String courseId) {
		courseService.deleteByCourseId(courseId);
		return "redirect:/courses/list";
	}


	@GetMapping("/showStatisticsOfCourse")
	public String showStatisticsOfCourse(@RequestParam("courseId") String courseId, Model model) {
		setStatistcsPageTile(courseId, model);
		Map<String, List<Double>> statsMap = courseService.getCourseStatistics(courseId);
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

	@GetMapping("/showStudentRegListOfCourse")
	public String showStudentRegListOfCourse(@RequestParam("courseId") String courseId, RedirectAttributes redirectAttributes) {
		Course course = courseService.findCourseByCourseId(courseId);
		redirectAttributes.addFlashAttribute("studRegTitle", getStudentRegPageTitle(course));
		return "redirect:/studentRegistrations?courseId=" + courseId;
	}
	
	@PostMapping("/upload")
	public String uploadCourseFile(@RequestParam("file") MultipartFile file) throws IOException {
		courseService.saveCoursesFromFile(file);
		return "redirect:/courses/list";
	}
}
