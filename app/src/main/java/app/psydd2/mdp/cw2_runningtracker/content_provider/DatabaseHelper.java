package app.psydd2.mdp.cw2_runningtracker.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import app.psydd2.mdp.cw2_runningtracker.content_provider.Contract.LocationTableData;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public DatabaseHelper(Context context) {
		super(context, "db_psydd2_locations", null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(
			"CREATE TABLE IF NOT EXISTS " + LocationTableData.TABLE_NAME + "("
			+ LocationTableData.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ LocationTableData.DATETIME + " DATE DEFAULT (datetime('now', 'localtime')),"
			+ LocationTableData.LAT + " INTEGER NOT NULL, "
			+ LocationTableData.LNG + " INTEGER NOT NULL"
			+ ");"
		);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + LocationTableData.TABLE_NAME);
		onCreate(db);
	}
}
