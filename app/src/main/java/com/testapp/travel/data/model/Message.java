package com.testapp.travel.data.model;

public class Message {
    public String idSender;
    public String idReceiver;
    public String text;
    public long timestamp;


    public Message(){
        idSender = "0";
        idReceiver = "0";
        text = "";
        timestamp = 0;
    }
}
