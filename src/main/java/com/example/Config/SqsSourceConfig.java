package com.example.Config;

import com.example.Handler.MessageTextHandlerService;
import com.example.service.SqsMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    Want a means to get the stuff that will read our desired queues
 */
@Configuration
public class SqsSourceConfig {

    // Will obviously want this to be a function of properties at some point
    String  queueName;

    // Reading from the queue is pointless without something to handle the read message
    @Autowired
    @Qualifier("subHandler")
    private MessageTextHandlerService subHandler;


    // At heart, pass back a queueService (for now) that system can use
    @Bean
    @Qualifier("bbService")
    public SqsMessageSource sqsService() {
//        queueName = "testing";
        queueName = "apocalypse";
        System.out.println("ConsumerConfig creating queueService as bean.  queueName = " +queueName );
        SqsMessageSource qs = new SqsMessageSource( queueName );
        qs.setMessageHandler(subHandler);
        return qs;
    }

    // Need to see if multiple queues will work
    @Bean
    @Qualifier("subService")
    public SqsMessageSource subService() {
//        queueName = "trying";
        queueName = "lastbreath";
        System.out.println("ConsumerConfig creating queueService as bean.  queueName = " +queueName );
        SqsMessageSource qs = new SqsMessageSource( queueName );
        qs.setMessageHandler(subHandler);
        return qs;
    }


}
