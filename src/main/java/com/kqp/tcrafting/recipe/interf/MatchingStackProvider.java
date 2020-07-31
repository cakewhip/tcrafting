package com.kqp.tcrafting.recipe.interf;

import net.minecraft.item.ItemStack;

public interface MatchingStackProvider {
    ItemStack[] getMatchingStacks();
}
