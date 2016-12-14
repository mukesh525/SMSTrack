package com.sms.mcube.smstrack;

/**
 * Created by gousebabjan on 7/12/16.
 */

public class SmsData {
    private String id;
    private String from;
    private String text;
    private String time;
    private String to;
    public SmsData() {

    }
    public SmsData(String to, String from, String text, String time) {
        this.to = to;
        this.from = from;
        this.text = text;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SmsData(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
