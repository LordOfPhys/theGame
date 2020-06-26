package com.first.myfirstchat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GerringAPI {
    @POST("/register/")
    Call<RequestRegistration> setData(@Body RequestRegistrationBody message);

    @POST("/login/")
    Call<RequestLogin> setDataLogin(@Body RequestLoginBody message);

    @Headers("Content-Type: text/html")
    @GET("/main_view")
    Call<RequestAllRoom> getRooms(@Header("Token") String token);

    @Headers("Content-Type: text/html")
    @GET("/logout_view")
    Call<RequestLogout> getLogout(@Header("Token") String token);

    @POST("/join_room/")
    Call<RequestRegRoom> try2RegRoom(@Body RequestRegRoomBody message);

    @POST("/look_room/")
    Call<RequestRoomStatus> getRoomSatus(@Body RequestRoomStatusBody room_name);

    @POST("/look_room_yet/")
    Call<RequestRoomStatusYet> getRoomSatusYet(@Body RequestRoomStatusYetBody token);

    @POST("/create_room/")
    Call<RequestCreateRoom> createRoom(@Body RequestCreateRoomBody message);

    @POST("/setLocation/")
    Call<RequestSendLocation>  sendLocation(@Body RequestSendBodyLocation data);

    @POST("/exit_room/")
    Call<RequestExitRoom> tryExitRoom(@Body RequestExitRoomBody message);

    @POST("/start_game/")
    Call<RequestStartGame> tryStartGame(@Body RequestStartGameBody message);

    @POST("/getInfoPlay/")
    Call<RequestInfoPlay> getInfoPlay(@Body RequestInfoPlayBody message);

    @POST("/sendMessage/")
    Call<RequestSendMessage> sendMessage(@Body RequestSendMessageBody message);

    @POST("/gameOver/")
    Call<RequestGameOver> gameOver(@Body RequestGameOverBody message);

    @POST("/killUser/")
    Call<RequestKillUser> killUser(@Body RequestKillUserBody message);
}