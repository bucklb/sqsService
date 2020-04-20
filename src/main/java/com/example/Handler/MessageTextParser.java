package com.example.Handler;

public interface MessageTextParser {

    // Idea is that parse will cover any crypto and any valdation (possibly via a mapper)
    public String parseMessageText(String messageText);

}
