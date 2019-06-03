package com.kong.spring.eventbus;

/**
 * @author: kong wang
 * @date: 2019/6/3
 */
public class TestMessage {
    private String name;

    public TestMessage() {
    }

    public TestMessage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
