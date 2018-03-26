
        package ca.uwaterloo.cw.castlewar.Activity;

/**
 * Created by Sparks on 2018-02-23.
 */

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.widget.TextView;

        import ca.uwaterloo.cw.castlewar.Item.Potion;
        import ca.uwaterloo.cw.castlewar.Structure.Id;
        import ca.uwaterloo.cw.castlewar.Item.Item;
        import ca.uwaterloo.cw.castlewar.Base.System;
        import ca.uwaterloo.cw.castlewar.Base.User;
        import ca.uwaterloo.cw.castlewar.R;

        import java.util.ArrayList;
        import java.util.List;


public class InventoryActivity extends AppCompatActivity {

    private List<Item> inventoryItems = new ArrayList<>();
    private long myCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        // Initialize items in the inventory and add them to the ArrayList
        inventoryItems = Potion.getAllPotion();

        findViewById(R.id.imageView3).setBackground(System.scaleDrawable(R.drawable.background_night_forest, null, System.getScreenHeight(), 2));
        findViewById(R.id.coinImageView).setBackground(System.scaleDrawable(R.drawable.gold_coin, 200, 200, 2));
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.inventoryItemsRecyclerView);

        // Get the TextView
        TextView myTextView = findViewById(R.id.coinNum);
        long money = User.getCOIN().getNum();
        myTextView.setText(Long.toString(money));

        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter myAdapter = new InventoryItemsRecyclerViewAdapter(inventoryItems, myTextView);
        myRecyclerView.setAdapter(myAdapter);
    }
}