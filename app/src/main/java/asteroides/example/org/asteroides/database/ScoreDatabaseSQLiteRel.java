package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ScoreDatabaseSQLiteRel extends SQLiteOpenHelper implements ScoreDatabase {
    private static final int VERSION = 2;
    private static final String DB_NAME = "HIGH_SCORES";
    private static final String TABLE_SCORES_NAME_V1 = "score";
    private static final String TABLE_SCORES_NAME = "score_rel";
    private static final String TABLE_USERS_NAME = "users";
    private static final String TAG = "ScoreDatabaseSQLiteRel";

    public ScoreDatabaseSQLiteRel(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new StringBuilder()
                        .append("CREATE TABLE ")
                        .append(TABLE_USERS_NAME)
                        .append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)")
                        .toString());
        db.execSQL(new StringBuilder()
                        .append("CREATE TABLE ")
                        .append(TABLE_SCORES_NAME)
                        .append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
                        .append("score INTEGER, date BIGINT, user INTEGER, ")
                        .append("FOREIGN KEY (user) REFERENCES ")
                        .append(TABLE_USERS_NAME)
                        .append(" (_id))")
                        .toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion==1 && newVersion==2){
            onCreate(db); //Crea las nuevas tablas
            Cursor cursor = db.rawQuery(
                    new StringBuilder()
                            .append("SELECT score, name, date ")
                            .append("FROM ")
                            .append(TABLE_SCORES_NAME_V1)
                            .toString(),null); //Recorre la tabla antigua
            while (cursor.moveToNext()) {
                storeScore(db, cursor.getInt(0), cursor.getString(1),cursor.getInt(2));
            } //Crea los nuevos registros
            cursor.close();
            db.execSQL("DROP TABLE "+TABLE_SCORES_NAME_V1); //Elimina tabla antigua
        }
    }

    @Override
    public void storeScore(int score, String name, long date) {
        SQLiteDatabase db = getWritableDatabase();
        storeScore(db, score, name, date);
        db.close();
    }

    private void storeScore(SQLiteDatabase db, int score, String name, long date){
        int user = searchAndInsert(db, name);
        db.execSQL("PRAGMA foreign_keys = 1");
        db.execSQL("INSERT INTO "+TABLE_SCORES_NAME+" VALUES ( null, " +
                score + ", " + date + ", " + user + ")");
    }

    private int searchAndInsert(SQLiteDatabase db, String name) {
        Cursor cursor = db.rawQuery("SELECT _id FROM "+TABLE_USERS_NAME
                + " WHERE name='" + name + "'", null);
        if (cursor.moveToNext()) {
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        } else {
            cursor.close();
            db.execSQL("INSERT INTO "+TABLE_USERS_NAME+" VALUES (null, '" + name
                    + "', 'correo@dominio.es')");
            return searchAndInsert(db, name);
        }

    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                new StringBuilder()
                        .append("SELECT score, name FROM ")
                        .append(TABLE_SCORES_NAME)
                        .append(", ")
                        .append(TABLE_USERS_NAME)
                        .append(" WHERE user = "+TABLE_USERS_NAME+"._id ORDER BY ")
                        .append("score DESC LIMIT ")
                        .append(size)
                        .toString(), null);
        while (cursor.moveToNext()){
            result.add(cursor.getInt(0)+" " +cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }

}
