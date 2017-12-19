package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import asteroides.example.org.asteroides.models.Score;

/**
 * Created by jmtt_ on 12/10/2017.
 */

public class ScoreDatabaseSQLite extends SQLiteOpenHelper implements ScoreDatabase {
    private static final int VERSION = 1;
    private static final String DB_NAME = "HIGH_SCORES";
    private static final String TABLE_NAME = "score";
    private static final String TABLE_SCORE_NAME_REL = "score_rel";
    private static final String TABLE_USER_NAME_REL = "users";

    public ScoreDatabaseSQLite(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(new StringBuilder()
                        .append("CREATE TABLE ")
                        .append(TABLE_NAME)
                        .append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER, name TEXT, date BIGINT)")
                        .toString());
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion==2 && newVersion==1){
            onCreate(db); //Crea las nuevas tablas
            Cursor cursor = db.rawQuery(
                    new StringBuilder()
                            .append("SELECT score, name, date FROM ")
                            .append(TABLE_SCORE_NAME_REL)
                            .append(", ")
                            .append(TABLE_USER_NAME_REL)
                            .append(" WHERE user = "+TABLE_USER_NAME_REL+"._id ORDER BY ")
                            .append("score DESC")
                            .toString(), null);
            while (cursor.moveToNext()){
                storeScore(db, cursor.getInt(0), cursor.getString(1),cursor.getInt(2));
            }
            cursor.close();
            db.execSQL(String.format("DROP TABLE %s", TABLE_SCORE_NAME_REL)); //Elimina tabla antigua
            db.execSQL(String.format("DROP TABLE %s", TABLE_USER_NAME_REL)); //Elimina tabla antigua
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void storeScore(int score, String name, long date) {
        SQLiteDatabase db = getWritableDatabase();
        storeScore(db, score, name, date);
        db.close();
    }

    private void storeScore(SQLiteDatabase db, int score, String name, long date){
        db.execSQL("INSERT INTO "+TABLE_NAME+" VALUES ( null, "+score+", '"+name+"', "+date+")");
    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] CAMPOS = {"score", "name"};

        /*
        Cursor cursor = db.rawQuery("SELECT score, name FROM " +
                TABLE_NAME+" ORDER BY score DESC LIMIT " +size, null);
        */
        Cursor cursor=db.query(
                TABLE_NAME,
                CAMPOS,
                null,
                null,
                null,
                null,
                "score DESC",
                Integer.toString(size));


        while (cursor.moveToNext()){
            result.add(cursor.getInt(0)+" " +cursor.getString(1));
        }
        cursor.close();
        db.close();
        return result;
    }

}
