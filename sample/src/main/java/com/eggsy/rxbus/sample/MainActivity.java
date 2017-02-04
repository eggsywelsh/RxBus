package com.eggsy.rxbus.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.eggsy.rxbus.RxBus;
import com.eggsy.rxbus.ThreadMode;
import com.eggsy.rxbus.annotation.EventSubscribe;
import com.eggsy.rxbus.sample.event.TestEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxBus.register(this);
    }

    @EventSubscribe(tmode = ThreadMode.IoThread)
    public void test(String testParam) {
        Log.i(TAG, "test IoThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test IoThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe(tmode = ThreadMode.NewThread)
    public void test2(String testParam) {
        Log.i(TAG, "test NewThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test NewThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe(tmode = ThreadMode.MainThread)
    public void test3(String testParam) {
        Log.i(TAG, "test MainThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test MainThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe(tmode = ThreadMode.ComputationThread)
    public void test4(String testParam) {
        Log.i(TAG, "test ComputationThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test ComputationThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe(tmode = ThreadMode.SingleThread)
    public void test5(String testParam) {
        Log.i(TAG, "test SingleThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test SingleThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe(tmode = ThreadMode.PostThread)
    public void test7(String testParam) {
        Log.i(TAG, "test PostThread, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test PostThread, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe
    public void testboolean(boolean testParam) {
        Log.i(TAG, "test boolean, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test boolean, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe
    public void testBoolean(Boolean testParam) {
        Log.i(TAG, "test Boolean, main thread id=" + getMainLooper().getThread().getId() + " , result=" + testParam);
        Log.i(TAG, "test Boolean, curr thread id=" + Thread.currentThread().getId() + " , result=" + testParam);
//        Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
    }

    @EventSubscribe
    public void testCustomEvent(TestEvent event) {
        Log.i(TAG, "test custom Event, event=" + event.toString());
    }

    @OnClick(R.id.btn_test)
    public void clickStepTest(View v) {
        Intent postEventIntent = new Intent(this, PostEventActivity.class);
        startActivity(postEventIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.unRegister(this);
    }
}
