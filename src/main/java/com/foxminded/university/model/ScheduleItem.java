package com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.Comparator;

@Entity
@Getter @Setter
@RequiredArgsConstructor @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"dayOfWeek", "lessonNumber", "classroom"})
@ToString
@Table(name = "schedule")
public class ScheduleItem {

    @Schema(description = "Unique identifier of the Lesson.")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({Views.Id.class})
    private Integer id;

    @NonNull
    @Column(name = "day")
    @JsonView({Views.FullSchedule.class})
    @Schema(example = "MONDAY", description = "Day of week from Monday to Saturday")
    private String dayOfWeek;

    @NonNull
    @JsonView({Views.FullSchedule.class})
    @Schema(example = "3", description = "Number of lesson from 1 to 6")
    private Integer lessonNumber;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classroom_id")
    @JsonView({Views.FullSchedule.class})
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @JsonView({Views.FullSchedule.class})
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    @JsonView({Views.FullSchedule.class})
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    @JsonView({Views.FullSchedule.class})
    @Schema(example = "1", description = "Id of teacher")
    private Teacher teacher;

    public ScheduleItem(String dayOfWeek, Integer lessonNumber, Classroom classroom, Course course, Group group, Teacher teacher) {
        this.dayOfWeek = dayOfWeek;
        this.lessonNumber = lessonNumber;
        this.course = course;
        this.group = group;
        this.teacher = teacher;
        this.classroom = classroom;
    }

    public static Comparator getComparator(){
        Comparator<ScheduleItem> comparator = Comparator.comparing(x -> DayOfWeek.valueOf(x.getDayOfWeek()));
        comparator = comparator.thenComparing(x -> x.getClassroom().getNumber());
        comparator = comparator.thenComparing(ScheduleItem::getLessonNumber);
        return comparator;
    }
}
