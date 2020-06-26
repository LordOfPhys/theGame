package com.first.myfirstchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String REQUEST_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_FINE_LOCAION_STATE = 10001;
    public EditText login;
    public EditText password;
    public Button btn_register;
    public EditText email;
    public Button r2l;
    public TextView error_text;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_FINE_LOCAION_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Wrong answer", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{REQUEST_FINE_LOCATION}, REQUEST_FINE_LOCAION_STATE);
                return ;
            }
        }
        error_text = (TextView) findViewById(R.id.register_error_txt);
        login = (EditText) findViewById(R.id.login_id);
        password = (EditText) findViewById(R.id.password_id);
        btn_register = (Button) findViewById(R.id.btn_register);
        email = (EditText) findViewById(R.id.email_id);
        r2l = (Button) findViewById(R.id.btn_reg_2_log);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regExpn =
                        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                                +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
                Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email.getText());
                if (matcher.matches()) {
                    if (login.getText().toString().length() >= 3 && password.getText().toString().length() >=8) {
                        RequestRegistrationBody message = new RequestRegistrationBody(login.getText().toString(),
                                password.getText().toString(),
                                email.getText().toString());
                        TryToRegister(message);
                    } else {
                        error_text.setText("Логин должен содержать не менее 3-х символов. Пароль должен содержать не менее 8 символов " +
                                "(минимум один знак верхнего регистра, один знак нижнего регистра и одну цифру)");
                    }
                } else {
                    error_text.setText("Email должен быть правильного формата");
                }
            }
        });

        r2l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
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
                if (response == null) {
                    return;
                }
                if (response.body().token.equals("Bad boy")) {
                } else {
                    Toast.makeText(MainActivity.this, "Регистрация успешна", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<RequestRegistration> call, Throwable t) {
            }
        });
    }
}