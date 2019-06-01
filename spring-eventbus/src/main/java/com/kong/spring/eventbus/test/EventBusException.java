package com.kong.spring.eventbus.test;

/**
 * @author: kong wang
 * @date: 2019/5/31
 */
public class EventBusException extends RuntimeException {

    private static final long serialVersionUID = -2912559384646531479L;

    public EventBusException(String detailMessage) {
        super(detailMessage);
    }

    public EventBusException(Throwable throwable) {
        super(throwable);
    }

    public EventBusException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
