package com.foxminded.university.repository;

import com.foxminded.university.model.ScheduleItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ScheduleItemRepo extends JpaRepository<ScheduleItem, Integer> {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    <S extends ScheduleItem> S save(S s) throws DataIntegrityViolationException;
}
