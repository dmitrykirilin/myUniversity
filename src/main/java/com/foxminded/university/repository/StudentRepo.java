package com.foxminded.university.repository;

import com.foxminded.university.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends JpaRepository<Student, Integer> {
    Student findByFirstNameAndLastName(String firstName, String lastName);
}
