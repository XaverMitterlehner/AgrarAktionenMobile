package htlperg.bhif17.agraraktionenmobilev2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.account.AccountActivity;
import htlperg.bhif17.agraraktionenmobilev2.image.ImageClassification;
import htlperg.bhif17.agraraktionenmobilev2.login.RegisterActivity;
import htlperg.bhif17.agraraktionenmobilev2.model.Item;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import htlperg.bhif17.agraraktionenmobilev2.service.TimeService;
import htlperg.bhif17.agraraktionenmobilev2.settings.SettingsActivity;
import lombok.SneakyThrows;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();
    Handler handler = new Handler(Looper.getMainLooper());

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Spinner spinner;
    Button filterButton;
    int selectedItem;

    List<Item> itemList;
    URL url;

    String[] spinnerOptions = {"Sortieren nach", "Preis absteigend", "Preis aufsteigend"};

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *  setup action bar with logo and don't show project title
         */
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.agraraktionen_logo_xsmall);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        refresh();

        /**
         * this time service cycles in an interval of 60 seconds and pushes a notification if something in the database has changed
         */
        startService(new Intent(this, TimeService.class));

        /**
         *  set up drop down with android.widget.Spinner to sort recycler view
         */

        spinner = (Spinner) findViewById(R.id.sorter);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_item, spinnerOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0, false);
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

        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        /**
         *  set up round button in bottom right corner
         *  onClick -> ImageSearch which returns items with similar image for selected image
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ImageClassification.class);
                /*if (extras != null) {
                    String loginData = extras.getString("response");
                    intent.putExtra("loginData", loginData);
                }*/
                startActivity(intent);
            }
        });

        /**
         *  set up recycler view with item list which is shown in main activity
         */
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new RecyclerAdapter(this, new ArrayList<Item>()));

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

    @SneakyThrows
    void checkUrl() {
        if (MyProperties.getInstance().selectedCategory != "") {
            String cat = MyProperties.getInstance().selectedCategory;
            String urlString = "https://student.cloud.htl-leonding.ac.at/20170033/api/categories/" + cat;
            url = new URL(urlString);
            MyProperties.getInstance().selectedCategory = cat;
        } else {
            url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/item/inserted");
        }

        Log.i(TAG, "checked url... taken: " + "'" + url + "'");

    }

    // download data from rest-api in async thread and send it to adapter
    void refresh() {
        Log.i(TAG, "loading/refreshing data...");
        checkUrl();
        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> displayData(doPosts))));
    }

    // set up recycler view with parameter list
    void displayData(Item items[]) {

        itemList = new LinkedList<>();
        itemList.addAll(Arrays.asList(items));

        recyclerAdapter = new RecyclerAdapter(this, itemList);
        recyclerView.setAdapter(recyclerAdapter);

        spinnerOnClick(selectedItem);
        recyclerAdapter.getPriceFilter().filter("");

    }

    // download data from rest-api
    Optional<Item[]> loadData() {
        Optional<Item[]> items = Optional.ofNullable(null);
        Log.i(TAG, "download items from api...");
        try {
            items = Optional.of(new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                    .readValue(url, Item[].class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded items successfully");
        return items;
    }

    // sort list in recycler view triggered by spinner.onItemSelected()
    public void spinnerOnClick(int position) {
        recyclerAdapter.getSortFilter().filter(spinnerOptions[position]);
    }

    // set up back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                onSettingsPressed();
                return true;
            case R.id.action_account:
                onAccountPressed();
                return true;
            case R.id.logout:
                onLogoutPressed();
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

    private void onSettingsPressed() {
        System.out.println("Settings opened!");
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onAccountPressed() {
        String userData = MyProperties.getInstance().userLoginData;
        if(userData == null || userData == ""){
            System.out.println("Opened without login!");
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        }else {
            System.out.println("Account opened!");
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }



    }

    private void onLogoutPressed() {
        Log.e(TAG, "Not working now!");
        Toast.makeText(getApplicationContext(), "coming soon!", Toast.LENGTH_SHORT).show();
        /*
        Account accountLogout = new Account();
        accountLogout.logout();
         */
        //TODO: if you try to get this working, you have to change MyProperties to the actual live user data by calling the api when the class gets called. You also have to change something in the Account class because it is doing the download task of the live user data, which can and should be outsourced to the MyProperties class!
    }
}