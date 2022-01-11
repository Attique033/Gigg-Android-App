package app.gigg.me.app.Activity.freelance.model;

import android.graphics.drawable.Drawable;

public class Job {
    private String title;
    private String color;
    private Drawable image;
    private String tag;

    public Job(String title, String color, Drawable image, String tag) {
        this.title = title;
        this.color = color;
        this.image = image;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
