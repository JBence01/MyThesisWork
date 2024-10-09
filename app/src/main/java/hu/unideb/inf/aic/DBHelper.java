package hu.unideb.inf.aic;

import android.database.Cursor;

public class DBHelper {

    // Biztonságos oszlopérték lekérés egy oszlopnév alapján, elkerülve a -1 visszatérési értéket a .getColumnIndex esetén

    public static String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index != -1) {
            return cursor.getString(index);
        }
        return null;  // Visszatér null-al, ha nem található az oszlop (azaz -1 lenne)
    }

    public static double getDoubleFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index != -1) {
            return cursor.getDouble(index);
        }
        return Double.NaN;  // Visszatér Double.NaN, ha nem található az oszlop (azaz -1 lenne)
    }

    public static int getIntFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index != -1) {
            return cursor.getInt(index);
        }
        return -1;  // Visszatér -1-el, ha nem található az oszlop
    }
}
