package org.altbeacon.beaconreference;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by dyoung on 12/13/13.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setDebug(true);

        beaconManager.setAndroidLScanningDisabled(true);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Calling setScanPeriods before starting the service will cause duplicate events
        // setScanPeriodsToDesiredTimes();

        startForegroundService();

        // Calling setScanPeriods after starting the service will work correctly
        setScanPeriodsToDesiredTimes();

        Region region = new Region("backgroundRegion", null, null, null);
        RegionBootstrap regionBootstrap = new RegionBootstrap(this, region);
    }

    protected void setScanPeriodsToDesiredTimes() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundBetweenScanPeriod(40100l);
        beaconManager.setBackgroundScanPeriod(4100l);
        beaconManager.setRegionExitPeriod(90000l);
        beaconManager.setForegroundBetweenScanPeriod(40100l);
        beaconManager.setForegroundScanPeriod(4100l);
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void startForegroundService() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.enableForegroundServiceScanning(getNotificationBuilder().build(), 123);
        beaconManager.setEnableScheduledScanJobs(false);
    }

    protected void getNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("notification_channel", "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public Notification.Builder getNotificationBuilder() {
        getNotificationChannel();
        Notification.Builder builder = new Notification.Builder(this, "notification_channel");
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle("Scanning for Beacons");
        return builder;
    }

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
