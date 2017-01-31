package com.eggsy.rxbus.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.eggsy.rxbus.annotation.EventSubscribe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @EventSubscribe
    public void test(int a,String b ){

    }
}
