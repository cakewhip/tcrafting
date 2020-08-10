package com.kqp.tcrafting.network.screen.crafting;

import com.kqp.tcrafting.network.base.BasePacketS2C;
import com.kqp.tcrafting.recipe.data.TRecipe;
import com.kqp.tcrafting.screen.TCraftingScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SyncCraftingSessionS2C extends BasePacketS2C {
    public SyncCraftingSessionS2C() {
        super("sync_crafting_session_s2c");
    }

    @Override
    public void accept(PacketContext context, PacketByteBuf data) {
        int recipeCount = data.readInt();

        List<TRecipe> recipes = new ArrayList(recipeCount);
        List<Boolean> recipeAvailabilities = new ArrayList(recipeCount);
        List<ItemStack> stackList = new ArrayList();

        for (int i = 0; i < recipeCount; i++) {
            TRecipe recipe = TRecipe.readFrom(data);
            boolean availability = data.readBoolean();

            recipes.add(i, recipe);
            recipeAvailabilities.add(i, availability);
        }

        context.getTaskQueue().execute(() -> {
            syncToClientSession(recipes, recipeAvailabilities);
        });
    }

    @Environment(EnvType.CLIENT)
    private static void syncToClientSession(List<TRecipe> recipes, List<Boolean> recipeAvailabilities) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player.currentScreenHandler instanceof TCraftingScreenHandler) {
            ((TCraftingScreenHandler) player.currentScreenHandler).craftingSession.syncFromServer(recipes, recipeAvailabilities);
        }
    }
}
