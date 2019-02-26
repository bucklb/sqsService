package com.example;

import com.example.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class SQSServiceApplication implements CommandLineRunner {

    // Get a queue service
    @Autowired
    private QueueService queueService;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(SQSServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    @Override
    public void run(String... args) throws Exception {

        boolean pollMode = false;
        String  queueName = "testing";
        long sleepyTime=10000;
        String  msgTxt = "";

//
//        System.out.println(
//            "SUMMARY REPORT: "
//            +"\ngeneration requested: "+11
//                +"\nprocessed: "+12
//                +"\nprocessed successful: "+(13)
//                +"\nprocessed error: "+14
//                +"\nprocessed warning: "+15 );


        // Put some space between us and the "reflection" moaning ...
        System.out.println(" ");
        System.out.println("===================== ");
        System.out.println(" ");


        // Two modes - poll will regularly check for messages.  Otherwise just push messages and then read them back
        if ( pollMode ){

            // When someone sticks stuff on the queue then report it
            while (true) {
                msgTxt = queueService.getMessage( queueName );
                Thread.sleep( sleepyTime );
            }

        } else {

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


        exit(0);
    }
}