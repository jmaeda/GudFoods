package edu.brandeis.cs.moseskim.gudfoods;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by moseskim on 11/19/16.
 */
public class MyLocationListener implements LocationListener {

    String coordinates;


    @Override
    public void onLocationChanged(Location location) {
        if (location!= null){
//            Log.e("Latitude :", "" + location.getLatitude());
//            Log.e("Longitude :", "" + location.getLongitude());
//
//
//            Log.v("please","work");
            this.coordinates = location.getLatitude() + "," + location.getLongitude();
        }
    }


    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public String getCoordinates(){
        return coordinates;
    }
}
