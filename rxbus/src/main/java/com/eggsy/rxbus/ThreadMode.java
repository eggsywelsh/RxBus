package com.eggsy.rxbus;

/**
 * Created by eggsy on 17-1-25.
 */

public enum ThreadMode {

    /**
     * Subscriber will be called in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete is a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     */
    PostThread,

    /**
     * Subscriber will be called in Android's main thread (sometimes referred to as UI thread). If the posting thread is
     * the main thread, event handler methods will be called directly. Event handlers using this mode must return
     * quickly to avoid blocking the main thread.
     */
    MainThread,

    /**
     *
     * Subscriber will be called in thread-pool that will grow as needed,intended for IO-bound work.
     * This can be used for asynchronously performing blocking IO.Do not perform computational work on this scheduler. Use ComputationThread instead.
     */
    IoThread,

    /**
     * Subscriber will be called in a new thread everytime. no matter the posting event thread is the main thread or the background thread,
     * it will execute in an new thread.
     */
    NewThread,

    /**
     * Subscriber will be called in common, single-thread,support benchmarks that pipeline data from the main thread to some other thread and
     * avoid core-bashing of computation's round-robin nature
     */
    SingleThread,

    /**
     * Subscriber will be called in a new thread not in main thread,This can be used for event-loops, processing callbacks and other computational work
     * Do not perform IO-bound work on this scheduler. Use IoThread instead.
     */
    ComputationThread,

/*    *//**
     * Subscriber will be queues work on the current thread to be executed after the current work completes,work with the posting event thread
     *//*
    Trampoline*/


}
