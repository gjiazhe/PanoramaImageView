package com.gjiazhe.panoramaimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by gjz on 19/12/2016.
 */

public class PanoramaImageView extends ImageView {

    // Enable panorama effect or not
    private boolean mEnablePanoramaMode = true;

    // If true, the image scroll left when the device clockwise rotate along y-axis.
    private boolean mInvertScrollDirection = true;

    // Image's width and height
    private int mDrawableWidth;
    private int mDrawableHeight;

    // View's width and height
    private int mWidth;
    private int mHeight;

    // Image's offset along x-axis from initial state(center in the view).
    private float mMaxOffsetX;

    // The scroll progress, form -1 to 1.
    private float mProgress;

    // Show scroll bar or not
    private boolean mEnableScrollbar = true;

    // The paint to draw scrollbar
    private Paint mScrollbarPaint;

    public PanoramaImageView(Context context) {
        this(context, null);
    }

    public PanoramaImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanoramaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        super.setScaleType(ScaleType.CENTER_CROP);

        if (mEnableScrollbar) {
            initScrollbarPaint();
        }
    }

    public void initScrollbarPaint() {
        mScrollbarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScrollbarPaint.setColor(Color.WHITE);
        mScrollbarPaint.setStrokeWidth(dp2px(1));
    }

    public void setGyroscopeObserver(GyroscopeObserver observer) {
        if (observer != null) {
            observer.addPanoramaImageView(this);
        }
    }

    void updateProgress(float progress) {
        if (mEnablePanoramaMode) {
            mProgress = progress;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (getDrawable() != null) {
            mDrawableWidth = getDrawable().getIntrinsicWidth();
            mDrawableHeight = getDrawable().getIntrinsicHeight();

            float imgScale = (float) mHeight / (float) mDrawableHeight;
            mMaxOffsetX = Math.abs((mDrawableWidth * imgScale - mWidth) * 0.5f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mEnablePanoramaMode || getDrawable() == null || isInEditMode()) {
            super.onDraw(canvas);
            return;
        }

        // Draw image
        if (mDrawableWidth * mHeight > mDrawableHeight * mWidth) {
            float currentOffsetX = mMaxOffsetX * mProgress;
            if (mInvertScrollDirection) {
                currentOffsetX = -currentOffsetX;
            }
            canvas.save();
            canvas.translate(currentOffsetX, 0);
            super.onDraw(canvas);
            canvas.restore();
        }

        // Draw scrollbar
        if (mEnableScrollbar) {
            float barBgWidth = mWidth * 0.9f;
            float barWidth = barBgWidth * mWidth / mDrawableWidth;

            float barBgStartX = mWidth/2 - barBgWidth/2;
            float barBgEndX = barBgStartX + barBgWidth;
            float barStartX = mInvertScrollDirection ?
                    barBgStartX + (barBgWidth-barWidth)/2 * (1 + mProgress):
                    barBgStartX + (barBgWidth-barWidth)/2 * (1 - mProgress);
            float barEndX = barStartX + barWidth;
            float barY = mHeight * 0.9f;

            mScrollbarPaint.setAlpha(100);
            canvas.drawLine(barBgStartX, barY, barBgEndX, barY, mScrollbarPaint);
            mScrollbarPaint.setAlpha(255);
            canvas.drawLine(barStartX, barY, barEndX, barY, mScrollbarPaint);
        }
    }

    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public void setEnablePanoramaMode(boolean enable) {
        mEnablePanoramaMode = enable;
    }

    public boolean isPanoramaModeEnabled() {
        return mEnablePanoramaMode;
    }

    public void setInvertScrollDirection(boolean invert) {
        if (mInvertScrollDirection != invert) {
            mInvertScrollDirection = invert;
        }
    }

    public boolean isInvertScrollDirection() {
        return mInvertScrollDirection;
    }

    public void setEnableScrollbar(boolean enable) {
        if (mEnableScrollbar != enable){
            mEnableScrollbar = enable;
            if (mEnableScrollbar) {
                initScrollbarPaint();
            } else {
                mScrollbarPaint = null;
            }
        }
    }

    public boolean isScrollbarEnabled() {
        return mEnableScrollbar;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        /**
         * Do nothing because PanoramaImageView only
         * supports {@link scaleType.CENTER_CROP}
         */
    }
}
