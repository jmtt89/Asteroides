package asteroides.example.org.asteroides.views.gameBoard;

import android.content.DialogInterface;
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
import asteroides.example.org.asteroides.database.ScoreDatabase;
import asteroides.example.org.asteroides.database.ScoreDatabasePreferences;

/**
 * Created by jmtt_ on 10/22/2017.
 */

public class Game extends AppCompatActivity implements MissileListener, ScoreUpdateListener, FinishListener{
    private static final String TAG = "GAME";

    ScoreDatabase database;

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

    private View wonView;
    private TextView wonMessage;
    private EditText nickname;
    private Button wonButton;

    private View looseView;
    private TextView looseMessage;
    private Button looseButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        database = new ScoreDatabasePreferences(getApplicationContext());

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

        wonView = findViewById(R.id.wonWrapper);
        wonMessage = findViewById(R.id.wonMessage);
        nickname = findViewById(R.id.userNickname);
        wonButton = findViewById(R.id.wonAccept);

        looseView = findViewById(R.id.looseWrapper);
        looseMessage = findViewById(R.id.looseMessage);
        looseButton = findViewById(R.id.looseAccept);
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
    public void wonMessage() {
        view.getThread().detener();
        view.desactivarSensores(getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wonView.setVisibility(View.VISIBLE);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                int seconds = (int) ((elapsedTime-stoppedTime) / 1000);
                final int finalScore = myScore + myScore/seconds*10;
                wonMessage.setText(getResources().getString(R.string.txt_score, finalScore));
                Log.d(TAG, "elapsed:"+ elapsedTime +" stopped: "+stoppedTime +" seconds: "+seconds);

                wonButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nickname.getText().toString();
                        Log.d(TAG, "nickname: "+nickname.getText());
                        Log.d(TAG, "SCORE: "+ name + " " + finalScore);
                        database.storeScore(finalScore, name, System.currentTimeMillis());
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void losseMessage() {
        view.getThread().detener();
        view.desactivarSensores(getApplicationContext());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                int seconds = (int) ((elapsedTime-stoppedTime) / 1000);
                int finalScore = myScore + myScore/seconds*10;
                looseMessage.setText(getResources().getString(R.string.txt_score, finalScore));

                looseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });
    }
}