package app.psydd2.mdp.cw2_runningtracker.activities;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import app.psydd2.mdp.cw2_runningtracker.GPSService;
import app.psydd2.mdp.cw2_runningtracker.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
	
	private final static int PERMISSION_REQUEST_CODE = 100;
	
	private FloatingActionButton fab;
	boolean serviceCreated = false;
	private GoogleMap map;
	private ArrayList<LatLng> locations = new ArrayList<>();
	
	private BroadcastReceiver broadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
			.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		fab = findViewById(R.id.floating_action_button);
		// Check if user has accepted the required permissions
		if (checkPermissions()) {
			fab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), GPSService.class);
					
					// Flip service from on/off
					if (!serviceCreated) {
						startService(intent);
						serviceCreated = true;
						// Change image to cross
						fab.setImageResource(R.drawable.ic_clear_white_32dp);
						map.setMaxZoomPreference(17);
					} else {
						stopService(intent);
						serviceCreated = false;
						// Change image to plus
						fab.setImageResource(R.drawable.ic_add_white_32dp);
						// Update camera to show entire route
						updateCameraPosition(-1);
						map.resetMinMaxZoomPreference();
					}
				}
			});
		}
	}
	
	/**
	 * Check that the application has the correct permissions
	 * Requests permission dialogue opens if permissions are not granted
	 *
	 * @return True if permissions are granted
	 */
	private boolean checkPermissions() {
		if (
			VERSION.SDK_INT >= VERSION_CODES.M
				&& ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED
			) {
			requestPermissions(new String[]{
				permission.ACCESS_FINE_LOCATION
			}, PERMISSION_REQUEST_CODE);
			
			return false;
		}
		return true;
	}
	
	@Override
	public void onRequestPermissionsResult(
		int requestCode, @NonNull String[] permissions,
		@NonNull int[] grantResults
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				checkPermissions();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (broadcastReceiver == null) {
			broadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					// Get location information from service
					LatLng currentPos = (LatLng) intent.getExtras()
						.get(getApplicationContext().getString(R.string.position_latlng));
					
					if (currentPos == null) {
						// Since from intent, can produce null
						return;
					}
					
					if (locations == null) {
						locations = new ArrayList<>();
					}
					locations.add(currentPos);
					
					// Create marker at new location
					MarkerOptions marker = new MarkerOptions()
						.position(currentPos)
						.icon(vectorToBitmap(R.drawable.ic_directions_run_black_32dp,
							getApplicationContext().getColor(R.color.colorPrimary))
						);
					// Clear the map of old markers and add new one
					// This is to prevent loads of markers on the screen at each data-point
					map.clear();
					map.addMarker(marker);
					
					// Update and animate camera position
					updateCameraPosition(10);
					
					// Draw line between all points to show route
					PolylineOptions line = new PolylineOptions()
						.addAll(locations)
						.width(10)
						.color(getApplicationContext().getColor(R.color.colorAccent));
					map.addPolyline(line);
				}
			};
		}
		
		registerReceiver(broadcastReceiver, new IntentFilter(
			getApplicationContext().getString(R.string.location_updates)
		));
	}
	
	/**
	 * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
	 * for use as a marker icon.
	 */
	private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
		Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
		
		Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
			vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		
		Canvas canvas = new Canvas(bitmap);
		vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		
		DrawableCompat.setTint(vectorDrawable, color);
		vectorDrawable.draw(canvas);
		
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}
	
	/**
	 * Update the map camera's position and zoom, and animate to it
	 *
	 * @param maxNoSteps Number of steps to keep in the frame (use -1 to show all)
	 */
	private void updateCameraPosition(int maxNoSteps) {
		CameraUpdate cameraUpdate;
		
		if (locations.size() < 1) {
			return;
		} else if (locations.size() == 1) {
			cameraUpdate = CameraUpdateFactory.newLatLngZoom(locations.get(0), 12);
		} else {
			
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			
			if (maxNoSteps < 0) {
				for (LatLng pos : locations) {
					builder.include(pos);
				}
			} else {
				int length = locations.size();
				for (int i = (length > maxNoSteps) ? length - maxNoSteps : 0; i < length; i++) {
					
					builder.include(locations.get(i));
				}
			}
			
			LatLngBounds bounds = builder.build();
			cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100);
		}
		
		map.animateCamera(cameraUpdate);
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
	}
}
