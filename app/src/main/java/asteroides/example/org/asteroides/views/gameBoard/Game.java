package asteroides.example.org.asteroides.views.gameBoard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.customViews.GameView;

/**
 * Created by jmtt_ on 10/22/2017.
 */

public class Game extends AppCompatActivity {
    private GameView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        view = findViewById(R.id.gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.getThread().reanudar();
        view.activarSensores(this);
    }

    @Override
    protected void onPause() {
        view.getThread().pausar();
        view.desactivarSensores(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        view.getThread().detener();
        super.onDestroy();
    }
}