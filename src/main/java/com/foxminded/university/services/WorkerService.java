package com.foxminded.university.services;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.ScheduleItem;
import com.foxminded.university.repository.ClassroomRepo;
import com.foxminded.university.repository.ScheduleItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WorkerService {

    private final ClassroomRepo classroomJPA;
    private final ScheduleItemRepo scheduleItemJPA;

    public List<ScheduleItem>[] getWeekSchedule() {
        int classroomAmount = classroomJPA.findAll().size();
        List<ScheduleItem> [] schedule = getScheduleGreed();
        List<ScheduleItem> scheduleItemList = scheduleItemJPA.findAll();
        for (DayOfWeek value : DayOfWeek.values()) {
            List<ScheduleItem> daySchedule = scheduleItemList.stream().filter(x -> x.getDayOfWeek().equals(value.name())).collect(Collectors.toList());
            for (int i = 1; i <= classroomAmount; i++) {
                int finalI = i;
                List<ScheduleItem> classroomSchedule = daySchedule.stream().filter(x -> x.getClassroom().getId() == finalI).collect(Collectors.toList());
                for (int j = 1; j <= 6; j++){
                    int finalJ = j;
                    if(classroomSchedule.stream().noneMatch(x -> x.getLessonNumber() == finalJ)){
                        schedule[value.getValue() - 1].add(new ScheduleItem(value.name(), j, classroomJPA.findById(i).get()));
                    }
                }
            }
        }
        return schedule;
    }

    public List<ScheduleItem>[] getClassroomWeekSchedule(Classroom classroom) {
        List<ScheduleItem> [] fullSchedule = getWeekSchedule();
        List<ScheduleItem> [] schedule = getScheduleGreed();
        for (int i = 0; i < schedule.length; i++) {
            List<ScheduleItem> collect = fullSchedule[i].stream().filter(x -> x.getClassroom().equals(classroom)).sorted(Comparator.comparingInt(ScheduleItem::getLessonNumber)).collect(Collectors.toList());
            schedule[i].addAll(collect);
        }
        return schedule;
    }

    private ArrayList<ScheduleItem>[] getScheduleGreed() {
        ArrayList<ScheduleItem>[] greed= new ArrayList[7];
        for (int i = 0; i < greed.length; i++) {
            greed[i] = new ArrayList<>();
        }
        return greed;
    }

}
