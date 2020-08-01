package com.kqp.tcrafting.api;

import com.kqp.tcrafting.recipe.data.ComparableItemStack;
import com.kqp.tcrafting.recipe.data.TRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface DynamicRecipe {
    List<TRecipe> getPossibleRecipes(PlayerEntity player, Set<Identifier> availableRecipeTypes, Map<ComparableItemStack, Integer> inputMap);
}
