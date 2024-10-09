package hu.unideb.inf.aic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private TextView valueTemp, valueHum, valueSoil, valueValve;
    private DBkezelo DBkezelo;
    private SzenzorViewModel szenzorViewModel;
    Button buttonTest;
    Button buttonKorabbi;

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
        valueValve = findViewById(R.id.valueValve);
        buttonKorabbi = findViewById(R.id.buttonKorabbi);
        buttonTest = findViewById(R.id.buttonTest);

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

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTestData();
            }
        });

    }

    private void adatPrint(SzenzorAdat szenzorAdat) {
        valueTemp.setText(String.valueOf(szenzorAdat.getTemperature()) + " °C");
        valueHum.setText(String.valueOf(szenzorAdat.getHumidity())+ " %");
        valueSoil.setText(String.valueOf(szenzorAdat.getSoilMoisture())+ " %");
        valueValve.setText(szenzorAdat.getValveState());
    }

    private void insertTestData() {
        DBkezelo.adatBe(19.0, 55.0, 30.2, "Zárva");
        DBkezelo.adatBe(23.4, 42.3, 30.6, "Zárva");
        DBkezelo.adatBe(26.3, 35.7, 30.5, "Zárva");
        DBkezelo.adatBe(27.0, 29.2, 31.5, "Zárva");
        DBkezelo.adatBe(28.5, 29.0, 30.2, "Zárva");
        DBkezelo.adatBe(28.0, 29.3, 27.6, "Zárva");
        DBkezelo.adatBe(27.3, 33.7, 28.1, "Zárva");
        DBkezelo.adatBe(26.7, 35.2, 27.5, "Zárva");
        DBkezelo.adatBe(26.0, 37.1, 29.3, "Zárva");
        DBkezelo.adatBe(24.4, 40.3, 30.5, "Zárva");
        DBkezelo.adatBe(21.0, 54.2, 31.0, "Zárva");
        DBkezelo.adatBe(20.2, 61.0, 32.5, "Zárva");
        DBkezelo.adatBe(20.0, 62.2, 34.4, "Zárva");
        DBkezelo.adatBe(18.3, 67.6, 40.5, "Zárva");
        DBkezelo.adatBe(17.7, 72.2, 70.7, "Zárva");
        DBkezelo.adatBe(17.2, 79.4, 83.5, "Zárva");
        DBkezelo.adatBe(16.5, 82.7, 88.4, "Zárva");
        DBkezelo.adatBe(16.1, 87.6, 84.9, "Zárva");
        DBkezelo.adatBe(15.4, 82.7, 83.5, "Zárva");
        DBkezelo.adatBe(15.1, 85.9, 84.3, "Zárva");
        DBkezelo.adatBe(15.3, 87.8, 81.9, "Zárva");
        DBkezelo.adatBe(15.8, 88.2, 78.7, "Zárva");
        DBkezelo.adatBe(16.2, 84.6, 76.5, "Zárva");
        DBkezelo.adatBe(17.4, 81.2, 77.1, "Zárva");

        Toast.makeText(this, "Tesztadatok hozzáadva az adatbázishoz!", Toast.LENGTH_SHORT).show();
    }

}