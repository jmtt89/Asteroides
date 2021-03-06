package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import asteroides.example.org.asteroides.R;

/**
 * Created by jmtt_ on 12/10/2017.
 */

public class ScoreDatabaseExtAplMemory implements ScoreDatabase {
    private static String FILE = "/highScores.txt";
    private Context context;

    public ScoreDatabaseExtAplMemory(Context context) {
        this.context = context;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.sd_no_write, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            FileOutputStream f = new FileOutputStream(getScoreDatabaseDir(context), true);
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
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED) &&
                !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, R.string.sd_no_read, Toast.LENGTH_LONG).show();
            return result;
        }
        try {
            FileInputStream f = new FileInputStream(getScoreDatabaseDir(context));
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

    private File getScoreDatabaseDir(Context context) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILE);
        if (!file.mkdirs()) {
            file.mkdirs();
        }
        return file;
    }
}
