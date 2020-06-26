package com.first.myfirstchat;

public class RequestRegistrationBody {
    public String login;
    public String password;
    public String email;

    RequestRegistrationBody(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }
}
