package ca.uwaterloo.cw.castlewar.Activity;

/**
 * Created by Sparks on 2018-02-23.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ca.uwaterloo.cw.castlewar.Model.GameObject;
import ca.uwaterloo.cw.castlewar.Model.Id;
import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.Model.UserProfile;
import ca.uwaterloo.cw.castlewar.R;

import java.util.ArrayList;
import java.util.List;


public class InventoryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private List<Item> inventoryItems = new ArrayList<>();
    private long myCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        // Initialize items in the shop and add them to the ArrayList
        for (Id.Item item : Id.Item.values()){
            inventoryItems.add(SystemData.createItem(item.ordinal()));
        }

        // Construct User Profile to get amount of coins
        myCoins = UserProfile.getCOIN().getNum();

        // Private reference to the progress bar
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Show the status before the start
        progressBar.setVisibility(View.VISIBLE);

        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.inventoryItemsRecyclerView);

        // Get TextView
        TextView myTextView = findViewById(R.id.coinNum);
        myTextView.setText(Long.toString(myCoins));

        // Get the ImageView
        ImageView myImageView = (ImageView) findViewById(R.id.imageView);

        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter myAdapter = new InventoryItemsRecyclerViewAdapter(inventoryItems);
        myRecyclerView.setAdapter(myAdapter);

        // Hide the progress bar when all items are presented
        progressBar.setVisibility(View.INVISIBLE);

    }
}