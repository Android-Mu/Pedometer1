package com.click369.pedometer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
/**
 * IntentService服务类
 * @author Administrator
 */
public abstract class MyService extends IntentService {
   
    public MyService(String name) {
        super(name);
        mName = name;
    }

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private boolean mRedelivery;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
            stopSelf(msg.arg1);
        }
    }

    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    /**
     *　  其实不应该为IntentService重写这个方法。
　　       *  重写onHandleIntent,IntentService时系统会请求receiver。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    //除非你在service里也调用bind,不然不需要实现这个方法,因为默认实现返回null。
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**在工作线程调用该方法要求的过程。一次只有一个意图处理,但处理上发生工作线程运行独立于其他应用程序逻辑。
     * 因此,如果这段代码需要很长时间,它会耽误其他请求IntentService相同,但它不会耽误什么。
     * 当所有请求已经处理,IntentService会自动停止
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    protected abstract void onHandleIntent(Intent intent);
}
