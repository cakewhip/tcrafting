package com.kqp.tcrafting.tab;

import com.kqp.inventorytabs.tabs.tab.Tab;
import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.init.TCraftingClient;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class CraftingScreenTab implements Tab {
    @Override
    public void open(ClientPlayerEntity clientPlayerEntity) {
        TCraftingClient.triggerOpenCraftingMenu();
    }

    @Override
    public boolean shouldBeRemoved(ClientPlayerEntity clientPlayerEntity) {
        return false;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    @Override
    public StringRenderable getHoverText() {
        return new TranslatableText(Util.createTranslationKey("gui", TCrafting.id("crafting")));
    }
}
