package asteroides.example.org.asteroides.database;

import java.util.List;
import java.util.Vector;

/**
 * Created by jmtt_ on 10/21/2017.
 * La Base de datos no deberia utilizarse directamente en las vistas, pero en la tarea piden que sea
 * de esta manera, asi que lo dejo en su propio package para ser modificado luego.
 */

public interface ScoreDatabase {
    public void storeScore(int score, String name, long date);
    public List<String> listScores(int size);
}
