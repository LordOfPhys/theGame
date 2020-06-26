package com.first.myfirstchat;

class RequestRoomStatusBody {

    public String room_name;
    public String token;

    RequestRoomStatusBody(String room_name, String token) {
        this.room_name = room_name;
        this.token = token;
    }
}
