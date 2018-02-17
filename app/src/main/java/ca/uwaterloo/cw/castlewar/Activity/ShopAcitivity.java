package ca.uwaterloo.cw.castlewar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ca.uwaterloo.cw.castlewar.R;

/**
 * Created by WangY on 2018-02-04.
 */

public class ShopAcitivity extends AppCompatActivity{

    private ProgressBar progressBar;
    private List<Pair<Pair<String, String>, String>> shopItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Private reference to the progress bar
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Show the status before the start
        progressBar.setVisibility(View.VISIBLE);

        // Initialize items in the shop and add them to the ArrayList
        shopItems.add(new Pair <> (new Pair <> ("A", "$100"), "file:///android_asset/ic_launcher.png"));
        shopItems.add(new Pair <> (new Pair <> ("B", "$100"), "file:///android_asset/ic_launcher.png"));
        shopItems.add(new Pair <> (new Pair <> ("C", "$100"), "file:///android_asset/ic_launcher.png"));
        shopItems.add(new Pair <> (new Pair <> ("D", "$100"), "file:///android_asset/ic_launcher.png"));
        shopItems.add(new Pair <> (new Pair <> ("E", "$100"), "file:///android_asset/ic_launcher.png"));

        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.shopItemsRecyclerView);

        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter myAdapter = new ShopItemsRecyclerViewAdapter(getApplicationContext(), shopItems);
        myRecyclerView.setAdapter(myAdapter);


        // Hide the progress bar when all items are presented
        progressBar.setVisibility(View.INVISIBLE);

    }
}
