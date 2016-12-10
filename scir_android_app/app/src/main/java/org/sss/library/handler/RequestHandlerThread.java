package org.sss.library.handler;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by khelender on 09-12-2016.
 */

public class RequestHandlerThread {

    public final static int MSG_SCIR_FEEDBACK_POINT = 0x02 ;
    final static int MSG_SCIR_OTHER = 0x04 ;

    static private RequestHandlerThread mRequestHandlerThread ;
    static private ScirRequestProcessingHandler mScirRequestProcessingHandler ;
    private HandlerThread mHandlerThread ;
    private Handler mHandler;
    private Looper mLooper ;

    private RequestHandlerThread(String name) {
        // Start a new thread
        mHandlerThread = new HandlerThread(name);
        mHandlerThread.start();
        // Extract Handler for it. Looper is already created for this!
        mLooper = mHandlerThread.getLooper() ;
        mHandler = new Handler(mLooper) {
            @Override
            public void handleMessage(Message msg) {
                // Do some specific processing if it required
                // EXample : some cross checks !!
                switch(msg.what) {
                    case MSG_SCIR_FEEDBACK_POINT:
                        Log.d("ScirMessageComponent","RequestReceived"+msg.obj);
                        break;
                    default:
                        // Handle the error scenario !
                        break;
                }
                super.handleMessage(msg);
            }
        };
        mScirRequestProcessingHandler = new ScirRequestProcessingHandler(mLooper);
    }

    public synchronized static RequestHandlerThread getRequestHandlerThread() {
        if( mRequestHandlerThread == null ) {
            mRequestHandlerThread = new RequestHandlerThread("ScirRequestHandlerThread");
        }
        return mRequestHandlerThread;
    }

    public static ScirRequestProcessingHandler getScirRequestProcessingHandler() {
        return getRequestHandlerThread().mScirRequestProcessingHandler;
    }

    public Handler getSubmitHandler() {
        return mHandler ;
    }

}
