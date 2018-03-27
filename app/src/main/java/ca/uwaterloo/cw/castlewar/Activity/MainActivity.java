package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.content.Intent;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import ca.uwaterloo.cw.castlewar.Model.Potion;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.Model.UserProfile;
import ca.uwaterloo.cw.castlewar.R;

public class MainActivity extends AppCompatActivity{
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        shareDialog = new ShareDialog(this);
        try {
            SystemData.oneTimeThread.submit(new Runnable() {
                @Override
                public void run() {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    SystemData.setContext(getApplicationContext());
                    SystemData.initializeConfig(point.x, point.y);
                    UserProfile.readFromDatabase();
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        findViewById(R.id.SinglePlayer).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,null,1));
        findViewById(R.id.Shop).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,null,1));
        findViewById(R.id.Inventory).setBackground(SystemData.scaleDrawable(R.drawable.blue_button,null,null,1));

        printKeyHash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView title = findViewById(R.id.GameTitleImage);
        title.setBackground(SystemData.getRandomTitleBackground());
    }

    public void enterSinglePlayer(View view) {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, SinglePlayerActivity.class);

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
        Uri videoFileUri = Uri.parse("file://" +getCacheDir().getAbsolutePath() + "/gameplay.mp4");
        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoFileUri)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setContentTitle("My combat Videos")
                .setContentDescription("Time to show my true skill")
                .setVideo(video)
                .build();
        Log.e("I'm a potato","Your head looks like a tomato");
        if(shareDialog.canShow(ShareVideoContent.class)){
            shareDialog.show(content);
        }
    }
    private void printKeyHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ca.uwaterloo.cw.castlewar",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}