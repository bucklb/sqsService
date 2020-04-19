package com.example.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    Got mechanism to create the sqsClient (and associated paraphenalia)

    Look at mechanism to do similar for consumer that will then need the sqsClient and paraphenalia
 */
@Configuration
public class ConsumerConfig {

    // Will obviously want this to be a function of properties at some point
    String  queueName = "testing";


    // At heart, pass back a queueService (for now) that system can use
    @Bean
    public QueueService sqsService() {

        System.out.println("ConsumerConfig creating queueService as bean.  queueName = " +queueName );
        QueueService qs = new QueueService( queueName );
        return qs;
    }



}
