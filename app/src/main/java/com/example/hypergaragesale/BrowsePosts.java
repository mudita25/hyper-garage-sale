package com.example.hypergaragesale;

public class BrowsePosts {
    //This class has the elements to be displayed on main screen
    public String mTitle;
    public String mPrice;
    public String mImage;
    public String mLat;
    public String mLong;

    public BrowsePosts (String title, String price, String image, String lat, String lon ) {
        this.mTitle = title;
        this.mPrice = price;
        this.mImage = image;
        this.mLat = lat;
        this.mLong = lon;
    }
}
