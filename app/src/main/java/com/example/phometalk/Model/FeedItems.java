package com.example.phometalk.Model;

import android.graphics.Bitmap;

public class FeedItems {
    Bitmap image;
    String tag;
    int star;

    public Bitmap getImage(){
        return image;
    }
    public void setImage(Bitmap image){
        this.image=image;
    }
    public String getTag(){
        return tag;
    }
    public void setTag(String tag){
        this.tag=tag;
    }
    public int getStar(){return star;}
    public void setStar(int star){this.star=star;}
}
