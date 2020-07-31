package com.kqp.tcrafting.screen.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.Iterator;

/**
 * Inventory class for the recipe look-up slot in Awaken's crafting slot.
 * Just 1 slot.
 */
public class TCraftingRecipeLookUpInventory implements Inventory {
    private final DefaultedList<ItemStack> stack;

    public TCraftingRecipeLookUpInventory() {
        this.stack = DefaultedList.ofSize(19, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 19;
    }

    @Override
    public boolean isEmpty() {
        Iterator var1 = this.stack.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = (ItemStack) var1.next();
        } while (itemStack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stack.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.removeStack(this.stack, slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.stack, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stack.set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stack.clear();
    }
}