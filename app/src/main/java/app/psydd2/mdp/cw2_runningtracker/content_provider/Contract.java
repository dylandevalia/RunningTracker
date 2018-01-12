package app.psydd2.mdp.cw2_runningtracker.content_provider;

import android.net.Uri;

public class Contract {
	public static final String AUTORITY = "app.psydd2.mdp.cw2_runningtracker.content_provider.LocationProvider";
	
	public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/LocationProvider.data.text";
	public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/LocationProvider.data.text";
	
	public static class LocationTableData {
		public static final String TABLE_NAME = "location_data";
		
		public static final Uri URI = Uri.parse("content://" + AUTORITY + "/" + TABLE_NAME);
		
		public static final String ID = "_id";
		public static final String DATETIME = "datetime";
		public static final String LAT = "lat";
		public static final String LNG = "lng";
	}
}
