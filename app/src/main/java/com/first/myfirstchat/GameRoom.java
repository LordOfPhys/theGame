package com.first.myfirstchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GameRoom extends AppCompatActivity {

    int time = 0;
    public Timer timer;
    public TextView room_len;
    public String user_status;
    public Button reg_2_room;
    public Button exit_room;
    public Button btn_start_game;
    public String room_status;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        final Bundle arguments = getIntent().getExtras();
        user_status = "1";
        room_status = "0";
        btn_start_game = (Button) findViewById(R.id.btn_start_game);
        room_len = (TextView) findViewById(R.id.id_room_len);
        room_len.setText("1");
        reg_2_room = (Button) findViewById(R.id.btn_reg_2_room);
        exit_room = (Button) findViewById(R.id.btn_exit_room);

        timer = new Timer();
        long delay = 10;
        long period = 2000;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user_status.equals("1")) {
                            reg_2_room.setClickable(false);
                        } else {
                            reg_2_room.setClickable(true);
                        }
                        if (Integer.parseInt(room_len.getText().toString()) >= 5) {
                            btn_start_game.setClickable(true);
                            btn_start_game.setTextColor(getResources().getColor(R.color.accept));
                        } else {
                            btn_start_game.setClickable(false);
                            btn_start_game.setTextColor(getResources().getColor(R.color.deny));
                        }
                        if (room_status.equals("1")) {
                            Intent intent = new Intent(GameRoom.this, RoomGameProgress.class);
                            intent.putExtra("token", arguments.get("token").toString());
                            startActivity(intent);
                            timer.cancel();
                        }
                        if (arguments.get("room") == null) {
                            GetRoomStatusYet(arguments.get("token").toString());
                        } else {
                            GetRoomStatus(arguments.get("room").toString(), arguments.get("token").toString());
                        }
                    }
                });
            }
        }, delay, period);

        reg_2_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TryToRegRoom(arguments.get("token").toString(), arguments.get("room").toString());
            }
        });
        exit_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_status.equals("0")) {
                } else {
                    ExitRoom(arguments.get("token").toString());
                }
                Intent intent = new Intent(GameRoom.this, MainRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
                timer.cancel();
            }
        });
        btn_start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(room_len.getText().toString()) >= 5) {
                    TryStartGame(arguments.get("token").toString());
                    Intent intent = new Intent(GameRoom.this, RoomGameProgress.class);
                    intent.putExtra("token", arguments.get("token").toString());
                    startActivity(intent);
                    timer.cancel();
                }
            }
        });
    }

    private void TryStartGame(String token) {
        final String BASE_URL = getString(R.string.url_server);
        GerringAPI gerritAPI;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerringAPI.class);
        Call<RequestStartGame> call = gerritAPI.tryStartGame(new RequestStartGameBody(token));
        call.enqueue(new Callback<RequestStartGame>() {
            @Override
            public void onResponse(Call<RequestStartGame> call, Response<RequestStartGame> response) {
            }
            @Override
            public void onFailure(Call<RequestStartGame> call, Throwable t) {
            }
        });
    }

    private void ExitRoom(String token) {
        final String BASE_URL = getString(R.string.url_server);
        GerringAPI gerritAPI;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerringAPI.class);
        Call<RequestExitRoom> call = gerritAPI.tryExitRoom(new RequestExitRoomBody(token));
        call.enqueue(new Callback<RequestExitRoom>() {
            @Override
            public void onResponse(Call<RequestExitRoom> call, Response<RequestExitRoom> response) {
            }
            @Override
            public void onFailure(Call<RequestExitRoom> call, Throwable t) {
            }
        });
    }

    private void TryToRegRoom(String token, final String room_name) {
        final String BASE_URL = getString(R.string.url_server);
        GerringAPI gerritAPI;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerringAPI.class);
        Call<RequestRegRoom> call = gerritAPI.try2RegRoom(new RequestRegRoomBody(token, room_name));
        call.enqueue(new Callback<RequestRegRoom>() {
            @Override
            public void onResponse(Call<RequestRegRoom> call, Response<RequestRegRoom> response) {
                if (response.body().result != null && response.body().result.toString().equals("yes")) {
                    reg_2_room.setClickable(false);
                    Toast.makeText(GameRoom.this, "Успех! Матч скоро начнется.", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.body().result.toString().equals("distance")) {
                        Toast.makeText(GameRoom.this, "Вы слишком далеко, что бы играть тут!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GameRoom.this, "Упс! Что то пошло не так...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<RequestRegRoom> call, Throwable t) {
            }
        });
    }

    private void GetRoomStatus(String room, String token) {
        final String BASE_URL = getString(R.string.url_server);
        GerringAPI gerritAPI;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerringAPI.class);
        Call<RequestRoomStatus> call = gerritAPI.getRoomSatus(new RequestRoomStatusBody(room, token));
        call.enqueue(new Callback<RequestRoomStatus>() {
            @Override
            public void onResponse(Call<RequestRoomStatus> call, Response<RequestRoomStatus> response) {
                if (response.body().room_len != null && response.body().user_status != null && response.body().room_status != null) {
                    room_len.setText(response.body().room_len);
                    user_status = response.body().user_status;
                    room_status = response.body().room_status;
                }
            }
            @Override
            public void onFailure(Call<RequestRoomStatus> call, Throwable t) {
            }
        });
    }

    private void GetRoomStatusYet(String token) {
        final String BASE_URL = getString(R.string.url_server);
        GerringAPI gerritAPI;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        gerritAPI = retrofit.create(GerringAPI.class);
        Call<RequestRoomStatusYet> call = gerritAPI.getRoomSatusYet(new RequestRoomStatusYetBody(token));
        call.enqueue(new Callback<RequestRoomStatusYet>() {
            @Override
            public void onResponse(Call<RequestRoomStatusYet> call, Response<RequestRoomStatusYet> response) {
                if (response.body().room_len != null && response.body().user_status != null && response.body().room_status != null) {
                    room_len.setText(response.body().room_len);
                    user_status = response.body().user_status;
                    room_status = response.body().room_status;
                }
            }
            @Override
            public void onFailure(Call<RequestRoomStatusYet> call, Throwable t) {
            }
        });
    }
}