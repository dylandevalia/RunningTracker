package app.psydd2.mdp.cw2_runningtracker.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import app.psydd2.mdp.cw2_runningtracker.R;
import com.google.android.gms.maps.model.LatLng;

public class GPSService extends Service {
	
	private LocationListener listener;
	private LocationManager locationManager;
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	// Already checked permissions so just suppressing the error
	@SuppressLint("MissingPermission")
	@Override
	public void onCreate() {
		// Create a location listener which sends the new latitude/longitude information
		// to the broadcast receiver
		listener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				// Send LatLng information
				Intent intent = new Intent(
					getApplicationContext().getString(R.string.location_updates)
				);
				intent.putExtra(
					getApplicationContext().getString(R.string.position_latlng),
					new LatLng(location.getLatitude(), location.getLongitude())
				);
				sendBroadcast(intent);
			}
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			
			}
			
			@Override
			public void onProviderEnabled(String provider) {
			
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// Open location settings if disabled
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		};
		
		// Get reference o system location service
		locationManager = (LocationManager) getApplicationContext()
			.getSystemService(Context.LOCATION_SERVICE);
		
		// Request user location every 3 seconds
		assert locationManager != null;
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
			// Detach listener
			locationManager.removeUpdates(listener);
		}
	}
}
