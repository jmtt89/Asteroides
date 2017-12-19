package asteroides.example.org.asteroides.views.gameBoard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.customViews.GameView;

/**
 * Algunos cambios con respecto a la actividad, decidi hacer la comunicacion entre el GameView
 * y el Game mediante una interface que se implemente a nivel del activity, de esta manera podemos
 * saber cuando el juego terminar para terminar la actividad sin pasar explicitamente la actividad
 * a la vista.
 */

public class Game extends AppCompatActivity implements MissileListener, ScoreUpdateListener, FinishListener{
    private static final String TAG = "GAME";

    private int myScore;
    private long stoppedTime;
    private long elapsedTime;

    private int numMissiles;

    private GameView view;

    private View missileWrapper;
    private ImageView missileA;
    private ImageView missileB;
    private ImageView missileC;
    private TextView currentScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        view = findViewById(R.id.gameView);

        view.setMissileListener(this);
        view.setScoreUpdateListener(this);
        view.setFinishListener(this);

        myScore = 0;
        elapsedTime = System.currentTimeMillis();
        stoppedTime = 0;
        numMissiles = 0;

        missileWrapper = findViewById(R.id.missileWrapper);
        missileA = findViewById(R.id.missileA);
        missileB = findViewById(R.id.missileB);
        missileC = findViewById(R.id.missileC);
        currentScore = findViewById(R.id.currentScore);

    }

    @Override
    protected void onResume() {
        super.onResume();
        view.getThread().reanudar();
        if(stoppedTime>0){
            stoppedTime = System.currentTimeMillis() - stoppedTime;
        }
        view.activarSensores(this);
    }

    @Override
    protected void onPause() {
        view.getThread().pausar();
        view.desactivarSensores(this);
        stoppedTime += System.currentTimeMillis();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        view.getThread().detener();
        super.onDestroy();
    }

    @Override
    public void fireMissile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (numMissiles%3){
                    case 0:
                        missileA.setColorFilter(Color.GRAY);
                        break;
                    case 1:
                        missileB.setColorFilter(Color.GRAY);
                        break;
                    case 2:
                        missileC.setColorFilter(Color.GRAY);
                        break;
                }
                numMissiles++;
            }
        });
    }

    @Override
    public void dismissMissile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                numMissiles--;
                switch (numMissiles%3){
                    case 0:
                        missileA.setColorFilter(Color.WHITE);
                        break;
                    case 1:
                        missileB.setColorFilter(Color.WHITE);
                        break;
                    case 2:
                        missileC.setColorFilter(Color.WHITE);
                        break;
                }
            }
        });

    }

    @Override
    public void scoreUpdate(final int points) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myScore += points;
                currentScore.setText(String.format("%09d", myScore));
            }
        });

    }

    @Override
    public void onGameFinished() {
        Bundle bundle = new Bundle();
        bundle.putLong("elapsedTime", elapsedTime-stoppedTime);
        bundle.putInt("score", myScore);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}