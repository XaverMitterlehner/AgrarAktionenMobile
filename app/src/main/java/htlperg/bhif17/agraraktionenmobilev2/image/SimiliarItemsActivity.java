package htlperg.bhif17.agraraktionenmobilev2.image;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.RecyclerAdapter;
import htlperg.bhif17.agraraktionenmobilev2.model.Item;

public class SimiliarItemsActivity extends AppCompatActivity {

    public final static String TAG = SimiliarItemsActivity.class.getSimpleName();
    Handler handler = new Handler(Looper.getMainLooper());

    TextView resultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similiar_items);

        resultMessage = findViewById(R.id.resultMessage);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> displayData(doPosts))));
    }

    void displayData(Item items[]) {
        List<Item> itemList = new LinkedList<>();
        itemList.addAll(Arrays.asList(items));

        if(itemList.isEmpty()){
            getResultMessage();
        }

        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(SimiliarItemsActivity.this, itemList);

        RecyclerView recyclerView = findViewById(R.id.classificationList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SimiliarItemsActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(recyclerAdapter);
        //listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.id.recyclerView, content));
    }

    Optional<Item[]> loadData() {
        Optional<Item[]> items = Optional.ofNullable(null);
        Log.i(TAG, "Download similar items from api...");
        try {
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/similarItems/getAll");
            //URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/similarItems");
            items = Optional.of(new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                    .readValue(url, Item[].class));
        } catch (Exception e) {
            getResultMessage();
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "Downloaded similar items succesfully");
        return items;
    }

    void getResultMessage(){
        resultMessage.setVisibility(View.VISIBLE);
    }

}