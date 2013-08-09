/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder;

import java.util.*;

import com.dinkydetails.consultantfinder.adapter.*;
import com.dinkydetails.consultantfinder.common.*;
import com.dinkydetails.consultantfinder.communication.*;
import com.dinkydetails.consultantfinder.model.*;

import android.os.*;
import android.app.*;
import android.view.*;
import android.widget.*;


public class MainActivity extends Activity {
	
	/** Variables */
	private Spinner spinnerCategory = null;
	private ArrayAdapter<String> spinnerArrayAdapter = null;
	private ArrayList<String> spinnerArray = null;
	private ListView listViewDirectories = null;
	private ArrayList<Directory> items = null;
	private DirectoryListAdapter adapter = null;
	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
		spinnerArray = new ArrayList<String>();
		spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, spinnerArray);
		spinnerCategory.setAdapter(spinnerArrayAdapter);
		spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onSelectCategory(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		listViewDirectories = (ListView) findViewById(R.id.listViewDirectories);
		items = new ArrayList<Directory>();
		adapter = new DirectoryListAdapter(this, R.layout.listview_row_directory, items);
		listViewDirectories.setAdapter(adapter);
		
		init();
		
	}
	
	/**
	 * Method to download & show directories from the remote JSON when the app launches
	 */
	private void init() {
		progressDialog = ProgressDialog.show(this, "", "Please wait ...");
		progressDialog.setCancelable(true);
		
		new Thread() {
			public void run() {
				DownloadDirectoryList service = new DownloadDirectoryList(getApplicationContext());
				final boolean bStatus = service.request();
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
							reload();
						}
					}
				});
			}
		}.start();
	}
	
	/**
	 * Method to reload the categories and directories from local database
	 */
	private void reload() {
		String[] categories = Utilities.getCategories(this);
		if (categories != null && categories.length > 0) {
			spinnerArray.clear();
			for (String category : categories) {
				spinnerArray.add(category);
			}
			spinnerArrayAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * Method to show the directories for it when user select any item of categories
	 */
	public void onSelectCategory(int position) {
		String strSelectedCategoryName = spinnerArray.get(position);
		
		ArrayList<Directory> newItems = Utilities.getDirectoryListByCategoryName(this, strSelectedCategoryName);
		
		items.clear();
		
		for (Directory directory : newItems) {
			items.add(directory);
		}
		
		adapter.notifyDataSetChanged();
		
	}

}
