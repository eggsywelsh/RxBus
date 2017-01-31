package com.eggsy.rxbus.assist;

import com.eggsy.rxbus.ThreadMode;

import java.util.List;

/**
 * Created by eggsy on 17-1-31.
 *
 * annotation method's info
 */

public class ProxyMethodInfo {

    private String methodName;

    private ThreadMode threadMode;

    private List<ProxyParameterInfo> parameterInfos;

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

    public List<ProxyParameterInfo> getParameterInfos() {
        return parameterInfos;
    }

    public void setParameterInfos(List<ProxyParameterInfo> parameterInfos) {
        this.parameterInfos = parameterInfos;
    }
}
