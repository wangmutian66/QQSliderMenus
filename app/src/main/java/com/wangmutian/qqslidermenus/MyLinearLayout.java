package com.wangmutian.qqslidermenus;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当sliderMenu打开的时候，拦截并消费调触摸事件
 * Created by wangmutian on 2018/2/4.
 */

public class MyLinearLayout extends LinearLayout{



    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private SliderMenu sliderMenu;
    public void setSliderMenu(SliderMenu sliderMenu){
        this.sliderMenu=sliderMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(sliderMenu!=null && sliderMenu.getCurrentState()== SliderMenu.DragState.Open){
            //如果slideMenu打开则应该拦截并消费处理
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 消费处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(sliderMenu!=null && sliderMenu.getCurrentState()== SliderMenu.DragState.Open){
            if(event.getAction() == MotionEvent.ACTION_UP){
                //抬起应该关闭sliderMenu
                sliderMenu.close();
            }
            //如果slideMenu打开则应该拦截并消费处理
            return true;
        }
        return super.onTouchEvent(event);
    }
}
