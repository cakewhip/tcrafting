package com.kqp.tcrafting.recipe.dynamic;

import com.kqp.tcrafting.api.DynamicRecipe;
import com.kqp.tcrafting.recipe.data.ComparableItemStack;
import com.kqp.tcrafting.recipe.data.TRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepairToolsDynamicRecipe implements DynamicRecipe {
    @Override
    public List<TRecipe> getPossibleRecipes(PlayerEntity player, Set<Identifier> availableRecipeTypes, Map<ComparableItemStack, Integer> inputMap) {
        List<TRecipe> recipes = new ArrayList();

        // TODO: implement

        return recipes;
    }
}
