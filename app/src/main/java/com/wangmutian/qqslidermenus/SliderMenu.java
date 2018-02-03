package com.wangmutian.qqslidermenus;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wangmutian on 2018/2/3.
 */

public class SliderMenu extends FrameLayout {
    private View menuView;
    private View mainView;
    public ViewDragHelper viewDragHelper;
    public int width;
    private float dragRange;//拖拽范围

    public SliderMenu(@NonNull Context context) {
        super(context);
        init();
    }

    public SliderMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SliderMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SliderMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        viewDragHelper = ViewDragHelper.create(this,callback);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //简单异常处理
        if(getChildCount()!=2){
            throw new IllegalArgumentException("田哥温馨提示：sliderMenu only have 2 childrend!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     *
     * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化宽高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.6f;
    }

    //处理是否需要拦截的
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewDragHelper帮我们判断是否需要拦截
        boolean result=viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {

            return (child==menuView) || (child==mainView);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return (int)dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(child == mainView){
                if(left<0) return 0;
                if(left > dragRange) left = (int)dragRange;
            }
            if(child == menuView){
                left = left - dx;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView == menuView){
                //
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };

}
