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
     */
    IoThread,

    /**
     *
     */
    NewThread,

    /**
     *
     */
    Single,

    /**
     *
     */
    Computation,

    /**
     *
     */
    Trampoline


}
