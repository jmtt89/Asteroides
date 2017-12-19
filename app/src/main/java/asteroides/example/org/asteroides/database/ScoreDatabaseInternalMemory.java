package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmtt_ on 12/10/2017.
 */

public class ScoreDatabaseInternalMemory implements ScoreDatabase {
    private static String FILE = "highScores.txt";
    private Context context;

    public ScoreDatabaseInternalMemory(Context context) {
        this.context = context;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        try {
            FileOutputStream f = context.openFileOutput(FILE, Context.MODE_APPEND);
            String texto = score + " " + name + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        try {
            FileInputStream f = context.openFileInput(FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = reader.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < size && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
        return result;
    }
}
