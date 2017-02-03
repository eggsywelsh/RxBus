package com.eggsy.rxbus.internal;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by eggsy on 17-1-25.
 */

public interface RxBusProxy<S> {

    CompositeDisposable register(S source);

    void unRegister();

}
