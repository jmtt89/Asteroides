package asteroides.example.org.asteroides.database;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class ScoreDatabaseXML_DOM implements ScoreDatabase {
    private static String FILE = "highScore.xml";
    private Context contexto;
    private Document documento;
    private boolean cargadoDocumento;

    public ScoreDatabaseXML_DOM(Context contexto) {
        this.contexto = contexto;
        cargadoDocumento = false;
    }

    @Override
    public void storeScore(int score, String name, long date) {
        try {
            if (!cargadoDocumento) {
                leerXML(contexto.openFileInput(FILE));
            }
        } catch (FileNotFoundException e) {
            crearXML();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        nuevo(score, name, date);
        try {
            escribirXML(contexto.openFileOutput(FILE, Context.MODE_PRIVATE));
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    @Override
    public List<String> listScores(int size) {
        try {
            if (!cargadoDocumento) {
                leerXML(contexto.openFileInput(FILE));
            }
        } catch (FileNotFoundException e) {
            crearXML();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return aVectorString();
    }

    private void crearXML() {
        try {
            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructor = fabrica.newDocumentBuilder();
            documento = constructor.newDocument();
            Element raiz = documento.createElement("lista_puntuaciones");
            documento.appendChild(raiz);
            cargadoDocumento = true;
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
    }

    private void leerXML(InputStream entrada) throws Exception {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructor = fabrica.newDocumentBuilder();
        documento = constructor.parse(entrada);
        cargadoDocumento = true;
    }

    /*
    * No es necesario implementar nada para un API < 8 ya que el minimo de este proyecto es API 15
    * */
    private void escribirXML(OutputStream salida) throws Exception {
        TransformerFactory fabrica = TransformerFactory.newInstance();
        Transformer transformador = fabrica.newTransformer();
        transformador.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
        transformador.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource fuente = new DOMSource(documento);
        StreamResult resultado = new StreamResult(salida);
        transformador.transform(fuente, resultado);
    }

    private void nuevo(int puntos, String nombre, long fecha) {
        Element puntuacion = documento.createElement("puntuacion");
        puntuacion.setAttribute("fecha", String.valueOf(fecha));
        Element e_nombre = documento.createElement("nombre");
        Text texto = documento.createTextNode(nombre);
        e_nombre.appendChild(texto);
        puntuacion.appendChild(e_nombre);
        Element e_puntos = documento.createElement("puntos");
        texto = documento.createTextNode(String.valueOf(puntos));
        e_puntos.appendChild(texto);
        puntuacion.appendChild(e_puntos);
        Element raiz = documento.getDocumentElement();
        raiz.appendChild(puntuacion);
    }

    private Vector aVectorString() {
        Vector result = new Vector();
        String nombre = "", puntos = "";
        Element raiz = documento.getDocumentElement();
        NodeList puntuaciones = raiz.getElementsByTagName("puntuacion");
        for (int i = 0; i < puntuaciones.getLength(); i++) {
            Node puntuacion = puntuaciones.item(i);
            NodeList propiedades = puntuacion.getChildNodes();
            for (int j = 0; j < propiedades.getLength(); j++) {
                Node propiedad = propiedades.item(j);
                String etiqueta = propiedad.getNodeName();
                if (etiqueta.equals("nombre")) {
                    nombre = propiedad.getFirstChild().getNodeValue();
                } else if (etiqueta.equals("puntos")) {
                    puntos = propiedad.getFirstChild().getNodeValue();
                }
            }
            result.add(nombre + " " + puntos);
        }
        return result;
    }


}
