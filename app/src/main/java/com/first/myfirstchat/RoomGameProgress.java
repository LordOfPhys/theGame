package com.first.myfirstchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoomGameProgress extends AppCompatActivity {

    int time = 0;
    public Timer timer;
    public TextView count_player;
    public TextView distance_killer;
    public TextView distance_target;
    public EditText your_mesage;
    public TextView message_killer;
    public TextView message_target;
    public Button btn_message;
    public Button btn_kill;
    public Button btn_exit;
    public LocationManager locationManager;
    public Location bestLocation = null;
    public double x_killer;
    public double y_killer;
    public double x_target;
    public double y_target;
    public String user_status;

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_game_progress);
        final Bundle arguments = getIntent().getExtras();
        user_status = "1";

        count_player = (TextView) findViewById(R.id.room_progress_player_count);
        distance_killer = (TextView) findViewById(R.id.room_progress_dist_killer);
        distance_target = (TextView) findViewById(R.id.room_progress_dist_target);
        your_mesage = (EditText) findViewById(R.id.room_progress_your_message);
        message_killer = (TextView) findViewById(R.id.room_progress_killer_message);
        message_target = (TextView) findViewById(R.id.room_progress_target_message);
        btn_exit = (Button) findViewById(R.id.room_progress_btn_exit);
        btn_kill = (Button) findViewById(R.id.room_progress_btn_kill);
        btn_message = (Button) findViewById(R.id.room_progress_btn_message);

        timer = new Timer();
        long delay = 10;
        long period = 2000;

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage(arguments.get("token").toString(), your_mesage.getText().toString());
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameOver(arguments.get("token").toString());
            }
        });

        btn_kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KillUser(arguments.get("token").toString());
            }
        });

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    Activity#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                return;
                            }
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                1000 * 10, 10, locationListener);
                        List<String> providers = locationManager.getProviders(true);
                        for (String provider : providers) {
                            Location l = locationManager.getLastKnownLocation(provider);
                            if (l == null) {
                                continue;
                            }
                            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                                // Found best last known location: %s", l);
                                bestLocation = l;
                            }
                        }
                        GetInfoPlay(arguments.get("token").toString());
                        if (user_status.equals("win")) {
                            Intent intent = new Intent(RoomGameProgress.this, RoomWin.class);
                            intent.putExtra("token", arguments.get("token").toString());
                            startActivity(intent);
                            timer.cancel();
                        }
                        if (user_status.equals("0")) {
                            Intent intent = new Intent(RoomGameProgress.this, RoomLose.class);
                            intent.putExtra("token", arguments.get("token").toString());
                            startActivity(intent);
                            timer.cancel();
                        }
                        distance_killer.setText(Double.toString(getDistance(bestLocation.getLatitude(), bestLocation.getLongitude(), x_killer, y_killer)));
                        distance_target.setText(Double.toString(getDistance(bestLocation.getLatitude(), bestLocation.getLongitude(), x_target, y_target)));
                        if (Double.parseDouble(distance_target.getText().toString()) <= 70.0) {
                            btn_kill.setClickable(true);
                            btn_kill.setTextColor(getResources().getColor(R.color.accept));
                        } else {
                            btn_kill.setClickable(false);
                            btn_kill.setTextColor(getResources().getColor(R.color.deny));
                        }
                    }
                });
            }
        }, delay, period);
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI / 180.0);
    }

    public double getDistance(double x, double y, double x_1, double y_1) {
        double R = 6371.0;
        double dLat = deg2rad(x_1 - x);
        double dLon = deg2rad(y_1 - y);
        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0) + Math.cos(deg2rad(x)) * Math.cos(deg2rad(x_1)) * Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c * 1000;
        return Math.ceil(d);
    }

    private void KillUser(final String token) {
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
        Call<RequestKillUser> call = gerritAPI.killUser(new RequestKillUserBody(token));
        call.enqueue(new Callback<RequestKillUser>() {
            @Override
            public void onResponse(Call<RequestKillUser> call, Response<RequestKillUser> response) {
                if (response.body().result.toString().equals("win")) {
                    Intent intent = new Intent(RoomGameProgress.this, RoomWin.class);
                    intent.putExtra("token", token);
                    startActivity(intent);
                    timer.cancel();
                }
            }
            @Override
            public void onFailure(Call<RequestKillUser> call, Throwable t) {
            }
        });
    }

    private void GameOver(String token) {
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
        Call<RequestGameOver> call = gerritAPI.gameOver(new RequestGameOverBody(token));
        call.enqueue(new Callback<RequestGameOver>() {
            @Override
            public void onResponse(Call<RequestGameOver> call, Response<RequestGameOver> response) {
            }
            @Override
            public void onFailure(Call<RequestGameOver> call, Throwable t) {
            }
        });
    }

    private void SendMessage(String token, String message) {
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
        Call<RequestSendMessage> call = gerritAPI.sendMessage(new RequestSendMessageBody(token, message));
        call.enqueue(new Callback<RequestSendMessage>() {
            @Override
            public void onResponse(Call<RequestSendMessage> call, Response<RequestSendMessage> response) {
            }
            @Override
            public void onFailure(Call<RequestSendMessage> call, Throwable t) {
            }
        });
    }

    private void GetInfoPlay(String token) {
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
        Call<RequestInfoPlay> call = gerritAPI.getInfoPlay(new RequestInfoPlayBody(token));
        call.enqueue(new Callback<RequestInfoPlay>() {
            @Override
            public void onResponse(Call<RequestInfoPlay> call, Response<RequestInfoPlay> response) {
                count_player.setText(response.body().count_player.toString());
                message_killer.setText(response.body().killer_message.toString());
                message_target.setText(response.body().target_message.toString());
                x_killer = Double.parseDouble(response.body().killer_x);
                y_killer = Double.parseDouble(response.body().killer_y);
                x_target = Double.parseDouble(response.body().target_x);
                y_target = Double.parseDouble(response.body().target_y);
                user_status = response.body().user_status;
            }
            @Override
            public void onFailure(Call<RequestInfoPlay> call, Throwable t) {
            }
        });
    }
}