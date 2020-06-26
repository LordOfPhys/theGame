package com.first.myfirstchat;

import com.google.gson.annotations.SerializedName;

class RequestSendLocation {
    @SerializedName("token")
    public String token;

    @SerializedName("user_status")
    public String user_status;
}
