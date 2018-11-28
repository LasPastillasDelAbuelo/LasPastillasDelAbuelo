package unlam.com.lpda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlanificationActivity extends Activity {

    //Items de los Spinners
    private String[] tolvas = new String[]{"Tolva 1", "Tolva 2"};
    private String[] horas = new String[]{"", "00", "01", "02", "03",
            "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
            "15", "16", "17", "18", "19", "20", "21", "22", "23",};
    private String[] minutos = new String[]{"", "00", "05", "10", "15", "20","25", "30", "35", "40", "45", "50", "55"};

    //Declaro los elementos gráficos
    private Button btnVolver, btnGuardar;
    private Spinner tolva, t1LunHs, t1LunMin, t1MarHs, t1MarMin, t1MieHs, t1MieMin, t1JueHs, t1JueMin, t1VieHs, t1VieMin, t1SabHs, t1SabMin,t1DomHs, t1DomMin,
            t2LunHs, t2LunMin, t2MarHs, t2MarMin, t2MieHs, t2MieMin, t2JueHs, t2JueMin, t2VieHs, t2VieMin, t2SabHs, t2SabMin, t2DomHs, t2DomMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asocio la el controlador con la vista
        setContentView(R.layout.activity_add_planification);

        //Asocio los botones con los botones gráficos
        btnVolver = findViewById(R.id.btnVolver);
        btnGuardar = findViewById(R.id.btnGuardarPlanificacion);

        //Seteo los listeners para los botones
        btnGuardar.setOnClickListener(botonesListeners);
        btnVolver.setOnClickListener(botonesListeners);

        //Indico que si se selecciona una tolva, se ejecute el metodo showHideSpinners()
        tolva = findViewById(R.id.spinTolva);
        tolva.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                showHideSpiners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        //Seteo los spinners de tolvas, horas y minutos
        setSpinners();

        //Lo hago en onCreate porque aunque el usuario no pueda interactuar, no lo tiene que ver
        showHideSpiners();
    }



    private View.OnClickListener botonesListeners = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            Intent intent;
            //Se determina que componente genero un evento
            switch (v.getId())
            {
                //Si se ocurrio un evento en el boton Volver
                case R.id.btnVolver:
                    intent=new Intent(PlanificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                //Si se ocurrio un evento en el boton Guardar Planificacion
                case R.id.btnGuardarPlanificacion:
                    //Se guarda la planificacion
                    guardarPlanificacion();
                    //Se mustra un mensaje de planificacion guardada
                    Toast.makeText(getApplicationContext(),"Planificacion guardada", Toast.LENGTH_LONG).show();
                    intent=new Intent(PlanificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(getApplicationContext(),"Error en Listener de botones", Toast.LENGTH_LONG).show();
            }


        }
    };

    private void setSpinners()
    {
        //Hs y Min Tolva 1
        t1LunHs = findViewById(R.id.t1_lun_hs);
        t1LunMin = findViewById(R.id.t1_lun_min);
        t1MarHs = findViewById(R.id.t1_mar_hs);
        t1MarMin = findViewById(R.id.t1_mar_min);
        t1MieHs = findViewById(R.id.t1_mie_hs);
        t1MieMin = findViewById(R.id.t1_mie_min);
        t1JueHs = findViewById(R.id.t1_jue_hs);
        t1JueMin = findViewById(R.id.t1_jue_min);
        t1VieHs = findViewById(R.id.t1_vie_hs);
        t1VieMin = findViewById(R.id.t1_vie_min);
        t1SabHs = findViewById(R.id.t1_sab_hs);
        t1SabMin = findViewById(R.id.t1_sab_min);
        t1DomHs = findViewById(R.id.t1_dom_hs);
        t1DomMin = findViewById(R.id.t1_dom_min);


        //Hs y Min Tolva 2
        t2LunHs = findViewById(R.id.t2_lun_hs);
        t2LunMin = findViewById(R.id.t2_lun_min);
        t2MarHs = findViewById(R.id.t2_mar_hs);
        t2MarMin = findViewById(R.id.t2_mar_min);
        t2MieHs = findViewById(R.id.t2_mie_hs);
        t2MieMin = findViewById(R.id.t2_mie_min);
        t2JueHs = findViewById(R.id.t2_jue_hs);
        t2JueMin = findViewById(R.id.t2_jue_min);
        t2VieHs = findViewById(R.id.t2_vie_hs);
        t2VieMin = findViewById(R.id.t2_vie_min);
        t2SabHs = findViewById(R.id.t2_sab_hs);
        t2SabMin = findViewById(R.id.t2_sab_min);
        t2DomHs = findViewById(R.id.t2_dom_hs);
        t2DomMin = findViewById(R.id.t2_dom_min);


        //Adaptadores de los Spinners
        ArrayAdapter<String> tolvaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tolvas);
        ArrayAdapter<String> horaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, horas);
        ArrayAdapter<String> minutosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutos);

        //Asocio Spinner de Tolva con Adapter
        tolva.setAdapter(tolvaAdapter);

        //Asocio Spinners Tolva 1 con Adapters
        t1LunHs.setAdapter(horaAdapter);
        t1LunMin.setAdapter(minutosAdapter);
        t1MarHs.setAdapter(horaAdapter);
        t1MarMin.setAdapter(minutosAdapter);
        t1MieHs.setAdapter(horaAdapter);
        t1MieMin.setAdapter(minutosAdapter);
        t1JueHs.setAdapter(horaAdapter);
        t1JueMin.setAdapter(minutosAdapter);
        t1VieHs.setAdapter(horaAdapter);
        t1VieMin.setAdapter(minutosAdapter);
        t1SabHs.setAdapter(horaAdapter);
        t1SabMin.setAdapter(minutosAdapter);
        t1DomHs.setAdapter(horaAdapter);
        t1DomMin.setAdapter(minutosAdapter);

        //Asocio Spinners Tolva 2 con Adapters
        t2LunHs.setAdapter(horaAdapter);
        t2LunMin.setAdapter(minutosAdapter);
        t2MarHs.setAdapter(horaAdapter);
        t2MarMin.setAdapter(minutosAdapter);
        t2MieHs.setAdapter(horaAdapter);
        t2MieMin.setAdapter(minutosAdapter);
        t2JueHs.setAdapter(horaAdapter);
        t2JueMin.setAdapter(minutosAdapter);
        t2VieHs.setAdapter(horaAdapter);
        t2VieMin.setAdapter(minutosAdapter);
        t2SabHs.setAdapter(horaAdapter);
        t2SabMin.setAdapter(minutosAdapter);
        t2DomHs.setAdapter(horaAdapter);
        t2DomMin.setAdapter(minutosAdapter);
    }

    //Muestro u oculto dropdown segun qué tolva haya seleccionado
    private void showHideSpiners()
    {
        if(tolva.getSelectedItem() == "Tolva 1")
        {
            //Muestro Spinners de Tolva 1
            t1LunHs.setVisibility(View.VISIBLE);
            t1LunMin.setVisibility(View.VISIBLE);
            t1MarHs.setVisibility(View.VISIBLE);
            t1MarMin.setVisibility(View.VISIBLE);
            t1MieHs.setVisibility(View.VISIBLE);
            t1MieMin.setVisibility(View.VISIBLE);
            t1JueHs.setVisibility(View.VISIBLE);
            t1JueMin.setVisibility(View.VISIBLE);
            t1VieHs.setVisibility(View.VISIBLE);
            t1VieMin.setVisibility(View.VISIBLE);
            t1SabHs.setVisibility(View.VISIBLE);
            t1SabMin.setVisibility(View.VISIBLE);
            t1DomHs.setVisibility(View.VISIBLE);
            t1DomMin.setVisibility(View.VISIBLE);

            //Oculto Spinners de Tova 2
            t2LunHs.setVisibility(View.GONE);
            t2LunMin.setVisibility(View.GONE);
            t2MarHs.setVisibility(View.GONE);
            t2MarMin.setVisibility(View.GONE);
            t2MieHs.setVisibility(View.GONE);
            t2MieMin.setVisibility(View.GONE);
            t2JueHs.setVisibility(View.GONE);
            t2JueMin.setVisibility(View.GONE);
            t2VieHs.setVisibility(View.GONE);
            t2VieMin.setVisibility(View.GONE);
            t2SabHs.setVisibility(View.GONE);
            t2SabMin.setVisibility(View.GONE);
            t2DomHs.setVisibility(View.GONE);
            t2DomMin.setVisibility(View.GONE);

        }
        else
        {
            //Muestro Spinners de Tolva 2
            t2LunHs.setVisibility(View.VISIBLE);
            t2LunMin.setVisibility(View.VISIBLE);
            t2MarHs.setVisibility(View.VISIBLE);
            t2MarMin.setVisibility(View.VISIBLE);
            t2MieHs.setVisibility(View.VISIBLE);
            t2MieMin.setVisibility(View.VISIBLE);
            t2JueHs.setVisibility(View.VISIBLE);
            t2JueMin.setVisibility(View.VISIBLE);
            t2VieHs.setVisibility(View.VISIBLE);
            t2VieMin.setVisibility(View.VISIBLE);
            t2SabHs.setVisibility(View.VISIBLE);
            t2SabMin.setVisibility(View.VISIBLE);
            t2DomHs.setVisibility(View.VISIBLE);
            t2DomMin.setVisibility(View.VISIBLE);

            //Oculto Spinners de Tova 1
            t1LunHs.setVisibility(View.GONE);
            t1LunMin.setVisibility(View.GONE);
            t1MarHs.setVisibility(View.GONE);
            t1MarMin.setVisibility(View.GONE);
            t1MieHs.setVisibility(View.GONE);
            t1MieMin.setVisibility(View.GONE);
            t1JueHs.setVisibility(View.GONE);
            t1JueMin.setVisibility(View.GONE);
            t1VieHs.setVisibility(View.GONE);
            t1VieMin.setVisibility(View.GONE);
            t1SabHs.setVisibility(View.GONE);
            t1SabMin.setVisibility(View.GONE);
            t1DomHs.setVisibility(View.GONE);
            t1DomMin.setVisibility(View.GONE);
        }
    }

    private void guardarPlanificacion() {
        String data = "";
        //Guardo en formato "tolva;dia;hora;minutos", tomando el lunes como dia 1
        //Tolva 1
        if(t1LunHs.getSelectedItem() != "" && t1LunMin.getSelectedItem() != "")
        {
            data += "1;1;"+t1LunHs.getSelectedItem()+";"+t1LunMin.getSelectedItem()+"\n";
        }
        if(t1MarHs.getSelectedItem() != "" && t1MarMin.getSelectedItem() != "")
        {
            data += "1;2;"+t1MarHs.getSelectedItem()+";"+t1MarMin.getSelectedItem()+"\n";
        }
        if(t1MieHs.getSelectedItem() != "" && t1MieMin.getSelectedItem() != "")
        {
            data += "1;3;"+t1MieHs.getSelectedItem()+";"+t1MieMin.getSelectedItem()+"\n";
        }
        if(t1JueHs.getSelectedItem() != "" && t1JueMin.getSelectedItem() != "")
        {
            data += "1;4;"+t1JueHs.getSelectedItem()+";"+t1JueMin.getSelectedItem()+"\n";
        }
        if(t1VieHs.getSelectedItem() != "" && t1VieMin.getSelectedItem() != "")
        {
            data += "1;5;"+t1VieHs.getSelectedItem()+";"+t1VieMin.getSelectedItem()+"\n";
        }
        if(t1SabHs.getSelectedItem() != "" && t1SabMin.getSelectedItem() != "")
        {
            data += "1;6;"+t1SabHs.getSelectedItem()+";"+t1SabMin.getSelectedItem()+"\n";
        }
        if(t1DomHs.getSelectedItem() != "" && t1DomMin.getSelectedItem() != "")
        {
            data += "1;7;"+t1DomHs.getSelectedItem()+";"+t1DomMin.getSelectedItem()+"\n";
        }
        //Tolva 2
        if(t2LunHs.getSelectedItem() != "" && t2LunMin.getSelectedItem() != "")
        {
            data += "2;1;"+t2LunHs.getSelectedItem()+";"+t2LunMin.getSelectedItem()+"\n";
        }
        if(t2MarHs.getSelectedItem() != "" && t2MarMin.getSelectedItem() != "")
        {
            data += "2;2;"+t2MarHs.getSelectedItem()+";"+t2MarMin.getSelectedItem()+"\n";
        }
        if(t2MieHs.getSelectedItem() != "" && t2MieMin.getSelectedItem() != "")
        {
            data += "2;3;"+t2MieHs.getSelectedItem()+";"+t2MieMin.getSelectedItem()+"\n";
        }
        if(t2JueHs.getSelectedItem() != "" && t2JueMin.getSelectedItem() != "")
        {
            data += "2;4;"+t2JueHs.getSelectedItem()+";"+t2JueMin.getSelectedItem()+"\n";
        }
        if(t2VieHs.getSelectedItem() != "" && t2VieMin.getSelectedItem() != "")
        {
            data += "2;5;"+t2VieHs.getSelectedItem()+";"+t2VieMin.getSelectedItem()+"\n";
        }
        if(t2SabHs.getSelectedItem() != "" && t2SabMin.getSelectedItem() != "")
        {
            data += "2;6;"+t2SabHs.getSelectedItem()+";"+t2SabMin.getSelectedItem()+"\n";
        }
        if(t2DomHs.getSelectedItem() != "" && t2DomMin.getSelectedItem() != "")
        {
            data += "2;7;"+t2DomHs.getSelectedItem()+";"+t2DomMin.getSelectedItem()+"\n";
        }

        //Guardo el archivo
        if(!data.equals(""))
        {
            //Las aplicaciones Android son independientes unas de otras y tienen una carpeta dedicada
            // en la memoria interna para poder leer y escribir en ella

            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new
                        File(getFilesDir()+File.separator+"data.txt")));
                bufferedWriter.write(data);
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
