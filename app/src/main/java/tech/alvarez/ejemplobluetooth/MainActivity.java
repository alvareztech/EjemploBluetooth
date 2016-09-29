package tech.alvarez.ejemplobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bm.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 123);
        }


    }

    public void seleccionarDispositivo(View view) {
        Intent intent = new Intent(this, DispositivosActivity.class);
        startActivity(intent);
    }
}
