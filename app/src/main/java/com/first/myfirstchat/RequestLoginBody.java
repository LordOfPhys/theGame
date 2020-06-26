package com.first.myfirstchat;

public class RequestLoginBody {
    public String login;
    public String password;

    RequestLoginBody(String login, String password) {
        this.login = login;
        this.password = password;
    }
}