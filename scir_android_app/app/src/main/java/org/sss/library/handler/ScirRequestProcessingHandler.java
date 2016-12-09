package org.sss.library.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.sss.library.scir.ScirInfraFeedbackPoint;

/**
 * Created by khelender on 09-12-2016.
 */

public class ScirRequestProcessingHandler extends Handler {

    private int numRequests = 0 ;

    /**
     * Use the provided {@link Looper} instead of the default one.
     *
     * @param looper The looper, must not be null.
     */
    public ScirRequestProcessingHandler(Looper looper) {
        super(looper);
    }

    /**
     * Subclasses must implement this to receive messages.
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        numRequests++ ;
        Log.d("BackendHandler","Received another request: Total Request:" + numRequests);
        ScirInfraFeedbackPoint scirInfraFeedbackPoint = (ScirInfraFeedbackPoint) msg.obj;
        Log.w("BackendHandler","Object details :" +
                "Problem Type:" + scirInfraFeedbackPoint.getScirDataProblemType().toString()+
                "Severity Type:" + scirInfraFeedbackPoint.getScirDataProblemSeverityLevel().toString()+
                "DateTime:" + scirInfraFeedbackPoint.getScirDataDateTimeFEServerForamt().toString()
            );
        super.handleMessage(msg);
    }
}
