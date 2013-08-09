
/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.adapter;

import java.util.*;

import com.dinkydetails.consultantfinder.*;
import com.dinkydetails.consultantfinder.view.*;
import com.dinkydetails.consultantfinder.model.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class DirectoryListAdapter extends ArrayAdapter<Directory> {
	
	/** Variables */
	private Context context = null;

	private ArrayList<Directory> items = null;

	private Directory item = null;

	private RemoteImageView remoteImageView = null;

	private TextView textViewCompany = null;

	private TextView textViewName = null;

	private TextView textViewPhone = null;
	
	private TextView textViewEmail = null;
	
	/**
	 * Constructor to show the list items for directories
	 */
	public DirectoryListAdapter(Context context, int textViewResourceId, ArrayList<Directory> items) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.items = items;
	}
	
	/**
	 * Override Method to show one list item in directories
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		item = items.get(position);
				
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = li.inflate(R.layout.listview_row_directory, null);
		
		remoteImageView = (RemoteImageView) view.findViewById(R.id.remoteImageView);
		remoteImageView.setRemoteURI(item.getImage());
		remoteImageView.loadImage();
		
		textViewCompany = (TextView) view.findViewById(R.id.textViewCompany);
		textViewCompany.setText(item.getCompany());
		
		textViewName = (TextView) view.findViewById(R.id.textViewName);
		textViewName.setText(item.getFirstname() + "," + item.getLastname());
		
		textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
		textViewEmail.setText(item.getEmail());
		
		textViewPhone = (TextView) view.findViewById(R.id.textViewPhone);
		textViewPhone.setText(item.getPhone());
		
		return view;
	}
	
	/**
	 * Method to get the directories for custom directory list adapter
	 */
	public ArrayList<Directory> getItems() {
		return items;
	}
	
	/**
	 * Method to set the directories for custom directory list adapter
	 */
	public void setItems(ArrayList<Directory> items) {
		this.items = items;
	}

}
