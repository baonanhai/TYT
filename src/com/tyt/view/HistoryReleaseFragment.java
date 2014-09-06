package com.tyt.view;

import com.dxj.tyt.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryReleaseFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View historyReleaseLayout = inflater.inflate(R.layout.history_release, container, false);
		return historyReleaseLayout;
	}
}
