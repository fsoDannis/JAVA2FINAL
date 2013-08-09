

/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Handler;

/**
 * Class to implement the HTTP Thread mechanism for remote image view
 */
public class HTTPThread extends Thread {
	public static final int STATUS_PENDING = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_FINISHED = 2;

	private boolean mError = false;
	private Exception mException = null;
	private String mUrl;
	private String mLocal;
	private int mStatus = STATUS_PENDING;
	private SoftReference<Handler> mHandler;

	public HTTPThread(String url, String local, Handler handler) {
		mUrl = url;
		mLocal = local;
		mHandler = new SoftReference<Handler>(handler);
	}

	@Override
	public void start() {
		if (getStatus() == STATUS_PENDING) {
			synchronized (this) {
				mStatus = STATUS_RUNNING;
			}
			super.start();
		}
	}

	public void run() {		
		try {
			HttpGet httpRequest = new HttpGet(new URL(mUrl).toURI());
			HttpClient httpClient = new DefaultHttpClient();
			SSLSocketFactory sf = (SSLSocketFactory) httpClient.getConnectionManager().getSchemeRegistry().getScheme("https").getSocketFactory();
			sf.setHostnameVerifier(new AllowAllHostnameVerifier());
			HttpResponse response = (HttpResponse) httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
            byte[] bytes = EntityUtils.toByteArray(entity);
		    FileOutputStream fos = new FileOutputStream(mLocal);
		    fos.write(bytes);
		    fos.flush();
		    fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		synchronized (this) {
			mStatus = STATUS_FINISHED;
		}
		Handler handler = getHandler();
		if (handler != null) {
			handler.sendEmptyMessage(STATUS_FINISHED);
		}
	}

	public int getStatus() {
		synchronized (this) {
			return mStatus;
		}
	}

	public boolean hasError() {
		return mError;
	}

	public Exception getException() {
		return mException;
	}

	public void setHandler(Handler handler) {
		mHandler = new SoftReference<Handler>(handler);
	}

	public Handler getHandler() {
		if (mHandler != null) {
			return mHandler.get();
		}
		return null;
	}

	@Override
	public long getId() {
		return mUrl.hashCode();
	}
}

