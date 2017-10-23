package asteroides.example.org.asteroides.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.database.ScoreDatabase;
import asteroides.example.org.asteroides.database.ScoreDatabaseArray;
import asteroides.example.org.asteroides.views.gameBoard.Game;
import asteroides.example.org.asteroides.views.scoreList.ScoreListActivity;
import asteroides.example.org.asteroides.views.aboutUs.AboutUsActivity;
import asteroides.example.org.asteroides.views.settings.PreferencesActivity;

public class MainActivity extends AppCompatActivity {

    public static ScoreDatabase database = new ScoreDatabaseArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAboutUs(v);
            }
        });
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

    public void showPreferences(View view) {
        Resources resources = getResources();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String message = String.format("%s: %b, %s: %s, %s: %s, %s: %b, %s: %s, %s: %s",
                resources.getString(R.string.has_music_title),
                pref.getBoolean("hasMusic", true),
                resources.getString(R.string.graphic_type_title),
                resources.getStringArray(R.array.tiposGraficos)[Integer.parseInt(pref.getString("graphicType", "1"))],
                resources.getString(R.string.asteroids_fragments_title),
                pref.getString("fragments", "3"),
                resources.getString(R.string.has_multiplayer_title),
                pref.getBoolean("hasMultiplayer", false),
                resources.getString(R.string.max_players_title),
                pref.getString("maxPlayers", "2"),
                resources.getString(R.string.connexion_type_title),
                resources.getStringArray(R.array.connectionTypes)[Integer.parseInt(pref.getString("connexionType", "0"))]
                );

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void launchAboutUs(View view) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void launchPreferences(View view) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void launchLastScores(View view) {
        Intent intent = new Intent(this, ScoreListActivity.class);
        startActivity(intent);
    }

    public void launchGame(View view) {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }
}
