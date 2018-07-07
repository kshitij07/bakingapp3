package com.example.android.bakingapp5.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp5.R;
import com.example.android.bakingapp5.RecipesData.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesMainAdapter extends RecyclerView.Adapter<RecipesMainAdapter.RecipeViewHolder> {

    private static final String RECIPES_IMAGE_RESOURCE_PREFIX = "recipe";
    private Context mContext;
    private ArrayList<Recipe> mRecipesArray;
    private final RecipeAdapterOnClickHandler mRecipeOnClickHandler;

    public RecipesMainAdapter(Context context, ArrayList<Recipe> recipesArray,
                              RecipeAdapterOnClickHandler recipeAdapterOnClickHandler) {
        mContext = context;
        mRecipesArray = recipesArray;
        mRecipeOnClickHandler = recipeAdapterOnClickHandler;
    }

    @Override
    public RecipesMainAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutIdItem = R.layout.recipe_item;
        boolean shouldAttachToParentImmediately = false;

        LinearLayout view = (LinearLayout) layoutInflater.inflate(layoutIdItem, parent, shouldAttachToParentImmediately);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesMainAdapter.RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipesArray.get(position);
        holder.setOnClickListener(holder.mRecipeCompleteView, recipe);
        String recipeServings = Integer.toString(recipe.getRecipeServings());
        String recipeName = recipe.getRecipeName();
        holder.mRecipeServingsView.setText(recipeServings);
        holder.mRecipesNameView.setText(recipeName);
        setImageResource(recipe, holder);
    }

    @Override
    public int getItemCount() {
        return mRecipesArray.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipe_item_name)
        TextView mRecipesNameView;
        @BindView(R.id.recipe_item_image)
        ImageView mRecipeImageView;
        @BindView(R.id.recipe_item_servings)
        TextView mRecipeServingsView;
        private LinearLayout mRecipeCompleteView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mRecipeCompleteView = (LinearLayout) itemView;
            ButterKnife.bind(this, mRecipeCompleteView);
        }

        private void setOnClickListener(LinearLayout recipeView, final Recipe recipe) {
            recipeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRecipeOnClickHandler.onClick(recipe);
                }
            });
        }
    }

    private void setImageResource(Recipe recipe, RecipeViewHolder recipeViewHolder) {
        if (recipe.getRecipeImage().substring(0, 6).equals(RECIPES_IMAGE_RESOURCE_PREFIX)) {
            int resourceId = mContext.getResources().getIdentifier(recipe.getRecipeImage(), "drawable", mContext.getPackageName());
            recipeViewHolder.mRecipeImageView.setImageResource(resourceId);
        } else {
            Picasso.with(mContext)
                    .load(recipe.getRecipeImage())
                    .placeholder(R.drawable.chef)
                    .error(R.drawable.chef)
                    .into(recipeViewHolder.mRecipeImageView);
        }
        recipeViewHolder.mRecipeImageView.setTag(recipe.getRecipeImage());
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }
}
