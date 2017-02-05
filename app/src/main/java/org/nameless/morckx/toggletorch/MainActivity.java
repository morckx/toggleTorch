package org.nameless.morckx.toggletorch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    protected static CameraManager mCameraManager;
    protected static String mRearCameraId;
    protected static boolean mTorchEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }

    static void initialize(Context c) {
        mCameraManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
        mCameraManager.registerTorchCallback(new TorchModeCallback(), null);
        mRearCameraId = getRearCameraId();
    }

    public static void toggleFlashLight() {
        try {
            mCameraManager.setTorchMode(mRearCameraId, !mTorchEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRearCameraId() {
        if (mRearCameraId == null) {
            try {
                for (final String cameraId : mCameraManager.getCameraIdList()) {
                    final CameraCharacteristics characteristics =
                            mCameraManager.getCameraCharacteristics(cameraId);
                    final int orientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (orientation == CameraCharacteristics.LENS_FACING_BACK) {
                        mRearCameraId = cameraId;
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                // Ignore
            }
        }
        return mRearCameraId;
    }

    private static class TorchModeCallback extends CameraManager.TorchCallback {
        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (!cameraId.equals(mRearCameraId)) return;
            mTorchEnabled = enabled;
        }

        @Override
        public void onTorchModeUnavailable(String cameraId) {
            if (!cameraId.equals(mRearCameraId)) return;
            mTorchEnabled = false;
        }
    }

    public static class ToggleTorchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mRearCameraId == null)
                initialize(context);
            toggleFlashLight();
        }
    }

    public static class MyBootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initialize(context);
        }
    }
}