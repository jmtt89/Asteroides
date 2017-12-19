package asteroides.example.org.asteroides.models;

import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ListaPuntuaciones {

    private List<Score> listaPuntuaciones;

    public ListaPuntuaciones() {
        listaPuntuaciones = new ArrayList<>();
    }

    public void nuevo(int puntos, String nombre, long fecha) {
        Score puntuacion = new Score();
        puntuacion.setScore(puntos);
        puntuacion.setName(nombre);
        puntuacion.setDate(fecha);
        listaPuntuaciones.add(puntuacion);
    }

    public List<String> aListString() {
        List<String> result = new ArrayList<>();
        for (Score puntuacion : listaPuntuaciones) {
            result.add(puntuacion.getName()+" "+puntuacion.getScore());
        }
        return result;
    }

    public void leerXML(InputStream entrada) throws Exception {
        SAXParserFactory fabrica = SAXParserFactory.newInstance();
        SAXParser parser = fabrica.newSAXParser();
        XMLReader lector = parser.getXMLReader();
        ManejadorXML manejadorXML = new ManejadorXML();
        lector.setContentHandler(manejadorXML);
        lector.parse(new InputSource(entrada));
    }

    public void escribirXML(OutputStream salida) {
        XmlSerializer serializador = Xml.newSerializer();
        try {
            serializador.setOutput(salida, "UTF-8");
            serializador.startDocument("UTF-8", true);
            serializador.startTag("", "lista_puntuaciones");
            for (Score puntuacion : listaPuntuaciones) {
                serializador.startTag("", "puntuacion");
                serializador.attribute("", "fecha",
                        String.valueOf(puntuacion.getDate()));
                serializador.startTag("", "nombre");
                serializador.text(puntuacion.getName());
                serializador.endTag("", "nombre");
                serializador.startTag("", "puntos");
                serializador.text(String.valueOf(puntuacion.getScore()));
                serializador.endTag("", "puntos");
                serializador.endTag("", "puntuacion");
            }
            serializador.endTag("", "lista_puntuaciones");
            serializador.endDocument();
        } catch (Exception e) {
            Log.e("Asteroides", e.getLocalizedMessage(), e);
        }
    }

    class ManejadorXML extends DefaultHandler {
        private StringBuilder cadena;
        private Score puntuacion;
        @Override
        public void startDocument() throws SAXException {
            listaPuntuaciones = new ArrayList<>();
            cadena = new StringBuilder();
        }
        @Override
        public void startElement(String uri, String nombreLocal, String
                nombreCualif, Attributes atr) throws SAXException {
            cadena.setLength(0);
            if (nombreLocal.equals("puntuacion")) {
                puntuacion = new Score();
                puntuacion.setDate(Long.parseLong(atr.getValue("fecha")));
            }
        }
        @Override
        public void characters(char ch[], int comienzo, int lon) {
            cadena.append(ch, comienzo, lon);
        }
        @Override
        public void endElement(String uri, String nombreLocal, String nombreCualif) throws SAXException {
            switch (nombreLocal) {
                case "puntos":
                    puntuacion.setScore(Integer.parseInt(cadena.toString()));
                    break;
                case "nombre":
                    puntuacion.setName(cadena.toString());
                    break;
                case "puntuacion":
                    listaPuntuaciones.add(puntuacion);
                    break;
            }
        }

        @Override
        public void endDocument() throws SAXException {}
    }
}
