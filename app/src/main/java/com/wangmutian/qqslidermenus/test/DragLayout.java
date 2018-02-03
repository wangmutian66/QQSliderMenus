package com.wangmutian.qqslidermenus.test;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by wangmutian on 2018/2/3.
 */
//FrameLayout 中 onMeasure 这个方法已经实现就不需要在实现了
public class DragLayout extends FrameLayout {

    public View redView;
    public View blueView;
    private Scroller scroller;

    //这是一个解析类需要传一个触摸对象
    private ViewDragHelper viewDragHelper;
    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        viewDragHelper = ViewDragHelper.create(this,callback);
        scroller=new Scroller(getContext());
    }


    /**
     * 当DragLayout的xml布局结束的标签被读取完成会执行该方法此时会知道自己自己有几个子view
     * 一般用来初始化子view的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView=getChildAt(0);
        blueView=getChildAt(1);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //要测量子view
////方法一
////        int size = (int) getResources().getDimension(R.dimen.width);//100dp
////        int measureSpec= MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width,MeasureSpec.EXACTLY);
////
////        redView.measure(measureSpec,measureSpec);
////        blueView.measure(measureSpec,measureSpec);
//        //如果没有特殊的view测量需求，可以用如下方法
//        measureChild(redView,widthMeasureSpec,heightMeasureSpec);
//        measureChild(blueView,widthMeasureSpec,heightMeasureSpec);
//    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        //测量完之后 还要在这里摆放
        int left = getPaddingLeft();
        int top = getPaddingTop();
        redView.layout(left,top,left + redView.getMeasuredWidth(),top + redView.getMeasuredHeight());
        blueView.layout(left,redView.getBottom(),left + redView.getMeasuredWidth(),redView.getBottom() + redView.getMeasuredHeight());


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    //处理是否需要拦截的
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewDragHelper帮我们判断是否需要拦截
        boolean result=viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸对象
         * @param child 当前触摸的子view
         * @param pointerId
         * @return true 就是捕获并解析 false 不处理
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child ==blueView;
        }

        //以下重写函数 用 win alt + insert,mac command + N
        /**
         * 当view被开始捕获和解析
         * @param capturedChild 当前获取的子view
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * 获取水平拖拽范围
         * @param child
         * @return 表示你想让child的left 变成一个值
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return super.getViewVerticalDragRange(child);
        }

        /**
         * 控制child在水平方向的移动
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if(left<0){
                left = 0;
            }else if(left > (getMeasuredWidth() - child.getMeasuredWidth())){
                left = (getMeasuredWidth() - child.getMeasuredWidth());
            }
            return left;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if(top<0){
                top = 0;
            }else if(top > (getMeasuredHeight() - child.getMeasuredHeight())){
                top = (getMeasuredHeight() - child.getMeasuredHeight());
            }
            return top;
        }


        /**
         * 当child位置发送改变的时候执行
         * @param changedView 位置改变的child
         * @param left 当前最新的也是改变之后
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView == blueView){
                //blueView 移动的时候需要让redView跟随移动
                redView.layout(redView.getLeft() + dx,redView.getTop() + dy,redView.getRight() + dx,redView.getBottom() + dy);
            }else if(changedView == redView){
                blueView.layout(blueView.getLeft() + dx,blueView.getTop() + dy,blueView.getRight() + dx,blueView.getBottom() + dy);
            }


            double fraction = (double) changedView.getLeft()/ (double)(getMeasuredWidth() - changedView.getMeasuredWidth());
            Log.e("提示",fraction+"");
            executeAnim((float) fraction);
        }

        /**
         * 手指抬起的时候执行该方法
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth()/2 - releasedChild.getMeasuredWidth()/2;
            if(releasedChild.getLeft()<centerLeft){
                //在左边
                viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this); //刷新整个布局
            }else{
                //在右边
                viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getMeasuredWidth(),releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this); //刷新整个布局
            }
        }


    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }

    private void executeAnim(float fraction){
        ViewHelper.setScaleX(redView,1+0.5f*fraction);
    }

}
