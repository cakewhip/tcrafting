package com.kqp.tcrafting.recipe.interf;

import com.kqp.tcrafting.recipe.TRecipeManager;

public interface TRecipeManagerContainer {
    TRecipeManager getTCraftingRecipeManager();

    void setTCraftingRecipeManager(TRecipeManager tRecipeManager);
}
