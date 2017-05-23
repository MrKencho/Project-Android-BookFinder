package com.example.android.bookfinder;

import android.graphics.Bitmap;

/**
 * Created by BaBa_RanChO on 21-05-2017.
 */

public class Book {
    private String mBookName;
    private String mAuthor;
    private Bitmap mThumbnail;
    private String mPublishDate;

    public Book(String bookName,String author,Bitmap img,String publishDate){
        mPublishDate = publishDate;
        mBookName = bookName;
        mAuthor = author;
        mThumbnail = img;
    }

    public String getPublishDate(){
        return mPublishDate;
    }

    public String getBookName(){
        return mBookName;
    }

    public String getAuthor(){
        return mAuthor;
    }

    public Bitmap getThumbnail(){
        return mThumbnail;
    }
}
