package com.example;

import com.example.Domain.Payload;
import com.example.service.SqsMessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private SqsMessageSource queueService;

    @Autowired
    @Qualifier("subService")
    private SqsMessageSource subService;

    String  queueName = "testing";



    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(CreateSqsQueueItems.class);
        app.setBannerMode(Banner.Mode.OFF);

        app.run(args);
    }

    private String getMsgText(Payload p){
        String s="";
        ObjectMapper om = new ObjectMapper();

        try {
            s =om.writeValueAsString(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }


    // This populates the queue and then (perhaps unexpectedly) kicks off proper service too
    @Override
    public void run(String... args) throws Exception {

        // Would help people (me especially) to register what is going on ..
        System.out.println("==========================================================");
        System.out.println("=== CreateSqsQueueItems before starting service proper ===");
        System.out.println("==========================================================");

        // Allow flick between add queue messages and not
        boolean fill = true;
//        fill = false;
        if(fill) {

            // Create a bunch of stuff on the queue and check that poller sees it
            queueService.sendMessage(getMsgText(new Payload("a","A")));
            queueService.sendMessage("b");
            queueService.sendMessage(getMsgText(new Payload("z","Z")));
            queueService.sendMessage("d");
            queueService.sendMessage(getMsgText(null));
            queueService.sendMessage("f");

            subService.sendMessage("1st");
            subService.sendMessage("2nd");
        }
    }

}
