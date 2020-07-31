package com.kqp.tcrafting.screen;

import com.google.common.collect.Sets;
import com.kqp.tcrafting.client.screen.TCraftingScreen;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.recipe.TRecipeManager;
import com.kqp.tcrafting.recipe.data.RecipeType;
import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.recipe.interf.RecipeAccessProvider;
import com.kqp.tcrafting.screen.inventory.TCraftingRecipeLookUpInventory;
import com.kqp.tcrafting.screen.inventory.TCraftingResultInventory;
import com.kqp.tcrafting.screen.slot.TRecipeSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.stream.Collectors;

public class CraftingSession {
    /**
     * Minimum of time needed to pass before a refresh can be done.
     */
    private static final long EXPIRATION_TIME = 10L;

    public final TCraftingResultInventory craftingResultInventory;
    public final TCraftingRecipeLookUpInventory lookUpResultInventory;

    public List<TRecipe> craftingRecipes = new ArrayList();
    public List<Boolean> craftingRecipeAvailabilities = new ArrayList();
    public List<ItemStack> craftingItemStacks = new ArrayList();

    public List<TRecipe> lookUpRecipes = new ArrayList();
    public List<ItemStack> lookUpItemStacks = new ArrayList();

    private final TCraftingScreenHandler screenHandler;
    private final PlayerEntity player;
    private final PlayerInventory playerInventory;
    private final boolean isClient;
    private final TRecipeManager tRecipeManager;


    public CraftingSession(TCraftingScreenHandler screenHandler, PlayerEntity player) {
        this.screenHandler = screenHandler;
        this.player = player;
        this.playerInventory = player.inventory;
        this.isClient = player.world.isClient;
        this.tRecipeManager = TRecipeManager.getFor(player.world);

        this.craftingResultInventory = screenHandler.resultInventory;
        this.lookUpResultInventory = screenHandler.lookUpInventory;
    }

    /**
     * Refreshes the list of available recipes.
     * Executed on server only and then synced to client.
     */
    public void refreshCraftingResults(boolean sync) {
        if (!isClient) {
            Set<TRecipe> oldRecipes = new HashSet(craftingRecipes);
            Set<TRecipe> calculatedRecipes = tRecipeManager.getMatches(
                    getAvailableRecipeTypes(),
                    playerInventory.main
            );

            Sets.difference(calculatedRecipes, oldRecipes)
                    .stream()
                    .sorted(Comparator.comparing(TRecipe::getSortString))
                    .forEach(craftingRecipes::add);

            for (int i = 0; i < craftingRecipes.size(); i++) {
                TRecipe recipe = craftingRecipes.get(i);
                boolean availability = calculatedRecipes.contains(recipe);

                if (i < craftingRecipeAvailabilities.size()) {
                    craftingRecipeAvailabilities.set(i, availability);
                } else {
                    craftingRecipeAvailabilities.add(i, availability);
                }

                if (i < craftingItemStacks.size()) {
                    craftingItemStacks.set(i, recipe.result.copy());
                } else {
                    craftingItemStacks.add(i, recipe.result.copy());
                }
            }

            if (sync) {
                syncToPlayer();
            }
        }
    }

    /**
     * Refresh the list of look up results.
     * Executed on server and client side (should match up, theoretically).
     */
    public void refreshLookUpResults() {
        ItemStack query = this.lookUpResultInventory.getStack(0);
        lookUpRecipes = tRecipeManager.getRecipesUsingItemStack(query);
        lookUpItemStacks = lookUpRecipes.stream().map(recipe -> recipe.result.copy()).collect(Collectors.toList());

        ((TCraftingScreen) MinecraftClient.getInstance().currentScreen).lookUpScrollPosition = 0F;
        scrollLookUpResults(0F);
    }

    /**
     * Gathers the valid recipe types given the player's proximity to blocks implementing {@link RecipeAccessProvider}.
     * Also gives recipes for the vanilla crafting table, furnace, and anvil.
     * <p>
     * TODO: un-hardcode this and make the blocks implement the interface
     */
    private String[] getAvailableRecipeTypes() {
        Set<String> types = new HashSet<>();

        // 2x2 recipes should always be accessible
        types.add(RecipeType.TWO_BY_TWO);

        // Finds blocks within a 6x3x6 box
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                for (int y = -1; y < 3; y++) {
                    Block block = player.world.getBlockState(player.getBlockPos().add(x, y, z)).getBlock();

                    if (block instanceof RecipeAccessProvider) {
                        types.addAll(Arrays.asList(((RecipeAccessProvider) block).getRecipeTypes()));
                    } else if (block == Blocks.CRAFTING_TABLE) {
                        types.add(RecipeType.CRAFTING_TABLE);
                    } else if (block == Blocks.FURNACE) {
                        types.add(RecipeType.FURNACE);
                    } else if (block == Blocks.ANVIL) {
                        types.add(RecipeType.ANVIL);
                    }
                }
            }
        }

        return types.toArray(new String[0]);
    }

    /**
     * Calculates what outputs should be in view given the position of the scroll bar.
     *
     * @param scrollPos Position of the scroll bar
     */
    public void scrollCraftingResults(float scrollPos) {
        int i = (this.craftingItemStacks.size() + 8 - 1) / 8 - 3;
        int j = (int) ((double) (scrollPos * (float) i) + 0.5D);
        if (j < 0) {
            j = 0;
        }

        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 8; ++l) {
                int listIndex = l + (k + j) * 8;

                ItemStack itemStack = ItemStack.EMPTY;
                int recipeIndex = -1;

                if (listIndex >= 0 && listIndex < this.craftingItemStacks.size()) {
                    itemStack = this.craftingItemStacks.get(listIndex);
                    recipeIndex = listIndex;
                }

                int slotIndex = l + k * 8;
                TRecipeSlot recipeSlot = (TRecipeSlot) screenHandler.getSlot(slotIndex);
                recipeSlot.setStack(itemStack);
                recipeSlot.recipeIndex = listIndex;

                TCraftingNetwork.SYNC_RECIPE_SLOT_S2C.sendToPlayer((ServerPlayerEntity) player, screenHandler, slotIndex);
            }
        }
    }

    /**
     * Calculates what results should be in view given the position of the scroll bar.
     *
     * @param position Position of the scroll bar
     */
    public void scrollLookUpResults(float position) {
        if (player.world.isClient) {
            int i = (this.lookUpItemStacks.size() + 3 - 1) / 3 - 6;
            int j = (int) ((double) (position * (float) i) + 0.5D);

            if (j < 0) {
                j = 0;
            }

            for (int k = 0; k < 6; ++k) {
                for (int l = 0; l < 3; ++l) {
                    int listIndex = (k + j) * 3 + l;

                    ItemStack itemStack = ItemStack.EMPTY;
                    int recipeIndex = -1;

                    if (listIndex >= 0 && listIndex < this.lookUpItemStacks.size()) {
                        itemStack = this.lookUpItemStacks.get(listIndex);
                        recipeIndex = listIndex;
                    }

                    int calcSlotIndex = l + k * 3;
                    int invIndex = 1 + calcSlotIndex;
                    int screenHandlerSlotIndex = 61 + calcSlotIndex;

                    lookUpResultInventory.setStack(invIndex, itemStack);
                    ((TRecipeSlot) screenHandler.getSlot(screenHandlerSlotIndex)).recipeIndex = recipeIndex;

                    //TCraftingNetwork.SYNC_RECIPE_SLOT_S2C.sendToPlayer((ServerPlayerEntity) player, screenHandler, screenHandlerSlotIndex);
                }
            }
        }
    }

    /**
     * Syncs the crafting session to its player client.
     */
    public void syncToPlayer() {
        TCraftingNetwork.SYNC_CRAFTING_SESSION_S2C.sendToPlayer((ServerPlayerEntity) player, buf -> {
            int recipeCount = craftingRecipes.size();
            buf.writeInt(recipeCount);

            for (int i = 0; i < recipeCount; i++) {
                TRecipe recipe = craftingRecipes.get(i);

                recipe.writeTo(buf);
                buf.writeBoolean(craftingRecipeAvailabilities.get(i));
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public void syncFromServer(List<TRecipe> recipes, List<Boolean> recipeAvailabilities) {
        this.craftingRecipes = recipes;
        this.craftingRecipeAvailabilities = recipeAvailabilities;

        for (int i = 0; i < recipes.size(); i++) {
            if (i < craftingItemStacks.size()) {
                craftingItemStacks.set(i, recipes.get(i).result.copy());
            } else {
                craftingItemStacks.add(i, recipes.get(i).result.copy());
            }
        }
    }
}
