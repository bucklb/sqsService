package com.example.populate;

import com.example.service.SqsMessageSource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan
public class PopulateQueue {

    // Will want an easy way to create queue entries
    @Autowired
    @Qualifier("bbService")
    private SqsMessageSource queueService;

    String  queueName = "testing";

    @Test
    public void addToQueue() {

        System.out.println(queueService);



    }


}
