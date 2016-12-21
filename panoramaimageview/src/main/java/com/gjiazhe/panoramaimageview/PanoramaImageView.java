package com.gjiazhe.panoramaimageview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by gjz on 19/12/2016.
 */

public class PanoramaImageView extends ImageView {

    // Enable panorama effect or not
    private boolean mEnablePanoramaMode = true;

    // If true, the image scroll left when the device clockwise rotate along y-axis.
    private boolean mInvertScroll = true;

    // Image's width and height
    private int mDrawableWidth;
    private int mDrawableHeight;
    // View's width and height
    private int mWidth;
    private int mHeight;

    // image's offset along x-axis from initial state(center in the view).
    private float mMaxOffsetX;
    private float mCurrentOffsetX;

    public PanoramaImageView(Context context) {
        this(context, null);
    }

    public PanoramaImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanoramaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        super.setScaleType(ScaleType.CENTER_CROP);
    }

    public void setGyroscopeObserver(GyroscopeObserver observer) {
        if (observer != null) {
            observer.addPanoramaImageView(this);
        }
    }

    void onGyroscopeObserverNotify(double progress) {
        mCurrentOffsetX = (float) (mMaxOffsetX * progress);
        if (mInvertScroll) {
            mCurrentOffsetX = -mCurrentOffsetX;
        }

        // Restrict it from exceeding the bounds
        if (mCurrentOffsetX < -mMaxOffsetX) {
            mCurrentOffsetX = -mMaxOffsetX;
        } else if (mCurrentOffsetX > mMaxOffsetX) {
            mCurrentOffsetX = mMaxOffsetX;
        } else {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        mDrawableWidth = getDrawable().getIntrinsicWidth();
        mDrawableHeight = getDrawable().getIntrinsicHeight();

        float imgScale = (float) mHeight / (float) mDrawableHeight;
        mMaxOffsetX = Math.abs((mDrawableWidth * imgScale - mWidth) * 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mEnablePanoramaMode) {
            if (mDrawableWidth * mHeight > mDrawableHeight * mWidth) {
                canvas.translate(mCurrentOffsetX, 0);
            }
        }

        super.onDraw(canvas);
    }

    public boolean isPanoramaModeEnabled() {
        return mEnablePanoramaMode;
    }

    public void setInvertScroll(boolean invert) {
        if (mInvertScroll != invert) {
            mInvertScroll = invert;
        }
    }

    public boolean isInvertScroll() {
        return mInvertScroll;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        /**
         * Do nothing because PanoramaImageView only
         * supports {@link scaleType.CENTER_CROP}
         */
    }
}
