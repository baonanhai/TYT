package com.tyt.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tyt.data.PersonInfo;

import android.app.Application;

public class TYTApplication extends Application {
	private ExecutorService mThreadPool;
	private PersonInfo mPersonInfo;
	
	public TYTApplication() {
		mThreadPool = Executors.newFixedThreadPool(3);
	}
	
	public void doInThread(Runnable task) {
		mThreadPool.submit(task);
	}

	public PersonInfo getPersonInfo() {
		return mPersonInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		mPersonInfo = personInfo;
	}
}
