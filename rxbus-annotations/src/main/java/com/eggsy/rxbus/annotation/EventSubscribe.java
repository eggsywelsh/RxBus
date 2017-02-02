package com.eggsy.rxbus.annotation;

import com.eggsy.rxbus.Strategy;
import com.eggsy.rxbus.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by eggsy on 17-1-25.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscribe {

    ThreadMode tmode() default ThreadMode.PostThread;

    Strategy bpstrategy() default Strategy.DEFAULT;

}
