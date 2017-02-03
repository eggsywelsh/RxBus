### RxBus
-----------------------------------------
Based on java APT technology, dynamically generated based on RxJava and RxAndroid code, and provides API to the user, so that users can easily register EventSubscribe annotation to receive the event method, and through the API to post the event in any place

### Import
-----------------------------------------
in you module's build.gradle then add below dependencies
````
dependencies {
    annotationProcessor 'com.eggsy:rxbus-processor:0.0.1'
    compile 'com.eggsy:rxbus:0.0.1'
    // RxJava and RxAndroid is necessary
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    compile 'io.reactivex.rxjava2:rxjava:2.0.4'
}
````

### How to use
In the class file with the EventSubscribe annotation, register it in the appropriate place
````
RxBus.register(this);
````

Subscribe to receive events
````
@EventSubscribe
public void testboolean(boolean testParam) {
    Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
}

// tmode, Specifies the thread to process after event reception
@EventSubscribe(tmode = ThreadMode.NewThread)
public void test(String testParam) {
    Log.i(TAG,"test IoThread, main thread id="+getMainLooper().getThread().getId()+" , result="+testParam);
    Log.i(TAG,"test IoThread, curr thread id="+Thread.currentThread().getId()+" , result="+testParam);
    Toast.makeText(this, testParam, Toast.LENGTH_SHORT).show();
}
````

Post an event anywhere
````
RxBus.post("eggsy test");
RxBus.post(true);
RxBus.post(new Boolean(false));
````

Then, where you want to unsubscribe, use the following method
````
 RxBus.unRegister(this);
````

### Sample
Fork or download my github project [RxBus](https://github.com/eggsywelsh) to see more Samples