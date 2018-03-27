
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

        import ca.uwaterloo.cw.castlewar.Model.Coin;
        import ca.uwaterloo.cw.castlewar.Model.GameObject;
        import ca.uwaterloo.cw.castlewar.Model.Id;
        import ca.uwaterloo.cw.castlewar.Model.Item;
        import ca.uwaterloo.cw.castlewar.Model.SystemData;
        import ca.uwaterloo.cw.castlewar.Model.UserProfile;
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
        for (Id.Item item : Id.Item.values()){
            inventoryItems.add(SystemData.createItem(item.ordinal()));
        }
        findViewById(R.id.imageView3).setBackground(SystemData.scaleDrawable(R.drawable.background_night_forest, null, SystemData.getScreenHeight(), 2));
        findViewById(R.id.coinImageView).setBackground(SystemData.scaleDrawable(R.drawable.gold_coin, SystemData.PIXEL, SystemData.PIXEL, 2));
    }


    @Override
    protected void onStart() {
        super.onStart();

        // Get the RecyclerView instance
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.inventoryItemsRecyclerView);

        // Get the TextView
        TextView myTextView = findViewById(R.id.coinNum);
        long money = UserProfile.getCOIN().getNum();
        myTextView.setText(Long.toString(money));

        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter myAdapter = new InventoryItemsRecyclerViewAdapter(inventoryItems, myTextView);
        myRecyclerView.setAdapter(myAdapter);

        // Hide the progress bar when all items are presented

    }
}