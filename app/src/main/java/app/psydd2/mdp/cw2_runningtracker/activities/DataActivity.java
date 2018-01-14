package app.psydd2.mdp.cw2_runningtracker.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.psydd2.mdp.cw2_runningtracker.R;
import app.psydd2.mdp.cw2_runningtracker.content_provider.DatabaseContract.RunDataTable;

public class DataActivity extends AppCompatActivity {
	
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		
		/* Navigation panel */
		
		drawerLayout = findViewById(R.id.drawer_layout_data);
		drawerToggle = new ActionBarDrawerToggle(
			this,
			drawerLayout,
			R.string.navigation_menu_open,
			R.string.navigation_menu_close
		);
		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
		// Shows menu button in action bar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Do stuff when items are pressed
		NavigationView navigationView = findViewById(R.id.navigation_data);
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.nav_map:
						setResult(RESULT_OK);
						finish();
						break;
					case R.id.nav_past_data:
						break;
					case R.id.nav_settings:
						Toast.makeText(
							DataActivity.this,
							R.string.navigation_menu_settings,
							Toast.LENGTH_SHORT
						).show();
						break;
				}
				
				drawerLayout.closeDrawers();
				return true;
			}
		});
		// Set current page to be highlighted
		navigationView.getMenu().getItem(1).setChecked(true);
		
		
		/* List View */
		
		String[] projection = new String[]{
			RunDataTable.ID,
			RunDataTable.START_TIME,
			RunDataTable.DURATION,
			RunDataTable.DISTANCE
		};
		
		Cursor cursor = getContentResolver().query(
			RunDataTable.URI,
			projection,
			null, null, null
		);
		
		String[] toDisplay = new String[]{
			RunDataTable.START_TIME,
			RunDataTable.DURATION,
			RunDataTable.DISTANCE
		};
		
		int[] columnResIds = new int[]{
			R.id.run_list_start,
			R.id.run_list_time,
			R.id.run_list_distance
		};
		
		final long[] max = new long[]{0, 0};
		
		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
			this,
			R.layout.run_listview_layout,
			cursor,
			toDisplay,
			columnResIds,
			0
		) {
			@Override
			public void setViewText(TextView v, String text) {
				super.setViewText(v, formatText(v, text));
			}
			
			private String formatText(TextView v, String text) {
				switch (v.getId()) {
					case R.id.run_list_start:
						long startTime = Long.parseLong(text);
						Date startDate = new Date(startTime);
						SimpleDateFormat startSdf = new SimpleDateFormat("EEE, dd MMM - h:mm a");
						return startSdf.format(startDate);
					case R.id.run_list_time:
						long millis = Long.parseLong(text);
						return longMillisToTime(millis);
					case R.id.run_list_distance:
						int dist = (int) Float.parseFloat(text);
						return String.valueOf(dist) + "m";
					default:
						return text;
				}
			}
		};
		
		ListView listView = findViewById(R.id.list_view);
		listView.setAdapter(dataAdapter);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			
			}
		});
		
		cursor = getContentResolver().query(
			RunDataTable.URI,
			projection,
			null, null,
			RunDataTable.DISTANCE + " DESC"
		);
		
		cursor.moveToFirst();
		String maxDist = cursor.getString(cursor.getColumnIndex(RunDataTable.DISTANCE));
		TextView dist = findViewById(R.id.data_max_dist);
		dist.setText(maxDist + "m");
		
		cursor = getContentResolver().query(
			RunDataTable.URI,
			projection,
			null, null,
			RunDataTable.DURATION + " ASC"
		);
		
		cursor.moveToLast();
		String maxTime = cursor.getString(cursor.getColumnIndex(RunDataTable.DURATION));
		long millis = Long.parseLong(maxTime);
		maxTime = longMillisToTime(millis);
		TextView time = findViewById(R.id.data_max_time);
		time.setText(maxTime);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			// Intercept the onClick and return true if it belongs to
			// the navigation drawer
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private String longMillisToTime(long millis) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
}
