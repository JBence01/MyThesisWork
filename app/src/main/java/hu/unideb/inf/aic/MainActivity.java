package hu.unideb.inf.aic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private TextView valueTemp, valueHum, valueSoil;
    private DBkezelo DBkezelo;
    private SzenzorViewModel szenzorViewModel;
    Button buttonKorabbi;
    Switch switchValueValve;
    String valveValue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        valueTemp = findViewById(R.id.valueTemp);
        valueHum = findViewById(R.id.valueHum);
        valueSoil = findViewById(R.id.valueSoil);
        buttonKorabbi = findViewById(R.id.buttonKorabbi);
        switchValueValve = findViewById(R.id.switchValueValve);

        DBkezelo = new DBkezelo(this);

        // SensorViewModel létrehozása a SensorViewModelFactory segítségével
        SzenzorViewModelFactory factory = new SzenzorViewModelFactory(DBkezelo);
        szenzorViewModel = new ViewModelProvider(this, factory).get(SzenzorViewModel.class);

        // LiveData figyelése és UI frissítése
        szenzorViewModel.getSzenzorAdat().observe(this, new Observer<SzenzorAdat>() {
            @Override
            public void onChanged(SzenzorAdat szenzorAdat) {
                adatPrint(szenzorAdat);
            }
        });

        buttonKorabbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KorabbiActivity.class);
                startActivity(intent);
            }
        });

    }

    private void adatPrint(SzenzorAdat szenzorAdat) {
        valueTemp.setText(String.valueOf(szenzorAdat.getTemperature()) + " °C");
        valueHum.setText(String.valueOf(szenzorAdat.getHumidity())+ " %");
        valueSoil.setText(String.valueOf(szenzorAdat.getSoilMoisture())+ " %");
        valveValue = szenzorAdat.getValveState();
        if (valveValue.equals("nyitva")) {
            switchValueValve.setChecked(true); // Bekapcsolt állapot
        } else {
            switchValueValve.setChecked(false); // Kikapcsolt állapot
        }
    }

    }



//Felhasznált képek forrása:
/*
tuya_temp.png: https://www.zigbee2mqtt.io/images/devices/IH-K009.png
tuya_soil.png: https://www.zigbee2mqtt.io/images/devices/TS0601_soil.png
giex_valve.png: https://www.zigbee2mqtt.io/images/devices/GX02.png

Az alkalmazás ikonjának forrása: OpenAI DALL-E
 */