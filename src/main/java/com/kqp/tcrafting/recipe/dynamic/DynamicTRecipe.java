package com.kqp.tcrafting.recipe.dynamic;

import com.kqp.tcrafting.recipe.data.ComparableItemStack;
import com.kqp.tcrafting.recipe.data.TRecipe;

import java.util.List;
import java.util.Map;

public interface DynamicTRecipe {
    List<TRecipe> getPossibleRecipes(Map<ComparableItemStack, Integer> inputMap);
}
