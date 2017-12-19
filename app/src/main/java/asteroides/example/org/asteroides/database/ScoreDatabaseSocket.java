package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ScoreDatabaseSocket implements ScoreDatabase {
    private static final String COMMAND = "PUNTUACIONES";
    private static final String HOST = "158.42.146.127";
    private static final int PORT = 1234;
    private Context context;

    public ScoreDatabaseSocket(Context context) {
        this.context = context;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        try {
            TareaGuardar tarea = new TareaGuardar();
            tarea.execute(String.valueOf(score), name);
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
            try {
                Socket sk = new Socket(HOST, PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(sk.getOutputStream()),true);
                output.println(param[0] + " " + URLEncoder.encode(param[1], "UTF-8"));
                String response = input.readLine();
                if (!response.equals("OK")) {
                    Log.e("Asteroides", "Error: respuesta de servidor incorrecta");
                }
                sk.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.getLocalizedMessage(), e);
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
            List<String> result = new ArrayList<>();
            try {
                Socket sk = new Socket(HOST, PORT);
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(sk.getInputStream()));
                PrintWriter output = new PrintWriter(
                        new OutputStreamWriter(sk.getOutputStream()),true);
                output.println(COMMAND);
                int n = 0;
                String response;
                do {
                    response = input.readLine();
                    if (response != null) {
                        result.add(response);
                        n++;
                    }
                } while (n < size[0] && response != null);
                sk.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.getLocalizedMessage(), e);
            }
            return result;
        }
    }

}
