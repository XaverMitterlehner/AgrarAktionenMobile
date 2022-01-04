package htlperg.bhif17.agraraktionenmobilev2.image;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import htlperg.bhif17.agraraktionenmobilev2.MyProperties;
import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.model.Image;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AlreadyUploadedImages extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alread_uploaded_images);

        new DownloadAlreadyUsedOnce().execute();
    }
}
class DownloadAlreadyUsedOnce extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... params) {

        String myProperties = MyProperties.getInstance().userLoginData;
        JSONObject jsonObjectResp = null;

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
                List<Image> image = objectMapper.readValue(networkResp,  new TypeReference<List<Image>>(){});
                for(Image imageList: image){
                    System.out.println(imageList.getUsername());
                    System.out.println(imageList.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            jsonObjectResp = parseJSONStringToJSONObject(networkResp);


        } catch (Exception ex) {
            String err = String.format("{\"result\":\"false\",\"error\":\"%s\"}", ex.getMessage());
            jsonObjectResp = parseJSONStringToJSONObject(err);
        }

        return jsonObjectResp;

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


}