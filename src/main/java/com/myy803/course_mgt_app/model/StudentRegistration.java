package com.myy803.course_mgt_app.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "student_registrations")
public class StudentRegistration {

	@Id
	@Column(name = "db_key_st")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbKeyStudReg;

	@NotNull(message = "Student's Registration Number cannot be null.")
	@Min(1)
	@Column(name = "student_id", unique = true) // used as pseudo primary key, very important
	private Integer studentId; // so that the instructor can update its value

	@NotEmpty(message = "Student's first name cannot be empty.")
	@Column(name = "first_name")
	private String firstName;

	@NotEmpty(message = "Student's last name cannot be empty.")
	@Column(name = "last_name")
	private String lastName;

	@NotEmpty(message = "Student's year of studies cannot be empty.")
	@Column(name = "year_of_studies")
	private String yearOfStudies;

	@NotEmpty(message = "Student's current semester cannot be empty.")
	@Column(name = "current_semester")
	private String semester;

	@Column(name = "student_course_id")
	private String courseId;

	@NotNull(message = "Student's year Of Registration cannot be empty.")
	@Min(1993)
	@Max(2022)
	@Column(name = "st_registration_year")
	private Integer yearOfRegistration;

	@NotNull(message = "Student's project grade cannot be empty.")
	@Max(value = 10)
	@Min(value = 0)
	@Column(name = "project_grade")
	private Double projectGrade;

	@NotNull(message = "Student's exam grade cannot be empty.")
	@Max(value = 10)
	@Min(value = 0)
	@Column(name = "exam_grade")
	private Double examGrade;

	@Transient
	private Double finalGrade;

	public StudentRegistration() {
	};

	public StudentRegistration(int dbKeyStudReg,
			@NotNull(message = "Student's Registration Number cannot be null.") @Min(1) Integer studentId,
			@NotEmpty(message = "Student's first name cannot be empty.") String firstName,
			@NotEmpty(message = "Student's last name cannot be empty.") String lastName,
			@NotNull(message = "Student's year Of Registration cannot be empty.") @Min(1993) @Max(2022) Integer yearOfRegistration,
			@NotEmpty(message = "Student's year of studies cannot be empty.") String yearOfStudies,
			@NotEmpty(message = "Student's current semester cannot be empty.") String semester, String courseId,
			@NotNull(message = "Student's project grade cannot be empty.") @Max(10) @Min(0) Double projectGrade,
			@NotNull(message = "Student's exam grade cannot be empty.") @Max(10) @Min(0) Double examGrade) {
		super();
		this.dbKeyStudReg = dbKeyStudReg;
		this.studentId = studentId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.yearOfStudies = yearOfStudies;
		this.semester = semester;
		this.courseId = courseId;
		this.yearOfRegistration = yearOfRegistration;
		this.projectGrade = projectGrade;
		this.examGrade = examGrade;
	}

	public StudentRegistration(int studentId, String name, String lastName, Integer yearOfRegistration,
			String yearOfStudies, String semester, String courseId, double projectGrade, double examGrade) {
		super();
		this.studentId = studentId;
		this.firstName = name;
		this.lastName = lastName;
		this.yearOfRegistration = yearOfRegistration;
		this.yearOfStudies = yearOfStudies;
		this.semester = semester;
		this.courseId = courseId;
		this.projectGrade = projectGrade;
		this.examGrade = examGrade;
	}

	public int getDbKeyStudReg() {
		return dbKeyStudReg;
	}

	public void setYearOfRegistration(Integer yearOfRegistration) {
		this.yearOfRegistration = yearOfRegistration;
	}

	public void setDbKeyStudReg(int dbKeyStudReg) {
		this.dbKeyStudReg = dbKeyStudReg;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getYearOfRegistration() {
		return yearOfRegistration;
	}

	public String getYearOfStudies() {
		return yearOfStudies;
	}

	public void setYearOfStudies(String yearOfStudies) {
		this.yearOfStudies = yearOfStudies;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public Double getProjectGrade() {
		return projectGrade;
	}

	public void setProjectGrade(Double projectGrade) {
		this.projectGrade = projectGrade;
	}

	public Double getExamGrade() {
		return examGrade;
	}

	public void setExamGrade(Double examGrade) {
		this.examGrade = examGrade;
	}

	public double getFinalGrade() {
		double finalGrade = 0.5 * examGrade + 0.5 * projectGrade;
		finalGrade = Math.round(finalGrade * 2) / 2.0;
		return finalGrade;
	}

	public void setFinalGrade(Double finalGrade) {
		this.finalGrade = finalGrade;
	}

	@Override
	public int hashCode() {
		return Objects.hash(courseId, dbKeyStudReg, examGrade, finalGrade, firstName, studentId, lastName, projectGrade,
				semester, yearOfRegistration, yearOfStudies);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StudentRegistration other = (StudentRegistration) obj;
		return Objects.equals(courseId, other.courseId) /* && dbKeyStudReg == other.dbKeyStudReg */ // this is auto
																									// generated and we
																									// use the studentId
																									// as our pseudo
																									// primary key
				&& Objects.equals(examGrade, other.examGrade) && Objects.equals(finalGrade, other.finalGrade)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(studentId, other.studentId)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(projectGrade, other.projectGrade)
				&& Objects.equals(semester, other.semester)
				&& Objects.equals(yearOfRegistration, other.yearOfRegistration)
				&& Objects.equals(yearOfStudies, other.yearOfStudies);
	}

	@Override
	public String toString() {
		return "StudentRegistration [dbKeyStudReg=" + dbKeyStudReg + ", id=" + studentId + ", name=" + firstName
				+ ", yearOfStudies=" + yearOfStudies + ", semester=" + semester + ", courseId=" + courseId
				+ ", yearOfRegistration=" + yearOfRegistration + ", projectGrade=" + projectGrade + ", examGrade="
				+ examGrade + ", finalGrade=" + finalGrade + "]";
	}

}
