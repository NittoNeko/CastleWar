package ca.uwaterloo.cw.castlewar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enterSinglePlayer(View view)
    {
        // Create an intent to show a new screen passing data to it
        Intent userIntent = new Intent(this, PrepareActivity.class);

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

    public void enterInventory(View view)
    {
        // Create an intent to show a new screen passing data to it
        Intent userSearchIntent = new Intent(this, InventoryActivity.class);

        // Show new screen
        startActivity(userIntent);
    }
}
