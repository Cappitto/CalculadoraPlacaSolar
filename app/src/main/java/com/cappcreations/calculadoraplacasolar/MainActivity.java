package com.cappcreations.calculadoraplacasolar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity{

    private EditText editTextEnergy, editTextTime, editTextInsolation, editTextPercent, editTextPower;
    /*
    E -> energy = Energia utilizada pelo local de instalação em KiloWatts.
    T -> time = Tempo em horas que os equipamentos conectados à rede funcionam por dia.
    I -> insolation = Índice de insolação da região (utilizando como base o mês com menor insolação).
    P -> power = Potencia da placa solar em watts a ser implantada no sistema.
    L -> percent = Porcentagem da efetividade do inversor do sistema (em média 80%).
     */
    private double energy, time, power = 250.0, insolation, percent = 80, result = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextEnergy = findViewById(R.id.editTextEnergy);
        editTextTime = findViewById(R.id.editTextTime);
        editTextInsolation = findViewById(R.id.editTextInsolation);
        editTextPercent = findViewById(R.id.editTextPercent);
        editTextPower = findViewById(R.id.editTextPower);

        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLength()){
                    /*
                    kwPerDay = Quilowatts/hora por dia dos equipamentos.
                    minPower =  Potência mínima para que o sistema fotovoltaico supra a energia necessária.
                    result =  Número de placas solares para que o sistema a demanda da potência mínima.

                    Formulas:
                    kwhperday = e * t
                    minpower = kwperday/(i * l)
                    n = minpower / power
                    */
                    // está sendo dividido por 1000 pois o usuario fornece em Watts, mas a formula trata como kW.
                    double kwPerDay = (energy * time)/1000;
                    // está sendo dividido por 100 pois é uma porcentagem e o usuario fornecer um valor entre 100 e 0, mas
                    // porcentagem está entre 1 e 0.
                    double minPower = kwPerDay * (percent/100)/ (insolation);
                    // está sendo multiplicado por 1000 pois minpower está em kW mas power está em watts.
                    result = Math.ceil(minPower*1000/power);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.panelAmount) + format(result));
                    builder.setCancelable(true);
                    builder.setPositiveButton(getString(R.string.calculate_again), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearBoxes();
                        }
                    });
                    builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Snackbar.make(view, getString(R.string.fill_boxes), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void clearBoxes(){
        editTextEnergy.setText("");
        editTextTime.setText("");
        editTextInsolation.setText("");
        editTextPercent.setText("");
        editTextPower.setText("");
    }

    private boolean checkLength(){
        String strEnergy = editTextEnergy.getText().toString();
        String strTime = editTextTime.getText().toString();
        String strInsolation = editTextInsolation.getText().toString();
        String strPercent = editTextPercent.getText().toString();
        String strPower = editTextPower.getText().toString();
        if (!strEnergy.trim().isEmpty() & !strTime.trim().isEmpty() & !strInsolation.trim().isEmpty() &
                !strPercent.trim().isEmpty() & !strPower.trim().isEmpty()){
            energy = Double.parseDouble(strEnergy);
            time = Double.parseDouble(strTime);
            insolation = Double.parseDouble(strInsolation);
            percent = Double.parseDouble(strPercent);
            power = Double.parseDouble(strPower);
            return true;
        }
        return false;

    }

    private static String format(double x){
        DecimalFormat df = new DecimalFormat("###,###,###,##0.###");
        return df.format(x);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about){
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
