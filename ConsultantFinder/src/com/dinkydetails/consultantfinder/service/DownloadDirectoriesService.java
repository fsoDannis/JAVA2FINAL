/** Dan Annis */

package com.dinkydetails.consultantfinder.service;

import com.dinkydetails.consultantfinder.*;
import com.dinkydetails.consultantfinder.communication.*;

import android.app.*;
import android.content.*;
import android.os.*;

/**
 * Class to implement the custom service to download the json to the directories
 */
public class DownloadDirectoriesService extends Service {

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION_ID = 999;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		// Before download the directories, clear the notification bar
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
		
		onDownload();
	}

	/**
	 * Method to download the directories from the remote json
	 */
	private void onDownload() {
		DownloadDirectoryList service = new DownloadDirectoryList(this);
		boolean bStatus = service.request();
		showNotification(bStatus);
	}
	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification(boolean bStatus) {
		String title = "";
		String message = "";
		if (bStatus) {
			title = "Directories downloaded successfully !";
		} else {
			title = "Failed to download directories !";
		}
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, title, System.currentTimeMillis());
		
		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		notification.setLatestEventInfo(this, title, message, intent);
		nm.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}