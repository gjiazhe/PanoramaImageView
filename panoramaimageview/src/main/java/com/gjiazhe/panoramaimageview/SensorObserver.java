package com.gjiazhe.panoramaimageview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.LinkedList;

/**
 * Created by naveen on 06/04/2017.
 */

public class SensorObserver implements SensorEventListener {
    private SensorManager mSensorManager;

    // For translate nanosecond to second.
    private static final float NS2S = 1.0f / 1000000000.0f;

    // The time in nanosecond when last sensor event happened.
    private long mLastTimestamp;

    // The radian the device already rotate along y-axis.
    private double mRotateRadianY;

    // The radian the device already rotate along x-axis.
    private double mRotateRadianX;
    // The maximum radian that the device should rotate along x-axis and y-axis to show image's bounds
    // The value must between (0, π/2].
    private double mMaxRotateRadian = Math.PI/2;

    // The PanoramaImageViews to be notified when the device rotate.
    private LinkedList<PanoramaImageView> mViews = new LinkedList<>();

    public void register(Context context) {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        PackageManager PM = context.getPackageManager();
        Sensor mSensor = null;
        if(PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
        else {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mLastTimestamp = 0;
        mRotateRadianY = mRotateRadianX = 0;
    }

    public void unregister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    void addPanoramaImageView(PanoramaImageView view) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mLastTimestamp == 0) {
            mLastTimestamp = event.timestamp;
            return;
        }

        float rotateX = event.values[0];
        float rotateY = event.values[1];
        float rotateZ = event.values[2];

        switch(event.sensor.getType()) {
            case Sensor.TYPE_GYROSCOPE:
                rotateX = Math.abs(rotateX);
                rotateY = Math.abs(rotateY);
                rotateZ = Math.abs(rotateZ);

                if (rotateY > rotateX + rotateZ) {
                    final float dT = (event.timestamp - mLastTimestamp) * NS2S;
                    mRotateRadianY += event.values[1] * dT;
                    if (mRotateRadianY > mMaxRotateRadian) {
                        mRotateRadianY = mMaxRotateRadian;
                    } else if (mRotateRadianY < -mMaxRotateRadian) {
                        mRotateRadianY = -mMaxRotateRadian;
                    } else {
                        for (PanoramaImageView view: mViews) {
                            if (view != null && view.getOrientation() == PanoramaImageView.ORIENTATION_HORIZONTAL) {
                                view.updateProgress((float) (mRotateRadianY / mMaxRotateRadian));
                            }
                        }
                    }
                } else if (rotateX > rotateY + rotateZ) {
                    final float dT = (event.timestamp - mLastTimestamp) * NS2S;
                    mRotateRadianX += event.values[0] * dT;
                    if (mRotateRadianX > mMaxRotateRadian) {
                        mRotateRadianX = mMaxRotateRadian;
                    } else if (mRotateRadianX < -mMaxRotateRadian) {
                        mRotateRadianX = -mMaxRotateRadian;
                    } else {
                        for (PanoramaImageView view: mViews) {
                            if (view != null && view.getOrientation() == PanoramaImageView.ORIENTATION_VERTICAL) {
                                view.updateProgress((float) (mRotateRadianX / mMaxRotateRadian));
                            }
                        }
                    }
                }
                mLastTimestamp = event.timestamp;
                break;

            case Sensor.TYPE_ACCELEROMETER:
                for (PanoramaImageView view: mViews) {
                    //treating 1g(9.8 m/s^2) = Math.PI/2,
                    if(rotateX > 9.8f)
                        mRotateRadianX = 9.8f;
                    else
                        mRotateRadianX = rotateX;

                    if(rotateY > 9)
                        mRotateRadianY = 9;
                    else
                        mRotateRadianY = rotateY;

                    if (view != null && view.getOrientation() == PanoramaImageView.ORIENTATION_HORIZONTAL) {
                        if(rotateX > (9.8f*(mMaxRotateRadian/1.57f)))
                            mRotateRadianX = (9.8f*(float)(mMaxRotateRadian/1.57f));
                        else if(rotateX < -(9.8f*(float)(mMaxRotateRadian/1.57f)))
                            mRotateRadianX = -(9.8f*(float)(mMaxRotateRadian/1.57f));
                        else
                            mRotateRadianX = rotateX;
                        float update = (float) ((mRotateRadianX)/9.8f);
                        view.updateProgress(update);
                    }
                    else if(view != null && view.getOrientation() == PanoramaImageView.ORIENTATION_VERTICAL){
                        if(rotateY > (9.8f*(mMaxRotateRadian/1.57f)))
                            mRotateRadianY = (9.8f*(float)(mMaxRotateRadian/1.57f));
                        else if(rotateX < -(9.8f*(float)(mMaxRotateRadian/1.57f)))
                            mRotateRadianY = -(9.8f*(float)(mMaxRotateRadian/1.57f));
                        else
                            mRotateRadianY = rotateY;
                        float update = (float) ((mRotateRadianY)/9.8f);
                        view.updateProgress(update);
                    }
                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setMaxRotateRadian(double maxRotateRadian) {
        if (maxRotateRadian <= 0 || maxRotateRadian > Math.PI/2) {
            throw new IllegalArgumentException("The maxRotateRadian must be between (0, π/2].");
        }
        this.mMaxRotateRadian = maxRotateRadian;
    }
}
