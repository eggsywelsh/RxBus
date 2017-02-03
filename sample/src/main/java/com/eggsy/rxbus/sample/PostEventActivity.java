package com.eggsy.rxbus.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.eggsy.rxbus.RxBus;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by eggsy on 17-2-2.
 */

public class PostEventActivity extends AppCompatActivity {

    private int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_post)
    public void clickPostEvent(View view) {
        RxBus.post("eggsy test " + (i++) + " times");
        RxBus.post(true);
        RxBus.post(new Boolean(false));
    }
}
