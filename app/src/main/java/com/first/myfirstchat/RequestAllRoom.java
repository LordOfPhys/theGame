package com.first.myfirstchat;

import com.google.gson.annotations.SerializedName;

class RequestAllRoom {
    @SerializedName("rooms")
    public String[] rooms;

    @SerializedName("x")
    public String[] x_coord;

    @SerializedName("y")
    public String[] y_coord;
}
