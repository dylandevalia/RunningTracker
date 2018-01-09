package app.psydd2.mdp.cw2_runningtracker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import app.psydd2.mdp.cw2_runningtracker.R;

public class SettingsActivity extends AppCompatActivity {
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		sharedPreferences = getSharedPreferences(getString(R.string.preferences_path), MODE_PRIVATE);
		editor = sharedPreferences.edit();
		
		editor.putInt(getString(R.string.preferences_max_zoom), 12);
	}
}
