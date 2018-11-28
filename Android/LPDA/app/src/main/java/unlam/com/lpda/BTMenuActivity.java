package unlam.com.lpda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BTMenuActivity extends Activity {

    private Button btnSensores, btnSincro, btnVolver;
    private static String address = null;
    private ProgressDialog mProgressDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;
    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Linkeo el controlador con la activity correspondiente
        setContentView(R.layout.activity_btmenu);

        //Linkeo los botones con los elementos de la pantalla correspondientes
        btnSensores = (Button) findViewById(R.id.btnSensores);
        btnSincro = (Button) findViewById(R.id.btnSincro);
        btnVolver = (Button) findViewById(R.id.btnVolver);

        //Seteo listeners a los botones
        btnSensores.setOnClickListener(botonesListeners);
        btnSincro.setOnClickListener(botonesListeners);
        btnVolver.setOnClickListener(botonesListeners);

        //obtengo el adaptador del bluethoot
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        address = extras.getString("Direccion_Bluethoot");
    }


    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            Intent intent;

            switch (v.getId())
            {
                case R.id.btnSensores:

                    intent = new Intent(BTMenuActivity.this, SensorsActivity.class);
                    intent.putExtra("Direccion_Bluethoot", address);
                    startActivity(intent);
                    break;

                case R.id.btnSincro:

                    //Se lee la planificacion y se envía por BT
                    leerPlanificacion();

                    break;
                case R.id.btnVolver:
                    intent=new Intent(BTMenuActivity.this, DeviceListActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones", Toast.LENGTH_LONG).show();
            }


        }
    };


    private void leerPlanificacion() {
        btConect();
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                    File(getFilesDir()+ File.separator+"data.txt")));
            String receiveString;
            ArrayList<String> lineas = new ArrayList<String> ();

            while ( (receiveString = bufferedReader.readLine()) != null )
            {
                lineas.add(receiveString);
            }

            bufferedReader.close();
            String planif;
            write("x");
            for (String linea: lineas)
            {
                //Reemplazo los ; por nada
                linea = linea.replace(";", "");
                //Envío un mensaje de la forma 'p+tolva+dia+horas+minutos+\n'
                planif = 'p'+linea+'\n';
                write(planif);
            }
            //Seteo el popup de alerta
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Sincro OK")
                    .setMessage("La sincronización fue exitosa")
                    .setPositiveButton("OK", null)
                    .show();
        }
        catch (FileNotFoundException e)
        {
            Log.e("Sincro activity", "File not found: " + e.toString());
        }
        catch (IOException e)
        {
            Log.e("Sincro activity", "Can not read file: " + e.toString());
        }
        try
        {
            btSocket.close();
        }
        catch (IOException e2)
        {
            Toast.makeText(getApplicationContext(),"No se pudo cerrar el Socket", Toast.LENGTH_LONG).show();
        }

    }

    //Metodo que crea el socket bluethoot
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void write(String input) {
        OutputStream mmOutStream = null;
        try
        {
            mmOutStream = btSocket.getOutputStream();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"No se pudo obtener el OutStream", Toast.LENGTH_SHORT).show();
        }
        byte[] msgBuffer = input.getBytes();
        try
        {
            mmOutStream.write(msgBuffer);
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"La conexion fallo", Toast.LENGTH_SHORT).show();
        }
    }

    private void btConect(){
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }

        try
        {
            btSocket.connect();
        }
        catch (IOException e)
        {
            Log.e("err", e.toString());
            try
            {
                btSocket.close();
            }
            catch (IOException e2)
            {
                Toast.makeText(getApplicationContext(),"No se pudo conectar con BT", Toast.LENGTH_LONG).show();
            }
        }
    }

}
