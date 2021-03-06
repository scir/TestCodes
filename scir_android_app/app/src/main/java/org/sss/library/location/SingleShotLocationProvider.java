package org.sss.library.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;


/**
 * Created by khelender on 28-12-2015.
 * Based on Borrowed code from public implementation available @ stack overflow by "Ashton Engberg"
 * http://stackoverflow.com/questions/29657948/get-the-current-location-fast-and-once-in-android
 *
 */

public class SingleShotLocationProvider {

    private static Location mScirCurrentLocation = null ;

    public static interface LocationCallback {
        public void onNewLocationAvailable(Location location);
    }

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mScirCurrentLocation = location;
                        callback.onNewLocationAvailable(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                }, null);
            } else {
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isNetworkEnabled) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);

                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            mScirCurrentLocation = location;
                            callback.onNewLocationAvailable(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, null);
                } else {
                }

            }
        } catch (SecurityException se) {

        }
    }

}