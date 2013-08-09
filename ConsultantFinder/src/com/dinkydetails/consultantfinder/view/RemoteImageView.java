
/** Dan Annis
 *  Java 2 August 2013
 */

package com.dinkydetails.consultantfinder.view;

import java.io.*;
import java.lang.ref.*;
import java.util.*;


import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import com.dinkydetails.consultantfinder.*;

/**
 * Class to implement the custom image view widget for remote image view
 * Handling the image again
 */
public class RemoteImageView extends ImageView {
	private static final String TAG = RemoteImageView.class.getSimpleName();
	private String mLocal;
	private String mRemote;
	private HTTPThread mThread = null;
	private static WeakHashMap<String, WeakReference<Drawable>> imageCache = new WeakHashMap<String, WeakReference<Drawable>>();
	
	public static void clearCache() {
		Set<String> keySet = imageCache.keySet();
		for (Object obj : keySet.toArray()) {
			WeakReference<Drawable> drawableRef = imageCache.get(obj);
			if (!drawableRef.isEnqueued()) {
				drawableRef.enqueue();
			}
		}
		imageCache.clear();
		
		System.gc();
	}
	
	public RemoteImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDrawingCacheEnabled(false);
	}

	public void setLocalURI(String local) {
		mLocal = local;
	}

	public void setRemoteURI(String uri) {
		if (uri.startsWith("http")) {
			mRemote = uri;
		}
	}

	public void loadImage() {
		if (mRemote != null) {
			if (mLocal == null) {
				mLocal = Environment.getExternalStorageDirectory() + "/.remote-image-view-cache/" + mRemote.hashCode() + ".jpg";
			}
			// check for the local file here instead of in the thread because
			// otherwise previously-cached files wouldn't be loaded until after
			// the remote ones have been downloaded.
			File local = new File(mLocal);
			if (local.exists()) {
				setFromLocal();
			} else {
				// we already have the local reference, so just make the parent
				// directories here instead of in the thread.
				local.getParentFile().mkdirs();
				queue();
			}
		}
	}

	@Override
	public void finalize() {
		if (mThread != null) {
			HTTPQueue queue = HTTPQueue.getInstance();
			queue.dequeue(mThread);
		}
	}

	private void queue() {
		if (mThread == null) {
			mThread = new HTTPThread(mRemote, mLocal, mHandler);
			HTTPQueue queue = HTTPQueue.getInstance();
			queue.enqueue(mThread, HTTPQueue.PRIORITY_HIGH);
		}
		setImageResource(R.drawable.empty_thumb);
	}

	private void setFromLocal() {
		mThread = null;
		Drawable d = null;
		WeakReference<Drawable> wr = null;
		if (imageCache.containsKey(mLocal)) {
			wr = imageCache.get(mLocal);
			d = wr.get();
		}
		
		if (d == null) {
			try {
				d = Drawable.createFromPath(mLocal);
				imageCache.put(mLocal, new WeakReference<Drawable>(d));
			} catch (OutOfMemoryError error) {
				Log.e(TAG, "OutOfMemoryError[" + mLocal + "]: " + error.getMessage());
				System.gc();
			}
		}
		if (d != null) {
			setImageDrawable(d);
		}
		else {
			setImageResource(R.drawable.empty_thumb);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setFromLocal();
		}
	};
}
