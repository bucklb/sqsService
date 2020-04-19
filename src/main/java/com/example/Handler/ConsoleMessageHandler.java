package com.example.Handler;

import org.springframework.stereotype.Component;

/*
    Baby steps. Just push the message to console with something about who wrote it there
 */
public class ConsoleMessageHandler implements MessageHandlerService {


    @Override
    public boolean handle(String msgText) {

        System.out.println("ConsoleHandler : " + msgText);
        return false;
    }
}
