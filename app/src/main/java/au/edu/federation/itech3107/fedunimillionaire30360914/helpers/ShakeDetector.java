package au.edu.federation.itech3107.fedunimillionaire30360914.helpers;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * How to detect shake event (calculation)
 * reference: https://stackoverflow.com/a/32803134
 */
public class ShakeDetector implements SensorEventListener {

    private static final String LOG_TAG = ShakeDetector.class.getSimpleName();
    private static final long SHAKE_TIMEOUT = 2000L;
    private static final float SHAKE_THRESHOLD = 4.0f;

    private final OnShakeListener mOnShakeListener;
    private long mLastShakeTime;

    public interface OnShakeListener {
        void onShake();
    }


    public ShakeDetector(OnShakeListener onShakeListener) {
        this.mOnShakeListener = onShakeListener;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        long now = System.currentTimeMillis();
        if ((now - mLastShakeTime) > SHAKE_TIMEOUT) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
            Log.d(LOG_TAG, "[SENSOR] Acceleration is " + String.format("%.4f", acceleration) + "m/s^2");

            if (acceleration > SHAKE_THRESHOLD) {
                mLastShakeTime = now;
                Log.d(LOG_TAG, "[SENSOR] Shake!");
                mOnShakeListener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
