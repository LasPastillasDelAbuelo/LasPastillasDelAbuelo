package unlam.com.lpda;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class SensorsActivity extends Activity implements SensorEventListener {

    //Seteo precisión del sensor para Shake
    private final static float ACC = 15;

    //Declaro los elementos en pantalla
    private Button btnVolver;
    private SensorManager mSensorManager;
    private TextView light;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket = null;

    // SPP UUID service  - Funciona en la mayoria de los dispositivos
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // String para la MAC address del Hc06
    private static String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Linkeo el controlador con la activity correspondiente
        setContentView(R.layout.activity_sensores);

        //Linkeo los botones con los elementos de la pantalla correspondientes
        btnVolver = (Button) findViewById(R.id.btnVolver);
        light = (TextView) findViewById(R.id.txtLuz);

        //Pido servicio de sensores
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Seteo listeners a los botones
        btnVolver.setOnClickListener(botonesListeners);

        //obtengo el adaptador del bluethoot
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //registro los sensores
        registrarSensores();

        //Tomo la direccion del HC06 pasada desde la activity anterior
        Intent intent=getIntent();
        Bundle extras=intent.getExtras();
        address= extras.getString("Direccion_Bluethoot");

        //Conecto el HC06 por BT
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        //se realiza la conexion del Bluethoot a traves de un socket
        try
        {
            btSocket = createBluetoothSocket(device);
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(),"La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        //Se establece la conexión con el socket
        try
        {
            btSocket.connect();
            Toast.makeText(getApplicationContext(),"CONECTADO!", Toast.LENGTH_LONG).show();

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

    //Metodo que crea el socket bluethoot
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //Metodo para enviar mensajes vía BT
    public void write(String input) {
        OutputStream mmOutStream = null;
        try
        {
            //Creo I/O streams para la conexión
            mmOutStream = btSocket.getOutputStream();
        } catch (IOException e) { }
        byte[] msgBuffer = input.getBytes();           //Convierto el string a bytes
        try {
            mmOutStream.write(msgBuffer);                //Escribo bytes al BT vía Outstream
        } catch (IOException e) {
            //Si no se pudo escribir, envío un mensaje
            Toast.makeText(getApplicationContext(),"La conexion fallo", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent intent;
            //Se determina que componente genero un evento
            switch (v.getId())
            {
                case R.id.btnVolver:
                    intent=new Intent(SensorsActivity.this, BTMenuActivity.class);
                    //Vuelvo a enviar la dirección de BT a la activity anterior
                    intent.putExtra("Direccion_Bluethoot", address);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Cada sensor puede lanzar un thread que pase por aqui
        // Para asegurarnos ante los accesos simultaneos sincronizamos esto

        synchronized (this)
        {
            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER :
                    if ((Math.abs(event.values[0]) > ACC || Math.abs(event.values[1]) > ACC || Math.abs(event.values[2]) > ACC))
                    {
                        //Si se realiza un shake, le envío 'a' al pastillero
                        write("a");
                    }
                    break;
                case Sensor.TYPE_PROXIMITY :

                    // Si detecta 0, le envío 'b' al pastillero
                    if( event.values[0] == 0 )
                    {
                        write("b");
                    }
                    break;
                case Sensor.TYPE_LIGHT :
                    //Muestro el valor de la luz por pantalla, si es menor o igual a 5, envío 'c' al
                    //pastillero
                    light.setText(String.valueOf(event.values[0]));
                    if( event.values[0] <= 5 )
                    {
                        write("c");
                    }
                    break;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //METODOS DEL CICLO DE VIDA
    @Override
    protected void onStop()
    {
        pararSensores();
        super.onStop();
    }

    @Override
    protected void onPause()
    {
        pararSensores();
        super.onPause();
        try
        {
            //Cierro el socket BT
            btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(getApplicationContext(),"No se pudo cerrar el Socket", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void registrarSensores()
    {
        //Registro los listeners para los sensores
        boolean accelerometerConnected, proximityConnected, lightConnected;
        accelerometerConnected = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),   SensorManager.SENSOR_DELAY_NORMAL);
        proximityConnected = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),   SensorManager.SENSOR_DELAY_NORMAL);
        lightConnected = mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),   SensorManager.SENSOR_DELAY_NORMAL);

        if (!accelerometerConnected)
        {
            Toast.makeText(this, "Acelerometro no soportado", Toast.LENGTH_SHORT).show();
        }
        if (!proximityConnected)
        {
            Toast.makeText(this, "Proximidad no soportados", Toast.LENGTH_SHORT).show();
        }
        if (!lightConnected)
        {
            Toast.makeText(this, "Luz no soportados", Toast.LENGTH_SHORT).show();
        }
    }

    //Elimino el listener de cada sensor
    private void pararSensores()
    {
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
    }
}
