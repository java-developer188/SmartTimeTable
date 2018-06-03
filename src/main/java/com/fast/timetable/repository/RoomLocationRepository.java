package com.fast.timetable.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fast.timetable.entity.RoomLocation;

public interface RoomLocationRepository extends CrudRepository<RoomLocation, Long>  {
	
	@Query("select rl.location from RoomLocation rl where rl.room = :room")
	String getLocationByRoom(@Param(value = "room") String room);

}
