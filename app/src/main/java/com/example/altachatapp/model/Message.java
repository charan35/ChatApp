package com.example.altachatapp.model;

public class Message{
    public String idSender;
    public String idReceiver;
    public String text;
    public String urlFile;
    public long timestamp;
    public String type;
    public String address;
    public String latitude;
    public String longitude;
    public String localFileUrl;
    public String download;

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }
}
