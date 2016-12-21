package com.gjiazhe.panoramaimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by gjz on 19/12/2016.
 */

public class PanoramaImageView extends ImageView implements SensorEventListener {

    private boolean mEnablePanoramaMode = true;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private static final float NS2S = 1.0f / 1000000000.0f; // For translate nanosecond to second.
    private long mLastTimestamp = 0; // The time in nanosecond when last sensor event happened.
    private float mTotalRotateY = 0; //

    // Image's width and height
    private int mDrawableWidth;
    private int mDrawableHeight;
    // View's width and height
    private int mWidth;
    private int mHeight;

    public PanoramaImageView(Context context) {
        this(context, null);
    }

    public PanoramaImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanoramaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initSensorManager();

        setScaleType(ScaleType.CENTER_CROP);
    }

    private void initSensorManager() {
        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mSensorManager == null || mSensor == null) {
            initSensorManager();
        }
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("register", "register");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
            mSensor = null;
            Log.d("unregister", "unregister");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mLastTimestamp == 0) {
            mLastTimestamp = event.timestamp;
            return;
        }

        float rotateX = Math.abs(event.values[0]);
        float rotateY = Math.abs(event.values[1]);
        float rotateZ = Math.abs(event.values[2]);

        if (rotateY > rotateX && rotateY > rotateZ) {
            final float dT = (event.timestamp - mLastTimestamp) * NS2S;
            mTotalRotateY += event.values[1] * dT;
            if (mTotalRotateY < -Math.PI || mTotalRotateY > Math.PI) {
                mTotalRotateY = (float) (mTotalRotateY - (int)((mTotalRotateY + Math.PI) / 2 / Math.PI) * 2 * Math.PI);
            }
            if (mTotalRotateY >= -Math.PI/6 && mTotalRotateY <= Math.PI/6) {
                invalidate();
            }
        }

        mLastTimestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        mDrawableWidth = getDrawable().getIntrinsicWidth();
        mDrawableHeight = getDrawable().getIntrinsicHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mEnablePanoramaMode || getDrawable() == null) {
            super.onDraw(canvas);
            return;
        }

        if (mDrawableWidth <= 0 || mDrawableHeight <= 0) {
            return;
        }

        if (mDrawableWidth * mHeight > mDrawableHeight * mWidth) {
            float imgScale = (float) mHeight / (float) mDrawableHeight;
            float max_dx = Math.abs((mDrawableWidth * imgScale - mWidth) * 0.5f);
            float translateX = (float) (-6 * max_dx / Math.PI * mTotalRotateY);

            //  Restrict it from exceeding the bounds
            if (translateX < -max_dx) {
                translateX = -max_dx;
            } else if (translateX > max_dx) {
                translateX = max_dx;
            }

            canvas.translate(translateX, 0);
        }

        super.onDraw(canvas);
    }

    public void setEnablePanoramaMode(boolean enable) {
        if (mEnablePanoramaMode != enable) {
            mEnablePanoramaMode = enable;
            mLastTimestamp = 0;
            if (enable) {
                initSensorManager();
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            } else if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
                mSensorManager = null;
                mSensor = null;
            }
        }
    }
}
