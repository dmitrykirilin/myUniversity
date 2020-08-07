package com.foxminded.university.util;

import com.foxminded.university.model.Teacher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeacherGenerator {

    private List<Teacher> persons;

    private Random rnd = new Random();

    public TeacherGenerator() {
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


    public List<Teacher> getPersons(int count) {
        int i = 0;
        while (i < count) {
            Teacher person = new Teacher(names[rnd.nextInt(names.length)], surnames[rnd.nextInt(surnames.length)]);
            if (persons.stream().noneMatch(person::equals)) {
                this.persons.add(person);
                i++;
            }
        }
        return persons;
    }
}
