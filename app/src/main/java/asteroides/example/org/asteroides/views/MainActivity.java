package asteroides.example.org.asteroides.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
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

        TextView title = findViewById(R.id.main_title);
        Animation title_anim = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
        title.startAnimation(title_anim);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem mSearchItem = menu.findItem(R.id.menu_search);

        mSearchItem.setOnActionExpandListener( new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "onMenuItemActionExpand", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "onMenuItemActionExpand", Toast.LENGTH_SHORT)
                        .show();
                return true;

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchPreferences(null);
            return true;
        }

        if (id == R.id.action_show_preferences){
            showPreferences(null);
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
        if(view != null){
            Animation title_anim = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
            view.startAnimation(title_anim);
        }

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
