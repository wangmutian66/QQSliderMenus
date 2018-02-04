package com.wangmutian.qqslidermenus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String[] NAMES=new String[]{"松江","送货","兜兜风","送货","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风","送货","兜兜风"};
    private ListView menu_listview,main_listview;
    private SliderMenu sliderMenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_listview=findViewById(R.id.menu_listview);
        main_listview=findViewById(R.id.main_listview);
        sliderMenu = findViewById(R.id.SliderMenu);
        iv_head=findViewById(R.id.iv_head);
        my_layout=findViewById(R.id.my_layout);

        //
        my_layout.setSliderMenu(sliderMenu);



        //ArrayAdapter 继承 baseadapter 重写里头的getview方法修改字体颜色
        menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,NAMES){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView)super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });

        main_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,NAMES){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = (convertView==null)?super.getView(position, convertView, parent):convertView;
                ViewHelper.setScaleX(view,0.5f);
                ViewHelper.setScaleY(view,0.5f);
                //以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });

        sliderMenu.setOnDragStateChangeLinstener(new SliderMenu.onDragStateChangeListener() {
            @Override
            public void onOpen() {
//                Log.e("tag","onOpen");
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }

            @Override
            public void onClose() {
//                Log.e("tag","onClose");
                // new CycleInterpolator(4)移动四次
                ViewPropertyAnimator.animate(iv_head).translationXBy(15).setInterpolator(new CycleInterpolator(4)).setDuration(500).start();


            }


            @Override
            public void onDraging(float fraction) {
//                Log.e("tag","onDraging"+fraction);
                ViewHelper.setAlpha(iv_head,1-fraction);
            }

        });

    }
}
