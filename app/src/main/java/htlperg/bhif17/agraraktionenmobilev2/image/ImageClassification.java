package htlperg.bhif17.agraraktionenmobilev2.image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import htlperg.bhif17.agraraktionenmobilev2.MainActivity;
import htlperg.bhif17.agraraktionenmobilev2.account.AccountActivity;
import htlperg.bhif17.agraraktionenmobilev2.login.RegisterActivity;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;
import htlperg.bhif17.agraraktionenmobilev2.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageClassification extends AppCompatActivity {

    public final static String TAG = ImageClassification.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    TextView file_name, loadingText;
    String file_path;
    Button upload, usedImages;
    ProgressBar progressBar;
    ImageView image_View;

    boolean finished;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classification);

        getSupportActionBar().setTitle("Bild-Suche");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Check if user logged in or not
        usedImages = findViewById(R.id.uploadedImagesButton);
        String userData = MyProperties.getInstance().userLoginData;
        if(userData == null || userData == ""){
            System.out.println("without login!");
            usedImages.setVisibility(View.GONE);
        }else {
            System.out.println("with login!");
            usedImages.setVisibility(View.VISIBLE);
        }

        Button takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        photoTaker();
                    } else {
                        requestPermission();
                    }
                } else {
                    photoTaker();
                }
            }
        });

        Button selectFile = findViewById(R.id.selectFile);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check permission greater than equal to marshmeellow we used run time permission
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        filePicker();
                    } else {
                        requestPermission();
                    }
                } else {
                    filePicker();
                }
            }
        });

        progressBar = findViewById(R.id.progress);
        upload = findViewById(R.id.upload);
        file_name = findViewById(R.id.filename);
        image_View = findViewById(R.id.imageView);
        loadingText = findViewById(R.id.loadingText);

        loadingText.setText("");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_path != null) {
                    UploadFile();
                } else {
                    Toast.makeText(ImageClassification.this, "Please Select File First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        usedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageClassification.this, UploadedImagesActivity.class);
                startActivity(intent);
            }
        });

    }

    private void UploadFile() {
        UploadTask uploadTask = new UploadTask();
        uploadTask.execute(new String[]{file_path});
    }

    private void photoTaker() {

        Toast.makeText(ImageClassification.this, "Camera opened", Toast.LENGTH_SHORT).show();
        //Intent takePictureIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            System.out.println("Failed!");
            // display error state to the user
        }

    }

    private void filePicker() {

        //.Now Permission Working
        Toast.makeText(ImageClassification.this, "File Picker Call", Toast.LENGTH_SHORT).show();
        //Let's Pick File
        Intent opengallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        opengallery.setType("image/*");
        startActivityForResult(opengallery, REQUEST_GALLERY);
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ImageClassification.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(ImageClassification.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(ImageClassification.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ImageClassification.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ImageClassification.this, "Permission Successfull", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageClassification.this, "Permission Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            //Call getRealPathFromUri, set and show image_View and set file_name
            String filePath = getRealPathFromUri(data.getData(), ImageClassification.this);
            Log.d("File Path : ", " " + filePath);
            image_View.setImageURI(data.getData());
            this.file_path = filePath;
            File file = new File(filePath);
            file_name.setText(file.getName());

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //Get Bitmap from picture
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image_View.setImageBitmap(photo);
            //Convert Bitmap to JPEG
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //Get path of the converted JPEG and save it in gallery
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
            //Get uri of the saved image
            String filePath = getRealPathFromUri(Uri.parse(path), ImageClassification.this);
            Log.d("File Path : ", " " + filePath);
            this.file_path = filePath;
            File file = new File(filePath);
            file_name.setText(file.getName());

        }


    }

    public String getRealPathFromUri(Uri uri, Activity activity) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }
    }


    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if (s.equalsIgnoreCase("true")) {
                Toast.makeText(ImageClassification.this, "File uploaded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ImageClassification.this, "Failed Upload", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            if (uploadFile(strings[0])) {
                return "true";
            } else {
                return "failed";
            }
        }

        private boolean uploadFile(String path) {

            File file = new File(path);

            finished = false;

            new Thread(new Runnable() {
                int progress = 0;

                public void run() {
                    //---do some work here---
                    while (finished == false) {
                        doSomeWork();
                    }
                    loadingText.setText("");
                }

                //---do some long lasting work here---
                private void doSomeWork() {
                    try {
                        //---simulate doing some work---
                        loadingText.setText("loading .");
                        Thread.sleep(500);
                        loadingText.setText("loading ..");
                        Thread.sleep(500);
                        loadingText.setText("loading ...");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }

            }).start();



            /*ProgressDialog dialog = new ProgressDialog(ImageClassification.this);
            dialog.setMessage("loading");
            dialog.setIcon(R.drawable.agraraktionen_logo_small);
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();*/

            try {
                String myProperties = MyProperties.getInstance().userLoginData;
                if (myProperties == null) {
                    myProperties = "";
                }
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("selectedFile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                        .addFormDataPart("userName", myProperties)
                        .build();


                Request request = new Request.Builder()
                        //.url("http://10.0.2.2:8080/api/image/upload")
                        //.url("http://10.0.2.2:8080/api/upload/imageAndData")
                        .url("https://student.cloud.htl-leonding.ac.at/20170033/api/upload/imageAndData")
                        .post(requestBody)
                        .build();


                //custom timeout, to prevent app form timeout exception while uploading an image to the server
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();

                Log.i(TAG, "Waiting for response ...");

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        System.out.println(ImageClassification.class.getSimpleName() + ": " + response.toString());

                        finished = true;

                        loadingText.setText("");

                        Intent intent = new Intent(ImageClassification.this, SimiliarItemsActivity.class);
                        startActivity(intent);
                    }

                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

    }

}