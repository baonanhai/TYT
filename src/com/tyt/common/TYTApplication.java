package com.tyt.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;

public class TYTApplication extends Application {
	private ExecutorService mThreadPool;
	
	public TYTApplication() {
		mThreadPool = Executors.newFixedThreadPool(3);
	}
	
	public void doInThread(Runnable task) {
		mThreadPool.submit(task);
	}
}
