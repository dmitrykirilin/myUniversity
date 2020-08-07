DROP TABLE IF EXISTS schedule, students, teachers, classrooms, courses, groups, faculties;
CREATE TABLE IF NOT EXISTS faculties (
                                      id   SERIAL PRIMARY KEY,
                                      name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS groups (
                                    id   SERIAL PRIMARY KEY,
                                    name VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
                                       id           SERIAL PRIMARY KEY,
                                       name         VARCHAR(20) UNIQUE NOT NULL,
                                       hours_per_week INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS classrooms (
                                      id   SERIAL PRIMARY KEY,
                                      number VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
                                        id          SERIAL PRIMARY KEY,
                                        first_name  VARCHAR(20) NOT NULL,
                                        last_name   VARCHAR(20) NOT NULL,
                                        group_id    INTEGER NOT NULL,
                                        faculty_id  INTEGER,
                                        FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE,
                                        FOREIGN KEY (faculty_id) REFERENCES faculties (id) ON DELETE CASCADE,
                                        UNIQUE(first_name, last_name)
);

CREATE TABLE IF NOT EXISTS teachers (
                                        id          SERIAL PRIMARY KEY,
                                        first_name  VARCHAR(20) NOT NULL,
                                        last_name   VARCHAR(20) NOT NULL,
                                        faculty_id  INTEGER NOT NULL,
                                        FOREIGN KEY (faculty_id) REFERENCES faculties (id) ON DELETE CASCADE,
                                        UNIQUE(first_name, last_name)
);

CREATE TABLE IF NOT EXISTS schedule (
                                            id                 SERIAL PRIMARY KEY,
                                            day   VARCHAR(20) NOT NULL,
                                            lesson_number INTEGER NOT NULL,
                                            classroom_id INTEGER NOT NULL,
                                            course_id        INTEGER NULL,
                                            group_id        INTEGER NULL,
                                            teacher_id        INTEGER NULL,
                                            FOREIGN KEY (classroom_id) REFERENCES classrooms (id) ON DELETE CASCADE,
                                            FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE,
                                            FOREIGN KEY (group_id) REFERENCES groups (id) ON DELETE CASCADE,
                                            FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE CASCADE,
                                            UNIQUE(day, lesson_number, classroom_id),
                                            UNIQUE(day, lesson_number, group_id)

);
