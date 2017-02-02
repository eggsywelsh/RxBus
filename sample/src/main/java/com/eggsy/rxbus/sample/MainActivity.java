package com.eggsy.rxbus.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.eggsy.rxbus.RxBus;
import com.eggsy.rxbus.annotation.EventSubscribe;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxBus.register(this);
    }

    @EventSubscribe
    public void test(String testParam) {
        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_test)
    public void clickStepTest(View v){
        Intent postEventIntent = new Intent(this,PostEventActivity.class);
        startActivity(postEventIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.unRegister(this);
    }
}
