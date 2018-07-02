package com.example.osj45.test2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = findViewById(R.id.btn1);

    }

    public void onClick(View view){
        setContentView(R.layout.change);
        Toast.makeText(getApplicationContext(),"Beep Bop",Toast.LENGTH_SHORT).show();
    }
}
