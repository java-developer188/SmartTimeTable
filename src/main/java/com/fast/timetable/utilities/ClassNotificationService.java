package com.fast.timetable.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClassNotificationService
{
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    volatile boolean isStopIssued;
    private int modEightCounter = 0;
    private int earlyNotifyHour = 7;
    
	@Autowired
    ClassNotificationTask classNotificationTask;

    public static void main(String[] args) {

		ClassNotificationService cns = new ClassNotificationService();
		cns.startExecutionAt(16, 28, 0);
	}
    

    public int getEarlyNotifyHour() {
		return earlyNotifyHour;
	}

	public void setEarlyNotifyHour(int earlyNotifyHour) {
		this.earlyNotifyHour = 8 - earlyNotifyHour;
	}

    
    public void startExecutionAt(int targetHour, int targetMin, int targetSec)
    {
        Runnable taskWrapper = new Runnable(){

            @Override
            public void run() 
            {
            	modEightCounter++;
                classNotificationTask.execute();
                modEightCounter = modEightCounter % 8;
                int hour =  modEightCounter + getEarlyNotifyHour() ;
                
                startExecutionAt(hour, targetMin, targetSec);
            }

        };
        long delay = computeNextDelay(targetHour, targetMin, targetSec);
        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay(int targetHour, int targetMin, int targetSec) 
    {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);

        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }

    public void stop()
    {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClassNotificationTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}