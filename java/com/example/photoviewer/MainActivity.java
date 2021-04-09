package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void exit(View view) {//退出程序
        System.exit(0);
    }

    public void Infomation(View view) {
        Intent intent= new Intent(MainActivity.this,InfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void start(View view) {
        Intent intent=new Intent(MainActivity.this,MultiviewActivity.class);
        startActivity(intent);
    }
}