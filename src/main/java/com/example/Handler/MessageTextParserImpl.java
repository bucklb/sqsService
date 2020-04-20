package com.example.Handler;

import com.example.Domain.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MessageTextParserImpl implements MessageTextParser{

    ObjectMapper om=new ObjectMapper();

    // Will want to be able to pass in something to handle Crypto
    // and might also want bespoke validator passed in to


    @Override
    public String parseMessageText(String messageText) {
        return isValidPayload(messageText) ? messageText : null;
    }

    // Crypto - might want it to respond with 401/403 type answer, via exception?


    // Check can be mapped to a submission.  Do we want to throw an "unparseable" exception?
    private boolean isValidPayload(String msgText) {
        boolean ok = false;
        if ( msgText != null ) {
            try {
                Payload p = om.readValue(msgText, Payload.class);

                // If we got anything resembling a payload, is it fairly valid?
                if ( p!= null ) {
                    if (p.getRef() != null && p.getDtl() != null) ok = true;
                }

            } catch (Exception e) {
//                System.out.println("!! Unable to map to a Payload");
//                e.printStackTrace();
            }
        }
        return ok;
    }




}
