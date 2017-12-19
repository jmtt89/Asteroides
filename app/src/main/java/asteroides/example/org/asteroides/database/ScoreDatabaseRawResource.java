package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import asteroides.example.org.asteroides.R;

/**
 * Created by jmtt_ on 12/10/2017.
 */

public class ScoreDatabaseRawResource implements ScoreDatabase {
    private Context context;

    public ScoreDatabaseRawResource(Context context) {
        this.context = context;
    }

    @Override
    public void storeScore(int score, String name, long date) {

    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        try {
            InputStream f = context.getResources().openRawResource(R.raw.high_score);
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
