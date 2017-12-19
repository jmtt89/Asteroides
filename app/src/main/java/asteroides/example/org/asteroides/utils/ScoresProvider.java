package asteroides.example.org.asteroides.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/*
 * El provider lo cree exatamente con los mismos nombres y estructuras por si hay algun tipo de pruebas
 * sobre consultar el provider desde alguna aplicacion foranea
 */
public class ScoresProvider extends ContentProvider {
    public static final String AUTORIDAD = "org.example.puntuacionesprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTORIDAD + "/puntuaciones");
    public static final int TODOS_LOS_ELEMENTOS = 1;
    public static final int UN_ELEMENTO = 2;
    private static UriMatcher URI_MATCHER = null;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTORIDAD, "puntuaciones", TODOS_LOS_ELEMENTOS);
        URI_MATCHER.addURI(AUTORIDAD, "puntuaciones/#", UN_ELEMENTO);
    }

    public static final String TABLA = "puntuaciones";
    private SQLiteDatabase baseDeDatos;

    @Override
    public boolean onCreate() {
        ScoresSQLiteHelper dbHelper = new ScoresSQLiteHelper(getContext());
        baseDeDatos = dbHelper.getWritableDatabase();
        return baseDeDatos != null && baseDeDatos.isOpen();
    }

    @Override
    public String getType(final Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case TODOS_LOS_ELEMENTOS:
                return "vnd.android.cursor.dir/vnd.org.example.puntuacion";
            case UN_ELEMENTO:
                return "vnd.android.cursor.item/vnd.org.example.puntuacion";
            default:
                throw new IllegalArgumentException("URI incorrecta: " + uri);
        }
    }

    @Override public Cursor query(Uri uri, String[] proyeccion, String
            seleccion, String[] argSeleccion, String orden) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLA);
        switch (URI_MATCHER.match(uri)) {
            case TODOS_LOS_ELEMENTOS:
                break;
            case UN_ELEMENTO:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere("_id = " + id);
                break;
            default:
                throw new IllegalArgumentException("URI incorrecta "+uri);
        }
        return queryBuilder.query(baseDeDatos, proyeccion, seleccion,
                argSeleccion, null, null, orden);
    }

    @Override public Uri insert(Uri uri, ContentValues valores) {
        long IdFila = baseDeDatos.insert(TABLA, null, valores);
        if (IdFila > 0) {
            return ContentUris.withAppendedId(CONTENT_URI, IdFila);
        } else {
            throw new SQLException("Error al insertar registro en "+uri);
        }
    }

    @Override public int update(Uri uri, ContentValues valores, String
            seleccion, String[] ArgumentosSeleccion) {
        switch (URI_MATCHER.match(uri)) {
            case TODOS_LOS_ELEMENTOS:
                break;
            case UN_ELEMENTO:
                String id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(seleccion)) {
                    seleccion = "_id = " + id;
                } else {
                    seleccion = "_id = " + id + " AND (" + seleccion + ")";
                }
                break;
            default:
                throw new IllegalArgumentException("URI incorrecta: " + uri);
        }
        return baseDeDatos.update(TABLA, valores, seleccion,
                ArgumentosSeleccion);
    }

    @Override
    public int delete(Uri uri, String seleccion, String[] argSeleccion) {
        switch (URI_MATCHER.match(uri)) {
            case TODOS_LOS_ELEMENTOS:
                break;
            case UN_ELEMENTO:
                String id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(seleccion)) {
                    seleccion = "_id = " + id;
                } else {
                    seleccion = "_id = " + id + " AND (" + seleccion + ")";
                }
                break;
            default:
                throw new IllegalArgumentException("URI incorrecta: " + uri);
        }
        return baseDeDatos.delete(TABLA, seleccion, argSeleccion);
    }
}