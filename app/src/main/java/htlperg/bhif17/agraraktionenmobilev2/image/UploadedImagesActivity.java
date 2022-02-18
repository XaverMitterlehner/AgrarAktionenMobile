package htlperg.bhif17.agraraktionenmobilev2.image;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import htlperg.bhif17.agraraktionenmobilev2.RecyclerAdapter;
import htlperg.bhif17.agraraktionenmobilev2.model.Item;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.model.Image;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UploadedImagesActivity extends AppCompatActivity {

    RecyclerView imageRecyclerView;
    List<Image> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded_images);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bereits hochgeladene Bilder");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        imageRecyclerView.setLayoutManager(gridLayoutManager);
        imageRecyclerView.setAdapter(new ImageListAdapter(this, new ArrayList<Image>()));

        displayData();

    }

    void displayData() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                String myProperties = MyProperties.getInstance().userLoginData;

                try {

                    MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();

                    okhttp3.RequestBody body = RequestBody.create(TEXT, myProperties);
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://student.cloud.htl-leonding.ac.at/20170033/api/image/getImagesForView/forSpecifiedUser")
                            .post(body)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();

                    //Map to Class Image

                    String networkResp = response.body().string();

                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        imageList = objectMapper
                                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                                .readValue(networkResp, new TypeReference<List<Image>>() {
                                });

                        for (Image image : imageList) {
                            System.out.println(image.getUsername());
                            System.out.println(image.getId());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                imageRecyclerView = findViewById(R.id.imageRecyclerView);
                ImageListAdapter imageListAdapter = new ImageListAdapter(UploadedImagesActivity.this, imageList);
                imageRecyclerView.setAdapter(imageListAdapter);

                return;

            }
        });

    }
}

