package hu.unideb.inf.aic;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class KorabbiActivity extends AppCompatActivity {

    Button buttonAktualis;
    private DBkezelo DBkezelo;
    private LineChart chart;
    CheckBox tempCheckBox, humCheckBox, soilCheckBox;

    private boolean showTemperature = true;
    private boolean showHumidity = true;
    private boolean showSoilMoisture = true;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        buttonAktualis = findViewById(R.id.buttonAktualis);
        chart = findViewById(R.id.chart);
        DBkezelo = new DBkezelo(this);
        tempCheckBox = findViewById(R.id.tempCheckBox);
        humCheckBox = findViewById(R.id.humCheckBox);
        soilCheckBox = findViewById(R.id.soilCheckBox);

        tempCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showTemperature = isChecked; // Állapot beállítása
                diagramRajzolas();           // Diagram újrarajzolása
            }
        });

        humCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showHumidity = isChecked;
                diagramRajzolas();
            }
        });

        soilCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showSoilMoisture = isChecked;
                diagramRajzolas();
            }
        });

        buttonAktualis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(KorabbiActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        
        diagramRajzolas();
    }

    private void diagramRajzolas() {

        SzenzorViewModelFactory factory = new SzenzorViewModelFactory(DBkezelo);
        SzenzorViewModel szenzorViewModel = new ViewModelProvider(this, factory).get(SzenzorViewModel.class);

        szenzorViewModel.adatOlvas30().observe(this, new Observer<List<SzenzorAdat>>() {
            @Override
            public void onChanged(List<SzenzorAdat> szenzorAdatok) {
                ArrayList<Entry> homersekletEntries = new ArrayList<>();
                ArrayList<Entry> paratartalomEntries = new ArrayList<>();
                ArrayList<Entry> talajnedvessegEntries = new ArrayList<>();
                int index = 0;

                for (SzenzorAdat adat : szenzorAdatok) {
                    float homerseklet = (float) adat.getTemperature();
                    float paratartalom = (float) adat.getHumidity();
                    float talajnedvesseg = (float) adat.getSoilMoisture();

                    // Bejegyzések hozzáadása a grafikonhoz
                    homersekletEntries.add(new Entry(index, homerseklet));
                    paratartalomEntries.add(new Entry(index, paratartalom));
                    talajnedvessegEntries.add(new Entry(index, talajnedvesseg));
                    index++;
                }

                LineDataSet homersekletDataSet = new LineDataSet(homersekletEntries, "Hőmérséklet");
                homersekletDataSet.setColor(Color.RED);
                homersekletDataSet.setValueTextSize(10f);
                LineDataSet paratartalomDataSet = new LineDataSet(paratartalomEntries, "Páratartalom");
                paratartalomDataSet.setColor(Color.BLUE);
                paratartalomDataSet.setValueTextSize(10f);
                LineDataSet talajnedvessegDataSet = new LineDataSet(talajnedvessegEntries, "Talajnedvesség");
                talajnedvessegDataSet.setColor(Color.GREEN);
                talajnedvessegDataSet.setValueTextSize(10f);

                LineData lineData = new LineData();
                if (showTemperature) {
                    lineData.addDataSet(homersekletDataSet);
                }
                if (showHumidity) {
                    lineData.addDataSet(paratartalomDataSet);
                }
                if (showSoilMoisture) {
                    lineData.addDataSet(talajnedvessegDataSet);
                }
                chart.setData(lineData);

                // Tengely feliratainak szövegmérete
                chart.getXAxis().setTextSize(14f);
                chart.getAxisLeft().setTextSize(14f);
                chart.getAxisRight().setTextSize(14f);

                // Legend (magyarázat) kikapcsolása a checkboxok miatt
                chart.getLegend().setEnabled(false);

                //Egy kis extra hely a diagram körül, hogy a szöveg ne vágodjon le
                chart.setExtraOffsets(10, 10, 10, 10);

                chart.invalidate();
            }
        });
    }
}
