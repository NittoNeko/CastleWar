package ca.uwaterloo.cw.castlewar.Activity;

/**
 * Created by Sparks on 2018-02-23.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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


public class ShopActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private List<Item> shopItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        // Initialize items in the shop and add them to the ArrayList
        for (Id.Item item : Id.Item.values()){
            shopItems.add(SystemData.createItem(item.ordinal()));
        }

        // Private reference to the progress bar
        progressBar = findViewById(R.id.progressBar);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Show the status before the start
        progressBar.setVisibility(View.VISIBLE);

        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.shopItemsRecyclerView);
        // Get the TextView
        TextView myTextView = findViewById(R.id.coinNum);
        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter myAdapter = new ShopItemsRecyclerViewAdapter(shopItems);
        myRecyclerView.setAdapter(myAdapter);



        // Hide the progress bar when all items are presented
        progressBar.setVisibility(View.INVISIBLE);

    }
}