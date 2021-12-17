package htlperg.bhif17.agraraktionenmobilev2.login;

import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import htlperg.bhif17.agraraktionenmobilev2.MainActivity;
import htlperg.bhif17.agraraktionenmobilev2.R;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;
    boolean isEmailValid, isPasswordValid;
    TextInputLayout emailError, passError;
    TextView textView;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);
        textView = findViewById(R.id.textView);
        checkBox = findViewById(R.id.checkBox);

        login.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    sendPost(email, password);
                    login.setVisibility(View.VISIBLE);
                }else{
                    login.setVisibility(View.INVISIBLE);
                }


            }
            private void sendPost(final EditText email, final EditText password) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/user/getUserByEmailAndPassword");
                            //URL url = new URL("http://10.0.2.2:8080/api/user/getUserByEmailAndPassword");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("email", email.getText());
                            jsonParam.put("password", password.getText());
                            jsonParam.put("loggedIn", false);

                            Log.i("JSON", jsonParam.toString());
                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                            os.writeBytes(jsonParam.toString());

                            os.flush();
                            os.close();

                            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                            Log.i("MSG", conn.getResponseMessage());

                            //Response from The POST Request
                            try(BufferedReader br = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                                StringBuilder response = new StringBuilder();
                                String responseLine = null;
                                while ((responseLine = br.readLine()) != null) {
                                    response.append(responseLine.trim());
                                }
                                System.out.println(response.toString());
                                String responseString = response.toString();
                                if(responseString.equals("[]") || responseString.isEmpty()){
                                    Looper.prepare();
                                    textView.setText("");
                                }else {
                                    Looper.prepare();
                                    textView.setText(response.toString());
                                }

                            }


                            conn.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
                newAction();
                checkBox.setChecked(false);
            }

            private void newAction() {
                String text = (String) textView.getText();
                System.out.println(text);
                if(text.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Failed to Login", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Successful!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    myIntent.putExtra("response", text); //Optional parameters
                    myIntent.putExtra("email", password.getText());
                    myIntent.putExtra("password", password.getText());
                    LoginActivity.this.startActivity(myIntent);
                    textView.setText("");
                }

            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void SetValidation() {
        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid password.
        if (password.getText().toString().isEmpty()) {
            passError.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (password.getText().length() < 6) {
            passError.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
            passError.setErrorEnabled(false);
        }

        if (isEmailValid && isPasswordValid) {
            // Toast.makeText(getApplicationContext(), "Correctly inserted!", Toast.LENGTH_SHORT).show();
        }

    }
}