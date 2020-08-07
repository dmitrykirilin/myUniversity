package com.foxminded.university.model;

import io.swagger.v3.oas.annotations.media.Schema;


public class Lesson {
    private Integer number;
    private String startTime;
    private String endTime;

    public Integer getNumber() {
        return number;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Lesson(Integer number, String startTime, String endTime) {
        this.number = number;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
