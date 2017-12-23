package com.fast.timetable.utilities;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fast.timetable.entity.TimeTable;
import com.fast.timetable.repository.TimeTableRepository;

@Component
public class ClassNotificationTask {

	@Autowired
	TimeTableRepository timeTableRepository ;
	
	public void execute() {
		 LocalDateTime localNow = LocalDateTime.now();
		 System.out.println(localNow.getDayOfWeek().name().charAt(0)+localNow.getDayOfWeek().name().substring(1).toLowerCase());
		 System.out.println(localNow.getHour() > 12 ? localNow.getHour()-12 : localNow.getHour());
		
	}

}
