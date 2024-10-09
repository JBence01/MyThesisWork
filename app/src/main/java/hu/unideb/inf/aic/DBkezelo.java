package hu.unideb.inf.aic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class DBkezelo extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sensors.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "sensor_data";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_SOIL_MOISTURE = "soil_moisture";
    private static final String COLUMN_SWITCH_STATE = "valve_state";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DBkezelo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEMPERATURE + " REAL, " +
                COLUMN_HUMIDITY + " REAL, " +
                COLUMN_SOIL_MOISTURE + " REAL, " +
                COLUMN_SWITCH_STATE + " TEXT, " +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean adatBe(double temperature, double humidity, double soilMoisture, String valveState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("temperature", temperature);
        contentValues.put("humidity", humidity);
        contentValues.put("soil_moisture", soilMoisture);
        contentValues.put("valve_state", valveState);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public LiveData<SzenzorAdat> adatOlvas() {
        MutableLiveData<SzenzorAdat> szenzorAdat = new MutableLiveData<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            double temperature = DBHelper.getDoubleFromCursor(cursor, "temperature");
            double humidity = DBHelper.getDoubleFromCursor(cursor, "humidity");
            double soilMoisture = DBHelper.getDoubleFromCursor(cursor, "soil_moisture");
            String valveState = DBHelper.getStringFromCursor(cursor, "valve_state");

            szenzorAdat.setValue(new SzenzorAdat(temperature, humidity, soilMoisture, valveState));
        }
        cursor.close();
        return szenzorAdat;
    }

    public LiveData<List<SzenzorAdat>> adatOlvas30() {
        MutableLiveData<List<SzenzorAdat>> szenzorAdatList = new MutableLiveData<>();
        List<SzenzorAdat> adatok = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM sensor_data ORDER BY timestamp DESC LIMIT 30", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                double temperature = DBHelper.getDoubleFromCursor(cursor, "temperature");
                double humidity = DBHelper.getDoubleFromCursor(cursor, "humidity");
                double soilMoisture = DBHelper.getDoubleFromCursor(cursor, "soil_moisture");
                String valveState = DBHelper.getStringFromCursor(cursor, "valve_state");

                adatok.add(new SzenzorAdat(temperature, humidity, soilMoisture, valveState));
            } while (cursor.moveToNext());
            cursor.close();
        }

        szenzorAdatList.setValue(adatok);
        return szenzorAdatList;
    }



}
