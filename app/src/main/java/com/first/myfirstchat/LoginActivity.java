package com.first.myfirstchat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.VKTokenExpiredHandler;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.internal.VKErrorUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String REQUEST_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_FINE_LOCAION_STATE = 10001;
    public EditText login;
    public EditText password;
    public Button enter2reg;
    public Button enter;
    public TextView error;
    public Boolean try_social;
    public Button test_vk;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCAION_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoginActivity.this, "", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Wrong answer", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
            @Override
            public void onLoginFailed(int i) {

            }

            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                String base = vkAccessToken.getUserId().toString();
                TryToLog(new RequestLoginBody(base + "occisorGame123", base + "Gamilton321"));
                if (try_social.equals(false)) {
                    RequestRegistrationBody message = new RequestRegistrationBody(base + "occisorGame123", base + "Gamilton321", "GameOccisor@go.ru");
                    TryToRegister(message);
                    TryToLog(new RequestLoginBody(base + "occisorGame123", base + "Gamilton321"));
                }
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences mySharedPreferences = this.getSharedPreferences("SavedText", MODE_PRIVATE);
        String sTest = mySharedPreferences.getString("SavedText", "123");
        if (!sTest.equals("123")) {
            Intent intent = new Intent(LoginActivity.this, MainRoom.class);
            intent.putExtra("token", sTest.toString());
            startActivity(intent);
        }
        try_social = false;
        login = (EditText) findViewById(R.id.login_id_enter);
        password = (EditText) findViewById(R.id.password_id_enter);
        enter = (Button) findViewById(R.id.btn_enter);
        error = (TextView) findViewById(R.id.error_login_id);
        enter2reg = (Button) findViewById(R.id.btn_enter2reg);
        test_vk = (Button) findViewById(R.id.test_vk);

        test_vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VK.login(LoginActivity.this);
            }
        });

        enter2reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TryToLog(new RequestLoginBody(login.getText().toString(), password.getText().toString()));
            }
        });
    }

    private void doSave(String token) {
        SharedPreferences sPref = this.getSharedPreferences("SavedText", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("SavedText", token);
        ed.apply();
    }

    private void TryToLog(final RequestLoginBody message) {
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
        Call<RequestLogin> call = gerritAPI.setDataLogin(message);
        call.enqueue(new Callback<RequestLogin>() {
            @Override
            public void onResponse(Call<RequestLogin> call, Response<RequestLogin> response) {
                if (response.body().token.equals("-1")) {
                    try_social = false;
                    error.setText("Неверный логин или пароль");
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainRoom.class);
                    intent.putExtra("token", response.body().token.toString());
                    doSave(response.body().token.toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<RequestLogin> call, Throwable t) {
            }
        });
    }

    private void TryToRegister(RequestRegistrationBody message) {
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
        Call<RequestRegistration> call = gerritAPI.setData(message);
        call.enqueue(new Callback<RequestRegistration>() {
            @Override
            public void onResponse(Call<RequestRegistration> call, Response<RequestRegistration> response) {
            }

            @Override
            public void onFailure(Call<RequestRegistration> call, Throwable t) {
            }
        });
    }
}
