package com.example.handler;

import com.example.Domain.Payload;
import com.example.Handler.MessageTextParser;
import com.example.Handler.MessageTextParserImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;

public class testParser {

    ObjectMapper om = new ObjectMapper();
    MessageTextParser mtp = new MessageTextParserImpl();

    private String getPayloadAsText(Payload p){
        String s = null;
        try {
            s = om.writeValueAsString(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    @Test
    public void testParserWithNull(){

        String msg = mtp.parseMessageText(getPayloadAsText(null));
        Assert.assertNull(msg);
    }

    @Test
    public void testParserWithInValidPayload(){

        String msg = mtp.parseMessageText(getPayloadAsText(new Payload(null,null)));
        Assert.assertNull(msg);
    }


    @Test
    public void testParserWithValidPayload(){

        String msg = mtp.parseMessageText(getPayloadAsText(new Payload("fred","wilma")));
        System.out.println(msg);
        Assert.assertNotNull(msg);
    }


}
