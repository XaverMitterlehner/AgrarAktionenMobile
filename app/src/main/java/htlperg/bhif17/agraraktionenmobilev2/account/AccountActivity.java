package htlperg.bhif17.agraraktionenmobilev2.account;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.MainActivity;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.login.LoginActivity;
import htlperg.bhif17.agraraktionenmobilev2.model.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AccountActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    Handler handler = new Handler(Looper.getMainLooper());
    EditText username, email, password;

    String storedUser = MyProperties.getInstance().userLoginData;
    String primSubId = storedUser.substring(storedUser.indexOf(":") + 1);
    String secondSubId = primSubId.substring(primSubId.indexOf(":") + 1);
    String userId = secondSubId.substring(0, secondSubId.indexOf(","));

    public static User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        refresh();

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    public void  update() {
        new UpdateTask().execute();
        Toast.makeText(this, "Account aktualisiert", Toast.LENGTH_SHORT).show();
    }
    public void  logout() {
        new LogoutTask().execute();
        Toast.makeText(this, "abgemeldet", Toast.LENGTH_SHORT).show();
    }

    private void removeMyProperties() {
        String test = MyProperties.getInstance().userLoginData = null;
        System.out.println(test);
    }

    public JSONObject createJson(){
        /** Get updated User Data **/
        //email
        Editable editAbleToUpdateEmail = email.getText();
        String toUpdateEmail = editAbleToUpdateEmail.toString();

        //password
        Editable editAbleToUpdatePassword = password.getText();
        String toUpdatePassword = editAbleToUpdatePassword.toString();

        //username
        Editable editAbleToUpdateUsername = username.getText();
        String toUpdateUsername = editAbleToUpdateUsername.toString();

        //loggedIn
        boolean toUpdateLoggedIn = user.isLoggedIn();

        //id
        int toUpdateId = user.getId();

        JSONObject json = new JSONObject();
        try {
            json.put("email", toUpdateEmail);
            json.put("id", toUpdateId);
            json.put("loggedIn", toUpdateLoggedIn);
            json.put("password", toUpdatePassword);
            json.put("username", toUpdateUsername);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static JSONObject parseJSONStringToJSONObject(final String strr) {

        JSONObject response = null;
        try {
            response = new JSONObject(strr);
        } catch (Exception ex) {
            //  Log.e("Could not parse malformed JSON: \"" + json + "\"");
            try {
                response = new JSONObject();
                response.put("result", "failed");
                response.put("data", strr);
                response.put("error", ex.getMessage());
            } catch (Exception exx) {
            }
        }
        return response;
    }

    void refresh() {
        CompletableFuture
                .supplyAsync(this::loadData)
                .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> displayData(doPosts))));
    }
    void displayData(User downloadedUser) {
            user = downloadedUser;

            //maxSystem.out.println(downloadedUser);

        /** Set User Data from API **/
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        password.setText(user.getPassword());

    }

    // download data from rest-api
    Optional<User> loadData() {
        Optional<User> users = Optional.ofNullable(null);
        Log.i(TAG, "download category names from api...");
        try {
            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/user/" + userId);
            users = Optional.of(new ObjectMapper().readValue(url, User.class));
        } catch (Exception e) {
            Log.e(TAG, "Failed to download", e);
        }
        Log.i(TAG, "downloaded users successfully");
        return users;
    }

    public class UpdateTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = createJson();
            JSONObject jsonObjectResp = null;

            try {

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();

                okhttp3.RequestBody body = RequestBody.create(JSON, json.toString());
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://student.cloud.htl-leonding.ac.at/20170033/api/user/update")
                        .put(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();

                String networkResp = response.body().string();

                Log.i("Account", networkResp);

                jsonObjectResp = parseJSONStringToJSONObject(networkResp);

            } catch (Exception ex) {
                String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
                jsonObjectResp = parseJSONStringToJSONObject(err);
            }

            return jsonObjectResp;

        }
    }
    public class LogoutTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = createJson();
            JSONObject jsonObjectResp = null;
            boolean check = false;

            try{
                check = (boolean) json.get("loggedIn");
            }catch (JSONException e) {
                e.printStackTrace();
            }

            if(check==true){
                try {

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();

                    okhttp3.RequestBody body = RequestBody.create(JSON, json.toString());
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://student.cloud.htl-leonding.ac.at/20170033/api/user/logout")
                            .post(body)
                            .build();

                    okhttp3.Response response = client.newCall(request).execute();

                    String networkResp = response.body().string();

                    //remove the existing user data from MyProperties
                    removeMyProperties();

                    System.out.println(networkResp);
                    jsonObjectResp = parseJSONStringToJSONObject(networkResp);

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                } catch (Exception ex) {
                    String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
                    jsonObjectResp = parseJSONStringToJSONObject(err);
                }
            }
            return jsonObjectResp;

        }
    }

}
