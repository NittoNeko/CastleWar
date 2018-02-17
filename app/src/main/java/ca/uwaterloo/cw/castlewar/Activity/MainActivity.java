package ca.uwaterloo.cw.castlewar.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.Window;

import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;

public class MainActivity extends AppCompatActivity implements Runnable{

    private Thread worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        worker = new Thread(this);
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

    }

    public void run()
    {
        System.out.println("With thread," + SystemData.getSize());
        SystemData.initializeBitmap(getApplicationContext());
        SystemData.initializeData();
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
        Intent userIntent = new Intent(this, ShopAcitivity.class);

        // Show new screen
        startActivity(userIntent);
    }

    /*public void enterInventory(View view)
    {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, InventoryActivity.class);

        // Show new screen
        startActivity(userIntent);
    }*/
}
