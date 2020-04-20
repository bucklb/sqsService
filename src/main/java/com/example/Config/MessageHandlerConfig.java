package com.example.Config;

import com.example.Handler.ConsoleMessageTextHandler;
import com.example.Handler.MessageTextHandlerService;
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
    MessageTextHandlerService subHandler() {
        // Could return any different kind of handler (emailing handler, report handler, etc)
        return new ConsoleMessageTextHandler();
    }


}
