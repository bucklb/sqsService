package com.example;

import com.example.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static java.lang.System.exit;

@SpringBootApplication
public class SQSServiceApplication { //implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        // Will cause sqsRunner to run
        SpringApplication.run( SQSServiceApplication.class, args );
    }
}