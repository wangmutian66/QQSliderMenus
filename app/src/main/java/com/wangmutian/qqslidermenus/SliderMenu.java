package com.wangmutian.qqslidermenus;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by wangmutian on 2018/2/3.
 */

public class SliderMenu extends FrameLayout {
    private View menuView;
    private View mainView;
    public ViewDragHelper viewDragHelper;
    public int width;
    private float dragRange;//拖拽范围
    private FloatEvaluator floatEvaluator; //一个很牛x的东西可以做计算处理 浮点数计算器
    private IntEvaluator intevaluator; // 整数计算器


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

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SliderMenu(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        viewDragHelper = ViewDragHelper.create(this,callback);
        floatEvaluator = new FloatEvaluator();
        intevaluator=new IntEvaluator();
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
//            if(child == menuView){
//                left = left - dx;
//            }
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView == menuView){
                //固定主menuView
                menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                //让mianview移动起来
                int newleft=mainView.getLeft()+dx;
                if(newleft<0) newleft=0;
                if(newleft > dragRange) newleft = (int)dragRange;
                mainView.layout(newleft,mainView.getTop()+dy,newleft+changedView.getMeasuredWidth(),mainView.getBottom()+dy);
            }

            //1.计算滑动的百分比
            float fraction  = mainView.getLeft()/dragRange;
            //2.执行伴随动画
            executeAnim(fraction);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if(mainView.getLeft()<dragRange/2){
                //在左边
                viewDragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
                ViewCompat.postInvalidateOnAnimation(SliderMenu.this);
            }else{
                //在右边
                viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
                ViewCompat.postInvalidateOnAnimation(SliderMenu.this);
            }
        }
    };

    /**
     * 执行伴随动画
     * @param fraction
     */
    private void executeAnim(float fraction){
        //缩小mainView
        //方法一
//        float scaleValue = 0.8f+0.2f*(1-fraction);
//        ViewHelper.setScaleX(mainView,scaleValue);
//        ViewHelper.setScaleY(mainView,scaleValue);
        //范围从 1 到 0。8
        ViewHelper.setScaleX(mainView,floatEvaluator.evaluate(fraction,1f,0.8f));
        ViewHelper.setScaleY(mainView,floatEvaluator.evaluate(fraction,1f,0.8f));

        ViewHelper.setTranslationX(menuView,intevaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        //改变menuView 的透明度
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
//        ColorUtil;
        //Mode.SRC_OVER 覆盖在上面
//        getBackground().setColorFilter(, PorterDuff.Mode.SRC_OVER);
//        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //如果动画没有结束 就在刷新一下
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SliderMenu.this);
        }
    }
}
