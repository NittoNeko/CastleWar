package ca.uwaterloo.cw.castlewar.Activity;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;

import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.concurrent.ExecutionException;

import ca.uwaterloo.cw.castlewar.Base.System;
import ca.uwaterloo.cw.castlewar.Base.User;
import ca.uwaterloo.cw.castlewar.R;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Handler handler = new Handler();
        try {
            System.oneTimeThread.submit(new Runnable() {
                @Override
                public void run() {
                    System.initialize(getWindowManager(), handler);
                    User.initialize();
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        findViewById(R.id.SinglePlayer).setBackground(System.scaleDrawable(R.drawable.blue_button,null,null,1));
        findViewById(R.id.MultiPlayer).setBackground(System.scaleDrawable(R.drawable.blue_button,null,null,1));
        findViewById(R.id.Shop).setBackground(System.scaleDrawable(R.drawable.blue_button,null,null,1));
        findViewById(R.id.Inventory).setBackground(System.scaleDrawable(R.drawable.blue_button,null,null,1));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView title = findViewById(R.id.GameTitleImage);
        title.setBackground(System.getRandomTitleBackground());
    }

    public void enterSinglePlayer(View view) {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, SinglePlayerActivity.class);

        // Show new screen
        startActivity(userIntent);
    }

    public void enterMultiPlayer(View view) {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, MultiPlayerActivity.class);

        // Show new screen
        startActivity(userIntent);
    }


    public void enterShop(View view)
    {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, ShopActivity.class);

        // Show new screen
        startActivity(userIntent);
    }

    public void enterInventory(View view) {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, InventoryActivity.class);

        // Show new screen
        startActivity(userIntent);
    }

    public void shareVideos(View view){
        ShareDialog shareDialog = new ShareDialog(this);
        Uri videoFileUri = Uri.parse("file://" +getCacheDir().getAbsolutePath() + "/gameplay.mp4");
        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoFileUri)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setContentTitle("My combat Videos")
                .setContentDescription("Time to show my true skill")
                .setVideo(video)
                .build();
        if(shareDialog.canShow(ShareVideoContent.class)){
            shareDialog.show(content);
        }
    }
}
