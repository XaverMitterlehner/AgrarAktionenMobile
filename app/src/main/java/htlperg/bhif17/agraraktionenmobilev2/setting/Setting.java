package htlperg.bhif17.agraraktionenmobilev2.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import htlperg.bhif17.agraraktionenmobilev2.MainActivity;
import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.image.ImageClassification;

public class Setting extends AppCompatActivity {

    private Switch notificationSwitch;
    private Button apply;

    private boolean notificationSwitchOnOff;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NOTIFICATIONSWITCH = "notificationSwitch";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationSwitch = (Switch)findViewById(R.id.notificationSwitch);
        apply = findViewById(R.id.saveSettings);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        loadData();
        updateView();

    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATIONSWITCH, notificationSwitch.isChecked());
        editor.apply();

        Toast.makeText(this, "settings updated", Toast.LENGTH_SHORT).show();
    }
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        notificationSwitchOnOff = sharedPreferences.getBoolean(NOTIFICATIONSWITCH, false);
    }
    public void updateView() {
    notificationSwitch.setChecked(notificationSwitchOnOff);
 }

}
