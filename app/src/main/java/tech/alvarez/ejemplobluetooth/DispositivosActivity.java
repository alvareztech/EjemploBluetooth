package tech.alvarez.ejemplobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class DispositivosActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bm.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.i("MIAPP", device.getName());
                Log.i("MIAPP", device.getAddress());

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.i("MIAPP", "Se terminó de buscar");

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }
}
