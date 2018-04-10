package com.ligf.androiduilib;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * 自定义ImageView<p>
 * 具有手势缩放、双击缩放、放大后可拖动的功能<p>
 *
 * Created by ligf on 2017/2/7.
 */
public class GFZoomImageView extends AppCompatImageView{

    private final String TAG = getClass().getName();
    private Context mContext = null;
    private enum State {NONE, DRAG, ZOOM, ANIMATE_ZOOM}
    private State mState = State.NONE;
    private Matrix mMatrix = null;
    private ScaleType mScaleType = null;
    private int mViewWidth;
    private int mViewHeight;
    private float mMatchViewWidth;
    private float mMatchViewHeight;
    private float mNormalScale;
    /**
     * the min scale size of the image
     */
    private float mMinScale;
    /**
     * the max scale size of the image
     */
    private float mMaxScale;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private GestureDetector mGestureDetector = null;
    private float[] m;

    public GFZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public GFZoomImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public GFZoomImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        mMatrix = new Matrix();
        mScaleType = ScaleType.MATRIX;
        setScaleType(mScaleType);
        setImageMatrix(mMatrix);
        setState(State.NONE);
        mNormalScale = 1.0f;
        mMinScale = 0.75f;

        /**
         * the max scale size of the image
         */
        mMaxScale = 6.0f;
        mScaleGestureDetector = new ScaleGestureDetector(mContext, new ZoomImageViewScaleGestureListener());
        mGestureDetector = new GestureDetector(new ZoomImageViewGestureListener());
        m = new float[9];
        super.setOnTouchListener(new ZoomImageViewOnTouchListener());
    }

    private void setState(State state){
        mState = state;
    }

    private class ZoomImageViewOnTouchListener implements OnTouchListener{

        private PointF startPoint = new PointF();
        private PointF endPoint = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScaleGestureDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
            endPoint.set(event.getX(), event.getY());
            if (mState == State.NONE || mState == State.DRAG){
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        setState(State.DRAG);
                        startPoint.set(event.getX(), event.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mState == State.DRAG){
                            float deltaX = endPoint.x - startPoint.x;
                            float deltaY = endPoint.y - endPoint.y;

                            float fixX = getFixDragTrans(deltaX, getImageWidth(), mViewWidth);
                            float fixY = getFixDragTrans(deltaY, getImageHeight(), mViewHeight);
                            mMatrix.postTranslate(fixX, fixY);
                            fixTrans();
                            startPoint.set(endPoint);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        setState(State.NONE);

                        break;
                }
            }
            setImageMatrix(mMatrix);
            return true;
        }
    }

    private float getFixDragTrans(float delta, float contentSize, float viewSize){
        if (contentSize < viewSize){
            return 0;
        }
        return delta;
    }

    private class ZoomImageViewScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            setState(State.NONE);
            float targetZoom = mNormalScale;
            if (mNormalScale > mMaxScale){
                targetZoom = mMaxScale;
            }
            if (mNormalScale < 1){
                targetZoom = 1;
            }
            DoubleTapZoom doubleTapZoom = new DoubleTapZoom(targetZoom, mViewWidth / 2, mViewHeight /2);
            compatPostOnAnimation(doubleTapZoom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
            setMeasuredDimension(0, 0);
            return;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mViewWidth = getViewSize(widthMode, widthSize, drawableWidth);
        mViewHeight = getViewSize(heightMode, heightSize, drawableHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
        fitImageToView();
    }

    /**
     * get the view size base on the layout params
     * @param mode
     * @param size
     * @param drawableSize
     * @return
     */
    private int getViewSize(int mode, int size, int drawableSize){
        int viewSize;
        switch (mode){
            case MeasureSpec.EXACTLY:
                viewSize = size;
                break;
            case MeasureSpec.AT_MOST:
                viewSize = Math.min(drawableSize, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                viewSize = size;
                break;
            default:
                viewSize = size;
        }
        return viewSize;
    }

    /**
     * fit the image to the center of the view
     */
    private void fitImageToView(){
        Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0){
            return;
        }
        if (mMatrix == null){
            return;
        }
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        float scaleX = (float) mViewWidth / drawableWidth;
        float scaleY = scaleX;
        float XSpace = mViewWidth - scaleX * drawableWidth;
        float YSpace = mViewHeight - scaleY * drawableHeight;
        mMatchViewWidth = mViewWidth - XSpace;
        mMatchViewHeight = mViewHeight - YSpace;
        mMatrix.setScale(scaleX, scaleY);
        if (mMatchViewHeight > mViewHeight){
            mMatrix.postTranslate(0, 0);
        } else {
            mMatrix.postTranslate(XSpace / 2, YSpace / 2);
        }
        setImageMatrix(mMatrix);
    }

    /**
     * scale the image
     * @param deltaScale
     * @param focusX
     * @param focusY
     */
    private void scaleImage(double deltaScale, float focusX, float focusY){
        float originalScale = mNormalScale;
        mNormalScale *= deltaScale;
        if (mNormalScale > mMaxScale){
            mNormalScale = mMaxScale;
            deltaScale = mMaxScale / originalScale;
        }
        if (mNormalScale < mMinScale){
            mNormalScale = mMinScale;
            deltaScale = mMinScale / originalScale;
        }
        fixScaleTranslate();
        Log.i(TAG, "focusX: " + focusX + ";focusY: " + focusY);
        mMatrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
//        mMatrix.postScale((float) deltaScale, (float) deltaScale);
    }

    /**
     * fit the image to the center of the view after scaling
     */
    private void fixScaleTranslate(){
        fixTrans();
        mMatrix.getValues(m);
        Log.i(TAG, "fixScaleTranslate trans_x:" + m[Matrix.MTRANS_X] + ";trans_y:" + m[Matrix.MTRANS_Y]);
        if (getImageWidth() < mViewWidth){
            m[Matrix.MTRANS_X] = (mViewWidth - getImageWidth()) / 2;
        }
        if (getImageHeight() < mViewHeight){
            m[Matrix.MTRANS_Y] = (mViewHeight - getImageHeight()) / 2;
        }
        mMatrix.setValues(m);
    }

    private void fixTrans() {
        mMatrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, mViewWidth, getImageWidth());
        float fixTransY = getFixTrans(transY, mViewHeight, getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            mMatrix.postTranslate(fixTransX, fixTransY);
        }
    }

    private float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;

        } else {
            minTrans = viewSize - contentSize;      //image内容大于view
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;     //缩小时或者拖动时，防止超出右侧边界（向右移动）（下侧同理）
        if (trans > maxTrans)
            return -trans + maxTrans;       //缩小时或者拖动时，防止超出左侧边界（向左移动）（上侧同理）
        return 0;
    }

    private float getImageWidth(){
        return mNormalScale * mMatchViewWidth;
    }

    private float getImageHeight(){
        return mNormalScale * mMatchViewHeight;
    }

    private class DoubleTapZoom implements Runnable{

        private long startTime;
        private static final float ZOOM_TIME = 500;
        private float startZoom;
        private float targetZoom;
        private float focusX;
        private float focusY;
        private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

        public DoubleTapZoom(float targetZoom, float focusX, float focusY){
            startTime = System.currentTimeMillis();
            startZoom = mNormalScale;
            this.targetZoom = targetZoom;
            this.focusX = focusX;
            this.focusY = focusY;
        }

        @Override
        public void run() {
            setState(State.ANIMATE_ZOOM);
            float t = interpolate();
            Log.i(TAG, "run t:" + t);
            double deltaScale = calculateDeltaScale(t);
            scaleImage(deltaScale, focusX, focusY);
            fixScaleTranslate();
            setImageMatrix(mMatrix);
            if (t < 1f){
                compatPostOnAnimation(this);
            } else {
                setState(State.NONE);
            }
        }

        private float interpolate() {
            long currTime = System.currentTimeMillis();
            float elapsed = (currTime - startTime) / ZOOM_TIME;
            elapsed = Math.min(1f, elapsed);
            return interpolator.getInterpolation(elapsed);
        }

        private double calculateDeltaScale(float t) {
            double zoom = startZoom + t * (targetZoom - startZoom);
            return zoom / mNormalScale;
        }
    }

    private void compatPostOnAnimation(Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable);
        } else {
            postDelayed(runnable, 1000 / 60);
        }
    }

    private class ZoomImageViewGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            boolean consumed = false;
            if (mState == State.NONE){
                float targetZoom = (mNormalScale == 1) ? mMaxScale : 1;
                DoubleTapZoom doubleTapZoom = new DoubleTapZoom(targetZoom, e.getX(), e.getY());
                compatPostOnAnimation(doubleTapZoom);
                consumed = true;
            }
            return consumed;
        }
    }
}
