package com.first.myfirstchat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainRoom extends AppCompatActivity {

    public ListView listRooms;
    public String[] rooms;
    int time = 0;
    public Timer timer;
    public Button btn_create_room;
    public Button btn_logout;
    public LocationManager locationManager;
    public Location bestLocation = null;
    public String user_status;
    public Button btn_rules;

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
        setContentView(R.layout.activity_main_room);
        final Bundle arguments = getIntent().getExtras();
        user_status = "0";
        rooms = new String[]{"Обновление списка..."};
        listRooms = (ListView) findViewById(R.id.list_id_rooms);
        btn_create_room = (Button) findViewById(R.id.btn_create_room);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_rules = (Button) findViewById(R.id.btn_2_rules);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        TryToSendLocation(arguments.get("token").toString(), bestLocation.getLatitude(), bestLocation.getLongitude());

        if (user_status.equals("1")) {
            Intent intent = new Intent(MainRoom.this, GameRoom.class);
            intent.putExtra("token", arguments.get("token").toString());
            startActivity(intent);
            timer.cancel();
        }

        timer = new Timer();
        long delay = 10;
        long period = 1500;
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run() {
                time++;
                        runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        GetRooms(arguments.get("token").toString());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainRoom.this,
                                android.R.layout.simple_list_item_1, rooms) {
                            @Override
                            public View getView (int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                // Initialize a TextView for ListView each Item
                                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                                // Set the text color of TextView (ListView Item)
                                tv.setTextColor(Color.WHITE);

                                // Generate ListView Item using TextView
                                return view;
                            }
                        };
                        listRooms.setAdapter(adapter);

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
                        if (user_status.equals("1")) {
                            Intent intent = new Intent(MainRoom.this, GameRoom.class);
                            intent.putExtra("token", arguments.get("token").toString());
                            startActivity(intent);
                            timer.cancel();
                        }
                        TryToSendLocation(arguments.get("token").toString(), bestLocation.getLatitude(), bestLocation.getLongitude());
                    }
                });
            }
        },delay,period);
        listRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainRoom.this, GameRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                intent.putExtra("room", parent.getAdapter().getItem(position).toString());
                startActivity(intent);
                timer.cancel();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(arguments.get("token").toString());
            }
        });

        btn_create_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRoom.this, CreateNewRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
                timer.cancel();
            }
        });

        btn_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRoom.this, RulesActivity.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
                timer.cancel();
            }
        });
    }

    private void doDelTok() {
        SharedPreferences sPref = this.getSharedPreferences("SavedText", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SavedText", "123");
        ed.apply();
    }

    private void Logout(String token) {
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
        Call<RequestLogout> call = gerritAPI.getLogout(token);
        call.enqueue(new Callback<RequestLogout>() {
            @Override
            public void onResponse(Call<RequestLogout> call, Response<RequestLogout> response) {
                doDelTok();
                Intent intent = new Intent(MainRoom.this, LoginActivity.class);
                startActivity(intent);
                timer.cancel();
            }
            @Override
            public void onFailure(Call<RequestLogout> call, Throwable t) {
            }
        });
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    private double getDistance(double x, double y, double x_1, double y_1) {
        double R = 6371;
        double dLat = deg2rad(x_1 - x);
        double dLon = deg2rad(y_1 - y);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(x)) * Math.cos(deg2rad(x_1)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.ceil(R * c * 1000);
    }

    private void GetRooms(String token) {
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
        Call<RequestAllRoom> call = gerritAPI.getRooms(token);
        call.enqueue(new Callback<RequestAllRoom>() {
            @Override
            public void onResponse(Call<RequestAllRoom> call, Response<RequestAllRoom> response) {
                ArrayList<Integer> indexies = new ArrayList<>();
                Log.d("length", String.valueOf(response.body().x_coord.length));
                for (int i = 0; i < response.body().x_coord.length; ++i) {
                    if (getDistance(Double.parseDouble(response.body().x_coord[i]), Double.parseDouble(response.body().y_coord[i]), bestLocation.getLatitude(), bestLocation.getLongitude()) < 30000) {
                        indexies.add(i);
                    }
                }
                ArrayList<String> arr = new ArrayList<>();
                for (int i = 0; i < indexies.size(); ++i) {
                    arr.add(response.body().rooms[indexies.get(i)]);
                }
                rooms = new String[arr.size()];
                for (int i = 0; i < arr.size(); ++i) {
                    rooms[i] = arr.get(i);
                }
            }
            @Override
            public void onFailure(Call<RequestAllRoom> call, Throwable t) {
            }
        });
    }

    private void TryToSendLocation(String token, double lat, double lon) {
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
        Call<RequestSendLocation> call = gerritAPI.sendLocation(new RequestSendBodyLocation(lat, lon, token));
        call.enqueue(new Callback<RequestSendLocation>() {
            @Override
            public void onResponse(Call<RequestSendLocation> call, Response<RequestSendLocation> response) {
                if (response.body().user_status != null ) {
                    user_status = response.body().user_status;
                }
            }

            @Override
            public void onFailure(Call<RequestSendLocation> call, Throwable t) {
            }
        });
    }
}