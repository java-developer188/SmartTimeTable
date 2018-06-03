package com.fast.timetable.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.CancelMakeupClass;

@Repository
public interface CancelMakeupClassRepository extends CrudRepository<CancelMakeupClass, Long> {


}
