INSERT INTO groups (id, name) VALUES (15, 'Ivan');
INSERT INTO courses (id, name, hours_per_week) VALUES (15, 'Spring', 200);
INSERT INTO classrooms (id, number) VALUES (15, 'S15');
INSERT INTO faculties (id, name) VALUES (15, 'Java');
INSERT INTO teachers (id, first_name, last_name, faculty_id) VALUES (15, 'Ivan', 'Ivanov', 15 );
INSERT INTO schedule (id, day, lesson_number, classroom_id, course_id, group_id, teacher_id) VALUES (1000, 'Monday', 1, 15, 15, 15, 15 );
