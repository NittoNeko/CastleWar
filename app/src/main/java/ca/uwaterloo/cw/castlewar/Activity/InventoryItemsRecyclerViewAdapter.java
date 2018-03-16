
        package ca.uwaterloo.cw.castlewar.Activity;

        import android.content.Context;
        import android.support.constraint.ConstraintLayout;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import ca.uwaterloo.cw.castlewar.Model.GameObject;
        import ca.uwaterloo.cw.castlewar.Model.Item;
        import ca.uwaterloo.cw.castlewar.Model.SystemData;
        import ca.uwaterloo.cw.castlewar.Model.UserProfile;
        import ca.uwaterloo.cw.castlewar.R;



        import java.util.List;

/**
 * Created by Sparks on 2018-02-23.
 */

public class InventoryItemsRecyclerViewAdapter extends RecyclerView.Adapter<InventoryItemsRecyclerViewAdapter.ViewHolder>{
    private List<Item> myInventoryItems;
    private TextView myTextView;

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemNameTextView;
        public Button itemButton;
        public ImageView itemPictureImageView;
        public TextView itemDescriptionTextView;

        // Construct the ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Construct the ViewAdapter
    public InventoryItemsRecyclerViewAdapter(List<Item> inventoryItems, TextView textView) {
        myInventoryItems= inventoryItems;
        myTextView = textView;
    }

    // Create new views
    @Override
    public InventoryItemsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        ConstraintLayout inventoryItemView = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_item, parent, false);

        // Get references to view components
        TextView itemNameView = inventoryItemView.findViewById(R.id.itemName);
        TextView itemDescriptionTextView = inventoryItemView.findViewById(R.id.itemDescription);
        ImageView itemPictureImageView = inventoryItemView.findViewById(R.id.itemPicture);
        Button itemButton = inventoryItemView.findViewById(R.id.itemButton);
        // Create a new ViewHolder instance and assign references to it
        InventoryItemsRecyclerViewAdapter.ViewHolder viewHolder = new InventoryItemsRecyclerViewAdapter.ViewHolder(inventoryItemView);
        viewHolder.itemNameTextView = itemNameView;
        viewHolder.itemDescriptionTextView = itemDescriptionTextView;
        viewHolder.itemPictureImageView = itemPictureImageView;
        viewHolder.itemButton = itemButton;
        return viewHolder;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get the pair to have its data displayed at position in the list
        final Item item = myInventoryItems.get(position);
        final Context context = SystemData.getContext();
        final TextView myItemTextView = holder.itemNameTextView;


        // Assign data to view components
        holder.itemNameTextView.setText(item.getName() + " x " + UserProfile.getItemNum(item.getId()));
        holder.itemDescriptionTextView.setText(item.getDescription());
        holder.itemPictureImageView.setBackground(SystemData.scaleDrawable(R.drawable.item_background, SystemData.PIXEL,SystemData.PIXEL,1));
        holder.itemButton.setBackground(SystemData.scaleDrawable(R.drawable.yellow_button, SystemData.PIXEL,SystemData.PIXEL,2));
        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.Sell()){
                    Toast.makeText(context,"Item Sold", Toast.LENGTH_SHORT).show();
                    myItemTextView.setText(item.getName() + " x " + UserProfile.getItemNum(item.getId()));
                    long money = UserProfile.getCOIN().getNum();
                    myTextView.setText(Long.toString(money));
                } else {
                    Toast.makeText(context,"Not Enough Item" , Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Load Picture to view components
        holder.itemPictureImageView.setImageBitmap(item.getPortrait());

    }


    // Return the size of myInventoryItems
    @Override
    public int getItemCount() {
        return myInventoryItems.size();
    }
}