package com.example.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    public ProgressBar mProgressBar;
    private RecyclerView rvBooks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.pb_loading);
        rvBooks = findViewById(R.id.rv_books);
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        rvBooks.setLayoutManager(booksLayoutManager);

        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
        URL bookUrl;

        try{
            if (query == null || query.isEmpty()) {
                bookUrl = ApiUtil.buildURL("cooking");
            }
            else{
                bookUrl = new URL(query);
            }
            new BooksQueryTask().execute(bookUrl);
        } catch (Exception e) {
            Log.d("error", Objects.requireNonNull(e.getMessage()));
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.book_list_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        ArrayList<String> recentList = SpUtil.getQueryList(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem recentMenu;
        for (int i=0; i<itemNum;i++){
            recentMenu = menu.add(Menu.NONE,i,Menu.NONE,recentList.get(i));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, Search.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId() + 1;
                String preferenceName = SpUtil.QUERY + position;
                String query = SpUtil.getPreferenceString(getApplicationContext(),preferenceName);
                String[] prefParams = query.split(",");
                String[] queryParams = new String[4];
                for (int i=0;i<prefParams.length;i++){

                    queryParams[i] = prefParams[i];
                }
                URL bookUrl = ApiUtil.buildURL(
                        (queryParams[0] == null)?"":queryParams[0],
                        (queryParams[1] == null)?"":queryParams[1],
                        (queryParams[2] == null)?"":queryParams[2],
                        (queryParams[3] == null)?"":queryParams[3]
                );
                new BooksQueryTask().execute(bookUrl);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
       try {
           URL bookUrl = ApiUtil.buildURL(s);
           new BooksQueryTask().execute(bookUrl);
       }
       catch (Exception e){
           Log.d("Error", Objects.requireNonNull(e.getMessage()));
       }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPostExecute(String result) {
            TextView tvError = findViewById(R.id.error_loading);
            mProgressBar.setVisibility(View.INVISIBLE);
            if(result == null){
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            }
            else{
                rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);

                ArrayList<Book> books = ApiUtil.getBooksFromJSON(result);

                BooksAdapter adapter = new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }

        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try{
                result = ApiUtil.getJSON(searchURL);
            }
            catch (IOException e){
                Log.d("Error", Objects.requireNonNull(e.getMessage()));
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


}