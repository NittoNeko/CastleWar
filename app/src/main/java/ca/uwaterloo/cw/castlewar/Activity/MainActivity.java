package ca.uwaterloo.cw.castlewar.Activity;

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
            SystemData.initializeBitmap(getApplicationContext());
            SystemData.initializeData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        TextView shopOption = findViewById(R.id.Shop);
        TextView singleOption = findViewById(R.id.SinglePlayer);
        TextView inventoryOption = findViewById(R.id.Inventory);
        ProgressBar loading = findViewById(R.id.InitializationBar);

        loading.setVisibility(View.VISIBLE);
        shopOption.setVisibility(View.INVISIBLE);
        inventoryOption.setVisibility(View.INVISIBLE);
        singleOption.setVisibility(View.INVISIBLE);

        Initializer init = new Initializer();
        init.start();
        try {
            init.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loading.setVisibility(View.INVISIBLE);
        shopOption.setVisibility(View.VISIBLE);
        inventoryOption.setVisibility(View.VISIBLE);
        singleOption.setVisibility(View.VISIBLE);
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
