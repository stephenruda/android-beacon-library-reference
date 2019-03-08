package org.altbeacon.beaconreference;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.startup.BootstrapNotifier;

/**
 * Created by dyoung on 12/13/13.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setDebug(true);

        beaconManager.setAndroidLScanningDisabled(true);

        beaconManager.setBackgroundBetweenScanPeriod(40100l);
        beaconManager.setBackgroundScanPeriod(4100l);
        beaconManager.setRegionExitPeriod(90000l);
        beaconManager.setForegroundBetweenScanPeriod(40100l);
        beaconManager.setForegroundScanPeriod(4100l);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Region region = new Region("backgroundRegion", null, null, null);
        RegionBootstrap regionBootstrap = new RegionBootstrap(this, region); }

    @Override
    public void didEnterRegion(Region region) {
        Log.d("ENTERED",region.getUniqueId());
        sendNotification("Entered");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d("EXITED",region.getUniqueId());
        sendNotification("Exited");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d("STATE = "+ state, region.getUniqueId());
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
