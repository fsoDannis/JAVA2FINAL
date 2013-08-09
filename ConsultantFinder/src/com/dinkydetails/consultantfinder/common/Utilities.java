/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.common;

import java.util.*;

import org.json.*;

import com.dinkydetails.consultantfinder.database.*;
import com.dinkydetails.consultantfinder.model.Directory;
import com.dinkydetails.consultantfinder.model.Directory.Directories;

import android.content.*;
import android.database.*;

/**
 * Class to implement the utilities used in the android application
 */

public class Utilities {
	
	/**
	 * Method to clear all data in the local database
	 */
	public static void clearDirectories(Context context) {
		ContentResolver cr = context.getContentResolver();
		cr.delete(Directories.CONTENT_URI, null, null);
	}
	
	/**
	 * Method to insert the json object with directory into local database(directories table)
	 */
	public static void insertDirectory(Context context, JSONObject jsonObject) throws JSONException {
		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		
		values.put(Directories.COMPANY, jsonObject.getString("company"));
		values.put(Directories.FIRSTNAME, jsonObject.getString("firstName"));
		values.put(Directories.LASTNAME, jsonObject.getString("lastName"));
		values.put(Directories.CATEGORY, jsonObject.getString("category"));
		values.put(Directories.EMAIL, jsonObject.getString("email"));
		values.put(Directories.PHONE, jsonObject.getString("phone"));
		values.put(Directories.DESCRIPTION, jsonObject.getString("description"));
		values.put(Directories.IMAGE, jsonObject.getString("image"));
		
		cr.insert(Directories.CONTENT_URI, values);
	}
	
	/**
	 * Method to get categories saved in the local database
	 */
	public static String[] getCategories(Context context) {
		String[] categories = null;
		
		String sql = "SELECT category, count(*) FROM directories GROUP BY category";
		
		ConsultantFinderContentProvider.DatabaseHelper dbConn = new ConsultantFinderContentProvider.DatabaseHelper(context);
		
		Cursor cursor = null;
		int i = 0;
		
		if (dbConn.openDatabase()) {
			cursor = dbConn.query(sql);
			categories = new String[cursor.getCount()];
			if (cursor.moveToFirst()) {
				do {
					categories[i] = cursor.getString(0);
					i++;
				}
				while(cursor.moveToNext());
			}
			
			cursor.close();
			dbConn.close();
		}
		
		return categories;
	}
	
	/**
	 * Method to get the directories with the specific category name
	 */
	public static ArrayList<Directory> getDirectoryListByCategoryName(Context context, String strCategory) {
		
		ArrayList<Directory> directoryList = new ArrayList<Directory>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(Directories.CONTENT_URI, null, Directories.CATEGORY + "='" + strCategory + "'", null, Directories.COMPANY + " ASC");
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Directory directoryItem = new Directory();
				directoryItem.setCompany(cursor.getString(cursor.getColumnIndex(Directories.COMPANY)));
				directoryItem.setFirstname(cursor.getString(cursor.getColumnIndex(Directories.FIRSTNAME)));
				directoryItem.setLastname(cursor.getString(cursor.getColumnIndex(Directories.LASTNAME)));
				directoryItem.setCategory(cursor.getString(cursor.getColumnIndex(Directories.CATEGORY)));
				directoryItem.setEmail(cursor.getString(cursor.getColumnIndex(Directories.EMAIL)));
				directoryItem.setPhone(cursor.getString(cursor.getColumnIndex(Directories.PHONE)));
				directoryItem.setDescription(cursor.getString(cursor.getColumnIndex(Directories.DESCRIPTION)));
				directoryItem.setImage(cursor.getString(cursor.getColumnIndex(Directories.IMAGE)));
				directoryList.add(directoryItem);
			} while (cursor.moveToNext());
			cursor.close();
		}

		return directoryList;
	}

}


