package htlperg.bhif17.agraraktionenmobilev2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.model.Item;

public class FilterActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();
    Handler handler = new Handler(Looper.getMainLooper());

    Spinner spinner;
    Button submitButton, resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refresh();

    }

    void refresh() {
        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> setFiltering(doPosts))));
    }

    void setFiltering(String categories[]) {
        spinner = (Spinner) findViewById(R.id.categoryList);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        int position = Arrays.asList(categories).indexOf(MyProperties.getInstance().selectedCategory);

        if (MyProperties.getInstance().selectedCategory == "") {
            spinner.setSelection(0, false);
        } else {
            spinner.setSelection(position, false);
        }

        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetFilterButton);
        resetButton.setEnabled(false);

        Intent intent = new Intent(FilterActivity.this, MainActivity.class);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = spinner.getSelectedItem().toString();
                MyProperties.getInstance().selectedCategory = cat;
                startActivity(intent);
            }
        });

        if(MyProperties.getInstance().selectedCategory != ""){
            resetButton.setEnabled(true);
        }

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyProperties.getInstance().selectedCategory = "";
                startActivity(intent);
            }
        });

    }

    // download data from rest-api
    Optional<String[]> loadData() {
        Optional<String[]> categories = Optional.ofNullable(null);
        Log.i(TAG, "download category names from api...");
        try {
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/categories/getPrimeCategories");
            categories = Optional.of(new ObjectMapper().readValue(url, String[].class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded category names successfully");
        return categories;
    }

}