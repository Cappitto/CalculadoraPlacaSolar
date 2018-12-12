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
    P -> power = Potencia da placa solar em watts a ser implantada no sistema. 250W por default.
    L -> percent = Porcentagem da efetividade do inversor do sistema (em média 80%). 0.8 ou 80% por default.
     */
    private double energy, time, power, insolation, percent, result = 0.0;

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
                    String dialogMessage = getString(R.string.panel_amount) + (int) result + "\n";
                    // verificando se os valores inseridos nas entradas Efetividade e Potencia estão vazios, para então colocar
                    // um aviso no final dizendo que os valores default estão sendo utilizados.
                    dialogMessage = percent == 80 ? dialogMessage + getString(R.string.default_percent) : dialogMessage;
                    dialogMessage = power == 250.0 ? dialogMessage + getString(R.string.default_power) : dialogMessage;
                    // caixa de dialogo mostrando resultado final.
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(dialogMessage);
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
        // limpa os campos para inserir novos valores caso o usuario clique em Calcular novamente.
        editTextEnergy.setText("");
        editTextTime.setText("");
        editTextInsolation.setText("");
        editTextPercent.setText("");
        editTextPower.setText("");
    }

    private boolean checkLength(){
        // essa função verifica se os campos foram preenchidos, se caso não estiverem retorna falso,
        // retorna verdadeiro caso os campos (exceto os que possuem default) estiverem preenchidos.
        String strEnergy = editTextEnergy.getText().toString();
        String strTime = editTextTime.getText().toString();
        String strInsolation = editTextInsolation.getText().toString();
        String strPercent = editTextPercent.getText().toString();
        String strPower = editTextPower.getText().toString();
        if (!strEnergy.trim().isEmpty() & !strTime.trim().isEmpty() & !strInsolation.trim().isEmpty()){
            energy = Double.parseDouble(strEnergy);
            time = Double.parseDouble(strTime);
            insolation = Double.parseDouble(strInsolation);
            percent = 80;
            power = 250.0;
            if (!strPercent.trim().isEmpty()) {
                percent = Double.parseDouble(strPercent);
            }
            if (!strPower.trim().isEmpty()){
                power = Double.parseDouble(strPower);
            }
            return true;
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about){
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
