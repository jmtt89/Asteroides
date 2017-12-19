package asteroides.example.org.asteroides.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jmtt_ on 12/10/2017.
 */

public class Score {
    private int score;
    private String name;
    private long date;

    public Score() {
    }

    public Score(int score, String name, long date) {
        this.score = score;
        this.name = name;
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getId() {
        return (this.date + this.name.hashCode() + this.score) + "";
    }

    public String toJson(){
        String string = "";
        try {
            JSONObject objeto = new JSONObject();
            objeto.put("score", this.score);
            objeto.put("name", this.name);
            objeto.put("date", this.date);
            string = objeto.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static Score fromJson(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            return new Score(
                    jsonObject.getInt("score"),
                    jsonObject.getString("name"),
                    jsonObject.getLong("date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
