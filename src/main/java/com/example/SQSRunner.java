package com.example;

import com.example.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SQSRunner implements CommandLineRunner{

    // Get a queue service to do the good stuff for us
    @Autowired
    private QueueService queueService;

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("Runner is running ...");

        boolean pollMode = true;
        String  queueName = "testing";
        long sleepyTime=10000;
        String  msgTxt = "";


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
    }
}