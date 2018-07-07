package com.example.android.bakingapp5.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp5.R;
import com.example.android.bakingapp5.RecipesData.Ingredient;
import com.example.android.bakingapp5.Utils.RecipeDataUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsMainAdapter extends RecyclerView.Adapter<IngredientsMainAdapter.IngredientViewHolder> {

    private Context mContext;
    private ArrayList<Ingredient> mIngredientsList;

    public IngredientsMainAdapter(Context context, ArrayList<Ingredient> ingredientsList) {
        mContext = context;
        mIngredientsList = ingredientsList;
    }

    @Override
    public IngredientsMainAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutIdItem = R.layout.ingredient_item;
        boolean shouldAttachToParentImmediately = false;

        LinearLayout view = (LinearLayout) layoutInflater.inflate(layoutIdItem, parent, shouldAttachToParentImmediately);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsMainAdapter.IngredientViewHolder holder, int position) {
        Ingredient ingredient = mIngredientsList.get(position);
        String ingredientNumber = Integer.toString(position + 1);
        String ingredientName = RecipeDataUtils.capitalizeString(ingredient.getIngredientName());
        String ingredientFormattedQuantityUnit = ingredient.getIngredientQuantity() + " " + ingredient.getIngredientUnit();
        holder.mIngredientNumberView.setText(ingredientNumber);
        holder.mIngredientNameView.setText(ingredientName);
        holder.mIngredientQuantityUnitView.setText(ingredientFormattedQuantityUnit);
    }

    @Override
    public int getItemCount() {
        return mIngredientsList.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_item_number)
        TextView mIngredientNumberView;
        @BindView(R.id.ingredient_item_name)
        TextView mIngredientNameView;
        @BindView(R.id.ingredient_item_quantity_unit)
        TextView mIngredientQuantityUnitView;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
