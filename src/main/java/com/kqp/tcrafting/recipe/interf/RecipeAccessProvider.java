package com.kqp.tcrafting.recipe.interf;

import com.kqp.tcrafting.recipe.data.RecipeType;

/**
 * Used only by blocks that will give players access to specified recipe types.
 * <p>
 * See {@link RecipeType} for all valid types.
 */
public interface RecipeAccessProvider {
    /**
     * Types of recipes that the implementing client will provide.
     *
     * @return String[] of recipe types
     */
    String[] getRecipeTypes();
}
