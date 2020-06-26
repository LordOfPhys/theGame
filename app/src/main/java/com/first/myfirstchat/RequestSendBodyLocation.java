package com.first.myfirstchat;

class RequestSendBodyLocation {
    public double lat;
    public double lon;
    public String token;

    public RequestSendBodyLocation(double lat, double lon, String token) {
        this.lat = lat;
        this.lon = lon;
        this.token = token;
    }
}
