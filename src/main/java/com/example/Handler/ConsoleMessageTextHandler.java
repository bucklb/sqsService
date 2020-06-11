package com.example.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    Baby steps. Just push the message to console with something about who wrote it there
 */
@Component
public class ConsoleMessageTextHandler implements MessageTextHandlerService {

    private final String FORBIDDEN_TEXT = "verboten";

    @Autowired
    MessageTextParserImpl messageTextParser;

    @Override
    public int handle(String msgText) {

        int code=0;

        if (msgText.contains(FORBIDDEN_TEXT)) {
            // refuse to handle if it has forbidden text
            code = 0;
        } else {
            // Handled
            code = 1;
        }

        if( messageTextParser != null ) {
            String msg = messageTextParser.parseMessageText(msgText);
            System.out.println( "ConsoleHandler : " + msgText + " : after mtp -> " + msg );
        } else {
            System.out.println("No mtp");
        }




        return code;
    }
}
