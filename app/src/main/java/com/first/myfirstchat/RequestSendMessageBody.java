package com.first.myfirstchat;

class RequestSendMessageBody {
    public String token;
    public String message;

    public RequestSendMessageBody(String token, String message) {
        this.token = token;
        this.message = message;
    }
}
