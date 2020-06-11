package com.example.service;

public interface MessageSource {

    public void begin();
    public void cease();
    public String getQueueUrl();

}
