package com.example.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {
    private  ApiUtil(){}

    public static  final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "Insert API key here";
    public static final String TITLE = "intitle:";
    public static final String AUTHOR = "inauthor:";
    public static final String PUBLISHER = "inpublisher:";
    public static final String ISBN = "isbn:";

    public static URL buildURL(String title) {
          URL url = null;
          Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                  .appendQueryParameter(QUERY_PARAMETER_KEY,title)
                  .appendQueryParameter(KEY,API_KEY)
                  .build();

        try{
            url = new URL(uri.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildURL(String title,String author,String publisher,String isbn){
        URL url = null;
        StringBuilder sb = new StringBuilder();
        if(!title.isEmpty()) sb.append(TITLE).append(title).append("+");
        if (!author.isEmpty()) sb.append(AUTHOR).append(author).append("+");
        if (!publisher.isEmpty()) sb.append(PUBLISHER).append(publisher).append("+");
        if (!ISBN.isEmpty()) sb.append(ISBN).append(isbn).append("+");
        sb.setLength(sb.length() - 1);
        String query = sb.toString();
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY,query)
                .appendQueryParameter(KEY,API_KEY)
                .build();
        try{
           url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }
    //this function connect to the URL
    public static String getJSON(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try{
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if(hasData){
                return scanner.next();
            }
            else {
                return null;
            }
        } catch (IOException e) {
            Log.d("ERROR",e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }

    }
    public static ArrayList<Book> getBooksFromJSON(String json){
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";
        final String ID = "id";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<Book>();

        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for(int i = 0; i < numberOfBooks; i++){
                JSONObject bookJson = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJson.getJSONObject(VOLUMEINFO);

                    JSONObject imageLinksJson =  volumeInfoJSON.getJSONObject(IMAGELINKS);

                int numOfAuthors;
                try {
                    numOfAuthors = volumeInfoJSON.getJSONArray(AUTHORS).length();
                }
                catch (Exception e){
                    numOfAuthors = 0;
                }
                String[] authors = new String[numOfAuthors];
                for(int j = 0; j < numOfAuthors; j++){
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }
                Book book = new Book(bookJson.getString(ID),
                        volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)?"":volumeInfoJSON.getString(SUBTITLE)),
                        (volumeInfoJSON.isNull(PUBLISHER)?"":volumeInfoJSON.getString(PUBLISHER)),
                        authors,
                        (volumeInfoJSON.isNull(PUBLISHED_DATE)?"":volumeInfoJSON.getString(PUBLISHED_DATE)),
                        (volumeInfoJSON.isNull(DESCRIPTION)?"":volumeInfoJSON.getString(DESCRIPTION)),
                        (imageLinksJson == null)?"":imageLinksJson.getString(THUMBNAIL)
                );
                books.add(book);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return books;

    }
}
