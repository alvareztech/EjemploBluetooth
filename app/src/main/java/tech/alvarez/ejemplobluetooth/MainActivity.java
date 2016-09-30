package tech.alvarez.ejemplobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mensajeEditText;
    private TextView mensajesTextView;

    private BluetoothAdapter bluetoothAdapter;

    private ConectarTask conectarTask;
    private AceptarConexionTask aceptarConexionTask;
    private ConectadosTask conectadosTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mensajeEditText = (EditText) findViewById(R.id.mensajeEditText);
        mensajesTextView = (TextView) findViewById(R.id.mensajesTextView);

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

        aceptarConexionTask = new AceptarConexionTask();
        AsyncTaskCompat.executeParallel(aceptarConexionTask);

    }

    public void seleccionarDispositivo(View view) {
        Intent intent = new Intent(this, DispositivosActivity.class);
        startActivityForResult(intent, 321);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 321 && resultCode == RESULT_OK) {
            String direccion = data.getExtras().getString("direccion");

            Toast.makeText(this, direccion, Toast.LENGTH_SHORT).show();

            conectarDispositivo(direccion);
        }
    }

    private void conectarDispositivo(String direccion) {

        BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(direccion);

        conectarTask = new ConectarTask(dispositivo);
        AsyncTaskCompat.executeParallel(conectarTask);
    }

    public void enviarMensaje(View view) {

        String mensaje = mensajeEditText.getText().toString();

        conectadosTask.enviar(mensaje);

    }

    public class AceptarConexionTask extends AsyncTask<Void, String, BluetoothSocket> {

        private final BluetoothServerSocket serverSocket;

        public AceptarConexionTask() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Servidor",
                        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
            } catch (IOException e) {
            }
            serverSocket = tmp;
        }

        @Override
        protected BluetoothSocket doInBackground(Void... params) {
            BluetoothSocket socket = null;
            while (true) {
                Log.i("MIAPP", ".");
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    return socket;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            super.onPostExecute(bluetoothSocket);
            Log.i("MIAPP", "AceptarConexionTask onPostExecute");

            if (bluetoothSocket != null) {
                conectadosTask = new ConectadosTask(bluetoothSocket);
                AsyncTaskCompat.executeParallel(conectadosTask);
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConectarTask extends AsyncTask<Void, String, Boolean> {

        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConectarTask(BluetoothDevice bluetoothDevice) {
            BluetoothSocket tmp = null;
            this.bluetoothDevice = bluetoothDevice;
            try {
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(
                        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
            } catch (IOException e) {
            }
            bluetoothSocket = tmp;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            bluetoothAdapter.cancelDiscovery();
            try {
                bluetoothSocket.connect();
            } catch (IOException connectException) {
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                conectadosTask = new ConectadosTask(bluetoothSocket);
                AsyncTaskCompat.executeParallel(conectadosTask);
            }
        }
    }


    public class ConectadosTask extends AsyncTask<Void, String, Void> {

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConectadosTask(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = bluetoothSocket.getInputStream();
                tmpOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
            }
            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        @Override
        protected Void doInBackground(Void... params) {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String mensaje = new String(buffer).substring(0, bytes);
                    publishProgress(mensaje);
                } catch (IOException e) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String mensaje = values[0];
            // mensaje listo para mostrar

            mensajesTextView.append(mensaje + "\n");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // mensaje listo para enviar
        public void enviar(String mensaje) {
            try {
                outputStream.write(mensaje.getBytes());
            } catch (IOException e) {
            }
        }
    }

}
