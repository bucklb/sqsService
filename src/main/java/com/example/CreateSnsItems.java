package com.example;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.Topic;
import com.amazonaws.services.sns.util.Topics;
import com.amazonaws.services.sqs.AmazonSQS;
import com.example.Domain.Payload;
import com.example.service.SqsMessageSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.ComponentScan;

/*
    Look at putting stuff on to SNS rather than directly to queue(s)
 */
@SpringBootApplication
// Restrict the scan so we don't kick off everything (including the reader)
@ComponentScan("com.example.Config, com.example.Handler")
public class CreateSnsItems implements CommandLineRunner {

    // Want SNS (and a topic)
    @Autowired
    private AmazonSNS sns;

    @Autowired
    @Qualifier("bbService")
    private SqsMessageSource sqsService;

    @Autowired
    AmazonSQS sqs;

    @Autowired
    NotificationMessagingTemplate notificationMessagingTemplate;


    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(CreateSnsItems.class);
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
        System.out.println("=====================================================");
        System.out.println("=== CreateSnsItems before starting service proper ===");
        System.out.println("=====================================================");

        // Create SNS bits
        String myTopic = "bbTopic";


        System.out.println("creating topic request");
        CreateTopicRequest myCreateTopicRequest = new CreateTopicRequest(myTopic);

        System.out.println("creating topic");
        CreateTopicResult createTopicResult = sns.createTopic(myCreateTopicRequest);
        String myTopicArn = sns.createTopic(myCreateTopicRequest).getTopicArn();

        // Purge the topic and create afresh
        System.out.println("purging topic");
        DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(myTopicArn);
        sns.deleteTopic(deleteTopicRequest);
        myTopicArn = sns.createTopic(myCreateTopicRequest).getTopicArn();


        System.out.println("creating subscription");
        Topics.subscribeQueue(sns, sqs, myTopicArn, sqsService.getQueueUrl());

        System.out.println("creating on topic");
        if (true) {
            notificationMessagingTemplate.sendNotification(myTopic, "first  from .. topic", "stuff");
            notificationMessagingTemplate.sendNotification(myTopic, "second from .. topic", "stuff");
            notificationMessagingTemplate.sendNotification(myTopic, "third  from .. topic", "stuff");
        }
    }

}
