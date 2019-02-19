package com.example.myshh.expandablelistview2_apiphotos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button button;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = findViewById(R.id.expandableListView);
        expandableListAdapter = new MyAdapter(this, jsonArray);
        expandableListView.setAdapter(expandableListAdapter);
        button = findViewById(R.id.btnLoadDownloadedData);
        button.setEnabled(false);

        //Read JSON array
        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
                try {
                    URL url = new URL("https://jsonplaceholder.typicode.com/photos");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder result = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }

                    jsonArray = new JSONArray(result.toString());
                    runOnUiThread(() -> button.setEnabled(true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }).start();
    }

    public void btnLoadDownloadedData(View v) {
        try {
            System.out.println("Length: " + jsonArray.length());
            ((MyAdapter)expandableListAdapter).setJsonArray(jsonArray);
            ((MyAdapter)expandableListAdapter).notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
