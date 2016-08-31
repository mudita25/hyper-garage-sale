package com.example.hypergaragesale;

import android.provider.BaseColumns;

public class Posts {
    // To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    public Posts() {}

    /* Inner class that defines the table contents */
    public static abstract class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_IMAGE_URI = "image_1";
        public static final String COLUMN_NAME_IMAGE_URI2 = "image_2";
        public static final String COLUMN_NAME_IMAGE_URI3 = "image_3";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LONG = "longitude";
    }
}
