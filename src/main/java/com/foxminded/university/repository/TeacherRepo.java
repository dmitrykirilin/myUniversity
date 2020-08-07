package com.foxminded.university.repository;

import com.foxminded.university.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepo extends JpaRepository<Teacher, Integer> {
    Teacher findByFirstNameAndLastName(String firstName, String lastName);
}
