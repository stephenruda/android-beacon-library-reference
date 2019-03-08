package org.altbeacon.beaconreference;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.altbeacon.beacon.BeaconManager;

/**
 * Created by stephenruda on 11/15/17.
 */

public class CustomForegroundService extends Service {

    @Override
    public void onCreate() {
        startForeground(123, getNotificationBuilder().build());
    }

    public Notification.Builder getNotificationBuilder() {
        Notification.Builder builder = new Notification.Builder(this, "notification_channel");
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Scanning for Beacons");
        return builder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundMode(false);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundMode(true);
    }
}