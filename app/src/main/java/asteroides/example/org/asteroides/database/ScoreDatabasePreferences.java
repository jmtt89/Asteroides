package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jmtt_ on 11/8/2017.
 */

public class ScoreDatabasePreferences implements ScoreDatabase {
    private static final String DB_NAME = "MAX_SCORES";
    private SharedPreferences scorePref;

    public ScoreDatabasePreferences(Context context) {
        scorePref = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void storeScore(int score, String name, long date) {
        SharedPreferences.Editor prefEditor = scorePref.edit();
        prefEditor.putString(String.valueOf(date), score +" "+name);
        prefEditor.apply();
    }

    @Override
    public List<String> listScores(int size) {
        List tmp = new ArrayList();
        tmp.addAll(scorePref.getAll().values());
        Collections.sort(tmp, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String p1 = o1.split(" ")[0];
                String p2 = o2.split(" ")[0];
                return  Integer.parseInt(p2) - Integer.parseInt(p1);
            }
        });
        return tmp;
    }
}
