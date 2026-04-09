package com.workfarm.timemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeManagementApplication.class, args);
    }
}
