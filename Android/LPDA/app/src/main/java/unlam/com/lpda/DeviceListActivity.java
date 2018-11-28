package unlam.com.lpda;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

/*********************************************************************************************************
 * Activity que muestra el listado de los dispositivos bluethoot encontrados
 **********************************************************************************************************/

public class DeviceListActivity extends Activity
{
    private TextView txtEstado;
    private ListView mListView;
    private DeviceListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private int posicionListBluethoot;
    private BluetoothAdapter mBluetoothAdapter;
    private ProgressDialog mProgressDlg;
    private Button btnVolver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Se asocia vista con controlador
        setContentView(R.layout.activity_paired_devices);

        //Asocio los elementos graficos
        btnVolver = (Button) findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(botonesListeners);
        txtEstado = (TextView) findViewById(R.id.txtEstado);
        mListView = (ListView) findViewById(R.id.lv_paired);

        //Se crea un adaptador para podermanejar el bluethoot del celular
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Se Crea la ventana de dialogo que indica que se esta buscando dispositivos bluethoot
        mProgressDlg = new ProgressDialog(this);

        mProgressDlg.setMessage("Buscando dispositivos...");
        mProgressDlg.setCancelable(false);

        //se asocia un listener al boton cancelar para la ventana de dialogo ue busca los dispositivos bluethoot
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", btnCancelarDialogListener);

        //se determina si existe bluethoot en el celular
        if (mBluetoothAdapter == null)
        {
            //si el celular no soporta bluethoot
            txtEstado.setText("Bluetooth no es soportado por el dispositivo movil");
        }
        else
        {
            //se determina si esta activado el bluethoot
            if (mBluetoothAdapter.isEnabled())
            {
                //se informa si esta habilitado
                txtEstado.setText("Bluetooth Habilitado");
                txtEstado.setTextColor(Color.BLUE);
            }
            else
            {
                //se informa si esta deshabilitado
                txtEstado.setText("Bluetooth Deshabilitado");
                txtEstado.setTextColor(Color.RED);
            }
        }

        //se definen un broadcastReceiver que captura el broadcast del SO cuando captura los siguientes eventos:
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //Cambia el estado del Bluethoot (Acrtivado /Desactivado)
        filter.addAction(BluetoothDevice.ACTION_FOUND); //Se encuentra un dispositivo bluethoot al realizar una busqueda
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //Cuando se comienza una busqueda de bluethoot
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //cuando la busqueda de bluethoot finaliza

        //se define (registra) el handler que captura los broadcast anterirmente mencionados.
        registerReceiver(mReceiver, filter);

    }

    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            Intent intent;

            //Se determina que componente genero un evento
            switch (v.getId())
            {
                case R.id.btnVolver:
                    //se genera un Intent para poder lanzar la activity principal
                    intent=new Intent(DeviceListActivity.this, MainActivity.class);
                    //se inicia la activity principal
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones", Toast.LENGTH_LONG).show();
            }


        }
    };

    //Si se cancela la búsqueda del BT, hago lo siguiente:
    private DialogInterface.OnClickListener btnCancelarDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mBluetoothAdapter.cancelDiscovery();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(!mBluetoothAdapter.isDiscovering()){
            if (pairedDevices == null || pairedDevices.size() == 0)
            {
                showToast("No se encontraron dispositivos emparejados");
            }
            else
            {
                ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

                list.addAll(pairedDevices);
                mDeviceList.addAll(pairedDevices);
                //defino un adaptador para el ListView donde se van mostrar en la activity los dispositovs encontrados
                mAdapter = new DeviceListAdapter(this);

                //asocio el listado de los dispositovos pasado en el bundle al adaptador del Listview
                mAdapter.setData(list);

                //defino un listener en el boton emparejar del listview
                mAdapter.setListener(listenerBotonEmparejar);
                mListView.setAdapter(mAdapter);

            }
        }

    }

    @Override
    //Cuando se llama al metodo OnPause se cancela la busqueda de dispositivos bluethoot
    public void onPause()
    {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mPairReceiver);
        super.onDestroy();
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    //Metodo que actua como Listener de los eventos que ocurren en los componentes graficos de la activty
    private DeviceListAdapter.OnPairButtonClickListener listenerBotonEmparejar = new DeviceListAdapter.OnPairButtonClickListener() {
        @Override
        public void onPairButtonClick(int position) {
            //Metodo para CONECTAR

            //Obtengo los datos del dispostivo seleccionado del listview por el usuario
            BluetoothDevice device = mDeviceList.get(position);

            //Se checkea si el sipositivo ya esta emparejado
            if (device.getBondState() == BluetoothDevice.BOND_BONDED)
            {
                String direccionBluethoot = device.getAddress();
                Intent i = new Intent(DeviceListActivity.this, BTMenuActivity.class);
                i.putExtra("Direccion_Bluethoot", direccionBluethoot);

                startActivity(i);

            }
        }
    };

    //Handler que captura los brodacast que emite el SO al ocurrir los eventos del bluethoot
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //Atraves del Intent obtengo el evento de Bluethoot que informo el broadcast del SO
            String action = intent.getAction();

            //si el SO detecto un emparejamiento o desemparjamiento de bulethoot
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                //Obtengo los parametro, aplicando un Bundle, que me indica el estado del Bluethoot
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                //se analiza si se puedo emparejar o no
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING)
                {
                    //Si se detecto que se puedo emparejar el bluethoot
                    showToast("Emparejado");
                    BluetoothDevice dispositivo = (BluetoothDevice) mAdapter.getItem(posicionListBluethoot);

                    //se inicia el Activity de comunicacion con el bluethoot, para transferir los datos.
                    //Para eso se le envia como parametro la direccion(MAC) del bluethoot Arduino
                    String direccionBluethoot = dispositivo.getAddress();
                    Intent i = new Intent(DeviceListActivity.this, BTMenuActivity.class);
                    i.putExtra("Direccion_Bluethoot", direccionBluethoot);

                    startActivity(i);

                }  //si se detrecto un desaemparejamiento
                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    showToast("No emparejado");
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };

    //Handler que captura los brodacast que emite el SO al ocurrir los eventos del bluethoot
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            //A traves del Intent obtengo el evento de Bluethoot que informo el broadcast del SO
            String action = intent.getAction();

            //Si cambio de estado el Bluethoot(Activado/desactivado)
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                //Obtengo el parametro, aplicando un Bundle, que me indica el estado del Bluethoot
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                //Si esta activado
                if (state == BluetoothAdapter.STATE_ON)
                {
                    showToast("Activar");
                    txtEstado.setText("Bluetooth Habilitar");
                    txtEstado.setTextColor(Color.BLUE);
                }
            }
            //Si se inicio la busqueda de dispositivos bluethoot
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
                //Creo la lista donde voy a mostrar los dispositivos encontrados
                //mDeviceList = new ArrayList<BluetoothDevice>();

                //muestro el cuadro de dialogo de busqueda
                mProgressDlg.show();
            }
            //Si finalizo la busqueda de dispositivos bluethoot
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                //se cierra el cuadro de dialogo de busqueda
                mProgressDlg.dismiss();

            }
            //si se encontro un dispositivo bluethoot
            else if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                //Se lo agregan sus datos a una lista de dispositivos encontrados
//                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                mDeviceList.add(device);
//                showToast("Dispositivo Encontrado:" + device.getName());
            }
        }
    };
}
