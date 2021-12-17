package htlperg.bhif17.agraraktionenmobilev2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.image.ImageClassification;
import htlperg.bhif17.agraraktionenmobilev2.model.Item;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();
    Handler handler = new Handler(Looper.getMainLooper());

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Spinner spinner;
    int selectedItem;

    List<Item> itemList;

    Bundle mBundle;

    String[] spinnerOptions = { "Sortieren nach", "Preis absteigend", "Preis aufsteigend"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *  set up drop down with android.widget.Spinner to sort recycler view
         */

        spinner = (Spinner) findViewById(R.id.sorter);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this , R.layout.spinner_item, spinnerOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                spinnerOnClick(position);
                selectedItem = position;
                //Toast.makeText(getApplicationContext(), spinnerOptions[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        /**
         *  setup action bar with logo and don't show project title
         */
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.agraraktionen_logo_xsmall);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /**
         *  set up round button in bottom right corner
         *  onClick -> ImageSearch which returns items with similar image for selected image
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBundle = getIntent().getExtras();
                Intent intent = new Intent(MainActivity.this, ImageClassification.class);
                if(mBundle != null) {
                    String loginData = mBundle.getString("response");
                    intent.putExtra("loginData", loginData);
                }
                startActivity(intent);
            }
        });

        /**
         *  set up recycler view with item list which is shown in main activity
         */
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        refresh();

        // set up swipe to refresh for reloading list from rest api into in recycler view
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    // download data from rest-api in async thread and send it to adapter
    void refresh(){
        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> displayData(doPosts))));
    }

    // set up recycler view with parameter list
    void displayData(Item items[]) {
        itemList = new LinkedList<>();
        itemList.addAll(Arrays.asList(items));

        recyclerAdapter = new RecyclerAdapter(this, itemList);
        spinnerOnClick(selectedItem);
        recyclerView.setAdapter(recyclerAdapter);
    }

    // download data from rest-api
    Optional<Item[]> loadData() {
        Optional<Item[]> items = Optional.ofNullable(null);
        Log.i(TAG, "download data from api...");
        try {
            //URL url = new URL("http://10.0.2.2:8080/api/item/inserted");
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/item");
            items = Optional.of(new ObjectMapper()
                                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                                        .readValue(url, Item[].class));
        } catch(Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded data succesfully");
        return items;
    }

    // sort list in recycler view triggered by spinner.onItemSelected()
    public void spinnerOnClick(int position){
        recyclerAdapter.getSortFilter().filter(spinnerOptions[position]);
    }

    // set up back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // set up search view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter the list in recycler view with string from search bar
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}