package org.scir.scir_android_app;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Created by khelender on 26-12-2015.
 */
public class SCIRLocationFinder implements LocationListener {

    private Location mScirDataCurrentLocation;
    private double mScirDataLatitude = 0.0;
    private double mScirDataLongitude = 0.0;
    private long mScirDataDatetime = 0 ;
    private double mScirDataAccuracy = 0.0 ;
    private int mScirDataStatus = 0 ;
    private int mScirDataStatusProvider ;
    private String mScirDataNameProvider ;

    public double getmScirDataLatitude() {
        return mScirDataLatitude;
    }
    public double getmScirDataLongitude() {
        return mScirDataLongitude;
    }
    public long getmScirDataDatetime() {
        return mScirDataDatetime;
    }
    public double getmScirDataAccuracy() {
        return mScirDataAccuracy;
    }

    public SCIRLocationFinder() {
        mScirDataCurrentLocation = null ;
        mScirDataStatusProvider = LocationProvider.OUT_OF_SERVICE ;

    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        // Update locations
        mScirDataCurrentLocation = location ;
        mScirDataLatitude = location.getLatitude() ;
        mScirDataLongitude = location.getLongitude() ;
        mScirDataAccuracy = location.getAccuracy() ;
        mScirDataDatetime = location.getTime() ;
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        mScirDataNameProvider = provider ;
        mScirDataStatusProvider = status ;
        mScirDataStatus = status ;
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

/* public boolean waitLocationAvailable() {}*/

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    public void updateBestLocation(Location newLocation) {
        if( isBetterLocation(newLocation, mScirDataCurrentLocation) ) {
            mScirDataCurrentLocation = newLocation ;
        }
    }

    public boolean isLocationQualityGood() {
        return true ;
        // TODO : To be fixed to provide correct evaluation of current location quality
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }




}
