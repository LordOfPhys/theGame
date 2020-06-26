package com.first.myfirstchat;

class RequestCreateRoomBody {
    public String token;
    public String room_name;
    public double lat;
    public double lon;

    RequestCreateRoomBody(String token, String room_name, double lat, double lon) {
        this.token = token;
        this.room_name = room_name;
        this.lat = lat;
        this.lon = lon;
    }
}
