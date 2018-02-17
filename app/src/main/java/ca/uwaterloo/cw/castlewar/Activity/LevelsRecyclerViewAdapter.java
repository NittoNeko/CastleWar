package ca.uwaterloo.cw.castlewar.Activity;

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

import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.R;

public class LevelsRecyclerViewAdapter extends RecyclerView.Adapter<LevelsRecyclerViewAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView levelTextView;
        public TextView terrainTextView;
        public TextView enemiesTextView;
        public TextView rewardsTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    // Construct the ViewAdapter
    public LevelsRecyclerViewAdapter() {}

    // Create new views
    @Override
    public LevelsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
        ConstraintLayout levelsView = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.levels, parent, false);

        // Get references to view components
        TextView levelTextView = levelsView.findViewById(R.id.Levels);
        TextView terrainTextVie = levelsView.findViewById(R.id.Terrain);
        TextView enemiesTextView = levelsView.findViewById(R.id.Enemies);
        TextView rewardsTextView = levelsView.findViewById(R.id.Rewards);

        // Create a new ViewHolder instance and assign references to it
        LevelsRecyclerViewAdapter.ViewHolder viewHolder = new LevelsRecyclerViewAdapter.ViewHolder(levelsView);
        viewHolder.levelTextView = levelTextView;
        viewHolder.terrainTextView = terrainTextVie;
        viewHolder.enemiesTextView = enemiesTextView;
        viewHolder.rewardsTextView = rewardsTextView;

        return viewHolder;
    }

    // Replace the contents of a view
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Level level = SystemData.getLevel(position);

        // Assign data to view components
        holder.levelTextView.setText(level.getName());
        holder.terrainTextView.setText(level.getTerrain().getName());
        holder.enemiesTextView.setText(level.getDisplayableEnemies());
        holder.rewardsTextView.setText(level.getDisplayableRewards());

    }

    // Return the size of myShopItems
    @Override
    public int getItemCount() {
        return SystemData.LEVEL_NUM;
    }
}
