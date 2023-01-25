package com.example.campus_positioning_system;

public class SlideModel {

    private int featured_image;
    private String the_caption_Title;
    private String the_description;

    public SlideModel(int hero, String title, String description) {
        this.featured_image = hero;
        this.the_caption_Title = title;
        this.the_description = description;
    }

    public int getFeatured_image() {
        return featured_image;
    }

    public String getThe_caption_Title() {
        return the_caption_Title;
    }

    public String getThe_Description() {
        return the_description;
    }

    public void setFeatured_image(int featured_image) {
        this.featured_image = featured_image;
    }

    public void setThe_caption_Title(String the_caption_Title) {
        this.the_caption_Title = the_caption_Title;
    }

    public void setThe_Description(String the_description) {
        this.the_description = the_description;
    }
}
