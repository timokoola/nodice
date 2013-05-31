package com.moarub.diceness;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class DiceAdapter extends BaseAdapter {
	private Context fContext;

	public DiceAdapter(Context context) {
		super();
		fContext = context;
	}

	@Override
	public int getCount() {
		return 30;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 DieView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new DieView(fContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (DieView) convertView;
	        }

	        return imageView;
	}

}
