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
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
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


    /*
        Create a topic (we may want more than one).  Return corresponding arn
     */
    private String createTopic(String theTopic) {

        System.out.println("creating topic request " + theTopic);
        CreateTopicRequest myCreateTopicRequest = new CreateTopicRequest(theTopic);

        System.out.println("creating topic " + theTopic);
        CreateTopicResult createTopicResult = sns.createTopic(myCreateTopicRequest);
        String topicArn = sns.createTopic(myCreateTopicRequest).getTopicArn();

        // Purge the topic and create afresh.  Seems needed while I stop and start things
        if (true) {
            System.out.println("purging topic");
            DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
            sns.deleteTopic(deleteTopicRequest);
            topicArn = sns.createTopic(myCreateTopicRequest).getTopicArn();
        }

        return topicArn;
    }


    // This populates the queue and then (perhaps unexpectedly) kicks off proper service too
    @Override
    public void run(String... args) throws Exception {

        // Would help people (me especially) to register what is going on ..
        System.out.println("=====================================================");
        System.out.println("=== CreateSnsItems before starting service proper ===");
        System.out.println("=====================================================");

        // Time to experiment with filtering.  Might have to be at SNS



        // Create SNS bits
        String myTopic = "bbTopic";
        String myTopicArn = createTopic(myTopic);

        String topicToo = "otherTopic";
        String topicTooArn = createTopic(topicToo);

        System.out.println("creating subscription");
        Topics.subscribeQueue(sns, sqs, myTopicArn, sqsService.getQueueUrl());
        Topics.subscribeQueue(sns, sqs, topicTooArn, sqsService.getQueueUrl());

//        System.out.println("Idling");
//        Thread.sleep(5000);

        System.out.println("creating on topic");
        if (true) {

            System.out.println("--------------------->>>>>");
            notificationMessagingTemplate.sendNotification(topicToo, "1st from .. topic", "nonsense");
            notificationMessagingTemplate.sendNotification(topicToo, "2nd from .. topic", "nonsense");
            notificationMessagingTemplate.sendNotification(topicToo, "3rd from .. topic", "nonsense");
            notificationMessagingTemplate.sendNotification(topicToo, " i from .. topic", "garbage");
            notificationMessagingTemplate.sendNotification(topicToo, " ii from .. topic", "garbage");
            notificationMessagingTemplate.sendNotification(topicToo, "iii from .. topic", "garbage");

            notificationMessagingTemplate.sendNotification(myTopic, "first  from .. topic", "stuff");
            notificationMessagingTemplate.sendNotification(myTopic, "second from .. topic", "stuff");
            notificationMessagingTemplate.sendNotification(myTopic, "third  from .. topic", "stuff");

        }
    }


//    public static void subscribeSQStoSNS(String queueUrl, String topicArn, AmazonSQS sqsClient, AmazonSNS snsClient) {
//
//
//        /* with AWS Java SDK v1, you could subscribe an SQS queue to an SNS topic by
//         * calling "Topics.subscribeQueue"()[1] where the endpoint object takes in queueUrl.
//         *
//         * with AWS Java SDK v2, to subscribe an SQS queue to an SNS topic
//         * one needs to use "SunscribeRequest.builder"() [2] where the endpoint object takes in queueArn.
//         *
//         * [1] https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sns/util/Topics.html
//         * [2] https://sdk.amazonaws.com/java/api/2.0.0-preview-11/software/amazon/awssdk/services/sns/model/SubscribeRequest.Builder.html
//         *
//         */
//
//        // Step 1: We call "get-queue-attributes" API to retrieve the SQS queue Arn
//
//        GetQueueAttributesRequest QueueAttributesRequest = GetQueueAttributesRequest.builder()
//                .queueUrl(queueUrl)
//                .attributeNamesWithStrings("All")
//                .build();
//
//        GetQueueAttributesResponse QueueAttributesResult = sqsClient.getQueueAttributes(QueueAttributesRequest);
//        Map<String, String> sqsAttributeMap = QueueAttributesResult.attributesAsStrings();
//
//        System.out.println("\n\n"+ QueueAttributesRequest);
//        String queueArn = sqsAttributeMap.get("QueueArn");
//        //System.out.println("\n\n"+ queueArn);
//
//
//        // Step 2: We call "SubscribeRequest.builder" to subscribe the SQS queue to the SNS topic
//
//        SubscribeRequest Qrequest = SubscribeRequest.builder()
//                .protocol("sqs")
//                .endpoint(queueArn)
//                .returnSubscriptionArn(true)
//                .topicArn(topicArn)
//                .build();
//
//        SubscribeResponse Qresponse = snsClient.subscribe(Qrequest);
//        System.out.println("\n\nCreated Subscription ARN: " + Qresponse.subscriptionArn()+ " " + " and StatusCode : " + Qresponse.sdkHttpResponse().statusCode());
//    }
}
