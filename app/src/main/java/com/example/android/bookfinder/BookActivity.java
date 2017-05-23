package com.example.android.bookfinder;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private String searchUrl;
    private TextView emptyTextview;
    private BooksAdapter mAdapter;
    private static int bookLoaderId = 1;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        ListView booksListView = (ListView)findViewById(R.id.list);
        mAdapter = new BooksAdapter(BookActivity.this ,new ArrayList<Book>());
        booksListView.setAdapter(mAdapter);

        progress = (ProgressBar)findViewById(R.id.loading);
        progress.setVisibility(View.GONE);
        emptyTextview = (TextView)findViewById(R.id.empty);
        emptyTextview.setText(getString(R.string.defaultMessage));
        booksListView.setEmptyView(emptyTextview);

        Button searchButton = (Button)findViewById(R.id.button);

        if(!checkConnectivity()){
            emptyTextview.setText(getText(R.string.notConnected));
            progress.setVisibility(View.GONE);
        }
        else
        {
            getLoaderManager().initLoader(bookLoaderId, null, BookActivity.this);
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clear();
                emptyTextview.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                EditText searchQuery = (EditText)findViewById(R.id.queryText);
                searchUrl = "https://www.googleapis.com/books/v1/volumes?q=";
                String params = searchQuery.getText().toString().toLowerCase();
                for(int i = 0;i < params.length();i++)
                {
                    if (params.charAt(i) == ' ') {
                        searchUrl = searchUrl + "%20";
                    } else
                        searchUrl = searchUrl + params.charAt(i);
                }
                if(params.isEmpty())
                {
                    emptyTextview.setVisibility(View.VISIBLE);
                    emptyTextview.setText(getText(R.string.invalidSQ));
                }
                if(checkConnectivity()) {
                    if (getLoaderManager().getLoader(bookLoaderId) != null) {
                        getLoaderManager().restartLoader(bookLoaderId, null, BookActivity.this);
                    }
                }
            }
        });
    }
    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, searchUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        ProgressBar loading = (ProgressBar)findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        mAdapter.clear();
        TextView temp = (TextView)findViewById(R.id.empty);
        temp.setText(getText(R.string.noBooks));
        if(data != null && !data.isEmpty())
            mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader)
    {
        mAdapter.clear();
    }

    private Boolean checkConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)BookActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
