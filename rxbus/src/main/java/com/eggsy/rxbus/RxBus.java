package com.eggsy.rxbus;

import com.eggsy.rxbus.internal.RxBusProxy;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;

/**
 * Created by eggsy on 17-1-25.
 */

public final class RxBus {

    public static final String SUFFIX = "_RxBusProxy";

    /**
     * proxyInstanceMap
     * key:proxy class full name
     * value:proxy instance
     */
    static HashMap<String, RxBusProxy> proxyInstanceMap = new HashMap<>();

    public static Disposable register(Object object) {
        Disposable disposable = null;
        RxBusProxy rxBusProxy = findRxBusProxy(object);
        if (rxBusProxy != null) {
            disposable = rxBusProxy.register(object);
        }
        return disposable;
    }

    public static void unRegister(Object object) {
        RxBusProxy rxBusProxy = findRxBusProxy(object);
        if (rxBusProxy != null) {
            rxBusProxy.unRegister();
            removeRxBusProxy(rxBusProxy);
        }
    }

    public static void post(Object event){
        RxBusHelper.getDefault().post(event);
    }

    private static RxBusProxy findRxBusProxy(Object object) {
        try {
            Class clazz = object.getClass();
            if (proxyInstanceMap.get(clazz.getName()) != null) {
                return proxyInstanceMap.get(clazz.getName());
            }
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            RxBusProxy instance = (RxBusProxy) injectorClazz.newInstance();
            proxyInstanceMap.put(clazz.getName(), instance);
            return instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(String.format("can not find %s , something when compiler.", object.getClass().getSimpleName() + SUFFIX));
    }

    private static void removeRxBusProxy(Object object) {
        Class clazz = object.getClass();
        RxBusProxy proxy = proxyInstanceMap.get(clazz.getName());
        if (proxy != null) {
            proxyInstanceMap.remove(clazz.getName());
            proxy = null;
        }
    }

}
