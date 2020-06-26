package com.first.myfirstchat;

class RequestRegRoomBody {

    public String token;
    public String room_name;

    RequestRegRoomBody(String token, String room_name) {
        this.token = token;
        this.room_name = room_name;
    }
}
