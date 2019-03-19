package org.altbeacon.beaconreference;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

/**
 * Created by dyoung on 12/13/13.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {

    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setDebug(true);
        setScanPeriodsToDesiredTimes();

        beaconManager.setAndroidLScanningDisabled(true);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Calling callPotentiallyBrokenMethods before starting the service will cause duplicate events
        callPotentiallyBrokenMethods();

        startForegroundService();

        // Calling callPotentiallyBrokenMethods after starting the service will work correctly
        // callPotentiallyBrokenMethods();

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
    }

    // Trying to do certain methods on the BeaconManager before starting the foreground service will lead to duplicate entry/exit events
    protected void callPotentiallyBrokenMethods() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        // UNCOMMENT THIS SECTION
        try {
            beaconManager.updateScanPeriods();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        // OR UNCOMMENT THIS SECTION
//        beaconManager.setBackgroundMode(true);


        // UNCOMMENT THIS SECTION
//        Region region = new Region("backgroundRegion", null, null, null);
//        try {
//            beaconManager.startRangingBeaconsInRegion(region);
//            beaconManager.addRangeNotifier(new RangeNotifier() {
//                @Override
//                public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
//                    Log.d("DID RANGE",region.getUniqueId());
//                }
//            });
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

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
