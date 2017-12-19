package asteroides.example.org.asteroides.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * El provider lo cree exatamente con los mismos nombres y estructuras por si hay algun tipo de pruebas
 * sobre consultar el provider desde alguna aplicacion foranea
 */

public class ScoresSQLiteHelper  extends SQLiteOpenHelper {
    public ScoresSQLiteHelper(Context context) {
        super(context, "puntuaciones", null, 1);
    }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "puntos INTEGER, "
                + "nombre TEXT, "
                + "fecha BIGINT)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion,
                                    int newVersion) {
        // En caso de una nueva versión habría que actualizar las tablas
    }
}