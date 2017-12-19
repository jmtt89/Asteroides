package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScoreDatabaseSW_PHP_AsyncTask implements ScoreDatabase {
    private static final String DEFAULT_HOST = "http://158.42.146.127";
    private static SharedPreferences pref;
    private Context context;

    public ScoreDatabaseSW_PHP_AsyncTask(Context context) {
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void storeScore(int score, String name, long date) {
        try {
            TareaGuardar tarea = new TareaGuardar();
            tarea.execute(String.valueOf(score), name, String.valueOf(date));
            tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(context, "Tiempo excedido al conectar", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(context, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
    }

    private static class TareaGuardar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String host = DEFAULT_HOST;
            if(!pref.getBoolean("useDefaultWebservice", true)){
                host = pref.getString("customWebservice", DEFAULT_HOST);
            }
            HttpURLConnection conexion = null;
            try {
                URL url = new URL(
                        host+"/puntuaciones/nueva.php"
                                + "?puntos=" + param[0] + "&nombre="
                                + URLEncoder.encode(param[1], "UTF-8")
                                + "&fecha=" + param[2]);
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
            return null;
        }
    }

    @Override
    public List<String> listScores(int size) {
        try {
            TareaLista tarea = new TareaLista();
            tarea.execute(size);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(context, "Tiempo excedido al conectar", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(context, "Error al conectar con servidor", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    private static class TareaLista extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... size) {
            String host = DEFAULT_HOST;
            if(!pref.getBoolean("useDefaultWebservice", true)){
                host = pref.getString("customWebservice", DEFAULT_HOST);
            }
            List<String> result = new ArrayList<>();
            HttpURLConnection conexion = null;
            try {
                URL url=new URL(host+"/puntuaciones/lista.php?max="+size[0]);
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
}
