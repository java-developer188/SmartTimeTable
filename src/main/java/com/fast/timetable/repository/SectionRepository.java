package com.fast.timetable.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.Section;

@Repository
public interface SectionRepository extends CrudRepository<Section, Long> {

	public Section findByName(String section);

}
