package htlperg.bhif17.agraraktionenmobilev2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import lombok.SneakyThrows;

public class FilterActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();
    Handler handler = new Handler(Looper.getMainLooper());

    Spinner spinner1, spinner2, spinner3;
    Button submitButton, resetButton;

    TextView categoryText, priceFilterInfoText;
    Boolean selected = false;

    TextInputLayout price1InputLayout, price2InputLayout;
    TextInputEditText price2EditText, price1EditText;

    ListView listView;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Filter");

        spinner1 = findViewById(R.id.categoryDropDown);
        spinner2 = findViewById(R.id.categoryDropDown2);
        spinner3 = findViewById(R.id.categoryDropDown3);

        categoryText = findViewById(R.id.faText);
        priceFilterInfoText = findViewById(R.id.priceFilterInfoText);

        refresh();

    }

    void refresh() {
        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> setFiltering(doPosts))));
    }

    void refresh2(){
        CompletableFuture
                .supplyAsync(this::loadSecondCategory)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> setSecondCategory(doPosts))));
    }

    void refresh3(){
        CompletableFuture
                .supplyAsync(this::loadThirdCategory)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> setThirdCategory(doPosts))));
    }

    void setFirstCategory(String categories[]){
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerAdapter);

        int position = Arrays.asList(categories).indexOf(MyProperties.getInstance().selectedCategory);

        if (MyProperties.getInstance().selectedCategory == "") {
            spinner1.setSelection(0, false);
        } else {
            spinner1.setSelection(position, false);
        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = spinner1.getSelectedItem().toString();
                MyProperties.getInstance().selectedCategory = cat;
                //categoryText.setText("Kategorien:" + "(" + spinner1.getSelectedItem().toString() + ")");
                refresh2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setSecondCategory(String categories2[]){

        ArrayAdapter spinnerAdapter2 = new ArrayAdapter(this, R.layout.spinner_item, categories2);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinnerAdapter2);

        int position = Arrays.asList(categories2).indexOf(MyProperties.getInstance().category2);

        if (MyProperties.getInstance().category2 == "") {
            spinner2.setSelection(0, false);
        } else {
            spinner2.setSelection(position, false);
        }

        //spinner2.setEnabled(false);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (selected){
                    String cat = spinner2.getSelectedItem().toString();
                    MyProperties.getInstance().category2 = cat;
                    //categoryText.setText("Kategorien:" + "(" + spinner1.getSelectedItem().toString() + "/" + spinner2.getSelectedItem().toString() + ")");
                    refresh3();
                //}
                //selected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setThirdCategory(String categories3[]){
        ArrayAdapter spinnerAdapter3 = new ArrayAdapter(this, R.layout.spinner_item, categories3);
        spinnerAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(spinnerAdapter3);

        int position = Arrays.asList(categories3).indexOf(MyProperties.getInstance().category3);

        if (MyProperties.getInstance().category3 == "") {
            spinner3.setSelection(0, false);
        } else {
            spinner3.setSelection(position, false);
        }

        //spinner2.setEnabled(false);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (selected){
                String cat = spinner3.getSelectedItem().toString();
                MyProperties.getInstance().category3 = cat;
                //categoryText.setText("Kategorien:" + "(" + spinner1.getSelectedItem().toString() + "/" + spinner2.getSelectedItem().toString() + ")");
                refresh3();
                //}
                //selected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    void setFiltering(String categories[]) {

        setFirstCategory(categories);

        setupPriceFilter();

        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetFilterButton);
        resetButton.setEnabled(false);

        Intent intent = new Intent(FilterActivity.this, MainActivity.class);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = spinner1.getSelectedItem().toString();
                if(selected == true){
                    cat = cat + "/" + spinner2.getSelectedItem().toString();
                }
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
                MyProperties.getInstance().category2 = "";
                MyProperties.getInstance().category3 = "";

                MyProperties.getInstance().priceFilter2 = 0;
                MyProperties.getInstance().priceFilter1 = 0;
                startActivity(intent);
            }
        });
    }

    void setupPriceFilter(){

        price1EditText = findViewById(R.id.priceFilterEditText1);
        price1InputLayout = findViewById(R.id.priceFilterInputLayout1);

        price2EditText = findViewById(R.id.priceFilterEditText2);
        price2InputLayout = findViewById(R.id.priceFilterInputLayout2);

        price1EditText.setTextColor(Color.WHITE);
        price2EditText.setTextColor(Color.WHITE);

        price1EditText.setText(""+MyProperties.getInstance().priceFilter1);
        price2EditText.setText(""+MyProperties.getInstance().priceFilter2);

        price1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                boolean isNumeric =  input.matches("[+-]?\\d*(\\.\\d+)?");
                if(!input.equals("") && isNumeric){
                    priceFilterInfoText.setVisibility(View.INVISIBLE);
                    MyProperties.getInstance().priceFilter1 = Integer.parseInt(input);
                    Log.i(TAG, "Filter1: "+MyProperties.getInstance().priceFilter1);
                } else {
                    priceFilterInfoText.setVisibility(View.VISIBLE);
                }

            }
        });

        price2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                boolean isNumeric =  input.matches("[+-]?\\d*(\\.\\d+)?");
                if(!input.equals("") && isNumeric){
                    priceFilterInfoText.setVisibility(View.INVISIBLE);
                    MyProperties.getInstance().priceFilter2 = Integer.parseInt(input);
                    Log.i(TAG, "Filter2: "+MyProperties.getInstance().priceFilter2);
                } else {
                    priceFilterInfoText.setVisibility(View.VISIBLE);
                }
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

    Optional<String[]> loadSecondCategory() {
        Optional<String[]> categories = Optional.ofNullable(null);
        Log.i(TAG, "download second category names from api...");
        try {
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170011/api/categories/getSecondCategories/"+MyProperties.getInstance().selectedCategory);
            categories = Optional.of(new ObjectMapper().readValue(url, String[].class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded  second category names successfully");
        return categories;
    }

    Optional<String[]> loadThirdCategory() {
        Optional<String[]> categories = Optional.ofNullable(null);
        Log.i(TAG, "download third category names from api...");
        try {
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170011/api/categories/getThirdCategories/"+MyProperties.getInstance().selectedCategory+"/"+MyProperties.getInstance().category2);
            categories = Optional.of(new ObjectMapper().readValue(url, String[].class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded  third category names successfully");
        return categories;
    }

}