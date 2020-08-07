package com.foxminded.university.model;

import io.swagger.v3.oas.annotations.media.Schema;

public enum Lessons {
    FIRST_LESSON(new Lesson(1, "08:30", "10:30")),
    SECOND_LESSON(new Lesson(2,"10:30", "12:30")),
    THIRD_LESSON(new Lesson(3, "12:30", "14:30")),
    FOURTH_LESSON(new Lesson(4, "14:30", "16:30")),
    FIFTH_LESSON(new Lesson(5, "16:30", "18:30")),
    SIXTH_LESSON(new Lesson(6, "18:30", "20:30"));

    Lesson lesson;

    Lessons(Lesson lesson) {
        this.lesson = lesson;
    }
    public int number(){
        return this.lesson.getNumber();
    }
    public String startTime(){
        return this.lesson.getStartTime();
    }
    public String endTime(){
        return this.lesson.getEndTime();
    }
}
