package asteroides.example.org.asteroides.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ScoreDatabaseProvider implements ScoreDatabase {
    private Activity activity;

    public ScoreDatabaseProvider(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        Uri uri = Uri.parse(
                "content://org.example.puntuacionesprovider/puntuaciones");
        ContentValues valores = new ContentValues();
        valores.put("nombre", name);
        valores.put("puntos", score);
        valores.put("fecha", date);
        try {
            activity.getContentResolver().insert(uri, valores);
        } catch (Exception e) {
            Toast.makeText(activity, "Verificar que está instalado org.example.puntuacionesprovider",Toast.LENGTH_LONG).show();
                    Log.e("Asteroides", "Error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        Uri uri = Uri.parse("content://org.example.puntuacionesprovider/puntuaciones");
        Cursor cursor = activity.getContentResolver()
                .query(uri,null, null, null, "fecha DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                int puntos = cursor.getInt(cursor.getColumnIndex("puntos"));
                result.add(puntos + " " + nombre);
            }
            cursor.close();
        }
        return result;
    }
}
