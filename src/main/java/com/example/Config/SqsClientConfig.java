package com.example.Config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

/**
 * components we'll need
 */
// Need an sqsClient to underpin sqsMessageSource
@Configuration
public class SqsClientConfig {

    String serviceEndpoint = "http://localhost:4576/";
    String signingRegion   = "us-east-1";
    String accessKey = "accessKey";
    String secretKey = "secretKey";
//    String queueUrl  = "queue/testing";

//    // TODO : AmazonSQSclient or AmazonSQS better option
//    @Bean
//    public AmazonSQSClient sqsClient() {
//
//        System.out.println("AmazonSQSClient");
//
//        // Build the client
//        AmazonSQSClient client = (AmazonSQSClient) AmazonSQSClientBuilder.standard()
//                    .withEndpointConfiguration(endpointConfiguration())
//                    .withCredentials(awsCredentialsProvider())
//                    .build();
//
//        return client;
//    }

    /**
     * Less deprecated approach
     * @return
     */
    @Bean
    public AmazonSQS amazonSQS(){

        AmazonSQS sqs= AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration())
                .withCredentials(awsCredentialsProvider())
                .build();

        return sqs;
    }

//    // 11/6/2020 - bundle this with the rest of the AWS stuff
//    @Bean
//    public AmazonSNS amazonSNS() {
//
//        return AmazonSNSClientBuilder
//                .standard()
//                .withEndpointConfiguration(endpointConfiguration())
//                .withCredentials(awsCredentialsProvider())
//                .build();
//    }

    // 11/6/2020 - bundle this with the rest of the AWS stuff
    @Bean
    public AmazonSNSClient amazonSNSClient() {

        return (AmazonSNSClient)AmazonSNSClientBuilder
                .standard()
//                .withRegion(signingRegion)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        "http://localhost:4575/",
                        signingRegion))
                .withCredentials(awsCredentialsProvider())
                .build();
    }

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS sns) {
        return new NotificationMessagingTemplate(sns);
    }

    // Security credentials
    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        return  new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        accessKey,
                        secretKey));
    }

    // End Pt Config
    @Bean
    public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(
                serviceEndpoint,
                signingRegion);
    }







}
