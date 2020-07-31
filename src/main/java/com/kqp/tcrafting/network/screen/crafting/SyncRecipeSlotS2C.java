package com.kqp.tcrafting.network.screen.crafting;

import com.kqp.tcrafting.client.screen.TCraftingScreen;
import com.kqp.tcrafting.network.base.BasePacketS2C;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import com.kqp.tcrafting.screen.slot.TRecipeSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class SyncRecipeSlotS2C extends BasePacketS2C {
    public SyncRecipeSlotS2C() {
        super("sync_recipe_slot_s2c");
    }

    public void sendToPlayer(ServerPlayerEntity player, TCraftingScreenHandler screenHandler, int slotIndex) {
        this.sendToPlayer(player, buf -> {
            TRecipeSlot recipeSlot = (TRecipeSlot) screenHandler.getSlot(slotIndex);
            buf.writeInt(slotIndex);
            buf.writeItemStack(recipeSlot.getStack());
            buf.writeInt(recipeSlot.recipeIndex);
        });
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        int slotIndex = data.readInt();
        ItemStack itemStack = data.readItemStack();
        int recipeIndex = data.readInt();

        context.getTaskQueue().execute(() -> {
            syncClientRecipeSlot(slotIndex, itemStack, recipeIndex);
        });
    }

    @Environment(EnvType.CLIENT)
    private static void syncClientRecipeSlot(int slotIndex, ItemStack itemStack, int recipeIndex) {
        TCraftingScreenHandler screenHandler = ((TCraftingScreen) MinecraftClient.getInstance().currentScreen).getScreenHandler();
        TRecipeSlot recipeSlot = (TRecipeSlot) screenHandler.getSlot(slotIndex);

        recipeSlot.setStack(itemStack);
        recipeSlot.recipeIndex = recipeIndex;
    }
}
