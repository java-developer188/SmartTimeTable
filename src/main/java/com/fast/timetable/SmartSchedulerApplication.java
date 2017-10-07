package com.fast.timetable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@ComponentScan(basePackages={"com.fast"})
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaRepositories("com.fast.timetable.repository")
public class SmartSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartSchedulerApplication.class, args);
//		AutomaticFetcher automaticFetcher = new AutomaticFetcher();
//		automaticFetcher.execute();
	}
}
