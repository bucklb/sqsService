package com.example.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Idea is that this will take sqsClient and use it to generate messages for an observer/exchange to handle (or maybe a handler)

    Do we need an intermediary or just push handler(s) in to the MessageSources?



 */
public class SqsMessageExchange {

    SqsMessageSource sqsQueueService;
    ExecutorService executor;

    // Take in a "poller" that needs to run its own thread
    public SqsMessageExchange(SqsMessageSource sqsQueueService){
        this.sqsQueueService = sqsQueueService;
    }

    // Start providing messages.  Maybe this is where we generate the thread
    public void start() {
        executor = Executors.newFixedThreadPool(2);
        executor.execute(sqsQueueService);
    }





}
