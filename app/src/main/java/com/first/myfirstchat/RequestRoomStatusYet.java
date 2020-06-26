package com.first.myfirstchat;

import com.google.gson.annotations.SerializedName;

class RequestRoomStatusYet {
    @SerializedName("room_len")
    public String room_len;

    @SerializedName("user_status")
    public String user_status;

    @SerializedName("room_status")
    public String room_status;
}
