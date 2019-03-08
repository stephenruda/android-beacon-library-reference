package org.altbeacon.beaconreference;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class StartServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stop_service);

        getNotificationChannel();
    }

    public void pressedButton(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        boolean isUsingForegroundService = preferences.getBoolean("foreground_service_active",false);
        if(isUsingForegroundService) {
            editor.putBoolean("foreground_service_active", false);
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), CustomForegroundService.class);
            stopService(intent);
        }
        else {
            editor.putBoolean("foreground_service_active", true);
            editor.commit();

            Intent intent = new Intent(getApplicationContext(), CustomForegroundService.class);
            startForegroundService(intent);
        }
    }

    protected void getNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("notification_channel", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendNotification(String direction) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notification_channel")
                .setContentTitle("Beacon Reference Application")
                .setContentText(direction + " a Beacon region")
                .setSmallIcon(R.drawable.ic_launcher);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
