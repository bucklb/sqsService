package com.example;

import com.example.service.SqsMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    Pending creating a decent read me ...

    Started early 2019 playing with SQS (via local stack). Wanting to check some of the basic concepts/etc

    Need localStack, ideally run via docker.  On my home PC in ~/myProjects/localstack
        On my DEll use powershell & docker-compose up in root of localstack folder
    Can see what's deployed via localhost:8080/#/infra

    Don't need AWS CLI to run this suite, but useful to manually add stuff to queue & see what happens
    Add to queue with:
        aws --endpoint-url=http://localhost:4576 sqs send-message --queue-url http://localhost:4576/queue/testing --message-body 'Test Message!'
    NOTE : Probably need to have run this demo to make the queue available!!

    This app has two modes "configurable" by setting pollMode in QueueService.
        If pollMode is on then it simply waits for something to appear on the queue.
        If pollMode is off it populates the queue, then reads it, then stops, so not much use really

    April 2020 - want to look at having a queuePoller/listener that will want a handler injected (or in constructor) that
    the poller/listener will pass events to.

    In due course might want to have a queuePoller/listener factory that will return a suitable poller/listener for
    - rabbit
    - sqs
    - other


 */





@SpringBootApplication
public class SqsServiceApplication implements CommandLineRunner {

    // Get a queue service
// 19/4/20 - via Config instead?
    @Autowired
    @Qualifier("bbService")
    private SqsMessageSource sqsService;

    @Autowired
    @Qualifier("subService")
    private SqsMessageSource subService;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(SqsServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Put some space between us and the "reflection" moaning ...
        System.out.println(" ");
        System.out.println("============================================================= ");
        System.out.println("  sqsService using : " + sqsService.getQueueName());
        System.out.println(" ");

        // Configuration has created us some message sources.  Let's use them
        sqsService.begin();
        subService.begin();

        // Need to think when we end, given there could be multiple queues polling at an given time
//        exit(0);
    }
}