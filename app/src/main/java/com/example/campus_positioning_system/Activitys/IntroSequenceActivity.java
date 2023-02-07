package com.example.campus_positioning_system.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.campus_positioning_system.R;
import com.example.campus_positioning_system.SlideModel;
import com.example.campus_positioning_system.SliderAdapter;

import java.util.ArrayList;

public class IntroSequenceActivity extends AppCompatActivity {

    //Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted;
    Button skip_btn;
    Button next_btn;
    Animation animation;
    int currentPos;
    int listItems_leng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.onboarding_activity);

        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);
        skip_btn = findViewById(R.id.skip_btn);
        next_btn = findViewById(R.id.next_btn);

        //Call adapter
        ArrayList listItems = new ArrayList<>() ;
        listItems.add(new SlideModel(R.drawable.ob_1_main,"Campus-Positioning-\nSystem","Willkommen zum Campus-Positioning-System der Hochschule Furtwangen. \n Mit diesem Indoor-Positioning-System können Sie einfach durch die Hochschule navigieren. \n Diese App hilft ihnen sich in der Hochschule zurecht zu finden, auch wenn Sie sich hier nicht auskennen! "));
        listItems.add(new SlideModel(R.drawable.ob_2_navbar,"Einfache Navigation", "Öffnen Sie einfach die Raum Liste, wählen Sie ihren gewünschten Raum und starten Sie die Navigation mit dem grünen Pfeil. \n So einfach gehts!"));
        listItems.add(new SlideModel(R.drawable.ob_3_favorites,"Funktionalitäten", "Entdecken Sie weitere nützliche Funktionalitäten wie die Favoriten, die Schnellwahl und den QR-Code Scanner!"));
        sliderAdapter = new SliderAdapter(this, listItems);
        listItems_leng = listItems.size();
        viewPager.setAdapter(sliderAdapter);
        //lets get started switch activity
        letsGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(MainActivity.class);
            }
        });
        //Dots
        addDots(listItems.size());
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position) {
        dots = new TextView[listItems_leng];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.black));
            dotsLayout.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[0].setTextColor(getResources().getColor(R.color.teal_700));
        }
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position != listItems_leng-1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                skip_btn.setVisibility(View.VISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            } else {
                /*
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this, R.anim.bottom_anim);
                letsGetStarted.setAnimation(animation);

                 */
                skip_btn.setVisibility(View.INVISIBLE);
                next_btn.setVisibility(View.INVISIBLE);
                letsGetStarted.setVisibility(View.VISIBLE);


            }
            for (TextView t: dots) {
                t.setTextColor(getResources().getColor(R.color.black));
            }
            dots[position].setTextColor(getResources().getColor(R.color.teal_700));


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private void switchActivities(Class<?> activityClass) {
        Intent switchActivityIntent = new Intent(this, activityClass);
        startActivity(switchActivityIntent);
    }

}