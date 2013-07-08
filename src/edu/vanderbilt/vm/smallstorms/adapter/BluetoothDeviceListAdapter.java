package edu.vanderbilt.vm.smallstorms.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Date: 6/15/13
 * Time: 2:17 AM
 */
public class BluetoothDeviceListAdapter extends BaseAdapter {

public BluetoothDeviceListAdapter(Context ctx, List<BluetoothDevice> list) {
    mCtx = ctx;
    mBtList = list;
}

private Context mCtx;
private List<BluetoothDevice> mBtList;
private static final int PADDING = 16;

@Override
public int getCount() {
    return mBtList.size();
}

@Override
public Object getItem(int i) {
    return mBtList.get(i);
}

@Override
public long getItemId(int i) {
    return 0;
}

@Override
public View getView(int i, View view, ViewGroup viewGroup) {

    TextView tv;
    if (view == null) {
        tv = new TextView(mCtx);
        tv.setPadding(PADDING, PADDING, PADDING, PADDING);
        tv.setTextSize(20);

    } else {
        tv = (TextView) view;
    }

    BluetoothDevice d = mBtList.get(i);
    tv.setText(d.getName() + " - " + d.getAddress());

    return tv;
}
}
