package com.first.myfirstchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {

    public Button btn_back_rules;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        final Bundle arguments = getIntent().getExtras();
        btn_back_rules = (Button) findViewById(R.id.btn_rules_back);
        btn_back_rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RulesActivity.this, MainRoom.class);
                intent.putExtra("token", arguments.get("token").toString());
                startActivity(intent);
            }
        });
    }
}
