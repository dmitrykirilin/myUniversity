package com.foxminded.university.services;

import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.repository.ScheduleItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TeacherService {

    @Autowired
    private ScheduleItemRepo scheduleItemJPA;

    public List<ScheduleItem>[] getWeekSchedule(Teacher teacher) {
        List<ScheduleItem> [] schedule = getScheduleGreed();
        List<ScheduleItem> groupList = scheduleItemJPA.findAll().stream().filter(x -> x.getTeacher().equals(teacher)).collect(Collectors.toList());
        for (DayOfWeek value : DayOfWeek.values()) {
            List<ScheduleItem> collect = groupList.stream().filter(x -> x.getDayOfWeek().equals(value.name())).sorted(Comparator.comparingInt(ScheduleItem::getLessonNumber)).collect(Collectors.toList());
            schedule[value.getValue() - 1].addAll(collect);
        }
        return schedule;
    }

    public List<ScheduleItem> getDaySchedule(Teacher teacher, LocalDate date) {
        return getWeekSchedule(teacher)[date.getDayOfWeek().getValue()];
    }

    private ArrayList<ScheduleItem>[] getScheduleGreed() {
        ArrayList<ScheduleItem>[] greed= new ArrayList[7];
        for (int i = 0; i < greed.length; i++) {
            greed[i] = new ArrayList<>();
        }
        return greed;
    }
}
