package com.eggsy.rxbus.annotation;

import com.eggsy.rxbus.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.reactivex.BackpressureStrategy;

/**
 * Created by eggsy on 17-1-25.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscribe {

    ThreadMode tmode() default ThreadMode.PostThread;

    BackpressureStrategy backpressurestrategy() default BackpressureStrategy.DROP;

}
