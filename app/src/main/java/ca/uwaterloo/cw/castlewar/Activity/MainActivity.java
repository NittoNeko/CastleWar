package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;

public class MainActivity extends AppCompatActivity{
    private class Initializer extends Thread
    {
        public void run()
        {
            SystemData.initializeConfig(MainActivity.this);
            SystemData.initializeBitmap(getApplicationContext().getResources());
            SystemData.initializeData();
            SystemData.setIfOutput(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initializer init = new Initializer();
        init.start();
        try {
            init.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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