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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    TextInputLayout price1InputLayout, price2InputLayout;
    TextInputEditText price2EditText, price1EditText;

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
        List<String> tempList = new ArrayList<>(Arrays.asList(categories));
        tempList.add(0, "Erste Kategorie auswählen");

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, tempList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinnerAdapter);

        int position = Arrays.asList(categories).indexOf(MyProperties.getInstance().selectedCategory);

        if (MyProperties.getInstance().selectedCategory == "") {
            spinner1.setSelection(0, false);
        } else {
            spinner1.setSelection(position+1, false);
            submitButton.setEnabled(true);
            refresh2();
        }

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = spinner1.getSelectedItem().toString();
                if(cat != "Erste Kategorie auswählen") {
                    MyProperties.getInstance().selectedCategory = cat;
                    submitButton.setEnabled(true);
                    refresh2();
                } else {
                    MyProperties.getInstance().selectedCategory = "";
                    submitButton.setEnabled(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setSecondCategory(String categories2[]){

        List<String> tempList = new ArrayList<>(Arrays.asList(categories2));
        tempList.add(0, "Zweite Kategorie auswählen");

        ArrayAdapter spinnerAdapter2 = new ArrayAdapter(this, R.layout.spinner_item, tempList);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinnerAdapter2);

        int position = Arrays.asList(categories2).indexOf(MyProperties.getInstance().category2);

        spinner2.setVisibility(View.VISIBLE);

        if (MyProperties.getInstance().category2 == "") {
            spinner2.setSelection(0, false);
        } else {
            spinner2.setSelection(position+1, false);
            refresh3();
        }

        //spinner2.setEnabled(false);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (selected){
                    String cat = spinner2.getSelectedItem().toString();
                    if(cat != "Zweite Kategorie auswählen"){
                        MyProperties.getInstance().category2 = cat;
                        refresh3();
                    } else {
                        MyProperties.getInstance().category2 = "";
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void setThirdCategory(String categories3[]){
        List<String> tempList = new ArrayList<>(Arrays.asList(categories3));
        tempList.add(0, "Dritte Kategorie auswählen");

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, tempList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(spinnerAdapter);

        int position = Arrays.asList(categories3).indexOf(MyProperties.getInstance().category3);

        spinner3.setVisibility(View.VISIBLE);

        if (MyProperties.getInstance().selectedCategory == "") {
            spinner3.setSelection(0, false);
        } else {
            spinner3.setSelection(position+1, false);
        }

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = spinner3.getSelectedItem().toString();
                if(cat != "Dritte Kategorie auswählen") {
                    MyProperties.getInstance().category3 = cat;
                } else {
                    MyProperties.getInstance().category3 = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    void setFiltering(String categories[]) {

        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetFilterButton);
        resetButton.setEnabled(false);

        submitButton.setEnabled(false);

        setFirstCategory(categories);

        setupPriceFilter();

        Intent intent = new Intent(FilterActivity.this, MainActivity.class);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = MyProperties.getInstance().selectedCategory; //spinner1.getSelectedItem().toString();
                /*if(selected == true){
                    cat = cat + "/" + spinner2.getSelectedItem().toString();
                }*/
                //MyProperties.getInstance().selectedCategory = cat;
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