package asteroides.example.org.asteroides.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import asteroides.example.org.asteroides.Asteroids;
import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.database.ScoreDatabase;
import asteroides.example.org.asteroides.database.ScoreDatabaseArray;
import asteroides.example.org.asteroides.database.ScoreDatabaseAssetsResource;
import asteroides.example.org.asteroides.database.ScoreDatabaseExtAplMemory;
import asteroides.example.org.asteroides.database.ScoreDatabaseExternalMemory;
import asteroides.example.org.asteroides.database.ScoreDatabaseGson;
import asteroides.example.org.asteroides.database.ScoreDatabaseInternalMemory;
import asteroides.example.org.asteroides.database.ScoreDatabaseJson;
import asteroides.example.org.asteroides.database.ScoreDatabasePreferences;
import asteroides.example.org.asteroides.database.ScoreDatabaseProvider;
import asteroides.example.org.asteroides.database.ScoreDatabaseRawResource;
import asteroides.example.org.asteroides.database.ScoreDatabaseSQLite;
import asteroides.example.org.asteroides.database.ScoreDatabaseSQLiteRel;
import asteroides.example.org.asteroides.database.ScoreDatabaseSW_PHP;
import asteroides.example.org.asteroides.database.ScoreDatabaseSW_PHP_AsyncTask;
import asteroides.example.org.asteroides.database.ScoreDatabaseSocket;
import asteroides.example.org.asteroides.database.ScoreDatabaseXML_DOM;
import asteroides.example.org.asteroides.database.ScoreDatabaseXML_SAX;
import asteroides.example.org.asteroides.models.Asteroid;
import asteroides.example.org.asteroides.views.converter.ConverterActivity;
import asteroides.example.org.asteroides.views.gameBoard.Game;
import asteroides.example.org.asteroides.views.scoreList.ScoreListActivity;
import asteroides.example.org.asteroides.views.aboutUs.AboutUsActivity;
import asteroides.example.org.asteroides.views.settings.PreferencesActivity;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_GAME = 15;
    private static final int ACTIVITY_PREFERENCE = 16;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 45;

    private View scoreView;
    private TextView wonMessage;
    private EditText nickname;
    private Button wonButton;

    public static RequestQueue colaPeticiones;
    public static ImageLoader lectorImagenes;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("animationReady", true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseInitializer();
        findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAboutUs(v);
            }
        });

        if (savedInstanceState == null || !savedInstanceState.getBoolean("animationReady", false)) {
            TextView title = findViewById(R.id.main_title);
            Animation title_anim = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
            title.startAnimation(title_anim);
        }

        scoreView = findViewById(R.id.scoreView);
        wonMessage = findViewById(R.id.wonMessage);
        nickname = findViewById(R.id.userNickname);
        wonButton = findViewById(R.id.wonAccept);

        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });

        /*
        Button start = findViewById(R.id.btn_start);
        Animation start_anim = AnimationUtils.loadAnimation(this, R.anim.aparecer);
        start.startAnimation(start_anim);

        Button config = findViewById(R.id.btn_config);
        Animation config_anim = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
        config.startAnimation(config_anim);

        Button about = findViewById(R.id.btn_about);
        Animation about_anim = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_izquierda);
        about.startAnimation(about_anim);

        Button exit = findViewById(R.id.btn_exit);
        Animation exit_anim = AnimationUtils.loadAnimation(this, R.anim.desplazamineto_fondo);
        exit.startAnimation(exit_anim);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchPreferences(null);
            return true;
        }

        if (id == R.id.menu_about_us) {
            launchAboutUs(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void launchAboutUs(View view) {
        /*
        if(view != null){
            Animation title_anim = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
            view.startAnimation(title_anim);
        }
        */

        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void launchPreferences(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivityForResult(intent, ACTIVITY_PREFERENCE);
    }

    public void launchLastScores(View view) {
        Intent intent = new Intent(this, ScoreListActivity.class);
        startActivity(intent);
    }

    public void launchGame(View view) {
        Intent intent = new Intent(this, Game.class);
        startActivityForResult(intent, ACTIVITY_GAME);
    }

    public void launchConverter(View view) {
        Intent intent = new Intent(this, ConverterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_GAME && resultCode == RESULT_OK && data != null) {
            long elapsedTime = data.getExtras().getLong("elapsedTime");
            int score = data.getExtras().getInt("score");
            wonMessage(score, elapsedTime);
        } else if (requestCode == ACTIVITY_PREFERENCE) {
            databaseInitializer();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                ScoreDatabase database;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    database = new ScoreDatabaseExternalMemory(this);
                } else {
                    Toast.makeText(this, R.string.no_permission_external_storage, Toast.LENGTH_LONG).show();
                    database = new ScoreDatabasePreferences(this);
                }
                ((Asteroids) getApplication()).setDatabase(database);
                return;
            }
        }
    }

    private void wonMessage(final int myScore, final long elapsedTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreView.setVisibility(View.VISIBLE);
                int seconds = (int) ((System.currentTimeMillis() - elapsedTime) / 1000);
                final int finalScore = myScore; //+ myScore/seconds*10;
                wonMessage.setText(getResources().getString(R.string.txt_score, finalScore));
                wonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nickname.getText().toString();
                        ((Asteroids) getApplication())
                                .getDatabase()
                                .storeScore(finalScore, name, System.currentTimeMillis());
                        launchLastScores(null);
                        scoreView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void databaseInitializer() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int storageOption = Integer.parseInt(pref.getString("scoreStorage", "1"));
        ScoreDatabase database;
        switch (storageOption) {
            case 0:
                database = new ScoreDatabaseArray();
                break;
            case 2:
                database = new ScoreDatabaseInternalMemory(this);
                break;
            case 3:
                // Here, thisActivity is the current activity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    return;
                } else {
                    database = new ScoreDatabaseExternalMemory(this);
                }
                break;
            case 4:
                database = new ScoreDatabaseExtAplMemory(this);
                break;
            case 5:
                database = new ScoreDatabaseRawResource(this);
                break;
            case 6:
                database = new ScoreDatabaseAssetsResource(this);
                break;
            case 7:
                database = new ScoreDatabaseXML_SAX(this);
                break;
            case 8:
                database = new ScoreDatabaseXML_DOM(this);
                break;
            case 9:
                database = new ScoreDatabaseGson(this);
                break;
            case 10:
                database = new ScoreDatabaseJson(this);
                break;
            case 11:
                database = new ScoreDatabaseSQLite(this);
                break;
            case 12:
                database = new ScoreDatabaseSQLiteRel(this);
                break;
            case 13:
                database = new ScoreDatabaseProvider(this);
                break;
            case 14:
                database = new ScoreDatabaseSocket(this);
                break;
            case 15:
                database = new ScoreDatabaseSW_PHP(this);
                break;
            case 16:
                database = new ScoreDatabaseSW_PHP_AsyncTask(this);
                break;
            default:
                database = new ScoreDatabasePreferences(this);
                break;
        }
        ((Asteroids) getApplication()).setDatabase(database);
    }
}
