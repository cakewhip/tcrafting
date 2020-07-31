package com.kqp.tcrafting.screen;

import com.kqp.tcrafting.init.TCrafting;
import com.kqp.tcrafting.network.init.TCraftingNetwork;
import com.kqp.tcrafting.screen.inventory.TCraftingRecipeLookUpInventory;
import com.kqp.tcrafting.screen.inventory.TCraftingResultInventory;
import com.kqp.tcrafting.screen.slot.TRecipeSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Screen handler for the crafting GUI.
 */
public class TCraftingScreenHandler extends ScreenHandler {
    public final TCraftingResultInventory resultInventory;
    public final TCraftingRecipeLookUpInventory lookUpInventory;
    private final ScreenHandlerContext context;
    private final PlayerEntity player;

    public final CraftingSession craftingSession;

    /**
     * Client-side constructor.
     *
     * @param syncId Sync ID
     */
    public TCraftingScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    /**
     * Server-side constructor.
     *
     * @param syncId          Sync ID
     * @param playerInventory Player's screen
     * @param context         Context for executing server-side tasks
     */
    public TCraftingScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(TCrafting.TCRAFTING_SCREEN_HANDLER, syncId);

        // Init fields
        this.context = context;
        this.player = playerInventory.player;

        // Init inventories
        resultInventory = new TCraftingResultInventory();
        lookUpInventory = new TCraftingRecipeLookUpInventory();

        // Crafting results screen (24 output)
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                this.addSlot(new TRecipeSlot(this, resultInventory, playerInventory, counter++, 8 + j * 18, 18 + i * 18, View.CRAFTING) {
                    @Override
                    public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
                        super.onStackChanged(originalItem, itemStack);

                        updateCraftingResults();
                    }
                });
            }
        }

        // Player Inventory (27 storage + 9 hotbar)
        {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 84 + i * 18) {
                        @Override
                        public void markDirty() {
                            super.markDirty();

                            updateCraftingResults();
                        }

                        @Override
                        public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
                            super.onStackChanged(originalItem, itemStack);

                            updateCraftingResults();
                        }
                    });
                }
            }

            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142) {
                    @Override
                    public void markDirty() {
                        super.markDirty();
                        updateCraftingResults();
                    }

                    @Override
                    public void onStackChanged(ItemStack originalItem, ItemStack itemStack) {
                        super.onStackChanged(originalItem, itemStack);

                        updateCraftingResults();
                    }
                });
            }
        }

        // Look-up screen (1 query slot + 18 result slots)
        {
            counter = 0;

            this.addSlot(new Slot(lookUpInventory, counter++, 209, 22) {
                @Override
                public void markDirty() {
                    super.markDirty();

                    if (player.world.isClient) {
                        updateRecipeLookUpResults();
                    }
                }
            });

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new TRecipeSlot(this, lookUpInventory, playerInventory, counter++, 186 + j * 18, 48 + i * 18, View.LOOK_UP));
                }
            }
        }

        this.craftingSession = new CraftingSession(this, player);
        craftingSession.refreshCraftingResults(false);
    }

    public void updateCraftingResults() {
        craftingSession.refreshCraftingResults(true);

        // If on server, notify the client that something has changed so the client can reply with the scroll bar position.
        if (!player.world.isClient) {
            TCraftingNetwork.REQUEST_SCROLL_POSITION_S2C.sendToPlayer((ServerPlayerEntity) player, View.CRAFTING);
        }
    }

    public void updateRecipeLookUpResults() {
        craftingSession.refreshLookUpResults();
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);

        // Drop look up slot
        player.inventory.offerOrDrop(player.world, lookUpInventory.removeStack(0));
    }

    /**
     * Handles the shift clicking.
     *
     * @param player  The player
     * @param invSlot The slot that is being shift clicked (I think)
     * @return The ItemStack (I don't know)
     */
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();

            if (invSlot < 24) {
                // Shift click inside result slots

                if (!this.insertItem(itemStack2, 51, 60, false)) {
                    if (!this.insertItem(itemStack2, 24, 51, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot >= 24 && invSlot < 51) {
                // Shift click inside main screen

                if (!this.insertItem(itemStack2, 60, 61, false)) {
                    if (!this.insertItem(itemStack2, 51, 60, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (invSlot >= 51 && invSlot < 60) {
                // Shift click inside hot-bar slots
                if (!this.insertItem(itemStack2, 60, 61, false)) {
                    if (!this.insertItem(itemStack2, 24, 51, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (invSlot == 60) {
                // Shift click inside recipe look-up slot
                if (!this.insertItem(itemStack2, 51, 60, false)) {
                    if (!this.insertItem(itemStack2, 24, 51, false)) {
                        slot.markDirty();
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemStack3 = slot.onTakeItem(player, itemStack2);
            if (invSlot == 0) {
                player.dropItem(itemStack3, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return !(slot instanceof TRecipeSlot);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public boolean shouldShowCraftingScrollbar() {
        return this.craftingSession.craftingItemStacks.size() > 24;
    }

    public boolean shouldShowLookUpScrollbar() {
        return this.craftingSession.lookUpItemStacks.size() > 18;
    }

    public static enum View {
        CRAFTING,
        LOOK_UP;

        public static View from(int cardinal) {
            return values()[cardinal];
        }
    }
}
