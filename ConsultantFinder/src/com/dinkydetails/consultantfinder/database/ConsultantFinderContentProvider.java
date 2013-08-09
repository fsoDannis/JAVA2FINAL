/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.database;



import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.util.*;

import com.dinkydetails.consultantfinder.model.Directory.Directories;

/**
 * Class to implement the custom content provider for the android application
 */
public class ConsultantFinderContentProvider extends ContentProvider {
	
	/** Constant to save the authority for the custom content provider */
	public static final String AUTHORITY = "com.dinkydetails.consultantfinder.providers.ConsultantFinderContentProvider";
	
	/** Constant to save the database file name for the custom content provider */
	private static final String DATABASE_NAME = "consultantfinder.db";
	
	/** Constant to save the database version for the custom content provider */
	private static final int DATABASE_VERSION = 1;
	
	/** Variables */
	private DatabaseHelper dbHelper = null;

	private static final UriMatcher sUriMatcher;
	
	private static final String TAG = ConsultantFinderContentProvider.class.getSimpleName();
	
	private static final String CREATE_TABLE_DIRECTORIES_SQL = "CREATE TABLE directories (company TEXT, firstname TEXT, lastname TEXT, category TEXT, email TEXT, phone TEXT, description TEXT, image TEXT);";
	
	public static final String DIRECTORIES_TABLE_NAME = "directories";
	
	/** Constant to save the id for directories table */
	private static final int DIRECTORIES = 1;
	
	/** Constant to save the hash map for directories table */
	private static HashMap<String, String> directoriesProjectionMap = null;


	
	/**
	 * Class to implement the custom SQLiteDatabaseHelp for the custom content provider
	 */
	public static class DatabaseHelper extends SQLiteOpenHelper {
		
		/** Variable to save the SQLiteDatase widget */
		private SQLiteDatabase mDatabase = null;
		
		/** Constructor */
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		/**
		 * Override Method to create the directories table using the SQL statement
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_DIRECTORIES_SQL);
		}
		
		/**
		 * Override Method to upgrade the exist old directories table
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DIRECTORIES_TABLE_NAME);
			onCreate(db);
		}
		
		/**
		 * Method to open the database for the content provider
		 */
		public boolean openDatabase() {
			try {
				mDatabase = SQLiteDatabase.openDatabase("/data/data/com.dinkydetails.consultantfinder/databases/consultantfinder.db", null, SQLiteDatabase.OPEN_READONLY);
			} catch (Exception e) {
				Log.e("DatabaseAccess", e.getLocalizedMessage());
				return false;
			}
			if(mDatabase == null)
				return false;

			return true;
		}
		
		/**
		 * Method to execute the query statement
		 */
		public Cursor query(String query) {			
			try {
				return mDatabase.rawQuery(query, null);
			}catch (android.database.sqlite.SQLiteDatabaseCorruptException e) {
				return null;
			}catch (SQLiteException exception) {
				Log.e("DatabaseAccess", exception.getLocalizedMessage());
				return null;
			}
		}
		
		/**
		 * Override Method to close the sql database
		 */
		@Override
		public synchronized void close() {

			if (mDatabase != null)
				mDatabase.close();

			super.close();

		}
		
		/**
		 * Method to check if the sql database is already opened
		 */
		public boolean isOpen() {
			if(mDatabase == null)
				return false;
			
			if(mDatabase.isOpen())
				return true;
			
			return false;
		}

	}

	/**
	 * Override Method to execute the delete statement
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case DIRECTORIES:
				count = db.delete(DIRECTORIES_TABLE_NAME, where, whereArgs);
				break;
			
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/**
	 * Override Method to get the uri type
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case DIRECTORIES:
				return Directories.CONTENT_TYPE;
			
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	/**
	 * Method to execute the insert statement
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		switch (sUriMatcher.match(uri)) {
			case DIRECTORIES:
				return insert(DIRECTORIES_TABLE_NAME, Directories.CONTENT_URI, initialValues);
			
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

	}
	
	/**
	 * Method to execute the insert statement with the specific table name and content values
	 */
	private Uri insert(String tableName, Uri contentUri, ContentValues initialValues) {
		
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(tableName, null, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		else {
			Log.e(TAG, "Failed to insert row into " + contentUri);
			//throw new SQLException("Failed to insert row into " + contentUri);
			return null;
		}
	}
	
	/**
	 * Override Method to create the custom DatabaseHelper
	 */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}
	
	/** 
	 * Override Method to execute the query statement
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
			case DIRECTORIES:
				qb.setTables(DIRECTORIES_TABLE_NAME);
				qb.setProjectionMap(directoriesProjectionMap);
				break;
	
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	/** 
	 * Override Method to execute the update statement
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case DIRECTORIES:
			count = db.update(DIRECTORIES_TABLE_NAME, values, where, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/**
	 * Static statement to initialize the uri matcher and hash map for directories table
	 */
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		/** deals */
		sUriMatcher.addURI(AUTHORITY, DIRECTORIES_TABLE_NAME, DIRECTORIES);

		directoriesProjectionMap = new HashMap<String, String>();
		directoriesProjectionMap.put(Directories.COMPANY, Directories.COMPANY);
		directoriesProjectionMap.put(Directories.FIRSTNAME, Directories.FIRSTNAME);
		directoriesProjectionMap.put(Directories.LASTNAME, Directories.LASTNAME);
		directoriesProjectionMap.put(Directories.CATEGORY, Directories.CATEGORY);
		directoriesProjectionMap.put(Directories.EMAIL, Directories.EMAIL);
		directoriesProjectionMap.put(Directories.PHONE, Directories.PHONE);
		directoriesProjectionMap.put(Directories.DESCRIPTION, Directories.DESCRIPTION);
		directoriesProjectionMap.put(Directories.IMAGE, Directories.IMAGE);
	}

}

