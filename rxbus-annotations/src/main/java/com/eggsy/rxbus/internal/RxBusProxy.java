package com.eggsy.rxbus.internal;

import io.reactivex.disposables.Disposable;

/**
 * Created by eggsy on 17-1-25.
 */

public interface RxBusProxy<S> {

    Disposable register(S source);

    void unRegister(S source);

}
