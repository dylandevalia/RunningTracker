package app.psydd2.mdp.cw2_runningtracker.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import app.psydd2.mdp.cw2_runningtracker.content_provider.Contract.LocationTableData;

public class LocationProvider extends ContentProvider {
	
	/**
	 * Matches URIs and returns codes set below
	 */
	private static final UriMatcher uriMatcher;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// Add Location Table
		uriMatcher.addURI(Contract.AUTORITY, LocationTableData.TABLE_NAME, 1);
		uriMatcher.addURI(Contract.AUTORITY, LocationTableData.TABLE_NAME + "/#", 2);
		// If not found return code '9'
		uriMatcher.addURI(Contract.AUTORITY, "*", 9);
	}
	
	/**
	 * Helper class which deals with interacting with the database
	 */
	private DatabaseHelper dbHelper;
	
	@Override
	public boolean onCreate() {
		// Create helper
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}
	
	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		if (uri.getLastPathSegment() == null) {
			return Contract.CONTENT_TYPE_MULTIPLE;
		} else {
			return Contract.CONTENT_TYPE_SINGLE;
		}
	}
	
	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String tableName;
		
		switch (uriMatcher.match(uri)) {
			case 1:
			case 2:
				tableName = LocationTableData.TABLE_NAME;
				break;
			default:
				throw new UnsupportedOperationException("Unknown Table");
		}
		
		long id = db.insert(tableName, null, values);
		Uri nu = ContentUris.withAppendedId(uri, id);
		
		getContext().getContentResolver().notifyChange(nu, null);
		
		return nu;
	}
	
	@Nullable
	@Override
	public Cursor query(
		@NonNull Uri uri,
		@Nullable String[] projection,
		@Nullable String selection,
		@Nullable String[] selectionArgs,
		@Nullable String sortOrder
	) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		switch (uriMatcher.match(uri)) {
			case 2:
				selection = LocationTableData.ID + " = " + uri.getLastPathSegment();
			case 1:
				return db.query(
					LocationTableData.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
			default:
				return null;
		}
	}
	
	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
		@Nullable String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection,
		@Nullable String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
