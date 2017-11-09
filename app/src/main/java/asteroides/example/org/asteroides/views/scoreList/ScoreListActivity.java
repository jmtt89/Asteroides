package asteroides.example.org.asteroides.views.scoreList;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import asteroides.example.org.asteroides.R;
import asteroides.example.org.asteroides.database.ScoreDatabase;
import asteroides.example.org.asteroides.database.ScoreDatabasePreferences;
import asteroides.example.org.asteroides.views.MainActivity;

/**
 * Created by jmtt_ on 10/21/2017.
 *
 */

public class ScoreListActivity extends AppCompatActivity {
    private RecyclerView list;
    private RecyclerView.LayoutManager layoutManager;
    private ScoresAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_list);

        // setup layout manager
        layoutManager = new LinearLayoutManager(this);

        // setup Adapter
        ScoreDatabase database = new ScoreDatabasePreferences(getApplicationContext());
        List<String> mList = database.listScores(10);
        adapter = new ScoresAdapter(mList);

        // setup RecyclerView
        list = findViewById(R.id.scores);
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

        if(mList.size() == 0){
            findViewById(R.id.noScore).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
