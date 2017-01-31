package com.eggsy.rxbus.internal;

import io.reactivex.disposables.Disposable;

/**
 * Created by eggsy on 17-1-25.
 */

public interface RxBusProxy {

    Disposable register(Object object);

    void unRegister(Object object);

}
