package org.sss.library.exception;

import android.util.Log;

/**
 * Created by khelender on 14-12-2016.
 */

public class SssUnhandledException extends Exception {

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public SssUnhandledException(String detailMessage) {
        super(detailMessage);
        Log.e("SssUnhandledException", "Unhandled exception : " + detailMessage);
    }
}
