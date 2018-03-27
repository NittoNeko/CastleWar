package ca.uwaterloo.cw.castlewar.Activity;

/**
 * Created by harri on 2018/3/27.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Base.User;
import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.Game.Level;
import ca.uwaterloo.cw.castlewar.Game.Terrain;
import ca.uwaterloo.cw.castlewar.Item.Item;
import ca.uwaterloo.cw.castlewar.R;
import ca.uwaterloo.cw.castlewar.Structure.Id;
import ca.uwaterloo.cw.castlewar.Unit.Unit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Base.User;
import ca.uwaterloo.cw.castlewar.Item.Item;
import ca.uwaterloo.cw.castlewar.Game.Level;
import ca.uwaterloo.cw.castlewar.Game.GameManager;
import ca.uwaterloo.cw.castlewar.Unit.Unit;
import ca.uwaterloo.cw.castlewar.R;


public class MultiPlayerActivity extends AppCompatActivity {
    private GameManager gameManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Random random = new Random();
        setContentView(R.layout.game_screen);
        ((ImageView) findViewById(R.id.GameLoading)).setImageBitmap(System.scaleBitmap(R.drawable.game_loading, 500, null, 1));
        final int player1 = random.nextInt(2);
        final int player2 = random.nextInt(2);
        System.oneTimeThread.execute(new Runnable() {
            @Override
            public void run() {
                gameManager = new GameManager(MultiPlayerActivity.this, new Terrain.Forest(), Id.Castle.values()[player1], Id.Castle.values()[player2]);
                gameManager.onFirstStart();
            }
        });
    }

    public void onBackPressed(View view)
    {
        super.onBackPressed();
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

