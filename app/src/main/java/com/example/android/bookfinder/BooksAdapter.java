package com.example.android.bookfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by BaBa_RanChO on 21-05-2017.
 */

public class BooksAdapter extends ArrayAdapter<Book> {
    public BooksAdapter(Context context, ArrayList<Book> books) {
        super(context,0,books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        Book currentBook = getItem(position);

        String bookName = currentBook.getBookName();
        String author = currentBook.getAuthor();
        Bitmap imgBitmap = currentBook.getThumbnail();
        String publishDate = currentBook.getPublishDate();
        TextView bookN = (TextView)convertView.findViewById(R.id.bookName);
        bookN.setText(bookName);
        TextView authorTV = (TextView)convertView.findViewById(R.id.author);
        authorTV.setText(author);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.thumbnail);
        imageView.setImageBitmap(imgBitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        TextView pDate = (TextView)convertView.findViewById(R.id.publishdate);
        pDate.setText(publishDate);
        return convertView;
    }
}
