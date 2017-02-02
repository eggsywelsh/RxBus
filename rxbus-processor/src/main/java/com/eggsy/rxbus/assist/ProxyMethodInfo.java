package com.eggsy.rxbus.assist;

import com.eggsy.rxbus.Strategy;
import com.eggsy.rxbus.ThreadMode;

/**
 * Created by eggsy on 17-1-31.
 *
 * annotation method's info
 */

public class ProxyMethodInfo {

    private String methodName;

    private ThreadMode threadMode;

    private Strategy backpressureStrategy;

    private ProxyParameterInfo parameterInfo;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public ProxyParameterInfo getParameterInfo() {
        return parameterInfo;
    }

    public void setParameterInfo(ProxyParameterInfo parameterInfo) {
        this.parameterInfo = parameterInfo;
    }

    public Strategy getBackpressureStrategy() {
        return backpressureStrategy;
    }

    public void setBackpressureStrategy(Strategy backpressureStrategy) {
        this.backpressureStrategy = backpressureStrategy;
    }
}
