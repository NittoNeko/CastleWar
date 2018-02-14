package ca.uwaterloo.cw.castlewar;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangY on 2018-02-04.
 */

public class ShopItemsRecyclerViewAdapter extends RecyclerView.Adapter<ShopItemsRecyclerViewAdapter.ViewHolder>{
    private List<Pair<Pair<String, String>, String>> myShopItems = new ArrayList<>();
    private Context myContext;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemNameTextView;
        public TextView coinsTextView;
        public ImageView itemPictureImageView;

        // Construct the ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Construct the ViewAdapter
    public ShopItemsRecyclerViewAdapter(Context context, List<Pair<Pair<String, String>, String>> shopItems) {
        myShopItems = shopItems;
        myContext = context;
    }

    // Create new views
    @Override
    public ShopItemsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        ConstraintLayout shopItemView = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false);

        // Get references to view components
        TextView itemNameView = shopItemView.findViewById(R.id.itemName);
        TextView coinsView = shopItemView.findViewById(R.id.coins);
        ImageView itemPictureImageView = shopItemView.findViewById(R.id.itemPicture);

        // Create a new ViewHolder instance and assign references to it
        ShopItemsRecyclerViewAdapter.ViewHolder viewHolder = new ShopItemsRecyclerViewAdapter.ViewHolder(shopItemView);
        viewHolder.itemNameTextView = itemNameView;
        viewHolder.coinsTextView = coinsView;
        viewHolder.itemPictureImageView = itemPictureImageView;

        return viewHolder;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the pair to have its data displayed at position in the list
        Pair<Pair<String, String>, String> item = myShopItems.get(position);

        // Assign data to view components
        holder.itemNameTextView.setText(item.first.first);
        holder.coinsTextView.setText(item.first.second);

        // Load Picture to view components
        Picasso.with(myContext).load(item.second).into(holder.itemPictureImageView);

    }

    // Return the size of myShopItems
    @Override
    public int getItemCount() {
        return myShopItems.size();
    }
}
