package ca.uwaterloo.cw.castlewar.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;


public class SinglePlayerActivity extends AppCompatActivity {
    private MultithreadGameLogic gameLogic = null;
    private final Handler handler = new Handler();

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

    public void startLevel(int levelId)
    {
        setContentView(R.layout.game_screen);

        gameLogic = new MultithreadGameLogic(this, handler,SystemData.getLevel(levelId));
        gameLogic.onResume();
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
