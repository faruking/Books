package com.example.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final EditText editTitle = findViewById(R.id.etTitle);
        final EditText editAuthor = findViewById(R.id.etAuthor);
        final EditText editPublisher = findViewById(R.id.etPublisher);
        final EditText editIsbn = findViewById(R.id.etISBN);
        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTitle.getText().toString().trim();
                String author = editAuthor.getText().toString().trim();
                String publisher  = editPublisher.getText().toString().trim();
                String isbn = editIsbn.getText().toString().trim();
                if(title.isEmpty() && author.isEmpty() && publisher.isEmpty() && isbn.isEmpty()){
                    String message = getString(R.string.no_search_data);
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }
                else {
                    URL queryURL = ApiUtil.buildURL(title,author,publisher,isbn);
                    //shared preference
                    Context context = getApplicationContext();
                    int position = SpUtil.getPreferenceInt(context,SpUtil.POSITION);
                    if (position == 0 ||position == 5){
                        position = 1;
                    }
                    else{
                        position++;
                    }
                    String key = SpUtil.QUERY + position;
                    String value = title + "," + author + "," + publisher + "," + isbn;
                    SpUtil.setPreferenceString(context,key,value);
                    SpUtil.setPreferenceInt(context,SpUtil.POSITION,position);

                    String QueryURLString = queryURL.toString();
                    Intent intent = new Intent(getApplicationContext(),BookListActivity.class);
                    intent.putExtra("Query",QueryURLString);
                    startActivity(intent);
                }
            }
        });
    }
}