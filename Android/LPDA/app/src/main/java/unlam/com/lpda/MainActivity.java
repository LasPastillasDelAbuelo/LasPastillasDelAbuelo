package unlam.com.lpda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Declaro los elementos en pantalla
    private Button btnPlanificar,btnBT;
    private TextView txtPlanificacionAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Linkeo el controlador con la activity correspondiente
        setContentView(R.layout.activity_main);

        //Linkeo los botones con los elementos de la pantalla correspondientes
        btnPlanificar = (Button) findViewById(R.id.btnPlanificar);
        btnBT = (Button) findViewById(R.id.btnBT);
        txtPlanificacionAct = (TextView) findViewById(R.id.txtPlanificacionAct);

        //Seteo listeners a los botones
        btnPlanificar.setOnClickListener(botonesListeners);
        btnBT.setOnClickListener(botonesListeners);

        //Leo y muestro la planificación actual
        setPlanificacionActual();

    }


    //Metodo que actua como Listener de los eventos que ocurren en los botones
    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {

        public void onClick(View v)
        {
            Intent intent;
            //Se determina que componente genero un evento
            switch (v.getId())
            {
                //Si se ocurrio un evento en el boton planificar
                case R.id.btnPlanificar:

                    //se genera un Intent para poder lanzar la activity de planificacion
                    intent=new Intent(MainActivity.this, PlanificationActivity.class);

                    //se inicia la activity
                    startActivity(intent);
                    break;
                case R.id.btnBT:
                    //se genera un Intent para poder lanzar la activity de lista de dispositivos
                    //vinculados al BT
                    intent=new Intent(MainActivity.this, DeviceListActivity.class);
                    //se inicia la activity
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones", Toast.LENGTH_LONG).show();
            }


        }
    };

    //Cargar la planificación del pastillero
    private void setPlanificacionActual()
    {
        String planif = leerPlanificacion();
        if(!planif.equals(""))
        {
            txtPlanificacionAct.setText(planif);
        }
        else
        {
            txtPlanificacionAct.setText("No existe planificación");
        }

    }

    //Leer el archivo de planificaciones, el mismo está en memoria interna, en el contexto de la app
    private String leerPlanificacion()
    {

        String planificacion = "";

        try {
            //Se declara un buffered reader para leer el archivo 'data.txt'
                BufferedReader bufferedReader = new BufferedReader(new FileReader(new
                    File(getFilesDir()+ File.separator + "data.txt")));
                String receiveString;
                ArrayList<String> lineas = new ArrayList<String> ();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    lineas.add(receiveString);
                }

                bufferedReader.close();
                //el formato de la planificación guardada es tolva;dia;horas;minutos,
                //hay que parsearla para mostrarla
                planificacion = parsearTxtPlanificacion(lineas);
        }
        catch (FileNotFoundException e) {
            Log.e("Main activity", "File not found: " + e.toString());
            return "";
        } catch (IOException e) {
            Log.e("Main activity", "Can not read file: " + e.toString());
            return "";
        }

        return planificacion;
    }

    private String parsearTxtPlanificacion(ArrayList<String> lineas)
    {
        String result = "";
        for (String linea: lineas) {
            //Separo las lineas por ;
            String[] parsedLine = linea.split(";");
            result += "Tolva " + parsedLine[0] + " - " + getDay(parsedLine[1]) + " - " + parsedLine[2] + ":" + parsedLine[3]+"\n";
        }
        return result;
    }

    private String getDay(String num)
    {
        switch (num)
        {
            case "1":
                return "Lunes";
            case "2":
                return "Martes";
            case "3":
                return "Miercoles";
            case "4":
                return "Jueves";
            case "5":
                return "Viernes";
            case "6":
                return "Sabado";
            case "7":
                return "Domingo";
        }
        return "";
    }
}
