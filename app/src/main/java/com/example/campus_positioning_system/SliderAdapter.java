package com.example.campus_positioning_system;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    Context Mcontext;
    List<SlideModel> slides;
    public SliderAdapter(Activity a, List<SlideModel> slides){
        super();
        Mcontext = a.getBaseContext();
        this.slides = slides;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.onboarding_slides,null);

        ImageView featured_image = sliderLayout.findViewById(R.id.slider_image);
        TextView caption_title = sliderLayout.findViewById(R.id.slider_heading);
        TextView description = sliderLayout.findViewById(R.id.slider_desc);

        featured_image.setImageResource(slides.get(position).getFeatured_image());
        caption_title.setText(slides.get(position).getThe_caption_Title());
        description.setText(slides.get(position).getThe_Description());
        container.addView(sliderLayout);
        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return slides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
