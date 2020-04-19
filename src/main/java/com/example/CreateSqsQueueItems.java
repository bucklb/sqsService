package com.example;

import com.example.service.SqsQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    Want this to simply fire stuff at the queues we're watching being polled.  Shouldn't get to production !!
 */
@SpringBootApplication
public class CreateSqsQueueItems implements CommandLineRunner {

    // Will want an easy way to create queue entries
    @Autowired
    @Qualifier("bbService")
    private SqsQueueService queueService;

    String  queueName = "testing";



    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(CreateSqsQueueItems.class);
        app.setBannerMode(Banner.Mode.OFF);

        app.run(args);
    }

    // This populates the queue and then (perhaps unexpectedly) kicks off proper service too
    @Override
    public void run(String... args) throws Exception {

        // Would help people (me especially) to register what is going on ..
        System.out.println("==========================================================");
        System.out.println("=== CreateSqsQueueItems before starting service proper ===");
        System.out.println("==========================================================");

        // Don't recall why this gets done ...
        queueService.doSQS();

        // Create a bunch of stuff on the queue and check that poller sees it
        queueService.sendMessage("a", queueName);
        queueService.sendMessage("b", queueName);
        queueService.sendMessage("c", queueName);
        queueService.sendMessage("d", queueName);
        queueService.sendMessage("e", queueName);
        queueService.sendMessage("f", queueName);


    }

}
