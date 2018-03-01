package ca.uwaterloo.cw.castlewar.Activity;

import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uwaterloo.cw.castlewar.Model.Ally;
import ca.uwaterloo.cw.castlewar.Model.Id;
import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.Potion;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.Model.Unit;
import ca.uwaterloo.cw.castlewar.Model.UserProfile;
import ca.uwaterloo.cw.castlewar.R;

public class LevelsRecyclerViewAdapter extends RecyclerView.Adapter<LevelsRecyclerViewAdapter.ViewHolder>{
    private final SinglePlayerActivity singlePlayerActivity;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView levelTextView;
        public TextView terrainTextView;
        public TextView enemiesTextView;
        public TextView rewardsTextView;
        public ImageButton startButton;
        public CardView backGround;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    // Construct the ViewAdapter
    public LevelsRecyclerViewAdapter(SinglePlayerActivity singlePlayerActivity) {
        this.singlePlayerActivity = singlePlayerActivity;
    }

    // Create new views
    @Override
    public LevelsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        ConstraintLayout levelsView = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.levels, parent, false);

        // Get references to view components
        TextView levelTextView = levelsView.findViewById(R.id.itemName);
        TextView terrainTextView = levelsView.findViewById(R.id.Terrain);
        TextView enemiesTextView = levelsView.findViewById(R.id.Enemies);
        TextView rewardsTextView = levelsView.findViewById(R.id.Rewards);
        ImageButton startButton = levelsView.findViewById(R.id.GoButton);
        CardView background = levelsView.findViewById(R.id.LevelBackground);

        // Create a new ViewHolder instance and assign references to it
        LevelsRecyclerViewAdapter.ViewHolder viewHolder = new LevelsRecyclerViewAdapter.ViewHolder(levelsView);
        viewHolder.levelTextView = levelTextView;
        viewHolder.terrainTextView = terrainTextView;
        viewHolder.enemiesTextView = enemiesTextView;
        viewHolder.rewardsTextView = rewardsTextView;
        viewHolder.startButton = startButton;
        viewHolder.backGround = background;

        return viewHolder;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final int freshPosition = holder.getAdapterPosition();
        final Level level = SystemData.createLevel(freshPosition);

        // Assign data to view components
        holder.levelTextView.setText(level.getName());
        holder.terrainTextView.setText(level.getDisplayableTerrain());
        holder.enemiesTextView.setText(level.getDisplayableEnemies());
        holder.rewardsTextView.setText(level.getDisplayableRewards());
        holder.startButton.setBackground(SystemData.scaleDrawable(R.drawable.button_right, SystemData.PIXEL, SystemData.PIXEL));
        holder.backGround.setBackground(SystemData.scaleDrawable(R.drawable.blue_button, SystemData.getScreenWidth(), SystemData.getScreenHeight() / 4));
        holder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singlePlayerActivity.startLevel(level,
                        new Unit[]{new Ally.SwordMan(), new Ally.Mage(), new Ally.Archer()},
                        new Item[]{new Potion.DefensePotion(), new Potion.HpPotion(), new Potion.AttackPotion(), new Potion.SpeedPotion()});
            }
        });
    }

    // Return the size of myShopItems
    @Override
    public int getItemCount() {
        return UserProfile.getAvailableLevelNum();
    }
}
