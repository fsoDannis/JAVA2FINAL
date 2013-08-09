
/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.model;

import com.dinkydetails.consultantfinder.database.ConsultantFinderContentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class to implement the model for directory (GETTERS and SETTERS) */
public class Directory {
	
	/** Variables*/
	private String company = null;
	private String firstname = null;
	private String lastname = null;
	private String category = null;
	private String email = null;
	private String phone = null;
	private String description = null;
	private String image = null;
	
	public Directory() {
		 this.company = null;
		 this.firstname = null;
		 this.lastname = null;
		 this.category = null;
		 this.email = null;
		 this.phone = null;
		 this.description = null;
		 this.image = null;
	}

	public Directory(String company, String firstname, String lastname,
			String category, String email, String phone, String description,
			String image) {
		this.company = company;
		this.firstname = firstname;
		this.lastname = lastname;
		this.category = category;
		this.email = email;
		this.phone = phone;
		this.description = description;
		this.image = image;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	/**
	 * Class to implement the Directory Table Columns for the custom content provider
	 *
	 */
	public static final class Directories implements BaseColumns {
		public Directories() {
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + ConsultantFinderContentProvider.AUTHORITY + "/directories");

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cf.directories";

		public static final String COMPANY = "company";

		public static final String FIRSTNAME = "firstname";

		public static final String LASTNAME = "lastname";

		public static final String CATEGORY = "category";

		public static final String EMAIL = "email";

		public static final String PHONE = "phone";

		public static final String DESCRIPTION = "description";

		public static final String IMAGE = "image";
		
	}

}

