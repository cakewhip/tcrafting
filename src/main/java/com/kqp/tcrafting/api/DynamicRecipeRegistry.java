package com.kqp.tcrafting.api;

import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.recipe.dynamic.RepairToolsDynamicRecipe;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class DynamicRecipeRegistry {
    private static Map<Identifier, DynamicRecipe> DYNAMIC_RECIPES = new HashMap();

    public static final DynamicRecipe REPAIR_TOOLS = register(TCrafting.id("repair_tools"), new RepairToolsDynamicRecipe());

    public static void init() {
    }

    public static DynamicRecipe register(Identifier identifier, DynamicRecipe recipe) {
        DYNAMIC_RECIPES.put(identifier, recipe);

        return recipe;
    }

    public static Map<Identifier, DynamicRecipe> getDynamicRecipes() {
        return DYNAMIC_RECIPES;
    }
}
