package com.dragbone.dg_fy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConnectActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        final AppCompatActivity activity = this;
        final View button = findViewById(R.id.connectButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.hostInput);
                MainActivity.host = input.getText().toString();

                Intent myIntent = new Intent(activity, MainActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
