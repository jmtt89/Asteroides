package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ScoreDatabaseSW_PHP implements ScoreDatabase {
    private static final String DEFAULT_HOST = "http://158.42.146.127";
    private final SharedPreferences pref;

    public ScoreDatabaseSW_PHP(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    @Override
    public void storeScore(int score, String name, long date) {
        String host = DEFAULT_HOST;
        if(!pref.getBoolean("useDefaultWebservice", true)){
            host = pref.getString("customWebservice", DEFAULT_HOST);
        }

        HttpURLConnection conexion = null;
        try {
            URL url=new URL(host + "/puntuaciones/nueva.php?"
                    + "puntos="+ score
                    + "&nombre="+ URLEncoder.encode(name, "UTF-8")
                    + "&fecha=" + date);
            conexion = (HttpURLConnection) url
                    .openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (!linea.equals("OK")) {
                    Log.e("Asteroides","Error en servicio Web nueva");
                }
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        } finally {
            if (conexion!=null) conexion.disconnect();
        }
    }

    @Override
    public List<String> listScores(int size) {
        List<String> result = new ArrayList<>();
        HttpURLConnection conexion = null;
        String host = DEFAULT_HOST;
        if(!pref.getBoolean("useDefaultWebservice", true)){
            host = pref.getString("customWebservice", DEFAULT_HOST);
        }
        try {
            URL url=new URL(host +"/puntuaciones/lista.php?max="+size);
            conexion = (HttpURLConnection) url.openConnection();
            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (!linea.equals("")) {
                    result.add(linea);
                    linea = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("Asteroides", conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        } finally {
            if (conexion!=null) conexion.disconnect();
        }
        return result;
    }
}
