package com.tornado.chatbot.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TitleGenerator {
    public String generate() {
        return "New Chat " + System.currentTimeMillis() % 1000;
    }
}
