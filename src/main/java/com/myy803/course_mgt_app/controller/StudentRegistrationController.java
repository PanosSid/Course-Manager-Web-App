package com.myy803.course_mgt_app.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.myy803.course_mgt_app.model.StudentRegistration;
import com.myy803.course_mgt_app.service.StudentRegistrationService;


@Controller
@RequestMapping("/studentRegistrations")
public class StudentRegistrationController {
	
	@Autowired
	private StudentRegistrationService studentRegService;
	
	@GetMapping()
	public String showStudentRegListOfCourse(@RequestParam("courseId") String courseId, Model model) {
		String studRegTitle = (String) model.asMap().get("studRegTitle");
		model.addAttribute("studRegTitle", studRegTitle);
		model.addAttribute("courseId", courseId);
		model.addAttribute("studRegList", studentRegService.findStudentRegistrationsByCourseId(courseId));
		return "/studentRegistration/list-studentRegistrations";
	}
	

	@GetMapping("/showFormForAddStudentReg")
	public String showFormForAddStudentRegistration(Model model, @RequestParam("courseId") String courseId) {
		StudentRegistration theStudentReg = new StudentRegistration();
		theStudentReg.setCourseId(courseId);
		model.addAttribute("studentReg", theStudentReg);
		model.addAttribute("courseId", courseId);
		return "/studentRegistration/studentReg-form";
	}
	
	@GetMapping("/showFormForUpdateStudentReg")
	public String showFormForUpdateStudentReg(Model model, @RequestParam("studentRegId") int studRegId) {
		StudentRegistration theStudentReg = studentRegService.findStudentRegistrationByStudentId(studRegId);
		model.addAttribute("studentReg", theStudentReg);
		// used for link back to Course MYY-xxx in the update page
		String courseId = theStudentReg.getCourseId();
		model.addAttribute("courseId", courseId);
		return "/studentRegistration/studentReg-form";
	}

	@PostMapping("/save")
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
	
	@RequestMapping("/deleteStudentReg")
	public String deleteStudentRegistration(@RequestParam("studentRegId") int studRegId) {
		String cId = studentRegService.findStudentRegistrationByStudentId(studRegId).getCourseId();
		studentRegService.deleteByStudentId(studRegId);
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + cId;
	}
	
	@PostMapping("/upload")
	public String uploadStudentRegFile(@RequestParam("file") MultipartFile file,
			@RequestParam("courseId") String courseId) throws IOException {
		studentRegService.saveStudRegsFromFile(file);
		return "redirect:/courses/showStudentRegListOfCourse?courseId=" + courseId;
	}
	
}
