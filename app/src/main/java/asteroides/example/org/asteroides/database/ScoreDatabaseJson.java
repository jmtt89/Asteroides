package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asteroides.example.org.asteroides.models.Score;

/**
 * A diferencia del libro agregue el parse a json nativo al objeto score ya que este es el que debe
 * manejar su propia serializacion a json como lo hace a string, tambien se agrego un metodo estatico
 * que crea una instancia de escore a partir de un string.
 */
public class ScoreDatabaseJson implements ScoreDatabase {
    private static final String DB_NAME = "JSON_DB";
    private SharedPreferences database;


    public ScoreDatabaseJson(Context context) {
        database = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void storeScore(int score, String name, long date) {
        Score toStore = new Score(score, name, date);
        database.edit()
                .putString(toStore.getId(), toStore.toJson())
                .apply();
    }

    @Override
    public List<String> listScores(int size) {
        List<Score> tmp = new ArrayList<>();
        for (Object json : database.getAll().values()) {
            if(json instanceof String){
                Score score = Score.fromJson(String.valueOf(json));
                if(score != null){
                    tmp.add(score);
                }
            }
        }
        Collections.sort(tmp, new Comparator<Score>() {
            @Override
            public int compare(Score scoreA, Score scoreB) {
                return scoreA.getScore() - scoreB.getScore();
            }
        });
        List<String> salida = new ArrayList<>();
        for (Score puntuacion : tmp) {
            salida.add(puntuacion.getScore()+" "+puntuacion.getName());
        }
        return salida;
    }
}
