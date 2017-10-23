package asteroides.example.org.asteroides.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmtt_ on 10/22/2017.
 *
 */

public class ScoreDatabaseArray implements ScoreDatabase {
    private List<String> scores;

    public ScoreDatabaseArray() {
        scores = new ArrayList<>();
        scores.add("123000 Pepito Domingez");
        scores.add("111000 Pedro Martinez");
        scores.add("011000 Paco Pérez");
    }

    @Override
    public void storeScore(int score, String name, long date) {
        scores.add(0, score + " " + name);
    }

    @Override
    public List<String> listScores(int size) {
        if(scores.size() > size){
            return scores.subList(0,size);
        }else{
            return scores;
        }
    }
}
