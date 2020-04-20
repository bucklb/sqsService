package com.example.populate;

import com.example.Domain.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

/*
    Just want mechanism to create serialised message
 */
public class CreateMessageTest {

    @Test
    public void getMsgText(){

        ObjectMapper om = new ObjectMapper();

        Payload p = new Payload("the reference","the detail");

        try {
            System.out.println(om.writeValueAsString(p).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
