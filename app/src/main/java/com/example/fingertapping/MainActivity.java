package com.example.fingertapping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void browseClicked(View view) {
        Intent i=new Intent(getBaseContext(), BrowseActivity.class);
        startActivity(i);
    }

    public void tapTwoClicked(View view) {
        Intent i=new Intent(getBaseContext(), TwoTapActivity.class);
        startActivity(i);
    }
}
