package com.example.android.bookfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem Building URL");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Book> fetchBookData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem Making http request");
        }
        List<Book> books = extractFeaturesFromJson(jsonResponse);
        return books;
    }

    private static List<Book> extractFeaturesFromJson(String JSONResponse) {
        if (TextUtils.isEmpty(JSONResponse)) {
            return null;
        }
        List<Book> books = new ArrayList<>();
        try {

            JSONObject obj = new JSONObject(JSONResponse);
            if (TextUtils.isEmpty(JSONResponse) || !obj.has("items")) {
                return null;
            }
            JSONArray arr = obj.getJSONArray("items");
            for(int i = 0; i < arr.length();i++){
                JSONObject item = arr.getJSONObject(i);
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                String author;
                if(volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    author = authors.join(", ");
                }
                else
                {
                    author = "Author N.A.";
                }
                String publishDate;
                if(volumeInfo.has("publishedDate"))
                    publishDate = volumeInfo.getString("publishedDate");
                else
                    publishDate = "DATE N.A.";
                JSONObject imageLinks;
                if(volumeInfo.has("imageLinks"))
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                else
                    imageLinks = null;
                String urlThumbnail;
                Bitmap thumbnail;
                if(imageLinks != null) {
                    urlThumbnail = imageLinks.getString("smallThumbnail");
                     thumbnail = imageProcess(urlThumbnail);
                }
                else
                {
                    thumbnail = imageProcess("https://www.google.co.in/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwiI-4OdzYLUAhXEMI8KHcobATEQjRwIBw&url=http%3A%2F%2Fmadan.wikia.com%2Fwiki%2FBouroullec&psig=AFQjCNFQ0zARJfJP4sgI9aCbRLouSOc5Eg&ust=1495511662901074");
                }
                books.add(new Book(title,author,thumbnail,publishDate));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing data",e);
        }
        return books;
    }

    private static Bitmap imageProcess(String thumbnailUrl) {
        if (thumbnailUrl == null) {
            return null;
        }
        URL imageUrl = createUrl(thumbnailUrl);
        if (imageUrl == null) {
            return null;
        }
        Bitmap img = null;
        HttpURLConnection httpconn = null;
        InputStream input = null;
        try {
            httpconn = (HttpURLConnection) imageUrl.openConnection();
            httpconn.setRequestMethod("GET");
            httpconn.connect();
            if (httpconn.getResponseCode() == 200) {
                input = httpconn.getInputStream();
                img = BitmapFactory.decodeStream(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpconn != null) {
                httpconn.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return img;
    }

}
