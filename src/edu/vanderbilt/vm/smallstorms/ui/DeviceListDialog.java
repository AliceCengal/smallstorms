package edu.vanderbilt.vm.smallstorms.ui;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import edu.vanderbilt.vm.smallstorms.R;
import edu.vanderbilt.vm.smallstorms.adapter.BluetoothDeviceListAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Date: 6/13/13
 * Time: 4:21 PM
 */
public class DeviceListDialog extends DialogFragment {


public interface BtDeviceReceiver {

    void receive(BluetoothDevice device);

}

public DeviceListDialog(BtDeviceReceiver receiver) {
    mReceiver = receiver;
}

private BtDeviceReceiver mReceiver;
private BluetoothAdapter mBtAdapter;
private ListView mView;
private List<BluetoothDevice> mList;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.single_list, container, false);
}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Init Bluetooth machinaries;
    mList = new LinkedList<BluetoothDevice>();
    mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    for (BluetoothDevice d : mBtAdapter.getBondedDevices()) {
        mList.add(d); }

    //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //getActivity().registerReceiver(mBroadcastReceiver, filter);

    getDialog().setTitle("Choose BlueTooth Device");

    // Init Views
    mView = (ListView) getView().findViewById(R.id.sl_listview);
    mView.setAdapter(new BluetoothDeviceListAdapter(getActivity(), mList));
    mView.setOnItemClickListener(mClickListener);

    /*TextView empty = new TextView(getActivity());
    empty.setText("Searching For Device...");
    empty.setPadding(4, 4, 4, 4);
    mView.setEmptyView(empty); */

    mView.invalidateViews();

    //mBtAdapter.startDiscovery();
}

@Override
public void onDestroy() {
    super.onDestroy();
    //disengageBluetooth();
}

private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SmallStorms_DeviceListDialog", "Broadcast received");
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mList.add(device);
            mView.invalidateViews();

        }

    }
};

private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //disengageBluetooth();
        mReceiver.receive(mList.get(i));
        dismiss();
    }
};

private DeviceListDialog disengageBluetooth() {
    if (mBtAdapter != null) {
        mBtAdapter.cancelDiscovery(); }

    getActivity().unregisterReceiver(mBroadcastReceiver);
    return this;
}

}
