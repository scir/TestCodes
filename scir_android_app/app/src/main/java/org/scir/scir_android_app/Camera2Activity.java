package org.scir.scir_android_app;

import android.app.Activity;
import android.os.Bundle;

import org.sss.library.camera.Camera2BasicFragment;

public class Camera2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance(getApplicationContext()))
                    .commit();
        }
    }
}