package com.first.myfirstchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RoomWin extends AppCompatActivity {

    public Button back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_room);
        final Bundle arguments = getIntent().getExtras();

        back = (Button) findViewById(R.id.btn_back_to_the_main_room);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomWin.this, MainRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
            }
        });
    }
}
