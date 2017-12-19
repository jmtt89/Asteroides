package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import asteroides.example.org.asteroides.models.Score;

/**
 * No sigo las recomendaciones del libro para realizar el parser y serlizacion del json porque me parece
 * una completa perdida de tiempo... no es para nada necesario hacer una clase extra para almacenar una lista
 * cuando Gson es perfetamente capaz de parsear y serializar listas...
 * Ademas esa solucion a mi parecer es ineficiente ya que cada vez que se va a agregar una nueva entrada
 * es necesario parsear todos los scores anteriormente almacenados lo cual es una perdida de tiempo
 * La solucion que propongo solo lee cuando va a listar a la hora de agregar no es necesario parsear nada
 *
 * Adicionalmente se puede optimizar aun mas manteniendo en memoria los score y solo realizar la lectura
 * a preferences al inicializar la clase, pero esta solucion puede causar problemas de memoria cuando hay
 * muchos datos.
 *
 * Igual hago uso de los metodos fromJson y toJson que es lo que imagino intentan evaluar en este apartado
 */

public class ScoreDatabaseGson implements ScoreDatabase {
    private static final String DB_NAME = "JSON_DB";
    private SharedPreferences database;
    private Gson gson;

    public ScoreDatabaseGson(Context context) {
        database = context.getSharedPreferences(DB_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    @Override
    public void storeScore(int score, String name, long date) {
        Score toStore = new Score(score, name, date);
        database.edit()
                .putString(toStore.getId(), gson.toJson(toStore))
                .apply();
    }

    @Override
    public List<String> listScores(int size) {
        List<Score> tmp = new ArrayList<>();
        for (Object json : database.getAll().values()) {
            if(json instanceof String){
                tmp.add(gson.fromJson(String.valueOf(json), Score.class));
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
