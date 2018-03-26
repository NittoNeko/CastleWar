package ca.uwaterloo.cw.castlewar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Base.User;
import ca.uwaterloo.cw.castlewar.Item.Item;
import ca.uwaterloo.cw.castlewar.Game.Level;
import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.Unit.Unit;
import ca.uwaterloo.cw.castlewar.R;


public class SinglePlayerActivity extends AppCompatActivity {
    private GameManager gameManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView imageView = findViewById(R.id.LevelBackground);
        imageView.setImageBitmap(System.scaleBitmap(R.drawable.plane_yellow, System.getScreenWidth(), System.getScreenHeight(),8));

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

    public void startLevel(final Level level, final Unit[] unitInStockPlayer1, final Item[] itemInStockPlayer1) {
        setContentView(R.layout.game_screen);
        System.oneTimeThread.execute(new Runnable() {
            @Override
            public void run() {
                gameManager = new GameManager(SinglePlayerActivity.this, level);
                gameManager.onFirstStart();
            }
        });


    }
    public void onResume()
    {
        super.onResume();
        if (gameManager != null)
            gameManager.onResume();
    }

    public void onPause()
    {
        super.onPause();
        if(gameManager != null)
            gameManager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (Unit unit : User.currentLawfuls()) {
            unit.getSprite().freeAll();
        }
        for (Unit unit : User.currentChaotics()) {
            unit.getSprite().freeAll();
        }
    }
}
