package com.example.Config;

import com.example.Handler.ConsoleMessageHandler;
import com.example.Handler.MessageHandlerService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    Want a means to get different flavours of handler to link with sources
 */
@Configuration
public class MessageHandlerConfig {

    @Bean
    @Qualifier("subHandler")
    MessageHandlerService subHandler() {
        // Could return any different kind of handler (emailing handler, report handler, etc)
        return new ConsoleMessageHandler();
    }


}
