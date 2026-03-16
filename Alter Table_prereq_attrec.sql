ALTER TABLE prerequisites
	DROP PRIMARY KEY,
	ADD COLUMN id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD UNIQUE KEY uq_prereq (course_id, prereq_course_id);
    
ALTER TABLE attendance_records
	DROP PRIMARY KEY,
	ADD COLUMN id INT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST,
    ADD UNIQUE KEY uq_att_record (attendance_session_id, student_id);