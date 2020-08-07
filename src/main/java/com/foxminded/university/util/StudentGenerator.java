package com.foxminded.university.util;

import com.foxminded.university.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentGenerator{

    private List<Student> persons;

    private Random rnd = new Random();

    public StudentGenerator() {
        this.persons = new ArrayList<>();
    }

    private final String [] names = {"Timothy",
            "James",
            "Jason",
            "John",
            "Simon",
            "Dominic",
            "Gordon",
            "Gerard",
            "Donald",
            "Brett",
            "Lynne",
            "Rubyу",
            "Jane",
            "Sybil",
            "Gladys",
            "Brianne",
            "Ann",
            "Brittney",
            "Sharlene",
            "Helena"};

    private final String [] surnames = {"Shelton",
            "Sullivan",
            "Barnett",
            "Farmer",
            "Douglas",
            "Houston",
            "Phelps",
            "Dixon",
            "Spencer",
            "Lambert",
            "Cameron",
            "Baker",
            "Lewis",
            "Edwards",
            "Melton",
            "Morgan",
            "Garrett",
            "Lawrence",
            "Hoover",
            "Norris"
    };


    public List<Student> getPersons(int count) {
        int i = 0;
        while (i < count) {
            Student person = new Student(names[rnd.nextInt(names.length)], surnames[rnd.nextInt(surnames.length)]);
            if (persons.stream().noneMatch(person::equals)) {
                this.persons.add(person);
                i++;
            }
        }
        return persons;
    }
}
