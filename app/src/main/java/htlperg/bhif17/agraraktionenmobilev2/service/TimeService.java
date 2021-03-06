package htlperg.bhif17.agraraktionenmobilev2.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import htlperg.bhif17.agraraktionenmobilev2.MainActivity;
import htlperg.bhif17.agraraktionenmobilev2.R;
import htlperg.bhif17.agraraktionenmobilev2.login.LoginActivity;
import htlperg.bhif17.agraraktionenmobilev2.model.CheckSum;
import htlperg.bhif17.agraraktionenmobilev2.model.MyProperties;

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 10 seconds
    public final static String TAG = MainActivity.class.getSimpleName();
    private static final String CHANNEL_ID = "1000";
    Handler handler = new Handler(Looper.getMainLooper());

    List<CheckSum> checkSumList;

    //Notification System
    private int notificationId = 1000;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    // variables from Settings to check if notifications are allowed
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String NOTIFICATION_SWITCH = "notificationSwitch";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);

    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    //check for notification status
                    boolean notificationParam = loadSettings();

                    if(notificationParam == true){

                        // display toast
                        //Toast.makeText(getApplicationContext(), "Notification enabled!", Toast.LENGTH_SHORT).show();

                        //load notification
                        refresh();
                    }

                }

            });
        }
        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
        void refresh(){
            CompletableFuture
                    .supplyAsync(this::loadData)
                    .thenAccept(posts -> posts.ifPresent(doPosts -> handler.post(() -> displayData(doPosts))));
        }
        void displayData(CheckSum checkSums[]) {
            checkSumList = new LinkedList<>();
            checkSumList.addAll(Arrays.asList(checkSums));
            for(CheckSum checkSum: checkSumList){
                if(!checkSum.isChanged()){

                    String username = "";
                    try{
                        username = MyProperties.getInstance().user.getUsername();
                        if(username == "" || username.isEmpty() || username.equals("")){
                            username = "Benutzer";
                        }
                    }catch (Exception e){
                        //System.err.println("Error: " + e);
                    }


                    //Notification Build
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    // Create the TaskStackBuilder and add the intent, which inflates the back stack
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.agraraktionen_a_large_scaled)
                            .setContentTitle("Agrar Aktionen Mobile")
                            .setContentText("Schau dir doch jetzt die neuen Produkte an!")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                   //.bigText("Schau dir die neuen Produkte jetzt an " + "\n" + "Wann: " + getDateTime()))
                                    .bigText("Hi " + username + " es sind neue Artikel verf??gbar!"))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);


                    //Notification alert
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    // notificationId is a unique int for each notification that you must define
                    //notificationId++;
                    notificationManager.notify(notificationId, builder.build());


                }else{
                    //Toast.makeText(getApplicationContext(), "Nothing changed!" + MyProperties.getInstance().user.getUsername(), Toast.LENGTH_SHORT).show();
                }

            }

        }

        Optional<CheckSum[]> loadData() {
            Optional<CheckSum[]> checksum = Optional.ofNullable(null);
            //Log.i(TAG, "download checksum from api...");
            try {
                //URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/checksum");
                URL url = new URL("https://student.cloud.htl-leonding.ac.at/20170033/api/checksum");
                checksum = Optional.of(new ObjectMapper()
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                        .readValue(url, CheckSum[].class));
            } catch(Exception e) {
                Log.e(TAG, "Failed to download", e);
            }
            //Log.i(TAG, "downloaded checksum succesfully");
            return checksum;
        }
        public boolean loadSettings() {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            return sharedPreferences.getBoolean(NOTIFICATION_SWITCH, false);
        }
    }
}
