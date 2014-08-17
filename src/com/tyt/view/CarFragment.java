package com.tyt.view;

import com.dxj.tyt.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CarFragment extends Fragment {
	private boolean mIsSearch = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View searchCarLayout = inflater.inflate(R.layout.search_content, container, false);
		ViewGroup search_header = (ViewGroup)searchCarLayout.findViewById(R.id.search_header);
		if (!mIsSearch) {
			search_header.addView(inflater.inflate(R.layout.search_start_header, search_header, false));
		} else {
			search_header.addView(inflater.inflate(R.layout.search_end_header, search_header, false));
		}
		return searchCarLayout;
	}

}
