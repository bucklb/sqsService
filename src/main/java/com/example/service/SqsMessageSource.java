package com.example.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Idea is that this will take sqsClient and use it to generate messages for an observer/exchange to handle (or maybe a handler)
 */
public class SqsMessageSource {

    SqsQueueService sqsQueueService;
    ExecutorService executor;

    // Take in a "poller" that needs to run its own thread
    public SqsMessageSource(SqsQueueService sqsQueueService){
        this.sqsQueueService = sqsQueueService;
    }

    // Start providing messages.  Maybe this is where we generate the thread
    public void start() {
        executor = Executors.newFixedThreadPool(2);
        executor.execute(sqsQueueService);
    }





}
