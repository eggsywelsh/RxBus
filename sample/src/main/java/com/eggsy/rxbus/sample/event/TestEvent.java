package com.eggsy.rxbus.sample.event;

/**
 * Created by eggsy on 17-2-4.
 */

public class TestEvent {

    private int times;

    private String content;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TestEvent{" +
                "times=" + times +
                ", content='" + content + '\'' +
                '}';
    }
}
