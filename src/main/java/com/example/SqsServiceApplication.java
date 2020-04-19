package com.example;

import com.example.service.SqsQueueService;
import com.example.service.RunARound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private SqsQueueService sqsService;

//    @Autowired
//    @Qualifier("rawQueue")
//    private QueueService queueService;

//    @EventListener(ApplicationStartedEvent.class)
//    public void handleApplicationStarted(){
//        System.out.println("Application Started according to listener");
//    }

    // Play with excecutor
    ExecutorService executor;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(SqsServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    // Add more threads in a brutal way
    private void addThreads() {
        // This just occupies the existing thread
        System.out.println("Runaround ....");
        if (false) {
            Runnable runAround = new RunARound("first",1);
            runAround.run();
        } else {
            // Create the extra threads "manually"
            Runnable runAround = new RunARound("first",1);
            Thread t1 = new Thread(runAround);
            Runnable runAroundNow = new RunARound("second",2);
            Thread t2 = new Thread(runAroundNow);

            t1.start();
            t2.start();

            try {
                Thread.sleep(7500);
            } catch (Exception e){
            }

            t1.stop();
            t2.stop();

        }
    }

    private void addThreadExecutors(){
        RunARound runAround = new RunARound("primary",1);
        RunARound runAroundNow = new RunARound("secondary",2);

//        ExecutorService executor;
        executor = Executors.newFixedThreadPool(2);
        executor.execute(runAround);
        executor.execute(runAroundNow);

        // Add a means to kill it off after a short wait
        try {
            Thread.sleep(7500);

            runAround.stopIt();
            runAroundNow.stopIt();


            if ( !executor.awaitTermination(2, TimeUnit.SECONDS) ) {
                executor.shutdown();
//                executor.shutdownNow();
            }
        } catch (Exception e){
            e.printStackTrace();
            executor.shutdown();
            executor.shutdownNow();
        }
        executor.shutdown();



    }






    @Override
    public void run(String... args) throws Exception {

        boolean pollMode = false;
        String  queueName = "testing";
        long sleepyTime=10000;
        String  msgTxt = "";

        System.out.println("  sqsService using : " + sqsService.getQueueName());
//        System.out.println("queueService using : " + queueService.getQueueName());


//        addThreads();
//        addThreadExecutors();


        // Quick toggle
        pollMode=true;



        // Put some space between us and the "reflection" moaning ...
        System.out.println(" ");
        System.out.println("===================== ");
        System.out.println(" ");


        // Two modes - poll will regularly check for messages.  Otherwise just push messages and then read them back
        if ( pollMode ){

            // Look at kicking the sqsStuff off in its own thread
            if ( true ) {
                System.out.println("Starting sqsService in its own thread");
                Thread t1 = new Thread(sqsService);
                t1.start();
                System.out.println("sqsService running");

            } else {
                // When someone sticks stuff on the queue then report it
                while (true) {
// 19/4/20 - via Config instead?
//                msgTxt = queueService.getMessage( queueName );
                    msgTxt = sqsService.getMessage(queueName);
                    Thread.sleep(sleepyTime);
                }
            }
        } else {

            // BB 19/4/20 - work around
            SqsQueueService queueService = sqsService;

            //
            queueService.doSQS();

            // Create a bunch of stuff on the queue and then read it all straight back
            queueService.sendMessage("a", queueName);
            queueService.sendMessage("b", queueName);
            queueService.sendMessage("c", queueName);
            queueService.sendMessage("d", queueName);
            queueService.sendMessage("e", queueName);
            queueService.sendMessage("f", queueName);

            boolean b = true;
            while ( b ) {
                msgTxt = queueService.getMessage(queueName);
                b = (msgTxt != "");
            }

        }

        // Need to think when we end, given there could be multiple queues polling at an given time
//        exit(0);
    }
}