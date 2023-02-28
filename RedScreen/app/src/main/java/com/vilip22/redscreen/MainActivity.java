package com.vilip22.redscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        // Check if device supports OpenGL ES 2.0
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        // Configuring surface view to use OpenGL ES 2.0
        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new RedScreenRenderer());
            renderSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
            return;
        }

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet) glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet) glSurfaceView.onResume();
    }

}