package ca.uwaterloo.cw.castlewar.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.Unit;
import ca.uwaterloo.cw.castlewar.R;


public class SinglePlayerActivity extends AppCompatActivity {
    private MultithreadGameLogic gameLogic = null;
    private final Handler handler = new Handler();
    private Bitmap levelBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get the RecyclerView instance
        RecyclerView levelsRecyclerView = findViewById(R.id.LevelsRecyclerView);

        // Set the LayoutManager to be vertical (default)
        levelsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Set the adapter which will fill the data on the RecyclerView items
        levelsRecyclerView.setAdapter(new LevelsRecyclerViewAdapter(this));
    }

    public void onBackPressed(View view)
    {
        super.onBackPressed();
    }

    public void startLevel(final Level level, final Unit[] unitInStockPlayer1, final Item[] itemInStockPlayer1)
    {
        setContentView(R.layout.game_screen);
        Thread initializer = new Thread(new Runnable() {
            @Override
            public void run() {
                gameLogic = new MultithreadGameLogic(SinglePlayerActivity.this, handler,level, unitInStockPlayer1, itemInStockPlayer1);
                gameLogic.onResume();
            }
        });
        initializer.start();
    }
    public void onResume()
    {
        super.onResume();
        if (gameLogic != null)
        {
            gameLogic.onResume();
        }
    }

    public void onPause()
    {
        super.onPause();
        if(gameLogic != null)
        {
            gameLogic.onPause();
        }
    }
}
