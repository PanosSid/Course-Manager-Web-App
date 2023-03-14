
CREATE DATABASE  IF NOT EXISTS `myy803_course_mgt_db`;
USE `myy803_course_mgt_db`;


CREATE TABLE `course` (
  `db_key` int NOT NULL AUTO_INCREMENT,
  `course_id` varchar(45) NOT NULL DEFAULT 'default',
  `course_name` varchar(45) DEFAULT NULL,
  `instructor_login` varchar(45) DEFAULT NULL,
  `semester` varchar(10) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `syllabus` varchar(125) DEFAULT NULL,
  PRIMARY KEY (`db_key`),
  UNIQUE KEY `course_id_UNIQUE` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1558 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `student_registrations` (
  `db_key_st` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL DEFAULT '-100',
  `first_name` varchar(45) DEFAULT '"NameDefault"',
  `last_name` varchar(45) DEFAULT '"Defaultopoulos"',
  `year_of_studies` varchar(45) DEFAULT NULL,
  `current_semester` varchar(45) DEFAULT NULL,
  `student_course_id` varchar(45) DEFAULT NULL,
  `project_grade` double DEFAULT '0',
  `exam_grade` double DEFAULT '0',
  `st_registration_year` int DEFAULT '2000',
  PRIMARY KEY (`db_key_st`),
  UNIQUE KEY `student_id_UNIQUE` (`student_id`),
  KEY `fk_course_id_idx` (`student_course_id`),
  CONSTRAINT `fk_course_id` FOREIGN KEY (`student_course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3880 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# this course is used for testing 
INSERT INTO `myy803_course_mgt_db`.`course` (`db_key`, `course_id`, `course_name`, `instructor_login`, `semester`, `year`, `syllabus`) VALUES ('1', 'MCK-000', 'Tes5tCo(urse', 'instructor_tester2', '4', '2', 'Used for testing (fix typos)');
# this stud Reg is used for testing
INSERT INTO `myy803_course_mgt_db`.`student_registrations` (`db_key_st`,`student_id`,`first_name`,`last_name`,`year_of_studies`,`current_semester`,`student_course_id`,`project_grade`,`exam_grade`,`st_registration_year`)VALUES(1,100,'TestStudent','SurrrnameA','5','4','MCK-000',7,3.5,2020);


INSERT INTO `myy803_course_mgt_db`.`course` (`db_key`, `course_id`, `course_name`, `instructor_login`, `semester`, `year`, `syllabus`) VALUES ('2', 'MYY-803', 'Software Engineering', 'zarras', '8', '4', 'Software Engineering and spring boot');

INSERT INTO `myy803_course_mgt_db`.`student_registrations` (`db_key_st`,`student_id`,`first_name`,`last_name`,`year_of_studies`,`current_semester`,`student_course_id`,`project_grade`,`exam_grade`,`st_registration_year`)VALUES(2,4000,'StudentA','SurnameA','4th','8th','MYY-803',3,2,2018);
INSERT INTO `myy803_course_mgt_db`.`student_registrations` (`db_key_st`,`student_id`,`first_name`,`last_name`,`year_of_studies`,`current_semester`,`student_course_id`,`project_grade`,`exam_grade`,`st_registration_year`)VALUES(3,4001,'StudentB','LastNameB','2nd','4th','MYY-803',7,7,2018);
INSERT INTO `myy803_course_mgt_db`.`student_registrations` (`db_key_st`,`student_id`,`first_name`,`last_name`,`year_of_studies`,`current_semester`,`student_course_id`,`project_grade`,`exam_grade`,`st_registration_year`)VALUES(4,4002,'StudentC','SurnameC','5th','9th','MYY-803',7.5,10,2016);

