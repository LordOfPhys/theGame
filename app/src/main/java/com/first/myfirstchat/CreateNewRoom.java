package com.first.myfirstchat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateNewRoom extends AppCompatActivity {

    public LocationManager locationManager;
    public Location bestLocation = null;
    public Button btn_createRoom;
    public Button btn_back;
    public EditText room_name;

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
        setContentView(R.layout.activity_create_room);
        final Bundle arguments = getIntent().getExtras();
        btn_back = (Button) findViewById(R.id.btn_back_create);
        btn_createRoom = (Button) findViewById(R.id.btn_create);
        room_name = (EditText) findViewById(R.id.id_new_room_name);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewRoom.this, MainRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
            }
        });

        btn_createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (room_name.getText().toString().length() > 0) {
                    CreateRoom(arguments.get("token").toString(), room_name.getText().toString(), bestLocation.getLatitude(), bestLocation.getLongitude());
                    doSaveState();
                    Intent intent = new Intent(CreateNewRoom.this, GameRoom.class);
                    intent.putExtra("token", arguments.get("token").toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateNewRoom.this, "Введите название комнаты.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doSaveState() {
        SharedPreferences sPref = this.getSharedPreferences("SavedState", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SavedState", "1");
        ed.apply();
    }

    private void CreateRoom (final String token, String room_name, double lat, double lon) {
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
        Call<RequestCreateRoom> call = gerritAPI.createRoom(new RequestCreateRoomBody(token, room_name, lat, lon));
        call.enqueue(new Callback<RequestCreateRoom>() {
            @Override
            public void onResponse(Call<RequestCreateRoom> call, Response<RequestCreateRoom> response) {
            }
            @Override
            public void onFailure(Call<RequestCreateRoom> call, Throwable t) {
            }
        });
    }
}
