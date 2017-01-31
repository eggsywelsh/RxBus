package com.eggsy.rxbus.assist;

import java.util.HashMap;

/**
 * Created by eggsy on 17-1-31.
 *
 * record proxy class info
 */

public class ProxyClassInfo {

    private String proxyClassFullName;

    private HashMap<String,ProxyMethodInfo> proxyMethodInfoMap = new HashMap<>();

    public HashMap<String, ProxyMethodInfo> getProxyMethodInfoMap() {
        return proxyMethodInfoMap;
    }

    public void setProxyMethodInfoMap(HashMap<String, ProxyMethodInfo> proxyMethodInfoMap) {
        this.proxyMethodInfoMap = proxyMethodInfoMap;
    }

    public String getProxyClassFullName() {
        return proxyClassFullName;
    }

    public void setProxyClassFullName(String proxyClassFullName) {
        this.proxyClassFullName = proxyClassFullName;
    }
}
