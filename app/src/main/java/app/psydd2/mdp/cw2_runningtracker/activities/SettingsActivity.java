package app.psydd2.mdp.cw2_runningtracker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import app.psydd2.mdp.cw2_runningtracker.R;

public class SettingsActivity extends AppCompatActivity {
	
	private static final int MAX_ZOOM = 0;
	private static final int MAX_ZOOM_RUN = 1;
	private static final int NO_POINTS = 2;
	
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setTitle(getString(R.string.navigation_menu_settings));
		
		pref = getSharedPreferences(getString(R.string.preferences_path), MODE_PRIVATE);
		editor = pref.edit();
		
		
		/* Default values */
		
		TextView maxZoomDefault = findViewById(R.id.settings_text_max_zoom_default);
		maxZoomDefault.setText(
			getResources().getString(
				R.string.settings_default, getResources().getInteger(R.integer.max_zoom)
			)
		);
		TextView maxZoomRunDefault = findViewById(R.id.settings_text_max_zoom_run_default);
		maxZoomRunDefault.setText(
			getResources().getString(
				R.string.settings_default, getResources().getInteger(R.integer.max_zoom_run)
			)
		);
		TextView noPointsDefault = findViewById(R.id.settings_text_no_points_default);
		noPointsDefault.setText(
			getResources().getString(
				R.string.settings_default, getResources().getInteger(R.integer.no_points)
			)
		);
		
		
		/* Sliders and values */
		
		// Get references
		final TextView textMaxZoom = findViewById(R.id.settings_text_max_zoom);
		final TextView textMaxZoomRun = findViewById(R.id.settings_text_max_zoom_run);
		final TextView textNoPoints = findViewById(R.id.settings_text_no_points);
		SeekBar seekMaxZoom = findViewById(R.id.seek_max_zoom);
		SeekBar seekMaxZoomRun = findViewById(R.id.seek_max_zoom_run);
		SeekBar seekNoPoints = findViewById(R.id.seek_no_points);
		
		// Get values
		final int[] values = new int[3];
		values[MAX_ZOOM] = pref.getInt(
			getResources().getString(R.string.preferences_max_zoom),
			getResources().getInteger(R.integer.max_zoom)
		);
		values[MAX_ZOOM_RUN] = pref.getInt(
			getResources().getString(R.string.preferences_max_zoom_run),
			getResources().getInteger(R.integer.max_zoom_run)
		);
		values[NO_POINTS] = pref.getInt(
			getResources().getString(R.string.preferences_no_points),
			getResources().getInteger(R.integer.no_points)
		);
		
		// Set to current values
		textMaxZoom.setText(String.valueOf(values[MAX_ZOOM]));
		seekMaxZoom.setProgress(values[MAX_ZOOM]);
		textMaxZoomRun.setText(String.valueOf(values[MAX_ZOOM_RUN]));
		seekMaxZoomRun.setProgress(values[MAX_ZOOM_RUN]);
		textNoPoints.setText(String.valueOf(values[NO_POINTS]));
		seekNoPoints.setProgress(values[NO_POINTS]);
		
		// Listeners
		seekMaxZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				// min is not defined before android 26 so this is a hacky workaround
				if (progress < 1) {
					progress = 1;
					seekBar.setProgress(1);
				}
				values[MAX_ZOOM] = progress;
				textMaxZoom.setText(String.valueOf(progress));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			
			}
		});
		seekMaxZoomRun.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				// min is not defined before android 26 so this is a hacky workaround
				if (progress < 1) {
					progress = 1;
					seekBar.setProgress(1);
				}
				values[MAX_ZOOM_RUN] = progress;
				textMaxZoomRun.setText(String.valueOf(progress));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			
			}
		});
		seekNoPoints.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
				// min is not defined before android 26 so this is a hacky workaround
				if (progress < 1) {
					progress = 1;
					seekBar.setProgress(1);
				}
				values[NO_POINTS] = progress;
				textNoPoints.setText(String.valueOf(progress));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			
			}
		});
		
		
		/* Save button */
		
		Button btn_save = findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Store values
				editor.putInt(getString(R.string.preferences_max_zoom), values[MAX_ZOOM]);
				editor.putInt(getString(R.string.preferences_max_zoom_run), values[MAX_ZOOM_RUN]);
				editor.putInt(getString(R.string.preferences_no_points), values[NO_POINTS]);
				
				// Save and return
				editor.commit();
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}
