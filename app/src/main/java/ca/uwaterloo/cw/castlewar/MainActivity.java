package ca.uwaterloo.cw.castlewar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*public void enterSinglePlayer(View view)
    {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, PrepareActivity.class);

        // Show new screen
        startActivity(userIntent);
    }*/

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
