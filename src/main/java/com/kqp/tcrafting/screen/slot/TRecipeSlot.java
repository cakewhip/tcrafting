package com.kqp.tcrafting.screen.slot;

import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

/**
 * Slot for both crafting and look-up results.
 */
public class TRecipeSlot extends Slot {
    private final TCraftingScreenHandler screenHandler;

    public PlayerInventory playerInventory;

    public final TCraftingScreenHandler.View view;
    public int recipeIndex = -1;

    public TRecipeSlot(TCraftingScreenHandler screenHandler,
                       Inventory inventory,
                       PlayerInventory playerInventory,
                       int invSlot, int xPosition, int yPosition,
                       TCraftingScreenHandler.View view) {
        super(inventory, invSlot, xPosition, yPosition);
        this.screenHandler = screenHandler;
        this.playerInventory = playerInventory;
        this.view = view;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        return super.takeStack(amount);
    }

    @Override
    protected void onCrafted(ItemStack itemStack, int amount) {
        onCrafted(itemStack);
    }

    @Override
    protected void onTake(int amount) {
    }

    /**
     * Only allow taking if this slot is a crafting slot.
     *
     * @param playerEntity
     * @return
     */
    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        if (view == TCraftingScreenHandler.View.CRAFTING) {
            List<Boolean> availabilities = screenHandler.craftingSession.craftingRecipeAvailabilities;
            if (recipeIndex < availabilities.size() && availabilities.get(recipeIndex)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called whenever an item is taken from this slot.
     *
     * @param stack ItemStack that is being crafted
     */
    @Override
    protected void onCrafted(ItemStack stack) {
        if (recipeIndex != -1 && !stack.isEmpty()) {
            TRecipe recipe = getRecipe();
            recipe.onCraft(playerInventory);
        }

        markDirty();
        screenHandler.updateCraftingResults();
    }

    @Override
    public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
        this.onCrafted(stack);

        return stack;
    }

    /**
     * Returns null if recipeIndex is -1.
     *
     * @return a recipe or null
     */
    public TRecipe getRecipe() {
        if (recipeIndex != -1) {
            List<TRecipe> recipeList = screenHandler.craftingSession.craftingRecipes;

            if (view == TCraftingScreenHandler.View.LOOK_UP) {
                recipeList = screenHandler.craftingSession.lookUpRecipes;
            }

            if (recipeIndex < recipeList.size()) {
                return recipeList.get(recipeIndex);
            }
        }

        return null;
    }
}
