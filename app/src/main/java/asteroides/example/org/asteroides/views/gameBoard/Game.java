package asteroides.example.org.asteroides.views.gameBoard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import asteroides.example.org.asteroides.R;

/**
 * Created by jmtt_ on 10/22/2017.
 */

public class Game extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
