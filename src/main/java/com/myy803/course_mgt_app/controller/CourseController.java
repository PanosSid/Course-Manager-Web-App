package com.myy803.course_mgt_app.controller;

import java.io.IOException;
import java.util.Arrays;
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
import com.myy803.course_mgt_app.service.GradeType;

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
		Map<String, Double> statsMap = courseService.getCourseStatistics(courseId);
		for (String statName : statsMap.keySet()) {
			model.addAttribute(statName, statsMap.get(statName));
		}
		return "/courses/course-statistics";
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
	
	@GetMapping("/distribution")
	public String showGradesDistribution(/*@RequestParam("courseId") String courseId, List<String> gradeTypes,*/ Model model) {
		List<String> gradeTypes = Arrays.asList("Project", "Exam");
		List<GradeType> selectedGradeTypes = GradeType.createFromStrings(gradeTypes);
		String distrJsonStr = courseService.getCourseGradeDistribution("MYY-301", selectedGradeTypes);
		model.addAttribute("jsonData", distrJsonStr);
		return "/courses/distribution";
	}
	
}
