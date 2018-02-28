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
import android.widget.ProgressBar;

import ca.uwaterloo.cw.castlewar.Model.GameObject;
import ca.uwaterloo.cw.castlewar.Model.Id;
import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;

import java.util.ArrayList;
import java.util.List;


public class InventoryActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private List<Item> inventoryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Private reference to the progress bar
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Show the status before the start
        progressBar.setVisibility(View.VISIBLE);


        // Initialize items in the shop and add them to the ArrayList
        for (int i = Id._START.ordinal() + 1; i < Id.POTION_END.ordinal(); ++i) {
            inventoryItems.add((Item) SystemData.create(Id.values()[i]));
        }


        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.inventoryItemsRecyclerView);

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