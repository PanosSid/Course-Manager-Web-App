package com.myy803.course_mgt_app.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "course")
public class Course {

	@Id
	@Column(name = "db_key")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int dbKey;

	@NotEmpty(message = "Course's id cannot be empty.")
	@Column(name = "course_id", unique = true) // used as pseudo primary key, very important
	private String courseId; // so that the instructor can update its value

	@Column(name = "instructor_login")
	private String instructorLogin;

	@NotEmpty(message = "Course's name cannot be empty.")
	@Column(name = "course_name")
	private String name;

	@NotEmpty(message = "Course's semester cannot be empty.")
	@Column(name = "semester")
	private String semester;

	@NotNull(message = "The year of the course cannot be null.")
	@Max(value = 10)
	@Min(value = 1)
	@Column(name = "year")
	private Integer year;

	@NotEmpty(message = "Course's syllabus cannot be null.")
	@Column(name = "syllabus")
	private String syllabus;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "student_course_id")
	private List<StudentRegistration> studentReg;

	public Course() {
	}

	// used for csv parser
	public Course(String courseId, String instructorLogin, String name, String semester, @Max(10) int year,
			String syllabus) {
		super();
		this.courseId = courseId;
		this.instructorLogin = instructorLogin;
		this.name = name;
		this.semester = semester;
		this.year = year;
		this.syllabus = syllabus;
	}

	public Course(@NotEmpty(message = "Course's id cannot be empty.") String courseId, String instructorLogin,
			@NotEmpty(message = "Course's name cannot be empty.") String name,
			@NotEmpty(message = "Course's semester cannot be empty.") String semester,
			@NotNull(message = "The year of the course cannot be null.") @Max(10) @Min(1) Integer year,
			@NotEmpty(message = "Course's syllabus cannot be null.") String syllabus) {
		super();
		this.courseId = courseId;
		this.instructorLogin = instructorLogin;
		this.name = name;
		this.semester = semester;
		this.year = year;
		this.syllabus = syllabus;
	}

	public Course(int dbKey, @NotEmpty(message = "Course's id cannot be empty.") String courseId,
			String instructorLogin, @NotEmpty(message = "Course's name cannot be empty.") String name,
			@NotEmpty(message = "Course's semester cannot be empty.") String semester,
			@NotNull(message = "The year of the course cannot be null.") @Max(10) @Min(1) Integer year,
			@NotEmpty(message = "Course's syllabus cannot be null.") String syllabus) {
		super();
		this.dbKey = dbKey;
		this.courseId = courseId;
		this.instructorLogin = instructorLogin;
		this.name = name;
		this.semester = semester;
		this.year = year;
		this.syllabus = syllabus;
	}

	public int getDbKey() {
		return dbKey;
	}

	public void setDbKey(int dbKey) {
		this.dbKey = dbKey;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getInstructorLogin() {
		return instructorLogin;
	}

	public void setInstructorLogin(String instructorLogin) {
		this.instructorLogin = instructorLogin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getSyllabus() {
		return syllabus;
	}

	public void setSyllabus(String syllabus) {
		this.syllabus = syllabus;
	}

	public List<StudentRegistration> getStudentReg() {
		return studentReg;
	}

	public void setStudentReg(List<StudentRegistration> studentReg) {
		this.studentReg = studentReg;
	}

	@Override
	public int hashCode() {
		return Objects.hash(courseId, dbKey, instructorLogin, name, semester, studentReg, syllabus, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		return Objects.equals(courseId, other.courseId) /* && dbKey == other.dbKey */
				&& Objects.equals(instructorLogin, other.instructorLogin) && Objects.equals(name, other.name)
				&& Objects.equals(semester, other.semester) /* && Objects.equals(studentReg, other.studentReg) */
				&& Objects.equals(syllabus, other.syllabus) && Objects.equals(year, other.year);
	}

	@Override
	public String toString() {
		return "Course [dbKey=" + dbKey + ", courseId=" + courseId + ", instructorLogin=" + instructorLogin + ", name="
				+ name + ", semester=" + semester + ", year=" + year + ", syllabus=" + syllabus + ", studentReg="
				+ studentReg + "]";
	}

}
