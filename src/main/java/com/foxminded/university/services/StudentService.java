package com.foxminded.university.services;

import com.foxminded.university.model.Group;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.model.Student;
import com.foxminded.university.repository.ScheduleItemRepo;
import com.foxminded.university.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StudentService {

    private final ScheduleItemRepo scheduleItemJPA;

    private final StudentRepo studentJPA;

    public List<ScheduleItem>[] getWeekSchedule(Group group) {
        List<ScheduleItem> [] schedule = getScheduleGreed();
        List<ScheduleItem> groupList = scheduleItemJPA.findAll().stream().filter(x -> x.getGroup().equals(group)).collect(Collectors.toList());
        for (DayOfWeek value : DayOfWeek.values()) {
            List<ScheduleItem> collect = groupList.stream().filter(x -> x.getDayOfWeek().equals(value.name())).sorted(Comparator.comparingInt(ScheduleItem::getLessonNumber)).collect(Collectors.toList());
            schedule[value.getValue() - 1].addAll(collect);
        }
        return schedule;
    }

    public List<ScheduleItem> getDaySchedule(Group group, LocalDate date) {
        return getWeekSchedule(group)[date.getDayOfWeek().getValue()];
    }

    public List<Student> getStudentsByGroup(Integer groupId){
        return studentJPA.findAll().stream().filter(x -> x.getGroup().getId().equals(groupId)).collect(Collectors.toList());
    }

    private ArrayList<ScheduleItem>[] getScheduleGreed() {
        ArrayList<ScheduleItem>[] greed= new ArrayList[7];
        for (int i = 0; i < greed.length; i++) {
            greed[i] = new ArrayList<>();
        }
        return greed;
    }

}
