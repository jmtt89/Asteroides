package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.List;

import asteroides.example.org.asteroides.models.ListaPuntuaciones;


public class ScoreDatabaseXML_SAX implements ScoreDatabase {
    private static String FILE = "highScore.xml";
    private Context contexto;
    private ListaPuntuaciones lista;
    private boolean cargadaLista;

    public ScoreDatabaseXML_SAX(Context contexto) {
        this.contexto = contexto;
        lista = new ListaPuntuaciones();
        cargadaLista = false;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        try {
            if (!cargadaLista){
                lista.leerXML(contexto.openFileInput(FILE));
                cargadaLista = true;
            }
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
        lista.nuevo(score, name, date);
        try {
            lista.escribirXML(contexto.openFileOutput(FILE, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<String> listScores(int size) {
        try {
            if (!cargadaLista){
                lista.leerXML(contexto.openFileInput(FILE));
                cargadaLista = true;
            }
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return lista.aListString();
    }
}
