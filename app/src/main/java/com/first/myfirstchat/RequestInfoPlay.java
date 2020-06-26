package com.first.myfirstchat;

import com.google.gson.annotations.SerializedName;

class RequestInfoPlay {
    @SerializedName("count_player")
    public String count_player;

    @SerializedName("killer_message")
    public String killer_message;

    @SerializedName("target_message")
    public String target_message;

    @SerializedName("killer_x")
    public String killer_x;

    @SerializedName("killer_y")
    public String killer_y;

    @SerializedName("target_x")
    public String target_x;

    @SerializedName("target_y")
    public String target_y;

    @SerializedName("user_status")
    public String user_status;
}
